package com.project.salon.dto;

import com.project.salon.entity.enums.ProviderStatus;

import java.time.LocalDateTime;

public class ProviderResponse {
    private Long id;
    private String name;
    private String specialization;
    private String phone;
    private String email;
    private ProviderStatus status;
    private LocalDateTime createdAt;

    public ProviderResponse() {
    }

    public ProviderResponse(Long id, String name, String specialization, String phone, String email, ProviderStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.phone = phone;
        this.email = email;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static ProviderResponseBuilder builder() {
        return new ProviderResponseBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public ProviderStatus getStatus() { return status; }
    public void setStatus(ProviderStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class ProviderResponseBuilder {
        private Long id;
        private String name;
        private String specialization;
        private String phone;
        private String email;
        private ProviderStatus status;
        private LocalDateTime createdAt;

        public ProviderResponseBuilder id(Long id) { this.id = id; return this; }
        public ProviderResponseBuilder name(String name) { this.name = name; return this; }
        public ProviderResponseBuilder specialization(String specialization) { this.specialization = specialization; return this; }
        public ProviderResponseBuilder phone(String phone) { this.phone = phone; return this; }
        public ProviderResponseBuilder email(String email) { this.email = email; return this; }
        public ProviderResponseBuilder status(ProviderStatus status) { this.status = status; return this; }
        public ProviderResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public ProviderResponse build() {
            return new ProviderResponse(id, name, specialization, phone, email, status, createdAt);
        }
    }
}
