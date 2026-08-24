package com.project.salon.dto;

import java.time.LocalTime;
import java.util.List;

public class AvailableSlotResponse {
    private LocalTime startTime;
    private LocalTime endTime;
    private int availableProvidersCount;
    private boolean available;
    private List<ProviderResponse> availableProviders;

    public AvailableSlotResponse() {
    }

    public AvailableSlotResponse(LocalTime startTime, LocalTime endTime, int availableProvidersCount, boolean available, List<ProviderResponse> availableProviders) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.availableProvidersCount = availableProvidersCount;
        this.available = available;
        this.availableProviders = availableProviders;
    }

    public static AvailableSlotResponseBuilder builder() {
        return new AvailableSlotResponseBuilder();
    }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public int getAvailableProvidersCount() { return availableProvidersCount; }
    public void setAvailableProvidersCount(int availableProvidersCount) { this.availableProvidersCount = availableProvidersCount; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public List<ProviderResponse> getAvailableProviders() { return availableProviders; }
    public void setAvailableProviders(List<ProviderResponse> availableProviders) { this.availableProviders = availableProviders; }

    public static class AvailableSlotResponseBuilder {
        private LocalTime startTime;
        private LocalTime endTime;
        private int availableProvidersCount;
        private boolean available;
        private List<ProviderResponse> availableProviders;

        public AvailableSlotResponseBuilder startTime(LocalTime startTime) { this.startTime = startTime; return this; }
        public AvailableSlotResponseBuilder endTime(LocalTime endTime) { this.endTime = endTime; return this; }
        public AvailableSlotResponseBuilder availableProvidersCount(int availableProvidersCount) { this.availableProvidersCount = availableProvidersCount; return this; }
        public AvailableSlotResponseBuilder available(boolean available) { this.available = available; return this; }
        public AvailableSlotResponseBuilder availableProviders(List<ProviderResponse> availableProviders) { this.availableProviders = availableProviders; return this; }

        public AvailableSlotResponse build() {
            return new AvailableSlotResponse(startTime, endTime, availableProvidersCount, available, availableProviders);
        }
    }
}
