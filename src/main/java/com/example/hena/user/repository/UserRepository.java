package com.example.hena.user.repository;

import com.example.hena.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
//    Provides database access for User, including finding users by username.
    Optional<User> findByUsername(String username);
}
