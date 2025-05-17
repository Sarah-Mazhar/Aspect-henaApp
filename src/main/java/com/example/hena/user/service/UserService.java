package com.example.hena.user.service;

import com.example.hena.event.entity.Event;
import com.example.hena.event.service.EventService;
import com.example.hena.notification.service.NotificationService;
import com.example.hena.user.entity.User;
import com.example.hena.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.hena.redis.service.Redis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Primary
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Redis redis;

    @Autowired
    private EventService eventService;

    @Autowired
    private NotificationService notificationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public User createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateUser(Long userId, User userDetails) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (userDetails.getUsername() != null) {
            user.setUsername(userDetails.getUsername());
        }
        if (userDetails.getEmail() != null) {
            user.setEmail(userDetails.getEmail());
        }
        if (userDetails.getRole() != null) {
            user.setRole(userDetails.getRole());
        }

        return userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        String key = "user:username:" + username;
        try {
            String cached = redis.get(key);
            if (cached != null) {
                return objectMapper.readValue(cached, User.class);
            }
        } catch (Exception e) {
            System.err.println("❌ Redis error (getUserByUsername): " + e.getMessage());
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            redis.set(key, objectMapper.writeValueAsString(user), Duration.ofMinutes(10));
        } catch (Exception e) {
            System.err.println("❌ Redis set error (getUserByUsername): " + e.getMessage());
        }

        return user;
    }

    public User getUserById(Long id) {
        String key = "user:id:" + id;
        try {
            String cached = redis.get(key);
            if (cached != null) {
                return objectMapper.readValue(cached, User.class);
            }
        } catch (Exception e) {
            System.err.println("❌ Redis error (getUserById): " + e.getMessage());
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID " + id));

        try {
            redis.set(key, objectMapper.writeValueAsString(user), Duration.ofMinutes(10));
        } catch (Exception e) {
            System.err.println("❌ Redis set error (getUserById): " + e.getMessage());
        }

        return user;
    }

    public User findByEmail(String email) {
        String key = "user:email:" + email;
        try {
            String cached = redis.get(key);
            if (cached != null) {
                return objectMapper.readValue(cached, User.class);
            }
        } catch (Exception e) {
            System.err.println("❌ Redis error (findByEmail): " + e.getMessage());
        }

        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null) {
            try {
                redis.set(key, objectMapper.writeValueAsString(user), Duration.ofMinutes(10));
            } catch (Exception e) {
                System.err.println("❌ Redis set error (findByEmail): " + e.getMessage());
            }
        }

        return user;
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public String rsvpToEvent(Long eventId, Long userId) {
        User user = getUserById(userId);
        Event event = eventService.findEventById(eventId);

        if (event.getRsvps().stream().anyMatch(u -> u.getId().equals(userId))) {
            throw new IllegalStateException("Already RSVP’d to this event.");
        }

        if (event.getCurrentAttendees() >= event.getMaxAttendees()) {
            throw new IllegalStateException("Event is full.");
        }

        event.getRsvps().add(user);
        event.setCurrentAttendees(event.getCurrentAttendees() + 1);
        eventService.saveEvent(event);

        notificationService.notifyUserRSVP(event, user);
        return "RSVP successful for event: " + event.getName();
    }

    public String cancelRSVP(Long eventId, Long userId) {
        User user = getUserById(userId);
        Event event = eventService.findEventById(eventId);

        boolean removed = event.getRsvps().removeIf(u -> u.getId().equals(userId));
        if (!removed) {
            throw new IllegalStateException("You are not RSVP’d to this event.");
        }

        event.setCurrentAttendees(Math.max(0, event.getCurrentAttendees() - 1));
        eventService.saveEvent(event);

        return "Canceled RSVP for event: " + event.getName();
    }


}
