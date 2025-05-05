

package com.example.hena.admin.controller;

import com.example.hena.user.dto.CreateUserDTO;
import com.example.hena.user.dto.UpdateUserDTO;
import com.example.hena.user.entity.User;
import com.example.hena.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
//    The Admin will have more elevated privileges (like managing users)
//    while Host will only have privileges related to creating, managing, and deleting their own events.
//    Defines endpoints like createUser, updateUser, so the Admin can manage users through delegation to UserService.

    @Autowired
    private UserService userService;

    // Create a new user (Admin only)
    @PostMapping("/createUser")
    public User createUser(@RequestBody CreateUserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setRole(userDTO.getRole()); // Assign role here (admin/host/user)
        return userService.createUser(user);
    }

    // Update user (Admin only)
    @PutMapping("/updateUser/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody UpdateUserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());
        return userService.updateUser(id, user);
    }
}
