package com.example.hena.user.dto;

public class UpdateUserDTO {
//    Used for clean data transfer on user creation and update (from HTTP requests).

    private String username;
    private String email;
    private String role; // 'user', 'host', or 'admin'
private String password;  // add password for update

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
