package com.example.hena.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminDTO {
    private String username;  // Admin's username
    private String email;     // Admin's email
    private String password;  // Admin's password
    private String role;      // Admin's role (e.g., ADMIN)
}
