package com.example.hena.event.controller;

import com.example.hena.event.entity.Event;
import com.example.hena.event.service.EventService;
import com.example.hena.user.entity.User;
import com.example.hena.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/event")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    // Create a new event (only for Host or Admin)
    @PostMapping("/create")
    public Event createEvent(@RequestBody Event event, Principal principal) {
        User adminUser = userService.getUserByUsername(principal.getName());
        event.setHost(adminUser);  // Host of the event
        return eventService.createEvent(event, adminUser);
    }

    // Update an event (only for Host or Admin)
    @PutMapping("/update/{id}")
    public Event updateEvent(@PathVariable Long id, @RequestBody Event event, Principal principal) {
        User adminUser = userService.getUserByUsername(principal.getName());
        return eventService.updateEvent(id, event, adminUser);
    }

    // Delete an event (only for Host or Admin)
    @DeleteMapping("/delete/{id}")
    public void deleteEvent(@PathVariable Long id, Principal principal) {
        User adminUser = userService.getUserByUsername(principal.getName());
        eventService.deleteEvent(id, adminUser);
    }

    // View events for a specific host (Host role only)
    @GetMapping("/host")
    public List<Event> getHostEvents(Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        return eventService.findEventsByHost(user);
    }

    // View all events created by an admin
    @GetMapping("/admin")
    public List<Event> getAdminEvents(Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        return eventService.findEventsByAdmin(user.getId());
    }

    // View a specific event by ID
    @GetMapping("/{id}")
    public Event getEvent(@PathVariable Long id) {
        return eventService.findEventById(id);
    }

    // RSVP to an event (only for registered users)
    @PostMapping("/rsvp/{eventId}")
    public String rsvpToEvent(@PathVariable Long eventId, Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        // Fetch the event by ID so we can access its name
        Event event = eventService.findEventById(eventId);
        eventService.rsvpToEvent(eventId, user);
        return "responded successfully to attend: "  + event.getName();
    }

    // View events based on search parameters (date or category)
    @GetMapping("/search")
    public List<Event> searchEvents(@RequestParam(required = false) String date,
                                    @RequestParam(required = false) String category) {
        return eventService.searchEvents(date, category);
    }
}
