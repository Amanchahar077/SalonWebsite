package com.project.salon.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class CreateAppointmentRequest {

    @NotNull(message = "Appointment date is required")
    @FutureOrPresent(message = "Appointment date cannot be in the past")
    private LocalDate appointmentDate;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private Long providerId;

    public CreateAppointmentRequest() {
    }

    public CreateAppointmentRequest(LocalDate appointmentDate, LocalTime startTime, BigDecimal amount, Long providerId) {
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
        this.amount = amount;
        this.providerId = providerId;
    }

    public static CreateAppointmentRequestBuilder builder() {
        return new CreateAppointmentRequestBuilder();
    }

    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Long getProviderId() { return providerId; }
    public void setProviderId(Long providerId) { this.providerId = providerId; }

    public static class CreateAppointmentRequestBuilder {
        private LocalDate appointmentDate;
        private LocalTime startTime;
        private BigDecimal amount;
        private Long providerId;

        public CreateAppointmentRequestBuilder appointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; return this; }
        public CreateAppointmentRequestBuilder startTime(LocalTime startTime) { this.startTime = startTime; return this; }
        public CreateAppointmentRequestBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public CreateAppointmentRequestBuilder providerId(Long providerId) { this.providerId = providerId; return this; }

        public CreateAppointmentRequest build() {
            return new CreateAppointmentRequest(appointmentDate, startTime, amount, providerId);
        }
    }
}
