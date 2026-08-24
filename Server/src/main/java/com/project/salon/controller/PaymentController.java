package com.project.salon.controller;

import com.project.salon.dto.PaymentOrderResponse;
import com.project.salon.dto.PaymentVerificationRequest;
import com.project.salon.security.CurrentUser;
import com.project.salon.security.UserPrincipal;
import com.project.salon.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payments", description = "Razorpay payment integration endpoints")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-order")
    @Operation(summary = "Create Razorpay Order for an appointment")
    public ResponseEntity<PaymentOrderResponse> createOrder(
            @RequestParam("appointmentId") Long appointmentId,
            @CurrentUser UserPrincipal principal) {
        return ResponseEntity.ok(paymentService.createOrder(appointmentId, principal.getUser()));
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify Razorpay payment signature after checkout")
    public ResponseEntity<PaymentOrderResponse> verifyPayment(
            @Valid @RequestBody PaymentVerificationRequest request,
            @CurrentUser UserPrincipal principal) {
        return ResponseEntity.ok(paymentService.verifyPayment(request, principal.getUser()));
    }

    @PostMapping("/webhook")
    @Operation(summary = "Handle incoming Razorpay Webhook events")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {
        paymentService.handleWebhook(payload, signature);
        return ResponseEntity.ok("Webhook processed successfully");
    }
}
