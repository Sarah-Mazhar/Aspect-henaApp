// ================= NotificationController.java =================

package com.example.hena.notification.controller;

import com.example.hena.notification.dto.NotificationDTO;
import com.example.hena.notification.entity.Notification;
import com.example.hena.notification.service.NotificationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for handling notification-related operations.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService service;

    // ============================
    // 🔹 Constructor
    // ============================

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    // ============================
    // 🔹 POST: Create Notification
    // ============================

    /**
     * Create a new notification manually.
     * @param dto Notification details (userId, eventId, type, content)
     * @return Created Notification
     */
    @PostMapping("/create")
    public ResponseEntity<Notification> createNotification(@RequestBody NotificationDTO dto) {
        Notification created = service.create(
                dto.getUserId(),
                dto.getEventId(),
                dto.getType(),
                dto.getContent()
        );
        return ResponseEntity.ok(created);
    }

    // ============================
    // 🔹 GET: Fetch Notifications for a User
    // ============================

    /**
     * Retrieve all notifications for a specific user.
     * @param userId User ID
     * @return List of Notifications
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getUserNotifications(userId));
    }
}
