package com.jewelryshop.controller;

import com.jewelryshop.entity.User;
import com.jewelryshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
public class DebugController {

    private final UserRepository userRepository;

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(Map.of(
            "success", true,
            "count", users.size(),
            "users", users.stream().map(u -> Map.of(
                "id", u.getId(),
                "fullName", u.getFullName(),
                "email", u.getEmail(),
                "role", u.getRole().toString(),
                "isActive", u.getIsActive(),
                "hasPassword", u.getPassword() != null && !u.getPassword().isEmpty()
            )).toList()
        ));
    }

    @GetMapping("/user-by-email")
    public ResponseEntity<Map<String, Object>> getUserByEmail(String email) {
        return userRepository.findByEmailAndDeletedAtIsNull(email)
            .map(user -> ResponseEntity.ok(Map.of(
                "success", true,
                "user", Map.of(
                    "id", user.getId(),
                    "fullName", user.getFullName(),
                    "email", user.getEmail(),
                    "role", user.getRole().toString(),
                    "isActive", user.getIsActive(),
                    "passwordHash", user.getPassword().substring(0, Math.min(20, user.getPassword().length())) + "...",
                    "emailVerified", user.getEmailVerified()
                )
            )))
            .orElse(ResponseEntity.ok(Map.of("success", false, "message", "User not found")));
    }
}
