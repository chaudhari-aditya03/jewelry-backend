package com.jewelryshop.service;

import com.jewelryshop.dto.UpdateUserRequest;
import com.jewelryshop.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse getUserProfile(Long userId);
    UserResponse updateUser(Long userId, UpdateUserRequest request);
    void deleteUser(Long userId);
    void blockUser(Long userId);
    Page<UserResponse> getAllUsers(Pageable pageable);
}
