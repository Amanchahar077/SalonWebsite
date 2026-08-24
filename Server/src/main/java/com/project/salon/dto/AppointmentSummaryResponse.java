package com.project.salon.dto;

import com.project.salon.entity.enums.AppointmentStatus;
import com.project.salon.entity.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentSummaryResponse {
    private Long id;
    private String appointmentReference;
    private String userName;
    private String providerName;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private AppointmentStatus status;
    private PaymentStatus paymentStatus;
    private BigDecimal amount;

    public AppointmentSummaryResponse() {
    }

    public AppointmentSummaryResponse(Long id, String appointmentReference, String userName, String providerName, LocalDate appointmentDate, LocalTime startTime, LocalTime endTime, AppointmentStatus status, PaymentStatus paymentStatus, BigDecimal amount) {
        this.id = id;
        this.appointmentReference = appointmentReference;
        this.userName = userName;
        this.providerName = providerName;
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.amount = amount;
    }

    public static AppointmentSummaryResponseBuilder builder() {
        return new AppointmentSummaryResponseBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAppointmentReference() { return appointmentReference; }
    public void setAppointmentReference(String appointmentReference) { this.appointmentReference = appointmentReference; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }
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

    public static class AppointmentSummaryResponseBuilder {
        private Long id;
        private String appointmentReference;
        private String userName;
        private String providerName;
        private LocalDate appointmentDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private AppointmentStatus status;
        private PaymentStatus paymentStatus;
        private BigDecimal amount;

        public AppointmentSummaryResponseBuilder id(Long id) { this.id = id; return this; }
        public AppointmentSummaryResponseBuilder appointmentReference(String appointmentReference) { this.appointmentReference = appointmentReference; return this; }
        public AppointmentSummaryResponseBuilder userName(String userName) { this.userName = userName; return this; }
        public AppointmentSummaryResponseBuilder providerName(String providerName) { this.providerName = providerName; return this; }
        public AppointmentSummaryResponseBuilder appointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; return this; }
        public AppointmentSummaryResponseBuilder startTime(LocalTime startTime) { this.startTime = startTime; return this; }
        public AppointmentSummaryResponseBuilder endTime(LocalTime endTime) { this.endTime = endTime; return this; }
        public AppointmentSummaryResponseBuilder status(AppointmentStatus status) { this.status = status; return this; }
        public AppointmentSummaryResponseBuilder paymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; return this; }
        public AppointmentSummaryResponseBuilder amount(BigDecimal amount) { this.amount = amount; return this; }

        public AppointmentSummaryResponse build() {
            return new AppointmentSummaryResponse(id, appointmentReference, userName, providerName, appointmentDate, startTime, endTime, status, paymentStatus, amount);
        }
    }
}
