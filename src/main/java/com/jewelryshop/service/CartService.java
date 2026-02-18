package com.jewelryshop.service;

import com.jewelryshop.dto.AddToCartRequest;
import com.jewelryshop.dto.CartResponse;
import com.jewelryshop.dto.UpdateCartItemRequest;

public interface CartService {
    CartResponse addToCart(Long userId, AddToCartRequest request);
    CartResponse updateCartItem(Long userId, UpdateCartItemRequest request);
    CartResponse removeCartItem(Long userId, Long productId);
    void clearCart(Long userId);
    CartResponse getCart(Long userId);
}
