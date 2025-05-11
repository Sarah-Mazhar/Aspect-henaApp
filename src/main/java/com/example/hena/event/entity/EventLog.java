package com.example.hena.event.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class EventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long adminId;  // ID of the admin who performed the action
    private String action;  // The action taken (e.g., CREATE_EVENT, UPDATE_EVENT, DELETE_EVENT)
    private Long eventId;   // The ID of the event the action was performed on
    private LocalDateTime timestamp;  // Timestamp of the action

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
