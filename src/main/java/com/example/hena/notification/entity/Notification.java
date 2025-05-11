package com.example.hena.notification.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Primary key for the notification

    private Long userId;   // ID of the user receiving the notification
    private Long eventId;  // ID of the related event

    private String type;   // Type: RSVP, REMINDER, CANCELLATION, UPDATE
    private String content; // Message content to display

    @Column(name = "is_read", nullable = false)
    private boolean read = false;  // Whether the user has read the notification

    private LocalDateTime timestamp = LocalDateTime.now();  // Creation time of the notification

    // ====== Getters and Setters ======

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    // Human-readable timestamp for UI display (e.g., 2025-05-12 08:15:00)
    public String getFormattedTimestamp() {
        return timestamp != null
                ? timestamp.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                : null;
    }
}
