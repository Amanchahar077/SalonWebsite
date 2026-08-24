package com.project.salon.event;

import com.project.salon.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EmailEventListener {

    private static final Logger log = LoggerFactory.getLogger(EmailEventListener.class);

    private final EmailService emailService;

    public EmailEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async
    @EventListener
    public void handleAppointmentConfirmed(AppointmentConfirmedEvent event) {
        log.info("EventListener received AppointmentConfirmedEvent for Ref={}", event.getAppointment().getAppointmentReference());
        emailService.sendAppointmentConfirmationEmail(event.getAppointment());
    }

    @Async
    @EventListener
    public void handleAppointmentCancelled(AppointmentCancelledEvent event) {
        log.info("EventListener received AppointmentCancelledEvent for Ref={}", event.getAppointment().getAppointmentReference());
        emailService.sendAppointmentCancellationEmail(event.getAppointment(), event.getCancelledBy(), event.getReason());
    }
}
