package com.jewelryshop.service.impl;

import com.jewelryshop.dto.WishlistResponse;
import com.jewelryshop.entity.Product;
import com.jewelryshop.entity.User;
import com.jewelryshop.entity.Wishlist;
import com.jewelryshop.exception.BadRequestException;
import com.jewelryshop.exception.ResourceNotFoundException;
import com.jewelryshop.repository.ProductRepository;
import com.jewelryshop.repository.UserRepository;
import com.jewelryshop.repository.WishlistRepository;
import com.jewelryshop.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public WishlistResponse addToWishlist(Long userId, Long productId) {
        log.info("Adding product {} to wishlist for user: {}", productId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Product product = productRepository.findByIdAndNotDeleted(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new BadRequestException("Product already in wishlist");
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setProduct(product);

        Wishlist savedWishlist = wishlistRepository.save(wishlist);
        log.info("Product added to wishlist successfully");

        return mapToWishlistResponse(savedWishlist);
    }

    @Override
    @Transactional
    public void removeFromWishlist(Long userId, Long productId) {
        log.info("Removing product {} from wishlist for user: {}", productId, userId);

        if (!wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new ResourceNotFoundException("Product not found in wishlist");
        }

        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
        log.info("Product removed from wishlist successfully");
    }

    @Override
    public List<WishlistResponse> getWishlist(Long userId) {
        log.info("Fetching wishlist for user: {}", userId);

        List<Wishlist> wishlistItems = wishlistRepository.findByUserIdWithProduct(userId);
        return wishlistItems.stream()
                .map(this::mapToWishlistResponse)
                .collect(Collectors.toList());
    }

    private WishlistResponse mapToWishlistResponse(Wishlist wishlist) {
        WishlistResponse response = new WishlistResponse();
        response.setId(wishlist.getId());
        response.setProductId(wishlist.getProduct().getId());
        response.setProductName(wishlist.getProduct().getName());

        if (!wishlist.getProduct().getImages().isEmpty()) {
            response.setProductImage(wishlist.getProduct().getImages().get(0).getImageUrl());
        }

        response.setProductPrice(wishlist.getProduct().getPrice().toString());
        response.setInStock(wishlist.getProduct().getStockQuantity() > 0);
        response.setAddedAt(wishlist.getCreatedAt());

        return response;
    }
}
