package com.example.hena.admin.repository;

import com.example.hena.admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
//    Handles database access for Admin (find, save, etc.).
    Admin findByUsername(String username);
}
