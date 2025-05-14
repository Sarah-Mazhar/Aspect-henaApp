
package com.example.hena.user.controller;

import com.example.hena.user.dto.CreateUserDTO;
import com.example.hena.user.dto.LoginDTO;
import com.example.hena.user.dto.UpdateUserDTO;
import com.example.hena.user.entity.User;
import com.example.hena.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.hena.security.JwtUtil;

import java.security.Principal;


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
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        User user = userService.findByEmail(loginDTO.getEmail());
        if (user == null
                || !userService.checkPassword(loginDTO.getPassword(), user.getPassword())
                || (loginDTO.getUsername() != null && !loginDTO.getUsername().equals(user.getUsername()))
                || (loginDTO.getRole() != null && !loginDTO.getRole().equals(user.getRole()))) {
            return ResponseEntity.status(401).body("Invalid login credentials");
        }
        String token = JwtUtil.generateToken(user.getEmail());
        return ResponseEntity.ok(token);
    }


    // Update personal profile info (only for the current user)
//    @PathVariable entirely and extract user identity from the token (
//    which is mapped to the Principal automatically by Spring Security).
    @PutMapping("/update")
    public User updateUser(@RequestBody UpdateUserDTO userDTO, Principal principal) {
        User currentUser = userService.findByEmail(principal.getName());
        User updated = new User();
        updated.setUsername(userDTO.getUsername());
        updated.setEmail(userDTO.getEmail());
        updated.setRole(userDTO.getRole()); // only editable if currentUser is admin
        return userService.updateUser(currentUser.getId(), updated);
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
