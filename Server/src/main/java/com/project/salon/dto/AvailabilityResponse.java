package com.project.salon.dto;

import java.time.LocalDate;
import java.util.List;

public class AvailabilityResponse {
    private LocalDate date;
    private int totalProvidersCount;
    private List<AvailableSlotResponse> slots;

    public AvailabilityResponse() {
    }

    public AvailabilityResponse(LocalDate date, int totalProvidersCount, List<AvailableSlotResponse> slots) {
        this.date = date;
        this.totalProvidersCount = totalProvidersCount;
        this.slots = slots;
    }

    public static AvailabilityResponseBuilder builder() {
        return new AvailabilityResponseBuilder();
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public int getTotalProvidersCount() { return totalProvidersCount; }
    public void setTotalProvidersCount(int totalProvidersCount) { this.totalProvidersCount = totalProvidersCount; }
    public List<AvailableSlotResponse> getSlots() { return slots; }
    public void setSlots(List<AvailableSlotResponse> slots) { this.slots = slots; }

    public static class AvailabilityResponseBuilder {
        private LocalDate date;
        private int totalProvidersCount;
        private List<AvailableSlotResponse> slots;

        public AvailabilityResponseBuilder date(LocalDate date) { this.date = date; return this; }
        public AvailabilityResponseBuilder totalProvidersCount(int totalProvidersCount) { this.totalProvidersCount = totalProvidersCount; return this; }
        public AvailabilityResponseBuilder slots(List<AvailableSlotResponse> slots) { this.slots = slots; return this; }

        public AvailabilityResponse build() {
            return new AvailabilityResponse(date, totalProvidersCount, slots);
        }
    }
}
