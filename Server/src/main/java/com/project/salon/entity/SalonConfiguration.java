package com.project.salon.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "salon_configuration")
public class SalonConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider_count", nullable = false)
    private int providerCount;

    @Column(name = "opening_time", nullable = false)
    private LocalTime openingTime;

    @Column(name = "closing_time", nullable = false)
    private LocalTime closingTime;

    @Column(name = "slot_duration", nullable = false)
    private int slotDuration;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public SalonConfiguration() {
    }

    public SalonConfiguration(Long id, int providerCount, LocalTime openingTime, LocalTime closingTime, int slotDuration, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.providerCount = providerCount;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.slotDuration = slotDuration;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static SalonConfigurationBuilder builder() {
        return new SalonConfigurationBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getProviderCount() { return providerCount; }
    public void setProviderCount(int providerCount) { this.providerCount = providerCount; }
    public LocalTime getOpeningTime() { return openingTime; }
    public void setOpeningTime(LocalTime openingTime) { this.openingTime = openingTime; }
    public LocalTime getClosingTime() { return closingTime; }
    public void setClosingTime(LocalTime closingTime) { this.closingTime = closingTime; }
    public int getSlotDuration() { return slotDuration; }
    public void setSlotDuration(int slotDuration) { this.slotDuration = slotDuration; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static class SalonConfigurationBuilder {
        private Long id;
        private int providerCount;
        private LocalTime openingTime;
        private LocalTime closingTime;
        private int slotDuration;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public SalonConfigurationBuilder id(Long id) { this.id = id; return this; }
        public SalonConfigurationBuilder providerCount(int providerCount) { this.providerCount = providerCount; return this; }
        public SalonConfigurationBuilder openingTime(LocalTime openingTime) { this.openingTime = openingTime; return this; }
        public SalonConfigurationBuilder closingTime(LocalTime closingTime) { this.closingTime = closingTime; return this; }
        public SalonConfigurationBuilder slotDuration(int slotDuration) { this.slotDuration = slotDuration; return this; }
        public SalonConfigurationBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public SalonConfigurationBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public SalonConfiguration build() {
            return new SalonConfiguration(id, providerCount, openingTime, closingTime, slotDuration, createdAt, updatedAt);
        }
    }
}
