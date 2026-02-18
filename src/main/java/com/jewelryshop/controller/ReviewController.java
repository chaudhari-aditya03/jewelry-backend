package com.jewelryshop.controller;

import com.jewelryshop.dto.ApiResponse;
import com.jewelryshop.dto.ReviewRequest;
import com.jewelryshop.dto.ReviewResponse;
import com.jewelryshop.security.CustomUserDetails;
import com.jewelryshop.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Review", description = "Product review management APIs")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Add product review")
    public ResponseEntity<ApiResponse<ReviewResponse>> addReview(
            Authentication authentication,
            @Valid @RequestBody ReviewRequest request) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        ReviewResponse review = reviewService.addReview(userDetails.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review added successfully", review));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update review")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequest request) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        ReviewResponse review = reviewService.updateReview(userDetails.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Review updated successfully", review));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete review")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            Authentication authentication,
            @PathVariable Long id) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        reviewService.deleteReview(userDetails.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Review deleted successfully", null));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get product reviews")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewResponse> reviews = reviewService.getProductReviews(productId, pageable);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }
}
