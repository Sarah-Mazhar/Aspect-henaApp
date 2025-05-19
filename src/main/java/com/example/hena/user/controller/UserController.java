package com.example.hena.user.controller;

import com.example.hena.event.entity.Event;
import com.example.hena.event.service.EventService;
import com.example.hena.redis.annotations.DistributedLock;
import com.example.hena.redis.annotations.RateLimit;
import com.example.hena.security.JwtUtil;
import com.example.hena.user.dto.CreateUserDTO;
import com.example.hena.user.dto.LoginDTO;
import com.example.hena.user.dto.UpdateUserDTO;
import com.example.hena.user.entity.User;
import com.example.hena.user.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller that handles user operations such as registration, login,
 * profile update, RSVP actions, and user lookup.
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    // ============================
    //  Registration
    // ============================

    /**
     * Register a new user with username, email, password, and role.
     */
    @PostMapping("/register")
    public User registerUser(@RequestBody CreateUserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword()); // encode password
        user.setRole(userDTO.getRole());
        return userService.createUser(user);
    }

    // ============================
    //  Login with JWT Token
    // ============================

    /**
     * Authenticates a user and returns a JWT token along with user ID and role.
     */
    @PostMapping("/login")
    // @RateLimit(limit = 1, duration = 10, keyPrefix = "login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        User user = userService.getUserByUsername(loginDTO.getUsername());

        if (user == null
                || !userService.checkPassword(loginDTO.getPassword(), user.getPassword())
                || (loginDTO.getRole() != null && !loginDTO.getRole().equals(user.getRole()))) {
            return ResponseEntity.status(401).body("Invalid login credentials");
        }

        String token = JwtUtil.generateToken(user.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getId());
        response.put("role", user.getRole());

        return ResponseEntity.ok(response);
    }

    // ============================
    //  Update Profile
    // ============================

    /**
     * Allows a user to update their own profile (username, email).
     */
    @PutMapping("/update/{id}")
    @RateLimit(limit = 1, duration = 10, keyPrefix = "login")
    public ResponseEntity<User> updateUser(@PathVariable("id") Long id, @RequestBody UpdateUserDTO userDTO) {
        User userDetails = new User();
        userDetails.setUsername(userDTO.getUsername());
        userDetails.setEmail(userDTO.getEmail());

        User updatedUser = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    // ============================
    //  RSVP to Event
    // ============================

    /**
     * Allows a user to RSVP to an event.
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/rsvp/{userId}/{eventId}")
    @DistributedLock(keyPrefix = "rsvp", keyIdentifierExpression = "#eventId", leaseTime = 10)
    public String rsvpToEvent(@PathVariable("userId") Long userId,
                              @PathVariable("eventId") Long eventId,
                              Principal principal) {
        User user = userService.getUserById(userId);
        Event event = eventService.findEventById(eventId);
        userService.rsvpToEvent(eventId, userId);
        return "responded successfully to attend: " + event.getName();
    }

    /**
     * Allows a user to cancel their RSVP for an event.
     */
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(
            value = "/rsvp/{userId}/{eventId}",
            method = RequestMethod.DELETE,
            consumes = MediaType.ALL_VALUE
    )
    @DistributedLock(keyPrefix = "rsvp-cancel", keyIdentifierExpression = "#eventId", leaseTime = 10)
    public String cancelRSVP(@PathVariable("userId") Long userId,
                             @PathVariable("eventId") Long eventId,
                             Principal principal) {
        User user = userService.getUserById(userId);
        Event event = eventService.findEventById(eventId);
        eventService.cancelRSVP(eventId, user);
        return "Successfully cancelled RSVP for: " + event.getName();
    }

    // ============================
    //  Utility & Debug Endpoints
    // ============================

    /**
     * Test endpoint to trigger logging logic.
     */
    @GetMapping("/test-log")
    public String testLog() {
        return userService.testLoggingAspect("Omar");
    }

    /**
     * Retrieve a user by their ID. Accessible to all authenticated roles.
     */
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'HOST')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return ResponseEntity.ok(user);
    }

}
