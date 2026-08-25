package com.project.salon.service;

import com.project.salon.dto.AppointmentResponse;
import com.project.salon.dto.CreateAppointmentRequest;
import com.project.salon.dto.UserResponse;
import com.project.salon.entity.*;
import com.project.salon.entity.enums.AppointmentStatus;
import com.project.salon.entity.enums.PaymentStatus;
import com.project.salon.entity.enums.ProviderStatus;
import com.project.salon.entity.enums.Role;
import com.project.salon.event.AppointmentCancelledEvent;
import com.project.salon.exception.*;
import com.project.salon.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private static final Logger log = LoggerFactory.getLogger(AppointmentService.class);

    private final AppointmentRepository appointmentRepository;
    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;
    private final SalonConfigurationService configurationService;
    private final AppointmentStatusHistoryRepository historyRepository;
    private final ProviderService providerService;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;

    @Value("${app.appointment.cancellation-window-hours:2}")
    private int cancellationWindowHours;

    @Value("${app.appointment.pending-expiration-minutes:15}")
    private int pendingExpirationMinutes;

    @Value("${app.appointment.minimum-booking-lead-minutes:60}")
    private int minimumBookingLeadMinutes;

    private static final List<AppointmentStatus> ACTIVE_STATUSES = Arrays.asList(
            AppointmentStatus.PENDING_PAYMENT,
            AppointmentStatus.CONFIRMED,
            AppointmentStatus.COMPLETED
    );

    public AppointmentService(AppointmentRepository appointmentRepository,
                              ProviderRepository providerRepository,
                              UserRepository userRepository,
                              SalonConfigurationService configurationService,
                              AppointmentStatusHistoryRepository historyRepository,
                              ProviderService providerService,
                              ApplicationEventPublisher eventPublisher,
                              Clock clock) {
        this.appointmentRepository = appointmentRepository;
        this.providerRepository = providerRepository;
        this.userRepository = userRepository;
        this.configurationService = configurationService;
        this.historyRepository = historyRepository;
        this.providerService = providerService;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public AppointmentResponse bookAppointment(CreateAppointmentRequest request, User currentUser) {
        SalonConfiguration config = configurationService.getOrCreateDefaultConfig();
        LocalDate date = request.getAppointmentDate();
        LocalTime startTime = request.getStartTime();
        LocalTime endTime = startTime.plusMinutes(config.getSlotDuration());
        LocalDateTime appointmentDateTime = LocalDateTime.of(date, startTime);
        LocalDateTime minimumAllowedStart = LocalDateTime.now(clock).plusMinutes(minimumBookingLeadMinutes);

        if (date.isBefore(LocalDate.now(clock))) {
            throw new InvalidRequestException("Cannot book appointments for past dates.");
        }
        if (appointmentDateTime.isBefore(LocalDateTime.now(clock))) {
            throw new InvalidRequestException("Cannot book appointments for past time slots.");
        }
        if (appointmentDateTime.isBefore(minimumAllowedStart)) {
            throw new InvalidRequestException("Appointments must be booked at least " + minimumBookingLeadMinutes + " minutes in advance.");
        }
        if (startTime.isBefore(config.getOpeningTime()) || endTime.isAfter(config.getClosingTime())) {
            throw new InvalidRequestException("Selected time slot is outside salon operating hours.");
        }

        List<Provider> availableProviders = providerRepository.findByStatus(ProviderStatus.AVAILABLE);
        if (availableProviders.isEmpty()) {
            throw new SlotUnavailableException("No active salon providers are currently available.");
        }

        Provider selectedProvider = null;

        if (request.getProviderId() != null) {
            Provider targetProvider = providerRepository.findById(request.getProviderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Provider not found with ID: " + request.getProviderId()));

            if (targetProvider.getStatus() != ProviderStatus.AVAILABLE) {
                throw new SlotUnavailableException("Requested provider is not currently available.");
            }

            List<Appointment> overlapping = appointmentRepository.findProviderOverlappingAppointmentsWithLock(
                    targetProvider.getId(), date, startTime, endTime, ACTIVE_STATUSES
            );
            if (!overlapping.isEmpty()) {
                throw new SlotUnavailableException("Requested provider is already booked for this time slot.");
            }
            selectedProvider = targetProvider;
        } else {
            for (Provider provider : availableProviders) {
                List<Appointment> overlapping = appointmentRepository.findProviderOverlappingAppointmentsWithLock(
                        provider.getId(), date, startTime, endTime, ACTIVE_STATUSES
                );
                if (overlapping.isEmpty()) {
                    selectedProvider = provider;
                    break;
                }
            }
        }

        if (selectedProvider == null) {
            throw new SlotUnavailableException("The selected time slot is no longer available. All providers are booked.");
        }

        String reference = "SALON-" + date.toString().replace("-", "") + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        Appointment appointment = Appointment.builder()
                .user(currentUser)
                .provider(selectedProvider)
                .appointmentDate(date)
                .startTime(startTime)
                .endTime(endTime)
                .status(AppointmentStatus.PENDING_PAYMENT)
                .paymentStatus(PaymentStatus.PENDING)
                .amount(request.getAmount())
                .appointmentReference(reference)
                .build();

        appointment = appointmentRepository.save(appointment);

        recordStatusHistory(appointment, AppointmentStatus.PENDING_PAYMENT, AppointmentStatus.PENDING_PAYMENT,
                currentUser.getEmail(), "Appointment created, awaiting payment.");

        log.info("Appointment booked: Ref={}, User={}, Provider={}, Date={}, Time={}",
                reference, currentUser.getEmail(), selectedProvider.getName(), date, startTime);

        return mapToResponse(appointment);
    }

    @Transactional
    public AppointmentResponse cancelAppointment(Long appointmentId, String reason, User currentUser) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

        if (!currentUser.getRole().equals(Role.ADMIN) && !appointment.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You are not authorized to cancel this appointment.");
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED ||
            appointment.getStatus() == AppointmentStatus.COMPLETED ||
            appointment.getStatus() == AppointmentStatus.EXPIRED) {
            throw new InvalidAppointmentStateException("Appointment cannot be cancelled in its current state: " + appointment.getStatus());
        }

        if (!currentUser.getRole().equals(Role.ADMIN)) {
            LocalDateTime appointmentDateTime = LocalDateTime.of(appointment.getAppointmentDate(), appointment.getStartTime());
            if (LocalDateTime.now(clock).plusHours(cancellationWindowHours).isAfter(appointmentDateTime)) {
                throw new InvalidAppointmentStateException("Appointments cannot be cancelled within " + cancellationWindowHours + " hours of the scheduled time.");
            }
        }

        AppointmentStatus oldStatus = appointment.getStatus();
        appointment.setStatus(AppointmentStatus.CANCELLED);
        if (appointment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            appointment.setPaymentStatus(PaymentStatus.REFUNDED);
        }

        appointment = appointmentRepository.save(appointment);

        recordStatusHistory(appointment, oldStatus, AppointmentStatus.CANCELLED,
                currentUser.getEmail(), reason != null ? reason : "Cancelled by user/admin");

        log.info("Appointment cancelled: Ref={}, CancelledBy={}", appointment.getAppointmentReference(), currentUser.getEmail());

        eventPublisher.publishEvent(new AppointmentCancelledEvent(this, appointment, currentUser.getEmail(), reason));

        return mapToResponse(appointment);
    }

    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentById(Long appointmentId, User currentUser) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

        if (!currentUser.getRole().equals(Role.ADMIN) && !appointment.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You are not authorized to view this appointment.");
        }

        return mapToResponse(appointment);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getUserAppointments(Long userId) {
        return appointmentRepository.findByUserIdOrderByAppointmentDateDescStartTimeDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponse> getAppointmentsWithFilters(LocalDate date, AppointmentStatus status, Long providerId, Pageable pageable) {
        return appointmentRepository.findAppointmentsWithFilters(date, status, providerId, pageable)
                .map(this::mapToResponse);
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void expirePendingAppointments() {
        LocalDateTime threshold = LocalDateTime.now(clock).minusMinutes(pendingExpirationMinutes);
        List<Appointment> expired = appointmentRepository.findByStatusAndCreatedAtBefore(AppointmentStatus.PENDING_PAYMENT, threshold);

        for (Appointment app : expired) {
            app.setStatus(AppointmentStatus.EXPIRED);
            app.setPaymentStatus(PaymentStatus.FAILED);
            appointmentRepository.save(app);
            recordStatusHistory(app, AppointmentStatus.PENDING_PAYMENT, AppointmentStatus.EXPIRED, "SYSTEM", "Payment window expired");
            log.info("Appointment expired automatically: Ref={}", app.getAppointmentReference());
        }
    }

    @Transactional
    public void recordStatusHistory(Appointment appointment, AppointmentStatus oldStatus, AppointmentStatus newStatus, String changedBy, String reason) {
        AppointmentStatusHistory history = AppointmentStatusHistory.builder()
                .appointment(appointment)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedBy(changedBy)
                .reason(reason)
                .build();
        historyRepository.save(history);
    }

    public AppointmentResponse mapToResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .appointmentReference(appointment.getAppointmentReference())
                .user(UserResponse.builder()
                        .id(appointment.getUser().getId())
                        .googleId(appointment.getUser().getGoogleId())
                        .name(appointment.getUser().getName())
                        .email(appointment.getUser().getEmail())
                        .profileImage(appointment.getUser().getProfileImage())
                        .role(appointment.getUser().getRole())
                        .enabled(appointment.getUser().isEnabled())
                        .createdAt(appointment.getUser().getCreatedAt())
                        .build())
                .provider(providerService.mapToResponse(appointment.getProvider()))
                .appointmentDate(appointment.getAppointmentDate())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .status(appointment.getStatus())
                .paymentStatus(appointment.getPaymentStatus())
                .amount(appointment.getAmount())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }
}
