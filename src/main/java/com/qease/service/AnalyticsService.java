package com.qease.service;

import com.qease.dto.AdminDto;
import com.qease.entity.*;
import com.qease.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private QueueTokenRepository queueTokenRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private CounterRepository counterRepository;

    public AdminDto.AnalyticsDto getAnalyticsData() {
        List<QueueToken> allTokens = queueTokenRepository.findAll();
        List<ServiceEntity> services = serviceRepository.findAll();
        List<Counter> counters = counterRepository.findAll();

        // 1. Tokens by Service
        List<AdminDto.AnalyticsDto.ServiceStat> serviceStats = services.stream().map(service -> {
            long total = allTokens.stream().filter(t -> t.getService().getId().equals(service.getId())).count();
            long completed = allTokens.stream().filter(t -> t.getService().getId().equals(service.getId()) && t.getStatus() == TokenStatus.COMPLETED).count();
            long cancelled = allTokens.stream().filter(t -> t.getService().getId().equals(service.getId()) && t.getStatus() == TokenStatus.CANCELLED).count();
            return AdminDto.AnalyticsDto.ServiceStat.builder()
                    .serviceName(service.getName())
                    .serviceCode(service.getCode())
                    .tokenCount(total)
                    .completedCount(completed)
                    .cancelledCount(cancelled)
                    .build();
        }).collect(Collectors.toList());

        // 2. Status Breakdown
        Map<TokenStatus, Long> statusCounts = allTokens.stream()
                .collect(Collectors.groupingBy(QueueToken::getStatus, Collectors.counting()));
        List<AdminDto.AnalyticsDto.StatusBreakdown> statusBreakdowns = Arrays.stream(TokenStatus.values())
                .map(status -> AdminDto.AnalyticsDto.StatusBreakdown.builder()
                        .status(status.name())
                        .count(statusCounts.getOrDefault(status, 0L))
                        .build())
                .collect(Collectors.toList());

        // 3. Daily Volume (Last 7 Days)
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("MMM dd");
        List<AdminDto.AnalyticsDto.DailyVolume> dailyVolumes = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String label = date.format(dayFormatter);

            long totalForDay = allTokens.stream()
                    .filter(t -> t.getIssueTime() != null && t.getIssueTime().toLocalDate().equals(date))
                    .count();

            long completedForDay = allTokens.stream()
                    .filter(t -> t.getIssueTime() != null && t.getIssueTime().toLocalDate().equals(date) && t.getStatus() == TokenStatus.COMPLETED)
                    .count();

            dailyVolumes.add(AdminDto.AnalyticsDto.DailyVolume.builder()
                    .date(label)
                    .totalTokens(totalForDay)
                    .completedTokens(completedForDay)
                    .build());
        }

        // 4. Counter Utilization
        List<AdminDto.AnalyticsDto.CounterUtilization> counterUtilizations = counters.stream().map(counter -> {
            long processed = allTokens.stream()
                    .filter(t -> t.getCounter() != null && t.getCounter().getId().equals(counter.getId()) && t.getStatus() == TokenStatus.COMPLETED)
                    .count();
            return AdminDto.AnalyticsDto.CounterUtilization.builder()
                    .counterNumber(counter.getCounterNumber())
                    .counterName(counter.getName())
                    .staffName(counter.getStaff() != null ? counter.getStaff().getFullName() : "Unassigned")
                    .processedTokens(processed)
                    .build();
        }).collect(Collectors.toList());

        // 5. Peak Hours (9 AM to 5 PM)
        Map<Integer, Long> hourCounts = allTokens.stream()
                .filter(t -> t.getIssueTime() != null)
                .collect(Collectors.groupingBy(t -> t.getIssueTime().getHour(), Collectors.counting()));

        List<AdminDto.AnalyticsDto.PeakHourStat> peakHours = new ArrayList<>();
        for (int hour = 8; hour <= 17; hour++) {
            String label = String.format("%02d:00", hour);
            long count = hourCounts.getOrDefault(hour, 0L);
            peakHours.add(AdminDto.AnalyticsDto.PeakHourStat.builder()
                    .hourLabel(label)
                    .count(count)
                    .build());
        }

        // 6. Average Wait Time & Service Duration
        double avgWaitMinutes = 0;
        List<QueueToken> servedTokens = allTokens.stream()
                .filter(t -> t.getIssueTime() != null && t.getServedTime() != null)
                .toList();
        if (!servedTokens.isEmpty()) {
            long totalWaitSeconds = servedTokens.stream()
                    .mapToLong(t -> Duration.between(t.getIssueTime(), t.getServedTime()).getSeconds())
                    .sum();
            avgWaitMinutes = Math.round((totalWaitSeconds / 60.0 / servedTokens.size()) * 10.0) / 10.0;
        }

        double avgServiceMinutes = 0;
        List<QueueToken> completedTokens = allTokens.stream()
                .filter(t -> t.getServedTime() != null && t.getCompletedTime() != null)
                .toList();
        if (!completedTokens.isEmpty()) {
            long totalServiceSeconds = completedTokens.stream()
                    .mapToLong(t -> Duration.between(t.getServedTime(), t.getCompletedTime()).getSeconds())
                    .sum();
            avgServiceMinutes = Math.round((totalServiceSeconds / 60.0 / completedTokens.size()) * 10.0) / 10.0;
        }

        return AdminDto.AnalyticsDto.builder()
                .serviceStats(serviceStats)
                .dailyVolumes(dailyVolumes)
                .statusBreakdowns(statusBreakdowns)
                .counterUtilizations(counterUtilizations)
                .peakHours(peakHours)
                .averageWaitTimeMinutes(avgWaitMinutes)
                .averageServiceTimeMinutes(avgServiceMinutes)
                .build();
    }
}
