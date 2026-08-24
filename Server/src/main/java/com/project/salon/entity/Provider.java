package com.project.salon.entity;

import com.project.salon.entity.enums.ProviderStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "providers")
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String specialization;

    @Column(length = 50)
    private String phone;

    @Column
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ProviderStatus status = ProviderStatus.AVAILABLE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Provider() {
    }

    public Provider(Long id, String name, String specialization, String phone, String email, ProviderStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.phone = phone;
        this.email = email;
        this.status = status != null ? status : ProviderStatus.AVAILABLE;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ProviderBuilder builder() {
        return new ProviderBuilder();
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
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static class ProviderBuilder {
        private Long id;
        private String name;
        private String specialization;
        private String phone;
        private String email;
        private ProviderStatus status = ProviderStatus.AVAILABLE;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public ProviderBuilder id(Long id) { this.id = id; return this; }
        public ProviderBuilder name(String name) { this.name = name; return this; }
        public ProviderBuilder specialization(String specialization) { this.specialization = specialization; return this; }
        public ProviderBuilder phone(String phone) { this.phone = phone; return this; }
        public ProviderBuilder email(String email) { this.email = email; return this; }
        public ProviderBuilder status(ProviderStatus status) { this.status = status; return this; }
        public ProviderBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ProviderBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Provider build() {
            return new Provider(id, name, specialization, phone, email, status, createdAt, updatedAt);
        }
    }
}
