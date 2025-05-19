package com.example.hena.notification.service;

import com.example.hena.notification.entity.Notification;
import com.example.hena.notification.repository.NotificationRepository;
import com.example.hena.event.entity.Event;
import com.example.hena.user.entity.User;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service layer for managing notifications.
 */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // ============================
    //  Constructor
    // ============================

    public NotificationService(NotificationRepository repository) {
        this.notificationRepository = repository;
    }

    // ============================
    //  Create Notification
    // ============================

    /**
     * Generic method to create and store a new notification.
     * @param userId  Target user ID
     * @param eventId Related event ID
     * @param type    Notification type
     * @param content Message content
     * @return Saved Notification
     */
    public Notification create(Long userId, Long eventId, String type, String content) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setEventId(eventId);
        n.setType(type);
        n.setContent(content);
        n.setTimestamp(LocalDateTime.now());
        n.setRead(false);
        return notificationRepository.save(n);
    }

    // ============================
    //  Get Notifications
    // ============================

    /**
     * Retrieve all notifications for a specific user.
     * @param userId User ID
     * @return List of Notifications
     */
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    // ============================
    //  Predefined Notification: RSVP Confirmation
    // ============================

    /**
     * Notify a user that they have successfully RSVP'd to an event.
     * Only applies to users with role "user".
     * @param event Event the user RSVP'd to
     * @param user  The user who RSVP'd
     */
    public void notifyUserRSVP(Event event, User user) {
        if ("user".equalsIgnoreCase(user.getRole())) {
            String content = "🌟 Successfully Registered For '" + event.getName() +
                    "'! 📅 Mark your calendar for " + event.getEventDate().toLocalDate() +
                    " at " + event.getEventDate().toLocalTime() +
                    " — SAVE THE DATE! 🎉";
            create(user.getId(), event.getId(), "RSVP_CONFIRMATION", content);
        }
    }
}
