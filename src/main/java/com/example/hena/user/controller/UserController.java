
package com.example.hena.user.controller;

import com.example.hena.event.entity.Event;
import com.example.hena.event.service.EventService;
import com.example.hena.redis.annotations.DistributedLock;
import com.example.hena.redis.annotations.RateLimit;
import com.example.hena.user.dto.CreateUserDTO;
import com.example.hena.user.dto.LoginDTO;
import com.example.hena.user.dto.UpdateUserDTO;
import com.example.hena.user.entity.User;
import com.example.hena.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.hena.security.JwtUtil;
import java.util.HashMap;
import java.util.Map;

import java.security.Principal;


@RestController
@RequestMapping("/api/user")
public class UserController {
//    A User role can search and RSVP to events, but won't have permissions to create or edit them.
//    Defines REST endpoints for registration, update, RSVP, and event search.

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;  // Inject EventService



    // Register a new user
    @PostMapping("/register")
    public User registerUser(@RequestBody CreateUserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword()); // Password should be encoded in real logic
        user.setRole(userDTO.getRole());
        return userService.createUser(user);
    }

//    // Login endpoint to generate JWT token
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
//        User user = userService.findByEmail(loginDTO.getEmail());
//        if (user == null || !userService.checkPassword(loginDTO.getPassword(), user.getPassword())) {
//            return ResponseEntity.status(401).body("Invalid email or password");
//        }
//        String token = JwtUtil.generateToken(user.getEmail());
//        return ResponseEntity.ok(token);
//    }

    @PostMapping("/login")
//    @RateLimit(limit = 1, duration = 10, keyPrefix = "login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        User user = userService.getUserByUsername(loginDTO.getUsername());

        if (user == null
                || !userService.checkPassword(loginDTO.getPassword(), user.getPassword())
                || (loginDTO.getRole() != null && !loginDTO.getRole().equals(user.getRole()))) {
            return ResponseEntity.status(401).body("Invalid login credentials");
        }

        String token = JwtUtil.generateToken(user.getUsername());

        // ✅ Return token + userId + role to frontend
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getId());
        response.put("role", user.getRole());

        return ResponseEntity.ok(response);
    }


    // Update personal profile info (only for the current user)
//    @PathVariable entirely and extract user identity from the token (
//    which is mapped to the Principal automatically by Spring Security).
    @PutMapping("/update/{id}")
    @RateLimit(limit = 1, duration = 10, keyPrefix = "login")
    public ResponseEntity<User> updateUser(@PathVariable("id") Long id, @RequestBody UpdateUserDTO userDTO) {
        // Create a new User object and set only username and email from DTO
        User userDetails = new User();
        userDetails.setUsername(userDTO.getUsername());
        userDetails.setEmail(userDTO.getEmail());

        // Do NOT set role here, so role remains unchanged in update service method

        User updatedUser = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(updatedUser);
    }







    // Placeholder endpoint: RSVP to an event
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/rsvp/{userId}/{eventId}")
    @DistributedLock(keyPrefix = "rsvp", keyIdentifierExpression = "#eventId", leaseTime = 10)
    public String rsvpToEvent(@PathVariable("userId") Long userId,
                              @PathVariable("eventId") Long eventId,
                              Principal principal) {
        User user =  userService.getUserById(userId);  // Get user by their ID
        // Fetch the event by ID so we can access its name
        Event event = eventService.findEventById(eventId);
        eventService.rsvpToEvent(eventId, user);
        return "responded successfully to attend: "  + event.getName();
    }
//    @PostMapping("/rsvp/{eventId}")
//    public ResponseEntity<String> rsvpToEvent(@PathVariable Long eventId, Principal principal) {
//        // Get logged-in user's email from security context
//        String loggedInUserEmail = principal.getName();
//
//        // Fetch user entity by email
//        User user = userService.findByEmail(loggedInUserEmail);
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
//        }
//
//        try {
//            // Call EventService's RSVP method with user entity
//            eventService.rsvpToEvent(eventId, user);
//
//            // Fetch event name for response message
//            String eventName = eventService.getEventNameById(eventId);
//
//            String response = "User '" + user.getUsername() + "' RSVP'd to event '" + eventName + "'";
//            return ResponseEntity.ok(response);
//        } catch (IllegalStateException e) {
//            // Handle case where event is full
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        } catch (RuntimeException e) {
//            // Handle event not found or other errors
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//        }
//    }


    // Placeholder endpoint: Search for events
    @GetMapping("/search")
    public String searchEvents(@RequestParam(required = false) String date,
                               @RequestParam(required = false) String category) {
        // You will integrate with EventService for real search logic
        return "Searched for events on date: " + date + ", category: " + category;
    }
}
