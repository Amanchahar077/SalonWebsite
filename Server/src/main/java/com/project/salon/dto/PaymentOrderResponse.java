package com.project.salon.dto;

import com.project.salon.entity.enums.PaymentStatus;

import java.math.BigDecimal;

public class PaymentOrderResponse {
    private Long appointmentId;
    private String appointmentReference;
    private String razorpayOrderId;
    private String keyId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;

    public PaymentOrderResponse() {
    }

    public PaymentOrderResponse(Long appointmentId, String appointmentReference, String razorpayOrderId, String keyId, BigDecimal amount, String currency, PaymentStatus status) {
        this.appointmentId = appointmentId;
        this.appointmentReference = appointmentReference;
        this.razorpayOrderId = razorpayOrderId;
        this.keyId = keyId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
    }

    public static PaymentOrderResponseBuilder builder() {
        return new PaymentOrderResponseBuilder();
    }

    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }
    public String getAppointmentReference() { return appointmentReference; }
    public void setAppointmentReference(String appointmentReference) { this.appointmentReference = appointmentReference; }
    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }
    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public static class PaymentOrderResponseBuilder {
        private Long appointmentId;
        private String appointmentReference;
        private String razorpayOrderId;
        private String keyId;
        private BigDecimal amount;
        private String currency;
        private PaymentStatus status;

        public PaymentOrderResponseBuilder appointmentId(Long appointmentId) { this.appointmentId = appointmentId; return this; }
        public PaymentOrderResponseBuilder appointmentReference(String appointmentReference) { this.appointmentReference = appointmentReference; return this; }
        public PaymentOrderResponseBuilder razorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; return this; }
        public PaymentOrderResponseBuilder keyId(String keyId) { this.keyId = keyId; return this; }
        public PaymentOrderResponseBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public PaymentOrderResponseBuilder currency(String currency) { this.currency = currency; return this; }
        public PaymentOrderResponseBuilder status(PaymentStatus status) { this.status = status; return this; }

        public PaymentOrderResponse build() {
            return new PaymentOrderResponse(appointmentId, appointmentReference, razorpayOrderId, keyId, amount, currency, status);
        }
    }
}
