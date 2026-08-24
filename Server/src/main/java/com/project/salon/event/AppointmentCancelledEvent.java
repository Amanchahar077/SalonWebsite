package com.project.salon.event;

import com.project.salon.entity.Appointment;
import org.springframework.context.ApplicationEvent;

public class AppointmentCancelledEvent extends ApplicationEvent {
    private final Appointment appointment;
    private final String cancelledBy;
    private final String reason;

    public AppointmentCancelledEvent(Object source, Appointment appointment, String cancelledBy, String reason) {
        super(source);
        this.appointment = appointment;
        this.cancelledBy = cancelledBy;
        this.reason = reason;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public String getCancelledBy() {
        return cancelledBy;
    }

    public String getReason() {
        return reason;
    }
}
