package com.jewelryshop.controller;

import com.jewelryshop.dto.ApiResponse;
import com.jewelryshop.dto.UserResponse;
import com.jewelryshop.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin - User", description = "Admin user management APIs")
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all users (Admin)")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @PutMapping("/block/{id}")
    @Operation(summary = "Block/Unblock user (Admin)")
    public ResponseEntity<ApiResponse<Void>> blockUser(@PathVariable Long id) {
        userService.blockUser(id);
        return ResponseEntity.ok(ApiResponse.success("User status updated successfully", null));
    }
}
