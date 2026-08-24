package com.project.salon.entity;

import com.project.salon.entity.enums.AppointmentStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointment_status_history")
public class AppointmentStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status", nullable = false, length = 50)
    private AppointmentStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 50)
    private AppointmentStatus newStatus;

    @Column(name = "changed_by", nullable = false)
    private String changedBy;

    @Column(length = 512)
    private String reason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public AppointmentStatusHistory() {
    }

    public AppointmentStatusHistory(Long id, Appointment appointment, AppointmentStatus oldStatus, AppointmentStatus newStatus, String changedBy, String reason, LocalDateTime createdAt) {
        this.id = id;
        this.appointment = appointment;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.reason = reason;
        this.createdAt = createdAt;
    }

    public static AppointmentStatusHistoryBuilder builder() {
        return new AppointmentStatusHistoryBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Appointment getAppointment() { return appointment; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }
    public AppointmentStatus getOldStatus() { return oldStatus; }
    public void setOldStatus(AppointmentStatus oldStatus) { this.oldStatus = oldStatus; }
    public AppointmentStatus getNewStatus() { return newStatus; }
    public void setNewStatus(AppointmentStatus newStatus) { this.newStatus = newStatus; }
    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class AppointmentStatusHistoryBuilder {
        private Long id;
        private Appointment appointment;
        private AppointmentStatus oldStatus;
        private AppointmentStatus newStatus;
        private String changedBy;
        private String reason;
        private LocalDateTime createdAt;

        public AppointmentStatusHistoryBuilder id(Long id) { this.id = id; return this; }
        public AppointmentStatusHistoryBuilder appointment(Appointment appointment) { this.appointment = appointment; return this; }
        public AppointmentStatusHistoryBuilder oldStatus(AppointmentStatus oldStatus) { this.oldStatus = oldStatus; return this; }
        public AppointmentStatusHistoryBuilder newStatus(AppointmentStatus newStatus) { this.newStatus = newStatus; return this; }
        public AppointmentStatusHistoryBuilder changedBy(String changedBy) { this.changedBy = changedBy; return this; }
        public AppointmentStatusHistoryBuilder reason(String reason) { this.reason = reason; return this; }
        public AppointmentStatusHistoryBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public AppointmentStatusHistory build() {
            return new AppointmentStatusHistory(id, appointment, oldStatus, newStatus, changedBy, reason, createdAt);
        }
    }
}
