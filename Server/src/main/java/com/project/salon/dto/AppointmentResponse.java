package com.project.salon.dto;

import com.project.salon.entity.enums.AppointmentStatus;
import com.project.salon.entity.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AppointmentResponse {
    private Long id;
    private String appointmentReference;
    private UserResponse user;
    private ProviderResponse provider;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private AppointmentStatus status;
    private PaymentStatus paymentStatus;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AppointmentResponse() {
    }

    public AppointmentResponse(Long id, String appointmentReference, UserResponse user, ProviderResponse provider, LocalDate appointmentDate, LocalTime startTime, LocalTime endTime, AppointmentStatus status, PaymentStatus paymentStatus, BigDecimal amount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.appointmentReference = appointmentReference;
        this.user = user;
        this.provider = provider;
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.amount = amount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AppointmentResponseBuilder builder() {
        return new AppointmentResponseBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAppointmentReference() { return appointmentReference; }
    public void setAppointmentReference(String appointmentReference) { this.appointmentReference = appointmentReference; }
    public UserResponse getUser() { return user; }
    public void setUser(UserResponse user) { this.user = user; }
    public ProviderResponse getProvider() { return provider; }
    public void setProvider(ProviderResponse provider) { this.provider = provider; }
    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static class AppointmentResponseBuilder {
        private Long id;
        private String appointmentReference;
        private UserResponse user;
        private ProviderResponse provider;
        private LocalDate appointmentDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private AppointmentStatus status;
        private PaymentStatus paymentStatus;
        private BigDecimal amount;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public AppointmentResponseBuilder id(Long id) { this.id = id; return this; }
        public AppointmentResponseBuilder appointmentReference(String appointmentReference) { this.appointmentReference = appointmentReference; return this; }
        public AppointmentResponseBuilder user(UserResponse user) { this.user = user; return this; }
        public AppointmentResponseBuilder provider(ProviderResponse provider) { this.provider = provider; return this; }
        public AppointmentResponseBuilder appointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; return this; }
        public AppointmentResponseBuilder startTime(LocalTime startTime) { this.startTime = startTime; return this; }
        public AppointmentResponseBuilder endTime(LocalTime endTime) { this.endTime = endTime; return this; }
        public AppointmentResponseBuilder status(AppointmentStatus status) { this.status = status; return this; }
        public AppointmentResponseBuilder paymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; return this; }
        public AppointmentResponseBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public AppointmentResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public AppointmentResponseBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public AppointmentResponse build() {
            return new AppointmentResponse(id, appointmentReference, user, provider, appointmentDate, startTime, endTime, status, paymentStatus, amount, createdAt, updatedAt);
        }
    }
}
