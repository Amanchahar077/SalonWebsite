package com.project.salon.service;

import com.project.salon.dto.AvailabilityResponse;
import com.project.salon.dto.AvailableSlotResponse;
import com.project.salon.dto.ProviderResponse;
import com.project.salon.entity.Appointment;
import com.project.salon.entity.Provider;
import com.project.salon.entity.SalonConfiguration;
import com.project.salon.entity.enums.AppointmentStatus;
import com.project.salon.entity.enums.ProviderStatus;
import com.project.salon.repository.AppointmentRepository;
import com.project.salon.repository.ProviderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;

@Service
public class AvailabilityService {

    private static final Logger log = LoggerFactory.getLogger(AvailabilityService.class);

    private final SalonConfigurationService configurationService;
    private final ProviderRepository providerRepository;
    private final AppointmentRepository appointmentRepository;
    private final ProviderService providerService;
    private final Clock clock;

    @Value("${app.appointment.minimum-booking-lead-minutes:60}")
    private int minimumBookingLeadMinutes;

    private static final List<AppointmentStatus> ACTIVE_STATUSES = Arrays.asList(
            AppointmentStatus.PENDING_PAYMENT,
            AppointmentStatus.CONFIRMED,
            AppointmentStatus.COMPLETED
    );

    public AvailabilityService(SalonConfigurationService configurationService,
                               ProviderRepository providerRepository,
                               AppointmentRepository appointmentRepository,
                               ProviderService providerService,
                               Clock clock) {
        this.configurationService = configurationService;
        this.providerRepository = providerRepository;
        this.appointmentRepository = appointmentRepository;
        this.providerService = providerService;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public AvailabilityResponse getAvailabilityForDate(LocalDate date) {
        SalonConfiguration config = configurationService.getOrCreateDefaultConfig();
        List<Provider> allAvailableProviders = providerRepository.findByStatus(ProviderStatus.AVAILABLE);

        LocalTime opening = config.getOpeningTime();
        LocalTime closing = config.getClosingTime();
        int durationMinutes = config.getSlotDuration();

        List<AvailableSlotResponse> slots = new ArrayList<>();
        LocalTime currentStart = opening;

        while (currentStart.plusMinutes(durationMinutes).isBefore(closing) || 
               currentStart.plusMinutes(durationMinutes).equals(closing)) {
            
            LocalTime currentEnd = currentStart.plusMinutes(durationMinutes);
            LocalDateTime slotStartDateTime = LocalDateTime.of(date, currentStart);
            boolean meetsLeadTime = !slotStartDateTime.isBefore(LocalDateTime.now(clock).plusMinutes(minimumBookingLeadMinutes));

            List<Appointment> overlappingAppointments = appointmentRepository.findOverlappingActiveAppointments(
                    date, currentStart, currentEnd, ACTIVE_STATUSES
            );

            Set<Long> bookedProviderIds = overlappingAppointments.stream()
                    .map(a -> a.getProvider().getId())
                    .collect(Collectors.toSet());

            List<ProviderResponse> freeProviders = allAvailableProviders.stream()
                    .filter(p -> !bookedProviderIds.contains(p.getId()))
                    .map(providerService::mapToResponse)
                    .collect(Collectors.toList());

            int freeCount = freeProviders.size();
            boolean isSlotAvailable = meetsLeadTime && freeCount > 0;

            slots.add(AvailableSlotResponse.builder()
                    .startTime(currentStart)
                    .endTime(currentEnd)
                    .availableProvidersCount(isSlotAvailable ? freeCount : 0)
                    .available(isSlotAvailable)
                    .availableProviders(isSlotAvailable ? freeProviders : List.of())
                    .build());

            currentStart = currentEnd;
        }

        return AvailabilityResponse.builder()
                .date(date)
                .totalProvidersCount(allAvailableProviders.size())
                .slots(slots)
                .build();
    }
}
