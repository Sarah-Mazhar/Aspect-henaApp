package com.example.hena.event.service;

import com.example.hena.admin.entity.AdminLog;
import com.example.hena.admin.repository.AdminLogRepository;
import com.example.hena.event.entity.Event;
import com.example.hena.event.entity.EventLog;
import com.example.hena.event.repository.EventRepository;
import com.example.hena.event.repository.EventLogRepository;
import com.example.hena.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.example.hena.redis.service.Redis;
import java.time.Duration;
import java.util.List;
import java.time.LocalDateTime;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventLogRepository eventLogRepository;

    @Autowired
    private AdminLogRepository adminLogRepository;

    @Autowired
    private Redis redis; // Redis for caching

    private final ObjectMapper objectMapper = new ObjectMapper(); // Jackson for JSON conversion

    // Create a new event
    public Event createEvent(Event event, User user) {
        event.setHost(user);

        // If user is admin, set created_by_admin_id
        if ("ADMIN".equals(user.getRole())) {
            event.setCreatedByAdminId(user.getId());
        }

        // Set attendees count fields
        event.setMaxAttendees(event.getMaxAttendees());
        event.setCurrentAttendees(0);

        Event createdEvent = eventRepository.save(event);

        // Log the creation in event_log
        EventLog eventLog = new EventLog();
        eventLog.setAdminId("ADMIN".equals(user.getRole()) ? user.getId() : null);
        eventLog.setAction("CREATE_EVENT");
        eventLog.setEventId(createdEvent.getId());
        eventLog.setTimestamp(LocalDateTime.now());
        eventLogRepository.save(eventLog);

        // Log admin action in admin_log
        if ("ADMIN".equals(user.getRole())) {
            AdminLog adminLog = new AdminLog();
            adminLog.setAdminId(user.getId());
            adminLog.setAction("CREATE_EVENT");
            adminLog.setTargetEntity("Event: " + createdEvent.getName());
            adminLog.setTimestamp(LocalDateTime.now());
            adminLogRepository.save(adminLog);
        }

        return createdEvent;
    }

    // Return all events (admin use)
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    // Save any event instance (used internally)
    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    // Update event details
    public Event updateEvent(Long id, Event eventDetails, User user) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
        event.setName(eventDetails.getName());
        event.setDescription(eventDetails.getDescription());
        event.setEventDate(eventDetails.getEventDate());
        event.setLocation(eventDetails.getLocation());
        event.setCategory(event.getCategory());

        // Optional max attendees update
        if (eventDetails.getMaxAttendees() > 0) {
            event.setMaxAttendees(eventDetails.getMaxAttendees());
        }

        // Track admin who performed update
        if ("ADMIN".equals(user.getRole())) {
            event.setCreatedByAdminId(user.getId());
        }

        Event updatedEvent = eventRepository.save(event);

        // Log update in event_log
        EventLog eventLog = new EventLog();
        eventLog.setAdminId("ADMIN".equals(user.getRole()) ? user.getId() : null);
        eventLog.setAction("UPDATE_EVENT");
        eventLog.setEventId(updatedEvent.getId());
        eventLog.setTimestamp(LocalDateTime.now());
        eventLogRepository.save(eventLog);

        // Log update in admin_log
        if ("ADMIN".equals(user.getRole())) {
            AdminLog adminLog = new AdminLog();
            adminLog.setAdminId(user.getId());
            adminLog.setAction("UPDATE_EVENT");
            adminLog.setTargetEntity("Event: " + updatedEvent.getName());
            adminLog.setTimestamp(LocalDateTime.now());
            adminLogRepository.save(adminLog);
        }

        return updatedEvent;
    }

    // Delete an event by ID
    public void deleteEvent(Long id, User user) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
        eventRepository.deleteById(id);

        // Log deletion in event_log
        EventLog eventLog = new EventLog();
        eventLog.setAdminId("ADMIN".equals(user.getRole()) ? user.getId() : null);
        eventLog.setAction("DELETE_EVENT");
        eventLog.setEventId(event.getId());
        eventLog.setTimestamp(LocalDateTime.now());
        eventLogRepository.save(eventLog);

        // Log deletion in admin_log
        if ("ADMIN".equals(user.getRole())) {
            AdminLog adminLog = new AdminLog();
            adminLog.setAdminId(user.getId());
            adminLog.setAction("DELETE_EVENT");
            adminLog.setTargetEntity("Event: " + event.getName());
            adminLog.setTimestamp(LocalDateTime.now());
            adminLogRepository.save(adminLog);
        }
    }

    // Get events hosted by a specific user (host), with Redis caching
    public List<Event> findEventsByHostId(Long hostId) {
        String key = "event:host:" + hostId;

        try {
            String cachedJson = redis.get(key);
            if (cachedJson != null) {
                System.out.println("✅ [CACHE] Returning events by hostId from Redis");
                return objectMapper.readValue(cachedJson, new TypeReference<List<Event>>() {});
            }
        } catch (Exception e) {
            System.err.println("❌ Redis read error (hostId): " + e.getMessage());
        }

        List<Event> events = eventRepository.findByHost_Id(hostId);

        try {
            redis.set(key, objectMapper.writeValueAsString(events), Duration.ofMinutes(10));
        } catch (Exception e) {
            System.err.println("❌ Redis write error (hostId): " + e.getMessage());
        }

        return events;
    }

    // Retrieve single event with Redis caching
    public Event findEventById(Long id) {
        String key = "event:id:" + id;

        try {
            String cachedJson = redis.get(key);
            if (cachedJson != null) {
                System.out.println("✅ [CACHE] Returning Event from Redis");
                return objectMapper.readValue(cachedJson, Event.class);
            }
        } catch (Exception e) {
            System.err.println("❌ Redis read error: " + e.getMessage());
        }

        System.out.println("🔍 [DB] Fetching Event ID: " + id);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        try {
            redis.set(key, objectMapper.writeValueAsString(event), Duration.ofMinutes(10));
        } catch (Exception e) {
            System.err.println("❌ Redis write error: " + e.getMessage());
        }

        return event;
    }

    // List all upcoming events (after now)
    public List<Event> findUpcomingEvents() {
        LocalDateTime now = LocalDateTime.now();
        return eventRepository.findByEventDateAfter(now);
    }

    // Cancel RSVP for a user
    public void cancelRSVP(Long eventId, User user) {
        Event event = findEventById(eventId);

        // Remove user RSVP by ID match
        boolean removed = event.getRsvps().removeIf(u -> u.getId().equals(user.getId()));
        if (!removed) {
            throw new IllegalStateException("User is not RSVP'd to this event.");
        }

        // Decrement attendee count safely
        int current = event.getCurrentAttendees();
        event.setCurrentAttendees(Math.max(0, current - 1));

        eventRepository.save(event);
    }

    // Find events a user RSVP'd to
    public List<Event> findEventsByRsvpUserId(Long userId) {
        return eventRepository.findEventsByRsvpUserId(userId);
    }

}
