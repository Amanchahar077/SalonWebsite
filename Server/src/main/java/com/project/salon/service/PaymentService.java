package com.project.salon.service;

import com.project.salon.dto.PaymentOrderResponse;
import com.project.salon.dto.PaymentVerificationRequest;
import com.project.salon.entity.Appointment;
import com.project.salon.entity.Payment;
import com.project.salon.entity.User;
import com.project.salon.entity.enums.AppointmentStatus;
import com.project.salon.entity.enums.PaymentStatus;
import com.project.salon.entity.enums.Role;
import com.project.salon.event.AppointmentConfirmedEvent;
import com.project.salon.event.PaymentSuccessfulEvent;
import com.project.salon.exception.ForbiddenException;
import com.project.salon.exception.InvalidAppointmentStateException;
import com.project.salon.exception.PaymentVerificationException;
import com.project.salon.exception.ResourceNotFoundException;
import com.project.salon.repository.AppointmentRepository;
import com.project.salon.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentService appointmentService;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${razorpay.key-id:rzp_test_dummykey}")
    private String razorpayKeyId;

    @Value("${razorpay.key-secret:dummyserversecret}")
    private String razorpayKeySecret;

    @Value("${razorpay.webhook-secret:dummywebhooksecret}")
    private String razorpayWebhookSecret;

    public PaymentService(PaymentRepository paymentRepository,
                          AppointmentRepository appointmentRepository,
                          AppointmentService appointmentService,
                          ApplicationEventPublisher eventPublisher) {
        this.paymentRepository = paymentRepository;
        this.appointmentRepository = appointmentRepository;
        this.appointmentService = appointmentService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public PaymentOrderResponse createOrder(Long appointmentId, User currentUser) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

        if (!currentUser.getRole().equals(Role.ADMIN) && !appointment.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You are not authorized to create payment for this appointment.");
        }

        if (appointment.getStatus() != AppointmentStatus.PENDING_PAYMENT) {
            throw new InvalidAppointmentStateException("Cannot create payment order. Appointment status is " + appointment.getStatus());
        }

        Optional<Payment> existingPaymentOpt = paymentRepository.findByAppointmentId(appointmentId);
        if (existingPaymentOpt.isPresent()) {
            Payment existingPayment = existingPaymentOpt.get();
            if (existingPayment.getStatus() == PaymentStatus.SUCCESS) {
                return mapToOrderResponse(existingPayment, razorpayKeyId);
            }
        }

        try {
            RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            long amountInPaise = appointment.getAmount().multiply(new BigDecimal(100)).longValue();

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", appointment.getAppointmentReference());

            Order razorpayOrder = client.orders.create(orderRequest);
            String orderId = razorpayOrder.get("id");

            Payment payment;
            if (existingPaymentOpt.isPresent()) {
                payment = existingPaymentOpt.get();
                payment.setRazorpayOrderId(orderId);
                payment.setAmount(appointment.getAmount());
                payment.setStatus(PaymentStatus.PENDING);
            } else {
                payment = Payment.builder()
                        .appointment(appointment)
                        .razorpayOrderId(orderId)
                        .amount(appointment.getAmount())
                        .status(PaymentStatus.PENDING)
                        .build();
            }

            payment = paymentRepository.save(payment);
            log.info("Razorpay Order created: OrderId={}, AppointmentRef={}", orderId, appointment.getAppointmentReference());

            return mapToOrderResponse(payment, razorpayKeyId);

        } catch (Exception ex) {
            log.error("Failed to create Razorpay Order: ", ex);
            throw new PaymentVerificationException("Failed to initiate payment with Razorpay: " + ex.getMessage());
        }
    }

    @Transactional
    public PaymentOrderResponse verifyPayment(PaymentVerificationRequest request, User currentUser) {
        Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found for Razorpay Order ID: " + request.getRazorpayOrderId()));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            log.info("Payment already verified successfully for OrderId={}", request.getRazorpayOrderId());
            return mapToOrderResponse(payment, razorpayKeyId);
        }

        Appointment appointment = payment.getAppointment();

        boolean isValidSignature;
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", request.getRazorpayOrderId());
            options.put("razorpay_payment_id", request.getRazorpayPaymentId());
            options.put("razorpay_signature", request.getRazorpaySignature());

            isValidSignature = Utils.verifyPaymentSignature(options, razorpayKeySecret);
        } catch (Exception ex) {
            log.error("Error during Razorpay signature verification: ", ex);
            isValidSignature = false;
        }

        if (!isValidSignature) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new PaymentVerificationException("Invalid Razorpay payment signature.");
        }

        payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
        payment.setRazorpaySignature(request.getRazorpaySignature());
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        AppointmentStatus oldStatus = appointment.getStatus();
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setPaymentStatus(PaymentStatus.SUCCESS);
        appointmentRepository.save(appointment);

        appointmentService.recordStatusHistory(appointment, oldStatus, AppointmentStatus.CONFIRMED,
                currentUser.getEmail(), "Payment verified successfully via Razorpay");

        log.info("Payment verified & Appointment confirmed: Ref={}, OrderId={}, PaymentId={}",
                appointment.getAppointmentReference(), request.getRazorpayOrderId(), request.getRazorpayPaymentId());

        eventPublisher.publishEvent(new PaymentSuccessfulEvent(this, payment));
        eventPublisher.publishEvent(new AppointmentConfirmedEvent(this, appointment));

        return mapToOrderResponse(payment, razorpayKeyId);
    }

    @Transactional
    public void handleWebhook(String payload, String signature) {
        try {
            boolean isValidWebhook = Utils.verifyWebhookSignature(payload, signature, razorpayWebhookSecret);
            if (!isValidWebhook) {
                log.warn("Invalid Razorpay Webhook signature received!");
                throw new PaymentVerificationException("Invalid webhook signature");
            }

            JSONObject json = new JSONObject(payload);
            String event = json.optString("event");
            log.info("Processing Razorpay Webhook event: {}", event);

            if ("payment.captured".equals(event)) {
                JSONObject payloadEntity = json.getJSONObject("payload").getJSONObject("payment").getJSONObject("entity");
                String orderId = payloadEntity.getString("order_id");
                String paymentId = payloadEntity.getString("id");

                Optional<Payment> paymentOpt = paymentRepository.findByRazorpayOrderId(orderId);
                if (paymentOpt.isPresent()) {
                    Payment payment = paymentOpt.get();
                    if (payment.getStatus() != PaymentStatus.SUCCESS) {
                        payment.setRazorpayPaymentId(paymentId);
                        payment.setStatus(PaymentStatus.SUCCESS);
                        paymentRepository.save(payment);

                        Appointment appointment = payment.getAppointment();
                        AppointmentStatus oldStatus = appointment.getStatus();
                        appointment.setStatus(AppointmentStatus.CONFIRMED);
                        appointment.setPaymentStatus(PaymentStatus.SUCCESS);
                        appointmentRepository.save(appointment);

                        appointmentService.recordStatusHistory(appointment, oldStatus, AppointmentStatus.CONFIRMED,
                                "RAZORPAY_WEBHOOK", "Payment captured via webhook");

                        eventPublisher.publishEvent(new PaymentSuccessfulEvent(this, payment));
                        eventPublisher.publishEvent(new AppointmentConfirmedEvent(this, appointment));
                        log.info("Webhook processed payment successfully for OrderId={}", orderId);
                    }
                }
            } else if ("payment.failed".equals(event)) {
                JSONObject payloadEntity = json.getJSONObject("payload").getJSONObject("payment").getJSONObject("entity");
                String orderId = payloadEntity.getString("order_id");

                Optional<Payment> paymentOpt = paymentRepository.findByRazorpayOrderId(orderId);
                if (paymentOpt.isPresent()) {
                    Payment payment = paymentOpt.get();
                    if (payment.getStatus() == PaymentStatus.PENDING) {
                        payment.setStatus(PaymentStatus.FAILED);
                        paymentRepository.save(payment);
                        log.info("Webhook marked payment as FAILED for OrderId={}", orderId);
                    }
                }
            }
        } catch (Exception ex) {
            log.error("Webhook processing error: ", ex);
            throw new PaymentVerificationException("Webhook processing error: " + ex.getMessage());
        }
    }

    private PaymentOrderResponse mapToOrderResponse(Payment payment, String keyId) {
        return PaymentOrderResponse.builder()
                .appointmentId(payment.getAppointment().getId())
                .appointmentReference(payment.getAppointment().getAppointmentReference())
                .razorpayOrderId(payment.getRazorpayOrderId())
                .keyId(keyId)
                .amount(payment.getAmount())
                .currency("INR")
                .status(payment.getStatus())
                .build();
    }
}
