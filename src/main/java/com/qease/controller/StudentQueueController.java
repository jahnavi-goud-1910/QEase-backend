package com.qease.controller;

import com.qease.dto.QueueDto;
import com.qease.entity.User;
import com.qease.repository.UserRepository;
import com.qease.service.NotificationService;
import com.qease.service.QueueService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/queue")
public class StudentQueueController {

    @Autowired
    private QueueService queueService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/take")
    public ResponseEntity<QueueDto.QueueTokenDto> takeToken(
            @Valid @RequestBody QueueDto.TakeTokenRequest request,
            Authentication authentication) {
        QueueDto.QueueTokenDto token = queueService.takeToken(authentication.getName(), request.getServiceId());
        return ResponseEntity.ok(token);
    }

    @GetMapping("/my-active")
    public ResponseEntity<QueueDto.QueueTokenDto> getMyActiveToken(Authentication authentication) {
        QueueDto.QueueTokenDto activeToken = queueService.getActiveTokenForStudent(authentication.getName());
        return ResponseEntity.ok(activeToken);
    }

    @GetMapping("/my-history")
    public ResponseEntity<List<QueueDto.QueueTokenDto>> getMyHistory(Authentication authentication) {
        List<QueueDto.QueueTokenDto> history = queueService.getStudentHistory(authentication.getName());
        return ResponseEntity.ok(history);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QueueDto.QueueTokenDto> getTokenDetails(@PathVariable Long id) {
        QueueDto.QueueTokenDto token = queueService.getTokenDetails(id);
        return ResponseEntity.ok(token);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<QueueDto.QueueTokenDto> cancelToken(@PathVariable Long id, Authentication authentication) {
        QueueDto.QueueTokenDto cancelledToken = queueService.cancelToken(id, authentication.getName());
        return ResponseEntity.ok(cancelledToken);
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<QueueDto.NotificationDto>> getNotifications(Authentication authentication) {
        User student = userRepository.findByEmail(authentication.getName()).orElseThrow();
        List<QueueDto.NotificationDto> notifications = notificationService.getStudentNotifications(student);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/notifications/read-all")
    public ResponseEntity<Void> markAllNotificationsRead(Authentication authentication) {
        User student = userRepository.findByEmail(authentication.getName()).orElseThrow();
        notificationService.markAllAsRead(student);
        return ResponseEntity.ok().build();
    }
}
