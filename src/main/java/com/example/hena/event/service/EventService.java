//package com.example.hena.event.service;
//
//import com.example.hena.admin.entity.AdminLog;
//import com.example.hena.admin.repository.AdminLogRepository;
//import com.example.hena.event.entity.Event;
//import com.example.hena.event.entity.EventLog;
//import com.example.hena.event.repository.EventRepository;
//import com.example.hena.event.repository.EventLogRepository;
//import com.example.hena.user.entity.User;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//public class EventService {
//
//    @Autowired
//    private EventRepository eventRepository;
//
//    @Autowired
//    private EventLogRepository eventLogRepository;
//
//    @Autowired
//    private AdminLogRepository adminLogRepository;
//
//    // Create a new event
//    public Event createEvent(Event event, User user) {
//        // Set host of the event
//        event.setHost(user);
//
//        // If user is admin, set created_by_admin_id
//        if ("ADMIN".equals(user.getRole())) {
//            event.setCreatedByAdminId(user.getId()); // Set the admin who created the event
//        }
//
//        Event createdEvent = eventRepository.save(event);
//
//        // Log the creation action in event_log
//        EventLog eventLog = new EventLog();
//        eventLog.setAdminId(user.getId());
//        eventLog.setAction("CREATE_EVENT");
//        eventLog.setEventId(createdEvent.getId());
//        eventLog.setTimestamp(LocalDateTime.now());
//        eventLogRepository.save(eventLog);
//
//        // Log the creation action in admin_log only if the user is an admin
//        if ("ADMIN".equals(user.getRole())) {
//            AdminLog adminLog = new AdminLog();
//            adminLog.setAdminId(user.getId());
//            adminLog.setAction("CREATE_EVENT");
//            adminLog.setTargetEntity("Event: " + createdEvent.getName());
//            adminLog.setTimestamp(LocalDateTime.now());
//            adminLogRepository.save(adminLog);
//        }
//
//        return createdEvent;
//    }
//
//    // Update an existing event
//    public Event updateEvent(Long id, Event eventDetails, User user) {
//        Event event = eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
//        event.setName(eventDetails.getName());
//        event.setDescription(eventDetails.getDescription());
//        event.setEventDate(eventDetails.getEventDate());
//        event.setLocation(eventDetails.getLocation());
//
//        // If the user is an admin, set created_by_admin_id again on update (if needed)
//        if ("ADMIN".equals(user.getRole())) {
//            event.setCreatedByAdminId(user.getId());
//        }
//
//        Event updatedEvent = eventRepository.save(event);
//
//        // Log the update action in event_log
//        EventLog eventLog = new EventLog();
//        eventLog.setAdminId(user.getId());
//        eventLog.setAction("UPDATE_EVENT");
//        eventLog.setEventId(updatedEvent.getId());
//        eventLog.setTimestamp(LocalDateTime.now());
//        eventLogRepository.save(eventLog);
//
//        // Log the update action in admin_log only if the user is an admin
//        if ("ADMIN".equals(user.getRole())) {
//            AdminLog adminLog = new AdminLog();
//            adminLog.setAdminId(user.getId());
//            adminLog.setAction("UPDATE_EVENT");
//            adminLog.setTargetEntity("Event: " + updatedEvent.getName());
//            adminLog.setTimestamp(LocalDateTime.now());
//            adminLogRepository.save(adminLog);
//        }
//
//        return updatedEvent;
//    }
//
//    // Delete an event
//    public void deleteEvent(Long id, User user) {
//        Event event = eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
//        eventRepository.deleteById(id);
//
//        // Log the deletion action in event_log
//        EventLog eventLog = new EventLog();
//        eventLog.setAdminId(user.getId());
//        eventLog.setAction("DELETE_EVENT");
//        eventLog.setEventId(event.getId());
//        eventLog.setTimestamp(LocalDateTime.now());
//        eventLogRepository.save(eventLog);
//
//        // Log the deletion action in admin_log only if the user is an admin
//        if ("ADMIN".equals(user.getRole())) {
//            AdminLog adminLog = new AdminLog();
//            adminLog.setAdminId(user.getId());
//            adminLog.setAction("DELETE_EVENT");
//            adminLog.setTargetEntity("Event: " + event.getName());
//            adminLog.setTimestamp(LocalDateTime.now());
//            adminLogRepository.save(adminLog);
//        }
//    }
//
//    // Find events by host
//    public List<Event> findEventsByHost(User host) {
//        return eventRepository.findByHost_Id(host.getId());
//    }
//
//    // Find events created by admin
//    public List<Event> findEventsByAdmin(Long adminId) {
//        return eventRepository.findByCreatedByAdminId(adminId);
//    }
//
//    // Find event by ID
//    public Event findEventById(Long id) {
//        return eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
//    }
//
//    // RSVP to an event
//    public void rsvpToEvent(Long eventId, User user) {
//        Event event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));
//        event.getRsvps().add(user);  // Assuming Event has an RSVP list to track users
//        eventRepository.save(event);
//
//        // Log the RSVP action in event_log
//        EventLog eventLog = new EventLog();
//        eventLog.setAdminId(user.getId()); // User is acting as the admin for the RSVP
//        eventLog.setAction("RSVP_EVENT");
//        eventLog.setEventId(event.getId());
//        eventLog.setTimestamp(LocalDateTime.now());
//        eventLogRepository.save(eventLog);
//
//        // Log the RSVP action in admin_log if user is an admin
//        if ("ADMIN".equals(user.getRole())) {
//            AdminLog adminLog = new AdminLog();
//            adminLog.setAdminId(user.getId());
//            adminLog.setAction("RSVP_EVENT");
//            adminLog.setTargetEntity("Event: " + event.getName());
//            adminLog.setTimestamp(LocalDateTime.now());
//            adminLogRepository.save(adminLog);
//        }
//    }
//
//    // Search events based on criteria
//    public List<Event> searchEvents(String date, String category) {
//        if (date != null && category != null) {
//            // Convert date string to LocalDateTime
//            LocalDateTime eventDate = LocalDateTime.parse(date);
//
//            // Log the search in admin_log
//            AdminLog adminLog = new AdminLog();
//            adminLog.setAction("SEARCH_EVENTS_BY_DATE_AND_CATEGORY");
//            adminLog.setTargetEntity("Date: " + eventDate + ", Category: " + category);
//            adminLog.setTimestamp(LocalDateTime.now());
//            adminLogRepository.save(adminLog);
//
//            return eventRepository.findByEventDateAndCategory(eventDate, category);
//        } else if (date != null) {
//            LocalDateTime eventDate = LocalDateTime.parse(date);
//            return eventRepository.findByEventDate(eventDate);
//        } else if (category != null) {
//            return eventRepository.findByCategory(category);
//        }
//        return eventRepository.findAll();
//    }
//}

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
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;



import java.time.LocalDateTime;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventLogRepository eventLogRepository;

    @Autowired
    private AdminLogRepository adminLogRepository;

    private int maxAttendees;

    private int currentAttendees = 0;

    @Autowired
    private Redis redis;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Create a new event
    public Event createEvent(Event event, User user) {
        event.setHost(user);

        // If user is admin, set created_by_admin_id
        if ("ADMIN".equals(user.getRole())) {
            event.setCreatedByAdminId(user.getId()); // Set the admin who created the event
        }


        // Ensure maxAttendees from the request is preserved
        event.setMaxAttendees(event.getMaxAttendees());
        event.setCurrentAttendees(0); // ✅ Set initial attendees to 0

        Event createdEvent = eventRepository.save(event);

        // Log the creation action in event_log
        EventLog eventLog = new EventLog();
        eventLog.setAdminId("ADMIN".equals(user.getRole()) ? user.getId() : null); // Admin ID only if admin
        eventLog.setAction("CREATE_EVENT");
        eventLog.setEventId(createdEvent.getId());
        eventLog.setTimestamp(LocalDateTime.now());
        eventLogRepository.save(eventLog);

        // Log the creation action in admin_log
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

    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    // Update an existing event
    public Event updateEvent(Long id, Event eventDetails, User user) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
        event.setName(eventDetails.getName());
        event.setDescription(eventDetails.getDescription());
        event.setEventDate(eventDetails.getEventDate());
        event.setLocation(eventDetails.getLocation());
        event.setCategory(event.getCategory());

        // Update maxAttendees if provided in the request
        if (eventDetails.getMaxAttendees() > 0) {
            event.setMaxAttendees(eventDetails.getMaxAttendees());
        }

        if ("ADMIN".equals(user.getRole())) {
            event.setCreatedByAdminId(user.getId());
        }

        Event updatedEvent = eventRepository.save(event);

        // Log the update action in event_log
        EventLog eventLog = new EventLog();
        eventLog.setAdminId("ADMIN".equals(user.getRole()) ? user.getId() : null); // Admin ID only if admin
        eventLog.setAction("UPDATE_EVENT");
        eventLog.setEventId(updatedEvent.getId());
        eventLog.setTimestamp(LocalDateTime.now());
        eventLogRepository.save(eventLog);

        // Log the update action in admin_log
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

    // Delete an event
    public void deleteEvent(Long id, User user) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
        eventRepository.deleteById(id);

        // Log the deletion action in event_log
        EventLog eventLog = new EventLog();
        eventLog.setAdminId("ADMIN".equals(user.getRole()) ? user.getId() : null); // Admin ID only if admin
        eventLog.setAction("DELETE_EVENT");
        eventLog.setEventId(event.getId());
        eventLog.setTimestamp(LocalDateTime.now());
        eventLogRepository.save(eventLog);

        // Log the deletion action in admin_log
        if ("ADMIN".equals(user.getRole())) {
            AdminLog adminLog = new AdminLog();
            adminLog.setAdminId(user.getId());
            adminLog.setAction("DELETE_EVENT");
            adminLog.setTargetEntity("Event: " + event.getName());
            adminLog.setTimestamp(LocalDateTime.now());
            adminLogRepository.save(adminLog);
        }
    }

    // Find events by host
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


    // Find events created by admin
    public List<Event> findEventsByAdmin(Long adminId) {
        String key = "event:admin:" + adminId;

        try {
            String cachedJson = redis.get(key);
            if (cachedJson != null) {
                System.out.println("✅ [CACHE] Returning events by admin from Redis");
                return objectMapper.readValue(cachedJson, new TypeReference<List<Event>>() {});
            }
        } catch (Exception e) {
            System.err.println("❌ Redis error (findEventsByAdmin): " + e.getMessage());
        }

        List<Event> events = eventRepository.findByCreatedByAdminId(adminId);

        try {
            redis.set(key, objectMapper.writeValueAsString(events), Duration.ofMinutes(10));
        } catch (Exception e) {
            System.err.println("❌ Redis write error (findEventsByAdmin): " + e.getMessage());
        }

        return events;
    }

    // Find event by ID
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


    // RSVP to an event
    public void rsvpToEvent(Long eventId, User user) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));
        event.getRsvps().add(user);
        eventRepository.save(event);
        if (event.getCurrentAttendees() >= event.getMaxAttendees()) {
            throw new IllegalStateException("Event is full.");
        }

        event.getRsvps().add(user);
        event.setCurrentAttendees(event.getCurrentAttendees() + 1);
        eventRepository.save(event);


        // Log the RSVP action in event_log
        EventLog eventLog = new EventLog();
        eventLog.setAdminId("ADMIN".equals(user.getRole()) ? user.getId() : null); // Admin ID only if admin
        eventLog.setAction("RSVP_EVENT");
        eventLog.setEventId(event.getId());
        eventLog.setTimestamp(LocalDateTime.now());
        eventLogRepository.save(eventLog);

        // Log the RSVP action in admin_log
        if ("ADMIN".equals(user.getRole())) {
            AdminLog adminLog = new AdminLog();
            adminLog.setAdminId(user.getId());
            adminLog.setAction("RSVP_EVENT");
            adminLog.setTargetEntity("Event: " + event.getName());
            adminLog.setTimestamp(LocalDateTime.now());
            adminLogRepository.save(adminLog);
        }
    }

    // Search events based on criteria
    public List<Event> searchEvents(String date, String category) {
        if (date != null && category != null) {
            LocalDateTime eventDate = LocalDateTime.parse(date);

            // Log the search in admin_log
            AdminLog adminLog = new AdminLog();
            adminLog.setAction("SEARCH_EVENTS_BY_DATE_AND_CATEGORY");
            adminLog.setTargetEntity("Date: " + eventDate + ", Category: " + category);
            adminLog.setTimestamp(LocalDateTime.now());
            adminLogRepository.save(adminLog);

            return eventRepository.findByEventDateAndCategory(eventDate, category);
        } else if (date != null) {
            LocalDateTime eventDate = LocalDateTime.parse(date);
            return eventRepository.findByEventDate(eventDate);
        } else if (category != null) {
            return eventRepository.findByCategory(category);
        }
        return eventRepository.findAll();
    }

    public String getEventNameById(Long eventId) {
        String key = "event:name:" + eventId;

        try {
            String cachedName = redis.get(key);
            if (cachedName != null) {
                System.out.println("✅ [CACHE] Returning Event name from Redis");
                return cachedName;
            }
        } catch (Exception e) {
            System.err.println("❌ Redis read error: " + e.getMessage());
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        String name = event.getName();

        try {
            redis.set(key, name, Duration.ofMinutes(10));
        } catch (Exception e) {
            System.err.println("❌ Redis write error: " + e.getMessage());
        }

        return name;
    }

    public List<Map<String, Object>> getAllEventsWithAttendeeDetails() {
        List<Event> events = eventRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Event event : events) {
            Map<String, Object> eventMap = new HashMap<>();
            eventMap.put("eventId", event.getId());
            eventMap.put("eventName", event.getName());
            eventMap.put("location", event.getLocation());
            eventMap.put("eventDate", event.getEventDate());
            eventMap.put("category", event.getCategory());
            eventMap.put("maxAttendees", event.getMaxAttendees());
            eventMap.put("currentAttendees", event.getCurrentAttendees());

            // Only extract non-sensitive fields from each User
            List<Map<String, Object>> attendeesList = new ArrayList<>();
            for (User user : event.getRsvps()) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", user.getId());
                userMap.put("email", user.getEmail());
                userMap.put("username", user.getUsername());
                userMap.put("role", user.getRole());
                attendeesList.add(userMap);
            }

            eventMap.put("attendees", attendeesList);
            result.add(eventMap);
        }

        return result;
    }


    public List<Map<String, Object>> getHostEventsWithAttendees(Long hostId) {
        List<Event> events = eventRepository.findByHost_Id(hostId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Event event : events) {
            Map<String, Object> eventMap = new HashMap<>();
            eventMap.put("eventId", event.getId());
            eventMap.put("eventName", event.getName());
            eventMap.put("location", event.getLocation());
            eventMap.put("eventDate", event.getEventDate());
            eventMap.put("category", event.getCategory());
            eventMap.put("maxAttendees", event.getMaxAttendees());
            eventMap.put("currentAttendees", event.getCurrentAttendees());

            List<Map<String, Object>> attendeesList = new ArrayList<>();
            for (User user : event.getRsvps()) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", user.getId());
                userMap.put("username", user.getUsername());
                userMap.put("email", user.getEmail());
                userMap.put("role", user.getRole());
                attendeesList.add(userMap);
            }

            eventMap.put("attendees", attendeesList);
            result.add(eventMap);
        }

        return result;
    }

    public List<Event> findUpcomingEvents() {
        LocalDateTime now = LocalDateTime.now();
        return eventRepository.findByEventDateAfter(now);
    }

    public void cancelRSVP(Long eventId, User user) {
        Event event = findEventById(eventId);

        // Remove by matching ID instead of the whole user object (to avoid equals/hashCode issues)
        boolean removed = event.getRsvps().removeIf(u -> u.getId().equals(user.getId()));
        if (!removed) {
            throw new IllegalStateException("User is not RSVP'd to this event.");
        }

        // Safely decrement current attendees
        int current = event.getCurrentAttendees();
        event.setCurrentAttendees(Math.max(0, current - 1));

        eventRepository.save(event);


    }
    public List<Event> findEventsByRsvpUserId(Long userId) {
        return eventRepository.findEventsByRsvpUserId(userId);
    }








}



