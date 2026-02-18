package com.jewelryshop.service.impl;

import com.jewelryshop.dto.ReviewRequest;
import com.jewelryshop.dto.ReviewResponse;
import com.jewelryshop.entity.Product;
import com.jewelryshop.entity.Review;
import com.jewelryshop.entity.User;
import com.jewelryshop.exception.BadRequestException;
import com.jewelryshop.exception.ResourceNotFoundException;
import com.jewelryshop.repository.ProductRepository;
import com.jewelryshop.repository.ReviewRepository;
import com.jewelryshop.repository.UserRepository;
import com.jewelryshop.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public ReviewResponse addReview(Long userId, ReviewRequest request) {
        log.info("Adding review for product: {}", request.getProductId());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        if (reviewRepository.existsByUserIdAndProductId(userId, request.getProductId())) {
            throw new BadRequestException("You have already reviewed this product");
        }

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review savedReview = reviewRepository.save(review);
        log.info("Review added successfully");

        return mapToReviewResponse(savedReview);
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(Long userId, Long reviewId, ReviewRequest request) {
        log.info("Updating review: {}", reviewId);

        Review review = reviewRepository.findByIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found or unauthorized"));

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review updatedReview = reviewRepository.save(review);
        log.info("Review updated successfully");

        return mapToReviewResponse(updatedReview);
    }

    @Override
    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        log.info("Deleting review: {}", reviewId);

        Review review = reviewRepository.findByIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found or unauthorized"));

        reviewRepository.delete(review);
        log.info("Review deleted successfully");
    }

    @Override
    public Page<ReviewResponse> getProductReviews(Long productId, Pageable pageable) {
        log.info("Fetching reviews for product: {}", productId);

        Page<Review> reviews = reviewRepository.findByProductId(productId, pageable);
        return reviews.map(this::mapToReviewResponse);
    }

    private ReviewResponse mapToReviewResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setUserId(review.getUser().getId());
        response.setUserName(review.getUser().getFullName());
        response.setProductId(review.getProduct().getId());
        response.setProductName(review.getProduct().getName());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt());
        return response;
    }
}
