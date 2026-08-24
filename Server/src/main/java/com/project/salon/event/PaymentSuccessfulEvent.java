package com.project.salon.event;

import com.project.salon.entity.Payment;
import org.springframework.context.ApplicationEvent;

public class PaymentSuccessfulEvent extends ApplicationEvent {
    private final Payment payment;

    public PaymentSuccessfulEvent(Object source, Payment payment) {
        super(source);
        this.payment = payment;
    }

    public Payment getPayment() {
        return payment;
    }
}
