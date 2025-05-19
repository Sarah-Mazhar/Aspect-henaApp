package com.example.hena.event.controller;

import com.example.hena.event.dto.CreateEventDTO;
import com.example.hena.event.entity.Event;
import com.example.hena.event.service.EventService;
import com.example.hena.redis.annotations.RateLimit;
import com.example.hena.user.entity.User;
import com.example.hena.user.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing event-related operations.
 */
@RestController
@RequestMapping("/api/event")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    /**
     * Create a new event by a HOST or ADMIN.
     * Rate-limited to 1 event every 10 seconds per user.
     *
     * @param adminOrHostId the ID of the user creating the event
     * @param dto           the event data
     * @return the created Event
     */
    @PreAuthorize("hasRole('HOST') or hasRole('ADMIN')")
    @PostMapping("/create/{adminOrHostId}")
    @RateLimit(limit = 1, duration = 10, keyPrefix = "createEvent")
    public Event createEvent(@PathVariable Long adminOrHostId, @RequestBody CreateEventDTO dto) {
        User user = userService.getUserById(adminOrHostId);

        Event event = new Event();
        event.setName(dto.getName());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());
        event.setEventDate(dto.getEventDate());
        event.setCategory(dto.getCategory());
        event.setMaxAttendees(dto.getMaxAttendees());
        event.setHost(user);

        return eventService.createEvent(event, user);
    }

    /**
     * Retrieve all events created by a specific HOST or ADMIN.
     *
     * @param hostId the ID of the user whose events are being retrieved
     * @return list of events or appropriate error if user is invalid
     */
    @PreAuthorize("hasRole('HOST') or hasRole('ADMIN')")
    @GetMapping("/host/{hostId}")
    public ResponseEntity<?> getHostEvents(@PathVariable Long hostId) {
        User hostUser = userService.getUserById(hostId);

        if (hostUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        if (!"HOST".equals(hostUser.getRole()) && !"ADMIN".equals(hostUser.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: Only hosts or admins can view these events.");
        }

        List<Event> events = eventService.findEventsByHostId(hostId);
        return ResponseEntity.ok(events);
    }

    /**
     * Update an existing event.
     * Only HOST or ADMIN can perform this.
     * Rate-limited to 1 update every 10 seconds per user.
     *
     * @param adminOrHostId ID of the user updating the event
     * @param id            ID of the event to update
     * @param dto           updated event data
     * @return the updated Event
     */
    @PreAuthorize("hasRole('HOST') or hasRole('ADMIN')")
    @PutMapping("/update/{adminOrHostId}/{id}")
    @RateLimit(limit = 1, duration = 10, keyPrefix = "updateEvent")
    public Event updateEvent(@PathVariable Long adminOrHostId, @PathVariable Long id, @RequestBody CreateEventDTO dto) {
        User user = userService.getUserById(adminOrHostId);

        Event event = new Event();
        event.setName(dto.getName());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());
        event.setEventDate(dto.getEventDate());
        event.setCategory(dto.getCategory());
        event.setMaxAttendees(dto.getMaxAttendees());

        return eventService.updateEvent(id, event, user);
    }

    /**
     * Delete an event.
     * Only HOST or ADMIN can delete events they own.
     *
     * @param adminOrHostId ID of the user requesting deletion
     * @param id            ID of the event to delete
     */
    @PreAuthorize("hasRole('HOST') or hasRole('ADMIN')")
    @DeleteMapping("/delete/{adminOrHostId}/{id}")
    public void deleteEvent(@PathVariable Long adminOrHostId, @PathVariable Long id) {
        User user = userService.getUserById(adminOrHostId);
        eventService.deleteEvent(id, user);
    }

    /**
     * Retrieve all events in the system.
     * Only accessible by ADMIN users.
     *
     * @return list of all events
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<?> getAllEventsForAdmin() {
        List<Event> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    /**
     * Retrieve a specific event by its ID.
     *
     * @param id the event ID
     * @return the event if found
     */
    @GetMapping("/{id}")
    public Event getEvent(@PathVariable Long id) {
        return eventService.findEventById(id);
    }

    /**
     * Retrieve a list of upcoming events.
     *
     * @return list of upcoming events
     */
    @GetMapping("/upcoming")
    public List<Event> getUpcomingEvents() {
        return eventService.findUpcomingEvents();
    }
}
