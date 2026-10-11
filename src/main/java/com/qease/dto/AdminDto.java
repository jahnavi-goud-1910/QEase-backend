package com.qease.dto;

import java.util.List;

public class AdminDto {

    public static class AdminDashboardSummaryDto {
        private long totalStudents;
        private long totalStaff;
        private long activeServices;
        private long activeCounters;
        private long waitingTokens;
        private long currentlyServing;
        private long completedTokens;
        private long cancelledTokens;

        public AdminDashboardSummaryDto() {}

        public AdminDashboardSummaryDto(long totalStudents, long totalStaff, long activeServices, long activeCounters, long waitingTokens, long currentlyServing, long completedTokens, long cancelledTokens) {
            this.totalStudents = totalStudents;
            this.totalStaff = totalStaff;
            this.activeServices = activeServices;
            this.activeCounters = activeCounters;
            this.waitingTokens = waitingTokens;
            this.currentlyServing = currentlyServing;
            this.completedTokens = completedTokens;
            this.cancelledTokens = cancelledTokens;
        }

        public long getTotalStudents() { return totalStudents; }
        public void setTotalStudents(long totalStudents) { this.totalStudents = totalStudents; }

        public long getTotalStaff() { return totalStaff; }
        public void setTotalStaff(long totalStaff) { this.totalStaff = totalStaff; }

        public long getActiveServices() { return activeServices; }
        public void setActiveServices(long activeServices) { this.activeServices = activeServices; }

        public long getActiveCounters() { return activeCounters; }
        public void setActiveCounters(long activeCounters) { this.activeCounters = activeCounters; }

        public long getWaitingTokens() { return waitingTokens; }
        public void setWaitingTokens(long waitingTokens) { this.waitingTokens = waitingTokens; }

        public long getCurrentlyServing() { return currentlyServing; }
        public void setCurrentlyServing(long currentlyServing) { this.currentlyServing = currentlyServing; }

        public long getCompletedTokens() { return completedTokens; }
        public void setCompletedTokens(long completedTokens) { this.completedTokens = completedTokens; }

        public long getCancelledTokens() { return cancelledTokens; }
        public void setCancelledTokens(long cancelledTokens) { this.cancelledTokens = cancelledTokens; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private long totalStudents;
            private long totalStaff;
            private long activeServices;
            private long activeCounters;
            private long waitingTokens;
            private long currentlyServing;
            private long completedTokens;
            private long cancelledTokens;

            public Builder totalStudents(long totalStudents) { this.totalStudents = totalStudents; return this; }
            public Builder totalStaff(long totalStaff) { this.totalStaff = totalStaff; return this; }
            public Builder activeServices(long activeServices) { this.activeServices = activeServices; return this; }
            public Builder activeCounters(long activeCounters) { this.activeCounters = activeCounters; return this; }
            public Builder waitingTokens(long waitingTokens) { this.waitingTokens = waitingTokens; return this; }
            public Builder currentlyServing(long currentlyServing) { this.currentlyServing = currentlyServing; return this; }
            public Builder completedTokens(long completedTokens) { this.completedTokens = completedTokens; return this; }
            public Builder cancelledTokens(long cancelledTokens) { this.cancelledTokens = cancelledTokens; return this; }

            public AdminDashboardSummaryDto build() {
                return new AdminDashboardSummaryDto(totalStudents, totalStaff, activeServices, activeCounters, waitingTokens, currentlyServing, completedTokens, cancelledTokens);
            }
        }
    }

    public static class AnalyticsDto {
        private List<ServiceStat> serviceStats;
        private List<DailyVolume> dailyVolumes;
        private List<StatusBreakdown> statusBreakdowns;
        private List<CounterUtilization> counterUtilizations;
        private List<PeakHourStat> peakHours;
        private double averageWaitTimeMinutes;
        private double averageServiceTimeMinutes;

        public AnalyticsDto() {}

        public AnalyticsDto(List<ServiceStat> serviceStats, List<DailyVolume> dailyVolumes, List<StatusBreakdown> statusBreakdowns, List<CounterUtilization> counterUtilizations, List<PeakHourStat> peakHours, double averageWaitTimeMinutes, double averageServiceTimeMinutes) {
            this.serviceStats = serviceStats;
            this.dailyVolumes = dailyVolumes;
            this.statusBreakdowns = statusBreakdowns;
            this.counterUtilizations = counterUtilizations;
            this.peakHours = peakHours;
            this.averageWaitTimeMinutes = averageWaitTimeMinutes;
            this.averageServiceTimeMinutes = averageServiceTimeMinutes;
        }

        public List<ServiceStat> getServiceStats() { return serviceStats; }
        public void setServiceStats(List<ServiceStat> serviceStats) { this.serviceStats = serviceStats; }

        public List<DailyVolume> getDailyVolumes() { return dailyVolumes; }
        public void setDailyVolumes(List<DailyVolume> dailyVolumes) { this.dailyVolumes = dailyVolumes; }

        public List<StatusBreakdown> getStatusBreakdowns() { return statusBreakdowns; }
        public void setStatusBreakdowns(List<StatusBreakdown> statusBreakdowns) { this.statusBreakdowns = statusBreakdowns; }

        public List<CounterUtilization> getCounterUtilizations() { return counterUtilizations; }
        public void setCounterUtilizations(List<CounterUtilization> counterUtilizations) { this.counterUtilizations = counterUtilizations; }

        public List<PeakHourStat> getPeakHours() { return peakHours; }
        public void setPeakHours(List<PeakHourStat> peakHours) { this.peakHours = peakHours; }

        public double getAverageWaitTimeMinutes() { return averageWaitTimeMinutes; }
        public void setAverageWaitTimeMinutes(double averageWaitTimeMinutes) { this.averageWaitTimeMinutes = averageWaitTimeMinutes; }

        public double getAverageServiceTimeMinutes() { return averageServiceTimeMinutes; }
        public void setAverageServiceTimeMinutes(double averageServiceTimeMinutes) { this.averageServiceTimeMinutes = averageServiceTimeMinutes; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private List<ServiceStat> serviceStats;
            private List<DailyVolume> dailyVolumes;
            private List<StatusBreakdown> statusBreakdowns;
            private List<CounterUtilization> counterUtilizations;
            private List<PeakHourStat> peakHours;
            private double averageWaitTimeMinutes;
            private double averageServiceTimeMinutes;

            public Builder serviceStats(List<ServiceStat> serviceStats) { this.serviceStats = serviceStats; return this; }
            public Builder dailyVolumes(List<DailyVolume> dailyVolumes) { this.dailyVolumes = dailyVolumes; return this; }
            public Builder statusBreakdowns(List<StatusBreakdown> statusBreakdowns) { this.statusBreakdowns = statusBreakdowns; return this; }
            public Builder counterUtilizations(List<CounterUtilization> counterUtilizations) { this.counterUtilizations = counterUtilizations; return this; }
            public Builder peakHours(List<PeakHourStat> peakHours) { this.peakHours = peakHours; return this; }
            public Builder averageWaitTimeMinutes(double averageWaitTimeMinutes) { this.averageWaitTimeMinutes = averageWaitTimeMinutes; return this; }
            public Builder averageServiceTimeMinutes(double averageServiceTimeMinutes) { this.averageServiceTimeMinutes = averageServiceTimeMinutes; return this; }

            public AnalyticsDto build() {
                return new AnalyticsDto(serviceStats, dailyVolumes, statusBreakdowns, counterUtilizations, peakHours, averageWaitTimeMinutes, averageServiceTimeMinutes);
            }
        }

        public static class ServiceStat {
            private String serviceName;
            private String serviceCode;
            private long tokenCount;
            private long completedCount;
            private long cancelledCount;

            public ServiceStat() {}
            public ServiceStat(String serviceName, String serviceCode, long tokenCount, long completedCount, long cancelledCount) {
                this.serviceName = serviceName;
                this.serviceCode = serviceCode;
                this.tokenCount = tokenCount;
                this.completedCount = completedCount;
                this.cancelledCount = cancelledCount;
            }

            public String getServiceName() { return serviceName; }
            public void setServiceName(String serviceName) { this.serviceName = serviceName; }
            public String getServiceCode() { return serviceCode; }
            public void setServiceCode(String serviceCode) { this.serviceCode = serviceCode; }
            public long getTokenCount() { return tokenCount; }
            public void setTokenCount(long tokenCount) { this.tokenCount = tokenCount; }
            public long getCompletedCount() { return completedCount; }
            public void setCompletedCount(long completedCount) { this.completedCount = completedCount; }
            public long getCancelledCount() { return cancelledCount; }
            public void setCancelledCount(long cancelledCount) { this.cancelledCount = cancelledCount; }

            public static Builder builder() { return new Builder(); }
            public static class Builder {
                private String serviceName;
                private String serviceCode;
                private long tokenCount;
                private long completedCount;
                private long cancelledCount;

                public Builder serviceName(String serviceName) { this.serviceName = serviceName; return this; }
                public Builder serviceCode(String serviceCode) { this.serviceCode = serviceCode; return this; }
                public Builder tokenCount(long tokenCount) { this.tokenCount = tokenCount; return this; }
                public Builder completedCount(long completedCount) { this.completedCount = completedCount; return this; }
                public Builder cancelledCount(long cancelledCount) { this.cancelledCount = cancelledCount; return this; }

                public ServiceStat build() {
                    return new ServiceStat(serviceName, serviceCode, tokenCount, completedCount, cancelledCount);
                }
            }
        }

        public static class DailyVolume {
            private String date;
            private long totalTokens;
            private long completedTokens;

            public DailyVolume() {}
            public DailyVolume(String date, long totalTokens, long completedTokens) {
                this.date = date;
                this.totalTokens = totalTokens;
                this.completedTokens = completedTokens;
            }

            public String getDate() { return date; }
            public void setDate(String date) { this.date = date; }
            public long getTotalTokens() { return totalTokens; }
            public void setTotalTokens(long totalTokens) { this.totalTokens = totalTokens; }
            public long getCompletedTokens() { return completedTokens; }
            public void setCompletedTokens(long completedTokens) { this.completedTokens = completedTokens; }

            public static Builder builder() { return new Builder(); }
            public static class Builder {
                private String date;
                private long totalTokens;
                private long completedTokens;

                public Builder date(String date) { this.date = date; return this; }
                public Builder totalTokens(long totalTokens) { this.totalTokens = totalTokens; return this; }
                public Builder completedTokens(long completedTokens) { this.completedTokens = completedTokens; return this; }

                public DailyVolume build() {
                    return new DailyVolume(date, totalTokens, completedTokens);
                }
            }
        }

        public static class StatusBreakdown {
            private String status;
            private long count;

            public StatusBreakdown() {}
            public StatusBreakdown(String status, long count) {
                this.status = status;
                this.count = count;
            }

            public String getStatus() { return status; }
            public void setStatus(String status) { this.status = status; }
            public long getCount() { return count; }
            public void setCount(long count) { this.count = count; }

            public static Builder builder() { return new Builder(); }
            public static class Builder {
                private String status;
                private long count;

                public Builder status(String status) { this.status = status; return this; }
                public Builder count(long count) { this.count = count; return this; }

                public StatusBreakdown build() { return new StatusBreakdown(status, count); }
            }
        }

        public static class CounterUtilization {
            private String counterNumber;
            private String counterName;
            private String staffName;
            private long processedTokens;

            public CounterUtilization() {}
            public CounterUtilization(String counterNumber, String counterName, String staffName, long processedTokens) {
                this.counterNumber = counterNumber;
                this.counterName = counterName;
                this.staffName = staffName;
                this.processedTokens = processedTokens;
            }

            public String getCounterNumber() { return counterNumber; }
            public void setCounterNumber(String counterNumber) { this.counterNumber = counterNumber; }
            public String getCounterName() { return counterName; }
            public void setCounterName(String counterName) { this.counterName = counterName; }
            public String getStaffName() { return staffName; }
            public void setStaffName(String staffName) { this.staffName = staffName; }
            public long getProcessedTokens() { return processedTokens; }
            public void setProcessedTokens(long processedTokens) { this.processedTokens = processedTokens; }

            public static Builder builder() { return new Builder(); }
            public static class Builder {
                private String counterNumber;
                private String counterName;
                private String staffName;
                private long processedTokens;

                public Builder counterNumber(String counterNumber) { this.counterNumber = counterNumber; return this; }
                public Builder counterName(String counterName) { this.counterName = counterName; return this; }
                public Builder staffName(String staffName) { this.staffName = staffName; return this; }
                public Builder processedTokens(long processedTokens) { this.processedTokens = processedTokens; return this; }

                public CounterUtilization build() {
                    return new CounterUtilization(counterNumber, counterName, staffName, processedTokens);
                }
            }
        }

        public static class PeakHourStat {
            private String hourLabel;
            private long count;

            public PeakHourStat() {}
            public PeakHourStat(String hourLabel, long count) {
                this.hourLabel = hourLabel;
                this.count = count;
            }

            public String getHourLabel() { return hourLabel; }
            public void setHourLabel(String hourLabel) { this.hourLabel = hourLabel; }
            public long getCount() { return count; }
            public void setCount(long count) { this.count = count; }

            public static Builder builder() { return new Builder(); }
            public static class Builder {
                private String hourLabel;
                private long count;

                public Builder hourLabel(String hourLabel) { this.hourLabel = hourLabel; return this; }
                public Builder count(long count) { this.count = count; return this; }

                public PeakHourStat build() {
                    return new PeakHourStat(hourLabel, count);
                }
            }
        }
    }
}
