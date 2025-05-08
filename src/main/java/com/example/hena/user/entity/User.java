package com.example.hena.user.entity;

import jakarta.persistence.*;

@Entity
public class User {
//    The User entity is the core identity in the system.
//    Every system actor — whether Admin, Host, or regular User — is represented using this class,
//    and their role is determined via a String role field.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String email;
    private String role;


    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    private Long createdByAdminId;
    @Column(name = "created_by_admin_id")   // ✅ For logging which admin created the user


//    createdByAdminId field (to track who created them)

    public Long getCreatedByAdminId() {
        return createdByAdminId;
    }

    public void setCreatedByAdminId(Long createdByAdminId) {
        this.createdByAdminId = createdByAdminId;
    }



}
