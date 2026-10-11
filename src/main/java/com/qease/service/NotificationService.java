package com.qease.service;

import com.qease.dto.QueueDto;
import com.qease.entity.Notification;
import com.qease.entity.QueueToken;
import com.qease.entity.User;
import com.qease.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Transactional
    public void createQueueNotification(User student, QueueToken token, String title, String message) {
        Notification notification = Notification.builder()
                .student(student)
                .token(token)
                .title(title)
                .message(message)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }

    public List<QueueDto.NotificationDto> getStudentNotifications(User student) {
        return notificationRepository.findByStudentOrderByCreatedAtDesc(student)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(Long notificationId, User student) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElse(null);
        if (notification != null && notification.getStudent().getId().equals(student.getId())) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    @Transactional
    public void markAllAsRead(User student) {
        List<Notification> unread = notificationRepository.findByStudentAndIsReadFalseOrderByCreatedAtDesc(student);
        for (Notification n : unread) {
            n.setRead(true);
        }
        notificationRepository.saveAll(unread);
    }

    public QueueDto.NotificationDto mapToDto(Notification notification) {
        return QueueDto.NotificationDto.builder()
                .id(notification.getId())
                .studentId(notification.getStudent().getId())
                .tokenId(notification.getToken() != null ? notification.getToken().getId() : null)
                .title(notification.getTitle())
                .message(notification.getMessage())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
