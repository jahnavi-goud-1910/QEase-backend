package com.qease.repository;

import com.qease.entity.Notification;
import com.qease.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByStudentOrderByCreatedAtDesc(User student);
    List<Notification> findByStudentAndIsReadFalseOrderByCreatedAtDesc(User student);
    long countByStudentAndIsReadFalse(User student);
}
