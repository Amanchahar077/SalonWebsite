package com.project.salon.dto;

import com.project.salon.entity.enums.NotificationStatus;

import java.time.LocalDateTime;

public class NotificationResponse {
    private Long id;
    private Long userId;
    private Long appointmentId;
    private String type;
    private String recipient;
    private NotificationStatus status;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;

    public NotificationResponse() {
    }

    public NotificationResponse(Long id, Long userId, Long appointmentId, String type, String recipient, NotificationStatus status, LocalDateTime sentAt, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.appointmentId = appointmentId;
        this.type = type;
        this.recipient = recipient;
        this.status = status;
        this.sentAt = sentAt;
        this.createdAt = createdAt;
    }

    public static NotificationResponseBuilder builder() {
        return new NotificationResponseBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public NotificationStatus getStatus() { return status; }
    public void setStatus(NotificationStatus status) { this.status = status; }
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class NotificationResponseBuilder {
        private Long id;
        private Long userId;
        private Long appointmentId;
        private String type;
        private String recipient;
        private NotificationStatus status;
        private LocalDateTime sentAt;
        private LocalDateTime createdAt;

        public NotificationResponseBuilder id(Long id) { this.id = id; return this; }
        public NotificationResponseBuilder userId(Long userId) { this.userId = userId; return this; }
        public NotificationResponseBuilder appointmentId(Long appointmentId) { this.appointmentId = appointmentId; return this; }
        public NotificationResponseBuilder type(String type) { this.type = type; return this; }
        public NotificationResponseBuilder recipient(String recipient) { this.recipient = recipient; return this; }
        public NotificationResponseBuilder status(NotificationStatus status) { this.status = status; return this; }
        public NotificationResponseBuilder sentAt(LocalDateTime sentAt) { this.sentAt = sentAt; return this; }
        public NotificationResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public NotificationResponse build() {
            return new NotificationResponse(id, userId, appointmentId, type, recipient, status, sentAt, createdAt);
        }
    }
}
