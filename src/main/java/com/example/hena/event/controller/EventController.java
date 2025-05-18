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

    import java.security.Principal;
    import java.util.List;
    import java.util.Map;

    @RestController
    @RequestMapping("/api/event")
    public class EventController {

        @Autowired
        private EventService eventService;

        @Autowired
        private UserService userService;

        // ✅ Create a new event (only for Host or Admin)
        @PreAuthorize("hasRole('HOST') or hasRole('ADMIN')")
        @PostMapping("/create/{adminOrHostId}")
        @RateLimit(limit = 1, duration = 10, keyPrefix = "createEvent")
        public Event createEvent(@PathVariable Long adminOrHostId, @RequestBody Event event) {
            User user = userService.getUserById(adminOrHostId);
            event.setHost(user);
            return eventService.createEvent(event, user);
        }

        // ✅ Get events created by a specific host (or admin acting as host)
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

        // ✅ Update an event (Host or Admin)
        @PreAuthorize("hasRole('HOST') or hasRole('ADMIN')")
        @PutMapping("/update/{adminOrHostId}/{id}")
        @RateLimit(limit = 1, duration = 10, keyPrefix = "updateEvent")
        public Event updateEvent(@PathVariable Long adminOrHostId, @PathVariable Long id, @RequestBody Event event) {
            User user = userService.getUserById(adminOrHostId);
            return eventService.updateEvent(id, event, user);
        }

        // ✅ Delete an event (Host or Admin)
        @PreAuthorize("hasRole('HOST') or hasRole('ADMIN')")
        @DeleteMapping("/delete/{adminOrHostId}/{id}")
        public void deleteEvent(@PathVariable Long adminOrHostId, @PathVariable Long id) {
            User user = userService.getUserById(adminOrHostId);
            eventService.deleteEvent(id, user);
        }

        // ✅ Admin can view all events (regardless of creator)
        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping("/all")
        public ResponseEntity<?> getAllEventsForAdmin() {
            List<Event> events = eventService.getAllEvents();
            return ResponseEntity.ok(events);
        }


        // ✅ View all events created by a specific admin
        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping("/admin/{adminId}")
        public ResponseEntity<?> getAdminEvents(@PathVariable Long adminId) {
            User admin = userService.getUserById(adminId);

            if (admin == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found.");
            }

            if (!"ADMIN".equals(admin.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Access denied: Only admins can view admin-created events.");
            }

            List<Event> events = eventService.findEventsByAdmin(adminId);
            return ResponseEntity.ok(events);
        }

        // ✅ Get a specific event by ID
        @GetMapping("/{id}")
        public Event getEvent(@PathVariable Long id) {
            return eventService.findEventById(id);
        }

        // ✅ RSVP to an event (User only)
        @PreAuthorize("hasRole('USER')")
        @PostMapping("/rsvp/{userId}/{eventId}")
        public String rsvpToEvent(@PathVariable Long userId, @PathVariable Long eventId) {
            User user = userService.getUserById(userId);
            Event event = eventService.findEventById(eventId);
            eventService.rsvpToEvent(eventId, user);
            return "Responded successfully to attend: " + event.getName();
        }

        // ✅ Search events by date and/or category
        @GetMapping("/search")
        public List<Event> searchEvents(@RequestParam(required = false) String date,
                                        @RequestParam(required = false) String category) {
            return eventService.searchEvents(date, category);
        }

        // ✅ Admin can view all events with attendee details
        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping("/admin/events-with-attendee-details/{adminId}")
        public ResponseEntity<?> getAllEventsWithAttendeeDetails(@PathVariable Long adminId) {
            User adminUser = userService.getUserById(adminId);

            if (adminUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found");
            }

            if (!"ADMIN".equals(adminUser.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Access denied: Only admins can perform this action.");
            }

            List<Map<String, Object>> events = eventService.getAllEventsWithAttendeeDetails();
            return ResponseEntity.ok(events);
        }

        @GetMapping("/upcoming")
        public List<Event> getUpcomingEvents() {
            return eventService.findUpcomingEvents();
        }



        // 🔒 Optional: Enable this when host wants to view attendee details
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
    //            return ResponseEntity.status(HttpStatus.FORBIDDEN)
    //                    .body("Access denied: Only hosts can perform this action.");
    //        }
    //
    //        List<Map<String, Object>> events = eventService.getHostEventsWithAttendees(hostId);
    //        return ResponseEntity.ok(events);
    //    }
    }
