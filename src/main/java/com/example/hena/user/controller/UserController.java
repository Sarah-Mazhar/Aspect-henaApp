
package com.example.hena.user.controller;

import com.example.hena.user.dto.CreateUserDTO;
import com.example.hena.user.dto.UpdateUserDTO;
import com.example.hena.user.entity.User;
import com.example.hena.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
//    A User role can search and RSVP to events, but won't have permissions to create or edit them.
//    Defines REST endpoints for registration, update, RSVP, and event search.

    @Autowired
    private UserService userService;

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

    // Update personal profile info (only for the current user)
    @PutMapping("/update/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody UpdateUserDTO userDTO) {
        User updated = new User();
        updated.setUsername(userDTO.getUsername());
        updated.setEmail(userDTO.getEmail());
        updated.setRole(userDTO.getRole()); // Only editable by Admin in practice
        return userService.updateUser(id, updated);
    }

    // Placeholder endpoint: RSVP to an event
    @PostMapping("/rsvp/{eventId}")
    public String rsvpToEvent(@PathVariable Long eventId) {
        // You will replace this with real event RSVP logic later
        return "User RSVP'd to event ID: " + eventId;
    }

    // Placeholder endpoint: Search for events
    @GetMapping("/search")
    public String searchEvents(@RequestParam(required = false) String date,
                               @RequestParam(required = false) String category) {
        // You will integrate with EventService for real search logic
        return "Searched for events on date: " + date + ", category: " + category;
    }
}
