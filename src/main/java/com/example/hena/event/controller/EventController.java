package com.example.hena.event.controller;

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
import java.util.Map;

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
    @PreAuthorize("hasRole('HOST') or hasRole('ADMIN')")
    @PostMapping("/create/{adminOrHostId}")
    @RateLimit(limit = 1, duration = 10, keyPrefix = "createEvent")
    public Event createEvent(@PathVariable Long adminOrHostId, @RequestBody Event event, Principal principal) {
        User adminUser = userService.getUserById(adminOrHostId);  // Get user by ID (Host/Admin)
        event.setHost(adminUser);  // Host of the event
        return eventService.createEvent(event, adminUser);
    }



    // Update an event (only for Host or Admin)
    @PreAuthorize("hasRole('HOST') or hasRole('ADMIN')")
    @PutMapping("/update/{adminOrHostId}/{id}")
    @RateLimit(limit = 1, duration = 10, keyPrefix = "updateEvent")
    public Event updateEvent(@PathVariable Long adminOrHostId, @PathVariable Long id, @RequestBody Event event, Principal principal) {
        User adminUser =userService.getUserById(adminOrHostId);  // Get user by ID (Host/Admin)
        return eventService.updateEvent(id, event, adminUser);
    }




    // Delete an event (only for Host or Admin)
    @PreAuthorize("hasRole('HOST') or hasRole('ADMIN')")
    @DeleteMapping("/delete/{adminOrHostId}/{id}")
    public void deleteEvent(@PathVariable Long adminOrHostId, @PathVariable Long id, Principal principal) {
        User adminUser = userService.getUserById(adminOrHostId);
        eventService.deleteEvent(id, adminUser);
    }


    // View events for a specific host (Host and admin role only)
    @PreAuthorize("hasRole('HOST') or hasRole('ADMIN')")
    @GetMapping("/host/{hostId}")
    public ResponseEntity<?> getHostEvents(@PathVariable Long hostId, Principal principal) {
        // Get the current logged-in user
        User currentUser = userService.getUserById(hostId);  // Fetch logged-in user

        // First, check if the current user has proper role (HOST or ADMIN) for making the request
        if (!"HOST".equals(currentUser.getRole()) && !"ADMIN".equals(currentUser.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Only hosts or admins can view events.");
        }

        // Now check if the passed hostId corresponds to a HOST or ADMIN
        User hostUser = userService.getUserById(hostId);
        if (hostUser == null || "USER".equals(hostUser.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: Regular users do not create events so they do not have event list.");
        }

        // Fetch the events for the given host
        List<Event> events = eventService.findEventsByHost(hostUser);
        return ResponseEntity.ok(events);
    }




// View all events created by an admin
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/admin/{adminId}")
public ResponseEntity<?> getAdminEvents(@PathVariable Long adminId, Principal principal) {
    // Get the user by their ID from the database
    User user = userService.getUserById(adminId);

    // Check if the user exists and if they have the 'ADMIN' role
    if (user == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin with ID " + adminId + " not found.");
    }

    if (!"ADMIN".equals(user.getRole())) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not an admin: Only admin users can view events created by admins.");
    }

    // If the user is an admin, fetch the events created by this admin
    List<Event> events = eventService.findEventsByAdmin(adminId);
    return ResponseEntity.ok(events);
}



    // View a specific event by ID
    @GetMapping("/{id}")
    public Event getEvent(@PathVariable Long id) {
        return eventService.findEventById(id);
    }


//    add later an option for the admin to register a specific user to respond to an event
    // RSVP to an event (only for registered users)
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/rsvp/{userId}/{eventId}")
    public String rsvpToEvent(@PathVariable Long userId, @PathVariable Long eventId, Principal principal) {
        User user =  userService.getUserById(userId);  // Get user by their ID
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/events-with-attendee-details/{adminId}")
    public ResponseEntity<?> getAllEventsWithAttendeeDetails(@PathVariable Long adminId) {
        User adminUser = userService.getUserById(adminId);

        if (adminUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found");
        }

        if (!"ADMIN".equals(adminUser.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Only admins can perform this action.");
        }

        List<Map<String, Object>> events = eventService.getAllEventsWithAttendeeDetails();
        return ResponseEntity.ok(events);
    }

//    @PreAuthorize("hasRole('HOST')")
//    @GetMapping("/host/events-with-attendee-details/{hostId}")
//    public ResponseEntity<?> getHostEventsWithAttendeeDetails(@PathVariable Long hostId) {
//        User hostUser = userService.getUserById(hostId);
//
//        if (hostUser == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Host not found");
//        }
//
//        if (!"HOST".equals(hostUser.getRole())) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Only hosts can perform this action.");
//        }
//
//        List<Map<String, Object>> events = eventService.getHostEventsWithAttendees(hostId);
//        return ResponseEntity.ok(events);
//    }





}
