package com.example.hena.notification.service;

import com.example.hena.notification.entity.Notification;
import com.example.hena.notification.repository.NotificationRepository;
import com.example.hena.event.entity.Event;
import com.example.hena.user.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // Inject the NotificationRepository
    public NotificationService(NotificationRepository repository) {
        this.notificationRepository = repository;
    }

    // Generic method to create and store a new notification
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

    // Retrieve all notifications for a given user
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    // Send reminder notifications to users 1 hour before the event starts
    public void createEventReminder(Event event, List<User> users) {
        if (event.getEventDate() == null) return;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime eventTime = event.getEventDate();

        long minutesUntilEvent = java.time.Duration.between(now, eventTime).toMinutes();
        if (minutesUntilEvent <= 60 && minutesUntilEvent >= 0) {
            for (User user : users) {
                if ("user".equalsIgnoreCase(user.getRole())) {
                    String content = "⏰ Reminder: Your event '" + event.getName() + "' starts in " + minutesUntilEvent + " minutes.";
                    create(user.getId(), event.getId(), "REMINDER", content);
                }
            }
        }
    }

    // Notify the host and admin when a user RSVPs to the event
    public void notifyHostRSVP(Event event, User attendee) {
        User host = event.getHost();
        if (host != null && "host".equalsIgnoreCase(host.getRole())) {
            String content = attendee.getUsername() + " has RSVP’d to your event: " + event.getName();
            create(host.getId(), event.getId(), "RSVP", content);
        }

        for (User u : event.getRsvps()) {
            if ("admin".equalsIgnoreCase(u.getRole())) {
                String content = attendee.getUsername() + " has RSVP’d to an event: " + event.getName();
                create(u.getId(), event.getId(), "RSVP", content);
            }
        }
    }

    // Notify users who RSVP’d when the event’s time or location is updated
    public void notifyEventUpdate(Event event, List<User> rsvps, String fieldChanged) {
        for (User u : rsvps) {
            if ("user".equalsIgnoreCase(u.getRole())) {
                String content = "Event '" + event.getName() + "' had its " + fieldChanged + " updated.";
                create(u.getId(), event.getId(), "UPDATE", content);
            }
        }
    }

    // Notify all attending users when the event is canceled
    public void notifyEventCancellation(Event event, List<User> rsvps) {
        for (User u : rsvps) {
            if ("user".equalsIgnoreCase(u.getRole())) {
                String content = "The event '" + event.getName() + "' has been canceled.";
                create(u.getId(), event.getId(), "CANCELLATION", content);
            }
        }
    }
}
