package com.project.salon.dto;

import java.math.BigDecimal;
import java.util.List;

public class DashboardResponse {
    private long todaysAppointments;
    private long upcomingAppointments;
    private long completedAppointments;
    private long cancelledAppointments;
    private BigDecimal totalRevenue;
    private BigDecimal todaysRevenue;
    private long totalUsers;
    private long availableProvidersCount;
    private long totalProvidersCount;
    private List<AppointmentSummaryResponse> recentAppointments;

    public DashboardResponse() {
    }

    public DashboardResponse(long todaysAppointments, long upcomingAppointments, long completedAppointments, long cancelledAppointments, BigDecimal totalRevenue, BigDecimal todaysRevenue, long totalUsers, long availableProvidersCount, long totalProvidersCount, List<AppointmentSummaryResponse> recentAppointments) {
        this.todaysAppointments = todaysAppointments;
        this.upcomingAppointments = upcomingAppointments;
        this.completedAppointments = completedAppointments;
        this.cancelledAppointments = cancelledAppointments;
        this.totalRevenue = totalRevenue;
        this.todaysRevenue = todaysRevenue;
        this.totalUsers = totalUsers;
        this.availableProvidersCount = availableProvidersCount;
        this.totalProvidersCount = totalProvidersCount;
        this.recentAppointments = recentAppointments;
    }

    public static DashboardResponseBuilder builder() {
        return new DashboardResponseBuilder();
    }

    public long getTodaysAppointments() { return todaysAppointments; }
    public void setTodaysAppointments(long todaysAppointments) { this.todaysAppointments = todaysAppointments; }
    public long getUpcomingAppointments() { return upcomingAppointments; }
    public void setUpcomingAppointments(long upcomingAppointments) { this.upcomingAppointments = upcomingAppointments; }
    public long getCompletedAppointments() { return completedAppointments; }
    public void setCompletedAppointments(long completedAppointments) { this.completedAppointments = completedAppointments; }
    public long getCancelledAppointments() { return cancelledAppointments; }
    public void setCancelledAppointments(long cancelledAppointments) { this.cancelledAppointments = cancelledAppointments; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    public BigDecimal getTodaysRevenue() { return todaysRevenue; }
    public void setTodaysRevenue(BigDecimal todaysRevenue) { this.todaysRevenue = todaysRevenue; }
    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public long getAvailableProvidersCount() { return availableProvidersCount; }
    public void setAvailableProvidersCount(long availableProvidersCount) { this.availableProvidersCount = availableProvidersCount; }
    public long getTotalProvidersCount() { return totalProvidersCount; }
    public void setTotalProvidersCount(long totalProvidersCount) { this.totalProvidersCount = totalProvidersCount; }
    public List<AppointmentSummaryResponse> getRecentAppointments() { return recentAppointments; }
    public void setRecentAppointments(List<AppointmentSummaryResponse> recentAppointments) { this.recentAppointments = recentAppointments; }

    public static class DashboardResponseBuilder {
        private long todaysAppointments;
        private long upcomingAppointments;
        private long completedAppointments;
        private long cancelledAppointments;
        private BigDecimal totalRevenue;
        private BigDecimal todaysRevenue;
        private long totalUsers;
        private long availableProvidersCount;
        private long totalProvidersCount;
        private List<AppointmentSummaryResponse> recentAppointments;

        public DashboardResponseBuilder todaysAppointments(long todaysAppointments) { this.todaysAppointments = todaysAppointments; return this; }
        public DashboardResponseBuilder upcomingAppointments(long upcomingAppointments) { this.upcomingAppointments = upcomingAppointments; return this; }
        public DashboardResponseBuilder completedAppointments(long completedAppointments) { this.completedAppointments = completedAppointments; return this; }
        public DashboardResponseBuilder cancelledAppointments(long cancelledAppointments) { this.cancelledAppointments = cancelledAppointments; return this; }
        public DashboardResponseBuilder totalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; return this; }
        public DashboardResponseBuilder todaysRevenue(BigDecimal todaysRevenue) { this.todaysRevenue = todaysRevenue; return this; }
        public DashboardResponseBuilder totalUsers(long totalUsers) { this.totalUsers = totalUsers; return this; }
        public DashboardResponseBuilder availableProvidersCount(long availableProvidersCount) { this.availableProvidersCount = availableProvidersCount; return this; }
        public DashboardResponseBuilder totalProvidersCount(long totalProvidersCount) { this.totalProvidersCount = totalProvidersCount; return this; }
        public DashboardResponseBuilder recentAppointments(List<AppointmentSummaryResponse> recentAppointments) { this.recentAppointments = recentAppointments; return this; }

        public DashboardResponse build() {
            return new DashboardResponse(todaysAppointments, upcomingAppointments, completedAppointments, cancelledAppointments, totalRevenue, todaysRevenue, totalUsers, availableProvidersCount, totalProvidersCount, recentAppointments);
        }
    }
}
