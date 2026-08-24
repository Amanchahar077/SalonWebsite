package com.project.salon.dto;

import com.project.salon.entity.enums.ProviderStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ProviderRequest {

    @NotBlank(message = "Provider name is required")
    private String name;

    private String specialization;
    private String phone;

    @Email(message = "Invalid email format")
    private String email;

    private ProviderStatus status;

    public ProviderRequest() {
    }

    public ProviderRequest(String name, String specialization, String phone, String email, ProviderStatus status) {
        this.name = name;
        this.specialization = specialization;
        this.phone = phone;
        this.email = email;
        this.status = status;
    }

    public static ProviderRequestBuilder builder() {
        return new ProviderRequestBuilder();
    }

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

    public static class ProviderRequestBuilder {
        private String name;
        private String specialization;
        private String phone;
        private String email;
        private ProviderStatus status;

        public ProviderRequestBuilder name(String name) { this.name = name; return this; }
        public ProviderRequestBuilder specialization(String specialization) { this.specialization = specialization; return this; }
        public ProviderRequestBuilder phone(String phone) { this.phone = phone; return this; }
        public ProviderRequestBuilder email(String email) { this.email = email; return this; }
        public ProviderRequestBuilder status(ProviderStatus status) { this.status = status; return this; }

        public ProviderRequest build() {
            return new ProviderRequest(name, specialization, phone, email, status);
        }
    }
}
