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

import java.time.LocalDateTime;
import java.util.List;

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
    public List<Event> findEventsByHost(User host) {
        return eventRepository.findByHost_Id(host.getId());
    }

    // Find events created by admin
    public List<Event> findEventsByAdmin(Long adminId) {
        return eventRepository.findByCreatedByAdminId(adminId);
    }

    // Find event by ID
    public Event findEventById(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
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
        // Fetch event by id from repository
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        return event.getName(); // or getEventName() based on your Event entity
    }

}

