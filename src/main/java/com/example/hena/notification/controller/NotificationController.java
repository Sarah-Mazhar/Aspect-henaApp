// ================= NotificationController.java =================
package com.example.hena.notification.controller;

import com.example.hena.notification.dto.*;
import com.example.hena.notification.entity.Notification;
import com.example.hena.notification.service.NotificationService;
import com.example.hena.event.entity.Event;
import com.example.hena.user.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController // Marks this class as a REST controller
@RequestMapping("/api/notifications") // Base URL for all endpoints in this controller
public class NotificationController {

    private final NotificationService service;

    // Constructor injection for NotificationService
    public NotificationController(NotificationService service) {
        this.service = service;
    }

    // Manually create a single notification (generic use)
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

    // Retrieve all notifications for a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getUserNotifications(userId));
    }

    // Send RSVP notifications to the host and admin(s)
    @PostMapping("/notify/rsvp")
    public ResponseEntity<String> notifyRSVP(@RequestBody RSVPDTO dto) {
        // Create event and host objects from DTO
        Event event = new Event();
        event.setId(dto.getEventId());
        event.setName(dto.getEventName());

        User host = new User();
        host.setId(dto.getHostId());
        host.setRole("host");
        event.setHost(host);

        // Create user object representing the attendee
        User attendee = new User();
        attendee.setId(dto.getUserId());
        attendee.setUsername(dto.getUsername());
        attendee.setRole("user");

        // Add a dummy admin for notification (can be dynamic in real apps)
        User admin = new User();
        admin.setId(99L);
        admin.setRole("admin");
        event.setRsvps(Set.of(admin));

        // Send RSVP notifications
        service.notifyHostRSVP(event, attendee);
        return ResponseEntity.ok("RSVP notifications sent.");
    }

    // Send reminders to users if the event starts in ≤60 minutes
    @PostMapping("/notify/reminder")
    public ResponseEntity<String> notifyReminder(@RequestBody ReminderDTO dto) {
        Event event = new Event();
        event.setId(dto.getEventId());
        event.setName(dto.getEventName());

        // Convert userIds from DTO into User objects with "user" role
        List<User> users = dto.getUserIds().stream().map(id -> {
            User u = new User();
            u.setId(id);
            u.setRole("user");
            return u;
        }).toList();

        service.createEventReminder(event, users);
        return ResponseEntity.ok("Reminder notifications sent to attendees.");
    }

    // Notify users that an event field (e.g. time, location) has been updated
    @PostMapping("/notify/update")
    public ResponseEntity<String> notifyUpdate(@RequestBody UpdateDTO dto) {
        Event event = new Event();
        event.setId(dto.getEventId());
        event.setName(dto.getEventName());

        // Map user IDs to User objects with role "user"
        List<User> users = dto.getUserIds().stream().map(id -> {
            User u = new User();
            u.setId(id);
            u.setRole("user");
            return u;
        }).toList();

        service.notifyEventUpdate(event, users, dto.getField());
        return ResponseEntity.ok("Update notifications sent.");
    }

    // Notify users that an event has been cancelled
    @PostMapping("/notify/cancel")
    public ResponseEntity<String> notifyCancel(@RequestBody CancelDTO dto) {
        Event event = new Event();
        event.setId(dto.getEventId());
        event.setName(dto.getEventName());

        List<User> users = dto.getUserIds().stream().map(id -> {
            User u = new User();
            u.setId(id);
            u.setRole("user");
            return u;
        }).toList();

        service.notifyEventCancellation(event, users);
        return ResponseEntity.ok("Cancellation notifications sent.");
    }
}
