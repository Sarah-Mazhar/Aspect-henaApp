

package com.example.hena.admin.controller;

import com.example.hena.admin.entity.Admin;
import com.example.hena.admin.entity.AdminLog;
import com.example.hena.admin.repository.AdminLogRepository;
import com.example.hena.user.dto.CreateUserDTO;
import com.example.hena.user.dto.UpdateUserDTO;
import com.example.hena.user.entity.User;
import com.example.hena.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;


import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin")
public class AdminController {
//    The Admin will have more elevated privileges (like managing users/approve events)
//    while Host will only have privileges related to creating, managing, and deleting their own events.
//    Defines endpoints like createUser, updateUser, so the Admin can manage users through delegation to UserService.
//     log what admin did what (e.g., who deleted a user, who approved an event).

    @Autowired
    private UserService userService;

    @Autowired
    private AdminLogRepository adminLogRepository;


    // Update user (Admin only)
    // ✅ Admin updates an existing user
    // Update user (Admin only)
    // Update user (Admin only)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/updateUser/{adminId}/{userId}")
    public User updateUser(@PathVariable Long adminId, @PathVariable Long userId, @RequestBody UpdateUserDTO userDTO) {
        // Find the admin by ID (via path variable)
        User adminUser = userService.getUserById(adminId);

        // Find and update the user by ID
        User updatedUser = new User();
        updatedUser.setUsername(userDTO.getUsername());
        updatedUser.setEmail(userDTO.getEmail());
        updatedUser.setRole(userDTO.getRole());
        User userAfterUpdate = userService.updateUser(userId, updatedUser);

        // Log the action of the admin
        AdminLog log = new AdminLog();
        log.setAdminId(adminUser.getId());  // Store the ID of the admin performing the action
        log.setAction("UPDATE_USER");  // Log the action as "UPDATE_USER"
        log.setTargetEntity(userAfterUpdate.getUsername());  // Store the updated user's username as the target entity
        log.setTimestamp(LocalDateTime.now());  // Store the current timestamp of the action
        adminLogRepository.save(log);  // Save the log to the database

        return userAfterUpdate;  // Return the updated user
    }



    //    to log actions by admin username
    // ✅ Admin creates a new user + logs it
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createUser/{adminId}")
    public User createUser(@PathVariable Long adminId, @RequestBody CreateUserDTO userDTO) {
        // Find the admin by ID (via path variable)
        User adminUser = userService.getUserById(adminId);

    // Save the new user
    User newUser = new User();
    newUser.setUsername(userDTO.getUsername());
    newUser.setEmail(userDTO.getEmail());
    newUser.setPassword(userDTO.getPassword());
    newUser.setRole(userDTO.getRole());
    newUser.setCreatedByAdminId(adminUser.getId());  // Set the admin who created the user
    User createdUser = userService.createUser(newUser);

    // Save a log entry (optional - if Admin entity exists separately)
    AdminLog log = new AdminLog();
    log.setAdminId(adminUser.getId());
    log.setAction("CREATE_USER");
    log.setTargetEntity(createdUser.getUsername());
    log.setTimestamp(LocalDateTime.now());
    adminLogRepository.save(log);

    return createdUser;
}
}
