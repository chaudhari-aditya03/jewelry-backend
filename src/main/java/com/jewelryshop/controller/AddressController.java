package com.jewelryshop.controller;

import com.jewelryshop.dto.AddressRequest;
import com.jewelryshop.dto.AddressResponse;
import com.jewelryshop.dto.ApiResponse;
import com.jewelryshop.security.CustomUserDetails;
import com.jewelryshop.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Tag(name = "Address", description = "Address management APIs")
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    @Operation(summary = "Get user addresses")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getUserAddresses(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        List<AddressResponse> addresses = addressService.getUserAddresses(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(addresses));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get address by ID")
    public ResponseEntity<ApiResponse<AddressResponse>> getAddressById(
            Authentication authentication,
            @PathVariable Long id) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        AddressResponse address = addressService.getAddressById(userDetails.getId(), id);
        return ResponseEntity.ok(ApiResponse.success(address));
    }

    @PostMapping
    @Operation(summary = "Create address")
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(
            Authentication authentication,
            @Valid @RequestBody AddressRequest request) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        AddressResponse address = addressService.createAddress(userDetails.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Address created successfully", address));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update address")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        AddressResponse address = addressService.updateAddress(userDetails.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Address updated successfully", address));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete address")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            Authentication authentication,
            @PathVariable Long id) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        addressService.deleteAddress(userDetails.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Address deleted successfully", null));
    }
}
