package com.project.salon.dto;

import jakarta.validation.constraints.NotBlank;

public class PaymentVerificationRequest {

    @NotBlank(message = "Razorpay order ID is required")
    private String razorpayOrderId;

    @NotBlank(message = "Razorpay payment ID is required")
    private String razorpayPaymentId;

    @NotBlank(message = "Razorpay signature is required")
    private String razorpaySignature;

    public PaymentVerificationRequest() {
    }

    public PaymentVerificationRequest(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        this.razorpayOrderId = razorpayOrderId;
        this.razorpayPaymentId = razorpayPaymentId;
        this.razorpaySignature = razorpaySignature;
    }

    public static PaymentVerificationRequestBuilder builder() {
        return new PaymentVerificationRequestBuilder();
    }

    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }
    public String getRazorpayPaymentId() { return razorpayPaymentId; }
    public void setRazorpayPaymentId(String razorpayPaymentId) { this.razorpayPaymentId = razorpayPaymentId; }
    public String getRazorpaySignature() { return razorpaySignature; }
    public void setRazorpaySignature(String razorpaySignature) { this.razorpaySignature = razorpaySignature; }

    public static class PaymentVerificationRequestBuilder {
        private String razorpayOrderId;
        private String razorpayPaymentId;
        private String razorpaySignature;

        public PaymentVerificationRequestBuilder razorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; return this; }
        public PaymentVerificationRequestBuilder razorpayPaymentId(String razorpayPaymentId) { this.razorpayPaymentId = razorpayPaymentId; return this; }
        public PaymentVerificationRequestBuilder razorpaySignature(String razorpaySignature) { this.razorpaySignature = razorpaySignature; return this; }

        public PaymentVerificationRequest build() {
            return new PaymentVerificationRequest(razorpayOrderId, razorpayPaymentId, razorpaySignature);
        }
    }
}
