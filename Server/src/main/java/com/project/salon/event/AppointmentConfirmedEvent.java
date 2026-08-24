package com.project.salon.event;

import com.project.salon.entity.Appointment;
import org.springframework.context.ApplicationEvent;

public class AppointmentConfirmedEvent extends ApplicationEvent {
    private final Appointment appointment;

    public AppointmentConfirmedEvent(Object source, Appointment appointment) {
        super(source);
        this.appointment = appointment;
    }

    public Appointment getAppointment() {
        return appointment;
    }
}
