package com.ghoul.leetcodetracker.repositories;

import com.ghoul.leetcodetracker.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
