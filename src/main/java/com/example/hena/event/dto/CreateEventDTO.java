package com.example.hena.event.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateEventDTO {
    private String name;
    private String description;
    private String location;
    private LocalDateTime eventDate;
    private String category;
    private int maxAttendees;
}
