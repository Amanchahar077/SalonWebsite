package com.project.salon.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public class SalonConfigurationRequest {

    @Min(value = 1, message = "Provider count must be at least 1")
    private int providerCount;

    @NotNull(message = "Opening time is required")
    private LocalTime openingTime;

    @NotNull(message = "Closing time is required")
    private LocalTime closingTime;

    @Min(value = 10, message = "Slot duration must be at least 10 minutes")
    private int slotDuration;

    public SalonConfigurationRequest() {
    }

    public SalonConfigurationRequest(int providerCount, LocalTime openingTime, LocalTime closingTime, int slotDuration) {
        this.providerCount = providerCount;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.slotDuration = slotDuration;
    }

    public static SalonConfigurationRequestBuilder builder() {
        return new SalonConfigurationRequestBuilder();
    }

    public int getProviderCount() { return providerCount; }
    public void setProviderCount(int providerCount) { this.providerCount = providerCount; }
    public LocalTime getOpeningTime() { return openingTime; }
    public void setOpeningTime(LocalTime openingTime) { this.openingTime = openingTime; }
    public LocalTime getClosingTime() { return closingTime; }
    public void setClosingTime(LocalTime closingTime) { this.closingTime = closingTime; }
    public int getSlotDuration() { return slotDuration; }
    public void setSlotDuration(int slotDuration) { this.slotDuration = slotDuration; }

    public static class SalonConfigurationRequestBuilder {
        private int providerCount;
        private LocalTime openingTime;
        private LocalTime closingTime;
        private int slotDuration;

        public SalonConfigurationRequestBuilder providerCount(int providerCount) { this.providerCount = providerCount; return this; }
        public SalonConfigurationRequestBuilder openingTime(LocalTime openingTime) { this.openingTime = openingTime; return this; }
        public SalonConfigurationRequestBuilder closingTime(LocalTime closingTime) { this.closingTime = closingTime; return this; }
        public SalonConfigurationRequestBuilder slotDuration(int slotDuration) { this.slotDuration = slotDuration; return this; }

        public SalonConfigurationRequest build() {
            return new SalonConfigurationRequest(providerCount, openingTime, closingTime, slotDuration);
        }
    }
}
