package com.project.salon.dto;

import com.project.salon.entity.enums.AppointmentStatus;

import java.time.LocalDateTime;

public class AppointmentHistoryResponse {
    private Long id;
    private Long appointmentId;
    private AppointmentStatus oldStatus;
    private AppointmentStatus newStatus;
    private String changedBy;
    private String reason;
    private LocalDateTime createdAt;

    public AppointmentHistoryResponse() {
    }

    public AppointmentHistoryResponse(Long id, Long appointmentId, AppointmentStatus oldStatus, AppointmentStatus newStatus, String changedBy, String reason, LocalDateTime createdAt) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.reason = reason;
        this.createdAt = createdAt;
    }

    public static AppointmentHistoryResponseBuilder builder() {
        return new AppointmentHistoryResponseBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }
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

    public static class AppointmentHistoryResponseBuilder {
        private Long id;
        private Long appointmentId;
        private AppointmentStatus oldStatus;
        private AppointmentStatus newStatus;
        private String changedBy;
        private String reason;
        private LocalDateTime createdAt;

        public AppointmentHistoryResponseBuilder id(Long id) { this.id = id; return this; }
        public AppointmentHistoryResponseBuilder appointmentId(Long appointmentId) { this.appointmentId = appointmentId; return this; }
        public AppointmentHistoryResponseBuilder oldStatus(AppointmentStatus oldStatus) { this.oldStatus = oldStatus; return this; }
        public AppointmentHistoryResponseBuilder newStatus(AppointmentStatus newStatus) { this.newStatus = newStatus; return this; }
        public AppointmentHistoryResponseBuilder changedBy(String changedBy) { this.changedBy = changedBy; return this; }
        public AppointmentHistoryResponseBuilder reason(String reason) { this.reason = reason; return this; }
        public AppointmentHistoryResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public AppointmentHistoryResponse build() {
            return new AppointmentHistoryResponse(id, appointmentId, oldStatus, newStatus, changedBy, reason, createdAt);
        }
    }
}
