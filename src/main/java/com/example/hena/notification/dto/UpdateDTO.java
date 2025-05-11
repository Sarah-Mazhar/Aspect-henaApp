package com.example.hena.notification.dto;

import java.util.List;

public class UpdateDTO {
    private Long eventId;
    private String eventName;
    private List<Long> userIds;
    private String field;

    // Getters and Setters
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public List<Long> getUserIds() { return userIds; }
    public void setUserIds(List<Long> userIds) { this.userIds = userIds; }

    public String getField() { return field; }
    public void setField(String field) { this.field = field; }
}
