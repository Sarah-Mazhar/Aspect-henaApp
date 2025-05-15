package com.example.hena.user.service;

import com.example.hena.user.entity.User;
import com.example.hena.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.hena.redis.service.Redis;
import java.time.Duration;



@Service
@Primary
public class UserService {
//    Contains the business logic for user creation and update.

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;  // ✅ Inject the PasswordEncoder

    @Autowired
    private Redis redis;

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
        System.out.println("Updating user with id: " + userId);
        System.out.println("New username: " + userDetails.getUsername());
        System.out.println("New email: " + userDetails.getEmail());
        System.out.println("New role: " + userDetails.getRole());


        if (userDetails.getUsername() != null) {
            user.setUsername(userDetails.getUsername());
        }
        if (userDetails.getEmail() != null) {
            user.setEmail(userDetails.getEmail());
        }
        if (userDetails.getRole() != null) {
            user.setRole(userDetails.getRole());
        }
//        user.setUsername(userDetails.getUsername());
//        user.setEmail(userDetails.getEmail());
//        user.setRole(userDetails.getRole());
        return userRepository.save(user);
    }


    public User getUserByUsername(String username) {
        String key = "user:username:" + username;
        try {
            String cached = redis.get(key);
            if (cached != null) {
                System.out.println("✅ [CACHE] Returning user by username from Redis");
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



    // method to fetch user by ID
    public User getUserById(Long id) {
        String key = "user:id:" + id;
        try {
            String cached = redis.get(key);
            if (cached != null) {
                System.out.println("✅ [CACHE] Returning user by ID from Redis");
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
                System.out.println("✅ [CACHE] Returning user by email from Redis");
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


//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
//
//        return org.springframework.security.core.userdetails.User
//                .withUsername(user.getEmail())
//                .password(user.getPassword())
//                .authorities("ROLE_" + user.getRole())
//                .build();
//    }





}
