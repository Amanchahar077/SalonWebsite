package com.project.salon.service;

import com.project.salon.dto.AppointmentSummaryResponse;
import com.project.salon.dto.DashboardResponse;
import com.project.salon.entity.Appointment;
import com.project.salon.entity.enums.AppointmentStatus;
import com.project.salon.entity.enums.ProviderStatus;
import com.project.salon.repository.AppointmentRepository;
import com.project.salon.repository.ProviderRepository;
import com.project.salon.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminDashboardService {

    private static final Logger log = LoggerFactory.getLogger(AdminDashboardService.class);

    private final AppointmentRepository appointmentRepository;
    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;

    public AdminDashboardService(AppointmentRepository appointmentRepository,
                                 ProviderRepository providerRepository,
                                 UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.providerRepository = providerRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboardStatistics() {
        LocalDate today = LocalDate.now();

        long todaysAppointments = appointmentRepository.countByAppointmentDate(today);
        long upcomingAppointments = appointmentRepository.countByStatus(AppointmentStatus.CONFIRMED);
        long completedAppointments = appointmentRepository.countByStatus(AppointmentStatus.COMPLETED);
        long cancelledAppointments = appointmentRepository.countByStatus(AppointmentStatus.CANCELLED);

        BigDecimal totalRevenue = appointmentRepository.calculateTotalRevenue();
        BigDecimal todaysRevenue = appointmentRepository.calculateRevenueForDate(today);

        long totalUsers = userRepository.count();
        long availableProvidersCount = providerRepository.countByStatus(ProviderStatus.AVAILABLE);
        long totalProvidersCount = providerRepository.count();

        List<AppointmentSummaryResponse> recentAppointments = appointmentRepository
                .findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt")))
                .getContent().stream()
                .map(this::mapToSummary)
                .collect(Collectors.toList());

        return DashboardResponse.builder()
                .todaysAppointments(todaysAppointments)
                .upcomingAppointments(upcomingAppointments)
                .completedAppointments(completedAppointments)
                .cancelledAppointments(cancelledAppointments)
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .todaysRevenue(todaysRevenue != null ? todaysRevenue : BigDecimal.ZERO)
                .totalUsers(totalUsers)
                .availableProvidersCount(availableProvidersCount)
                .totalProvidersCount(totalProvidersCount)
                .recentAppointments(recentAppointments)
                .build();
    }

    private AppointmentSummaryResponse mapToSummary(Appointment app) {
        return AppointmentSummaryResponse.builder()
                .id(app.getId())
                .appointmentReference(app.getAppointmentReference())
                .userName(app.getUser() != null ? app.getUser().getName() : "Customer")
                .providerName(app.getProvider() != null ? app.getProvider().getName() : "Unassigned")
                .appointmentDate(app.getAppointmentDate())
                .startTime(app.getStartTime())
                .endTime(app.getEndTime())
                .status(app.getStatus())
                .paymentStatus(app.getPaymentStatus())
                .amount(app.getAmount())
                .build();
    }
}
