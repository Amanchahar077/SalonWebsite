package com.project.salon.controller;

import com.project.salon.dto.AppointmentResponse;
import com.project.salon.dto.AvailabilityResponse;
import com.project.salon.dto.CreateAppointmentRequest;
import com.project.salon.security.CurrentUser;
import com.project.salon.security.UserPrincipal;
import com.project.salon.service.AppointmentService;
import com.project.salon.service.AvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@Tag(name = "Appointments", description = "User appointment management and slot availability endpoints")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AvailabilityService availabilityService;

    public AppointmentController(AppointmentService appointmentService, AvailabilityService availabilityService) {
        this.appointmentService = appointmentService;
        this.availabilityService = availabilityService;
    }

    @GetMapping("/availability")
    @Operation(summary = "View available appointment dates and dynamic time slots")
    public ResponseEntity<AvailabilityResponse> getAvailability(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(availabilityService.getAvailabilityForDate(date));
    }

    @PostMapping
    @Operation(summary = "Book a new salon appointment")
    public ResponseEntity<AppointmentResponse> bookAppointment(
            @Valid @RequestBody CreateAppointmentRequest request,
            @CurrentUser UserPrincipal principal) {
        AppointmentResponse response = appointmentService.bookAppointment(request, principal.getUser());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/my")
    @Operation(summary = "View authenticated user's appointment history")
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments(@CurrentUser UserPrincipal principal) {
        return ResponseEntity.ok(appointmentService.getUserAppointments(principal.getUser().getId()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "View detailed information of a specific appointment")
    public ResponseEntity<AppointmentResponse> getAppointmentDetails(
            @PathVariable("id") Long id,
            @CurrentUser UserPrincipal principal) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id, principal.getUser()));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel an appointment according to cancellation policy")
    public ResponseEntity<AppointmentResponse> cancelAppointment(
            @PathVariable("id") Long id,
            @RequestParam(value = "reason", required = false) String reason,
            @CurrentUser UserPrincipal principal) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(id, reason, principal.getUser()));
    }
}
