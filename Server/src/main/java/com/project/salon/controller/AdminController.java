package com.project.salon.controller;

import com.project.salon.dto.*;
import com.project.salon.entity.AppointmentStatusHistory;
import com.project.salon.entity.Notification;
import com.project.salon.entity.Payment;
import com.project.salon.entity.enums.AppointmentStatus;
import com.project.salon.entity.enums.ProviderStatus;
import com.project.salon.repository.AppointmentStatusHistoryRepository;
import com.project.salon.repository.NotificationRepository;
import com.project.salon.repository.PaymentRepository;
import com.project.salon.repository.UserRepository;
import com.project.salon.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Admin management & configuration endpoints")
public class AdminController {

    private final AdminDashboardService dashboardService;
    private final SalonConfigurationService configurationService;
    private final ProviderService providerService;
    private final AppointmentService appointmentService;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final AppointmentStatusHistoryRepository historyRepository;
    private final NotificationRepository notificationRepository;

    public AdminController(AdminDashboardService dashboardService,
                           SalonConfigurationService configurationService,
                           ProviderService providerService,
                           AppointmentService appointmentService,
                           UserRepository userRepository,
                           PaymentRepository paymentRepository,
                           AppointmentStatusHistoryRepository historyRepository,
                           NotificationRepository notificationRepository) {
        this.dashboardService = dashboardService;
        this.configurationService = configurationService;
        this.providerService = providerService;
        this.appointmentService = appointmentService;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.historyRepository = historyRepository;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard statistics")
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboardStatistics());
    }

    @GetMapping("/users")
    @Operation(summary = "View all registered users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userRepository.findAll().stream()
                .map(u -> UserResponse.builder()
                        .id(u.getId())
                        .googleId(u.getGoogleId())
                        .name(u.getName())
                        .email(u.getEmail())
                        .profileImage(u.getProfileImage())
                        .role(u.getRole())
                        .enabled(u.isEnabled())
                        .createdAt(u.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/appointments")
    @Operation(summary = "View all appointments with date/status/provider filters")
    public ResponseEntity<Page<AppointmentResponse>> getAppointments(
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(value = "status", required = false) AppointmentStatus status,
            @RequestParam(value = "providerId", required = false) Long providerId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        Page<AppointmentResponse> result = appointmentService.getAppointmentsWithFilters(date, status, providerId, PageRequest.of(page, size));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/payments")
    @Operation(summary = "View all payment records")
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentRepository.findAll());
    }

    @PutMapping("/salon/configuration")
    @Operation(summary = "Update salon opening/closing hours and provider count")
    public ResponseEntity<SalonConfigurationResponse> updateSalonConfiguration(
            @Valid @RequestBody SalonConfigurationRequest request) {
        return ResponseEntity.ok(configurationService.updateConfiguration(request));
    }

    @GetMapping("/providers")
    @Operation(summary = "Get all salon providers / barbers")
    public ResponseEntity<List<ProviderResponse>> getAllProviders() {
        return ResponseEntity.ok(providerService.getAllProviders());
    }

    @PostMapping("/providers")
    @Operation(summary = "Add a new salon provider / stylist")
    public ResponseEntity<ProviderResponse> addProvider(@Valid @RequestBody ProviderRequest request) {
        ProviderResponse response = providerService.addProvider(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/providers/{id}")
    @Operation(summary = "Update provider details")
    public ResponseEntity<ProviderResponse> updateProvider(
            @PathVariable("id") Long id,
            @Valid @RequestBody ProviderRequest request) {
        return ResponseEntity.ok(providerService.updateProvider(id, request));
    }

    @DeleteMapping("/providers/{id}")
    @Operation(summary = "Remove a provider")
    public ResponseEntity<Void> deleteProvider(@PathVariable("id") Long id) {
        providerService.deleteProvider(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/providers/{id}/status")
    @Operation(summary = "Enable or disable a provider (AVAILABLE / UNAVAILABLE)")
    public ResponseEntity<ProviderResponse> updateProviderStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") ProviderStatus status) {
        return ResponseEntity.ok(providerService.updateProviderStatus(id, status));
    }

    @GetMapping("/appointments/{id}/history")
    @Operation(summary = "View status change audit history for an appointment")
    public ResponseEntity<List<AppointmentHistoryResponse>> getAppointmentHistory(@PathVariable("id") Long id) {
        List<AppointmentStatusHistory> historyList = historyRepository.findByAppointmentIdOrderByCreatedAtDesc(id);
        List<AppointmentHistoryResponse> responses = historyList.stream()
                .map(h -> AppointmentHistoryResponse.builder()
                        .id(h.getId())
                        .appointmentId(h.getAppointment().getId())
                        .oldStatus(h.getOldStatus())
                        .newStatus(h.getNewStatus())
                        .changedBy(h.getChangedBy())
                        .reason(h.getReason())
                        .createdAt(h.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/notifications")
    @Operation(summary = "View email notification history logs")
    public ResponseEntity<List<NotificationResponse>> getNotificationLogs() {
        List<Notification> notifications = notificationRepository.findTop50ByOrderByCreatedAtDesc();
        List<NotificationResponse> responses = notifications.stream()
                .map(n -> NotificationResponse.builder()
                        .id(n.getId())
                        .userId(n.getUser().getId())
                        .appointmentId(n.getAppointment() != null ? n.getAppointment().getId() : null)
                        .type(n.getType())
                        .recipient(n.getRecipient())
                        .status(n.getStatus())
                        .sentAt(n.getSentAt())
                        .createdAt(n.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}
