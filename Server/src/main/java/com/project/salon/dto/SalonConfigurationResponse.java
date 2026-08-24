package com.project.salon.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class SalonConfigurationResponse {
    private Long id;
    private int providerCount;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private int slotDuration;
    private LocalDateTime updatedAt;

    public SalonConfigurationResponse() {
    }

    public SalonConfigurationResponse(Long id, int providerCount, LocalTime openingTime, LocalTime closingTime, int slotDuration, LocalDateTime updatedAt) {
        this.id = id;
        this.providerCount = providerCount;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.slotDuration = slotDuration;
        this.updatedAt = updatedAt;
    }

    public static SalonConfigurationResponseBuilder builder() {
        return new SalonConfigurationResponseBuilder();
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
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static class SalonConfigurationResponseBuilder {
        private Long id;
        private int providerCount;
        private LocalTime openingTime;
        private LocalTime closingTime;
        private int slotDuration;
        private LocalDateTime updatedAt;

        public SalonConfigurationResponseBuilder id(Long id) { this.id = id; return this; }
        public SalonConfigurationResponseBuilder providerCount(int providerCount) { this.providerCount = providerCount; return this; }
        public SalonConfigurationResponseBuilder openingTime(LocalTime openingTime) { this.openingTime = openingTime; return this; }
        public SalonConfigurationResponseBuilder closingTime(LocalTime closingTime) { this.closingTime = closingTime; return this; }
        public SalonConfigurationResponseBuilder slotDuration(int slotDuration) { this.slotDuration = slotDuration; return this; }
        public SalonConfigurationResponseBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public SalonConfigurationResponse build() {
            return new SalonConfigurationResponse(id, providerCount, openingTime, closingTime, slotDuration, updatedAt);
        }
    }
}
