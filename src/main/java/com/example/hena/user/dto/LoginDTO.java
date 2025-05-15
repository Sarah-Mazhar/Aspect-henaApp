package com.example.hena.user.dto;

public class LoginDTO {
    private String username;  // add if you want to accept username
    private String email;
    private String password;
    private String role;      // add if you want to accept role

    // getters and setters for all fields

//    public String getUsername() { return username; }
//    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
