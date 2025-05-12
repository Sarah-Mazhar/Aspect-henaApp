package com.example.hena.notification.repository;

import com.example.hena.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Repository interface for accessing Notification data in the database
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Find all notifications for a specific user by their user ID
    List<Notification> findByUserId(Long userId);
}
