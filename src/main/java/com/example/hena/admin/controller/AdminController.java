package com.example.hena.admin.controller;

import com.example.hena.admin.entity.AdminLog;
import com.example.hena.admin.repository.AdminLogRepository;
import com.example.hena.user.dto.CreateUserDTO;
import com.example.hena.user.entity.User;
import com.example.hena.user.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * REST controller for admin-specific actions, including user creation.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private AdminLogRepository adminLogRepository;

    /**
     * Allows an admin to create a new user and logs the action.
     *
     * @param adminId ID of the admin performing the action (path variable)
     * @param userDTO Data Transfer Object containing new user details (request body)
     * @return The created User object
     */
    @PreAuthorize("hasRole('ADMIN')") // Ensures only users with ADMIN role can access this endpoint
    @PostMapping("/createUser/{adminId}")
    public User createUser(@PathVariable Long adminId, @RequestBody CreateUserDTO userDTO) {
        // Retrieve the admin user by ID
        User adminUser = userService.getUserById(adminId);

        // Create a new User instance and populate it from the DTO
        User newUser = new User();
        newUser.setUsername(userDTO.getUsername());
        newUser.setEmail(userDTO.getEmail());
        newUser.setPassword(userDTO.getPassword());
        newUser.setRole(userDTO.getRole());
        newUser.setCreatedByAdminId(adminUser.getId());

        // Persist the new user
        User createdUser = userService.createUser(newUser);

        // Log the admin action
        AdminLog log = new AdminLog();
        log.setAdminId(adminUser.getId());
        log.setAction("CREATE_USER");
        log.setTargetEntity(createdUser.getUsername());
        log.setTimestamp(LocalDateTime.now());
        adminLogRepository.save(log);

        return createdUser;
    }
}
