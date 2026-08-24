package com.project.salon.service;

import com.project.salon.dto.PaymentOrderResponse;
import com.project.salon.dto.PaymentVerificationRequest;
import com.project.salon.entity.Appointment;
import com.project.salon.entity.Payment;
import com.project.salon.entity.User;
import com.project.salon.entity.enums.AppointmentStatus;
import com.project.salon.entity.enums.PaymentStatus;
import com.project.salon.entity.enums.Role;
import com.project.salon.exception.PaymentVerificationException;
import com.project.salon.repository.AppointmentRepository;
import com.project.salon.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private AppointmentService appointmentService;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PaymentService paymentService;

    private User testUser;
    private Appointment testAppointment;

    @BeforeEach
    void setUp() throws Exception {
        var fieldSecret = PaymentService.class.getDeclaredField("razorpayKeySecret");
        fieldSecret.setAccessible(true);
        fieldSecret.set(paymentService, "dummyserversecret");

        var fieldKey = PaymentService.class.getDeclaredField("razorpayKeyId");
        fieldKey.setAccessible(true);
        fieldKey.set(paymentService, "rzp_test_dummykey");

        testUser = User.builder().id(1L).email("user@example.com").role(Role.USER).build();
        testAppointment = Appointment.builder()
                .id(50L)
                .user(testUser)
                .amount(new BigDecimal("500.00"))
                .appointmentReference("SALON-REF-1001")
                .status(AppointmentStatus.PENDING_PAYMENT)
                .paymentStatus(PaymentStatus.PENDING)
                .build();
    }

    @Test
    void testVerifyPayment_Idempotency() {
        Payment alreadySuccessPayment = Payment.builder()
                .id(1L)
                .appointment(testAppointment)
                .razorpayOrderId("order_12345")
                .razorpayPaymentId("pay_67890")
                .status(PaymentStatus.SUCCESS)
                .amount(new BigDecimal("500.00"))
                .build();

        when(paymentRepository.findByRazorpayOrderId("order_12345")).thenReturn(Optional.of(alreadySuccessPayment));

        PaymentVerificationRequest request = PaymentVerificationRequest.builder()
                .razorpayOrderId("order_12345")
                .razorpayPaymentId("pay_67890")
                .razorpaySignature("dummy_sig")
                .build();

        PaymentOrderResponse response = paymentService.verifyPayment(request, testUser);

        assertNotNull(response);
        assertEquals(PaymentStatus.SUCCESS, response.getStatus());
        // Ensure double processing did not trigger event again
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void testVerifyPayment_InvalidSignature_ThrowsException() {
        Payment pendingPayment = Payment.builder()
                .id(1L)
                .appointment(testAppointment)
                .razorpayOrderId("order_12345")
                .status(PaymentStatus.PENDING)
                .amount(new BigDecimal("500.00"))
                .build();

        when(paymentRepository.findByRazorpayOrderId("order_12345")).thenReturn(Optional.of(pendingPayment));

        PaymentVerificationRequest request = PaymentVerificationRequest.builder()
                .razorpayOrderId("order_12345")
                .razorpayPaymentId("pay_invalid")
                .razorpaySignature("invalid_signature")
                .build();

        assertThrows(PaymentVerificationException.class, () -> paymentService.verifyPayment(request, testUser));
    }
}
