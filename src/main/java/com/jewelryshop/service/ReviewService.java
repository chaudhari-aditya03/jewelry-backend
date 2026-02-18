package com.jewelryshop.service;

import com.jewelryshop.dto.ReviewRequest;
import com.jewelryshop.dto.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    ReviewResponse addReview(Long userId, ReviewRequest request);
    ReviewResponse updateReview(Long userId, Long reviewId, ReviewRequest request);
    void deleteReview(Long userId, Long reviewId);
    Page<ReviewResponse> getProductReviews(Long productId, Pageable pageable);
}
