package com.findly.api.notifications.repository;

import com.findly.api.notifications.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findByUserIdAndDeletedFalseOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    List<Notification> findByUserIdAndReadFalseAndDeletedFalse(UUID userId);

    long countByUserIdAndReadFalseAndDeletedFalse(UUID userId);

    long countByReadFalseAndDeletedFalse();
}