package com.jewelryshop.controller;

import com.jewelryshop.dto.ApiResponse;
import com.jewelryshop.dto.UpdateUserRequest;
import com.jewelryshop.dto.UserResponse;
import com.jewelryshop.security.CustomUserDetails;
import com.jewelryshop.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User management APIs")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "Get user profile")
    public ResponseEntity<ApiResponse<UserResponse>> getUserProfile(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserResponse userResponse = userService.getUserProfile(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(userResponse));
    }

    @PutMapping("/update")
    @Operation(summary = "Update user profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            Authentication authentication,
            @Valid @RequestBody UpdateUserRequest request) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserResponse userResponse = userService.updateUser(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", userResponse));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Delete user account")
    public ResponseEntity<ApiResponse<Void>> deleteUser(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        userService.deleteUser(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Account deleted successfully", null));
    }
}
