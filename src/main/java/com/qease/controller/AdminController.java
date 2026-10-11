package com.qease.controller;

import com.qease.dto.AdminDto;
import com.qease.dto.AuthDto;
import com.qease.dto.CounterDto;
import com.qease.dto.QueueDto;
import com.qease.dto.ServiceDto;
import com.qease.repository.QueueTokenRepository;
import com.qease.service.AdminService;
import com.qease.service.AnalyticsService;
import com.qease.service.QueueService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private QueueTokenRepository queueTokenRepository;

    @Autowired
    private QueueService queueService;

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDto.AdminDashboardSummaryDto> getDashboardSummary() {
        return ResponseEntity.ok(adminService.getDashboardSummary());
    }

    // --- Users & Staff ---
    @GetMapping("/users")
    public ResponseEntity<List<AuthDto.UserDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/staff")
    public ResponseEntity<List<AuthDto.UserDto>> getAllStaff() {
        return ResponseEntity.ok(adminService.getAllStaff());
    }

    @PostMapping("/staff")
    public ResponseEntity<AuthDto.UserDto> addStaff(@Valid @RequestBody AuthDto.RegisterRequest request) {
        return ResponseEntity.ok(adminService.addStaff(request));
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<AuthDto.UserDto> toggleUserStatus(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.toggleUserStatus(id));
    }

    // --- Services ---
    @GetMapping("/services")
    public ResponseEntity<List<ServiceDto>> getAllServices() {
        return ResponseEntity.ok(adminService.getAllServices());
    }

    @PostMapping("/services")
    public ResponseEntity<ServiceDto> createService(@Valid @RequestBody ServiceDto serviceDto) {
        return ResponseEntity.ok(adminService.createService(serviceDto));
    }

    @PutMapping("/services/{id}")
    public ResponseEntity<ServiceDto> updateService(@PathVariable Long id, @Valid @RequestBody ServiceDto serviceDto) {
        return ResponseEntity.ok(adminService.updateService(id, serviceDto));
    }

    @PatchMapping("/services/{id}/status")
    public ResponseEntity<ServiceDto> toggleServiceStatus(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.toggleServiceStatus(id));
    }

    // --- Counters ---
    @GetMapping("/counters")
    public ResponseEntity<List<CounterDto>> getAllCounters() {
        return ResponseEntity.ok(adminService.getAllCounters());
    }

    @PostMapping("/counters")
    public ResponseEntity<CounterDto> createCounter(@Valid @RequestBody CounterDto counterDto) {
        return ResponseEntity.ok(adminService.createCounter(counterDto));
    }

    @PutMapping("/counters/{id}")
    public ResponseEntity<CounterDto> updateCounter(@PathVariable Long id, @Valid @RequestBody CounterDto counterDto) {
        return ResponseEntity.ok(adminService.updateCounter(id, counterDto));
    }

    @PatchMapping("/counters/{id}/status")
    public ResponseEntity<CounterDto> toggleCounterStatus(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.toggleCounterStatus(id));
    }

    // --- Active Queues & History ---
    @GetMapping("/queues")
    public ResponseEntity<List<QueueDto.QueueTokenDto>> getAllQueues() {
        List<QueueDto.QueueTokenDto> tokens = queueTokenRepository.findAll().stream()
                .map(queueService::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tokens);
    }

    // --- Analytics ---
    @GetMapping("/analytics")
    public ResponseEntity<AdminDto.AnalyticsDto> getAnalytics() {
        return ResponseEntity.ok(analyticsService.getAnalyticsData());
    }
}
