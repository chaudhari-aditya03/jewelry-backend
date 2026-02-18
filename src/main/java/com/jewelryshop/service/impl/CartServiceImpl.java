package com.jewelryshop.service.impl;

import com.jewelryshop.dto.*;
import com.jewelryshop.entity.*;
import com.jewelryshop.exception.BadRequestException;
import com.jewelryshop.exception.ResourceNotFoundException;
import com.jewelryshop.repository.*;
import com.jewelryshop.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CartResponse addToCart(Long userId, AddToCartRequest request) {
        log.info("Adding product to cart for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Product product = productRepository.findByIdAndNotDeleted(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        if (!product.getIsActive()) {
            throw new BadRequestException("Product is not available");
        }

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new BadRequestException("Insufficient stock. Available: " + product.getStockQuantity());
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        CartItem existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId())
                .orElse(null);

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            if (newQuantity > product.getStockQuantity()) {
                throw new BadRequestException("Total quantity exceeds available stock");
            }
            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());

            if (request.getVariantId() != null) {
                ProductVariant variant = productVariantRepository.findById(request.getVariantId())
                        .orElseThrow(() -> new ResourceNotFoundException("Variant", "id", request.getVariantId()));
                cartItem.setVariant(variant);
            }

            cartItemRepository.save(cartItem);
        }

        log.info("Product added to cart successfully");
        return getCart(userId);
    }

    @Override
    @Transactional
    public CartResponse updateCartItem(Long userId, UpdateCartItemRequest request) {
        log.info("Updating cart item for user: {}", userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        Product product = cartItem.getProduct();
        if (request.getQuantity() > product.getStockQuantity()) {
            throw new BadRequestException("Quantity exceeds available stock");
        }

        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);

        log.info("Cart item updated successfully");
        return getCart(userId);
    }

    @Override
    @Transactional
    public CartResponse removeCartItem(Long userId, Long productId) {
        log.info("Removing cart item for user: {}", userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));

        cartItemRepository.deleteByCartIdAndProductId(cart.getId(), productId);

        log.info("Cart item removed successfully");
        return getCart(userId);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        log.info("Clearing cart for user: {}", userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));

        cartItemRepository.deleteAllByCartId(cart.getId());

        log.info("Cart cleared successfully");
    }

    @Override
    public CartResponse getCart(Long userId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setUserId(cart.getUser().getId());

        List<CartItemResponse> items = cart.getItems().stream()
                .map(this::mapToCartItemResponse)
                .collect(Collectors.toList());

        response.setItems(items);
        response.setTotalItems(items.size());
        response.setTotalPrice(calculateTotalPrice(items));

        return response;
    }

    private CartItemResponse mapToCartItemResponse(CartItem item) {
        CartItemResponse response = new CartItemResponse();
        response.setId(item.getId());
        response.setProductId(item.getProduct().getId());
        response.setProductName(item.getProduct().getName());
        
        if (!item.getProduct().getImages().isEmpty()) {
            response.setProductImage(item.getProduct().getImages().get(0).getImageUrl());
        }

        BigDecimal price = item.getProduct().getDiscountPrice() != null 
                ? item.getProduct().getDiscountPrice() 
                : item.getProduct().getPrice();

        if (item.getVariant() != null) {
            response.setVariantId(item.getVariant().getId());
            response.setVariantName(item.getVariant().getVariantName());
            price = price.add(item.getVariant().getAdditionalPrice());
        }

        response.setPrice(price);
        response.setQuantity(item.getQuantity());
        response.setSubtotal(price.multiply(BigDecimal.valueOf(item.getQuantity())));

        return response;
    }

    private BigDecimal calculateTotalPrice(List<CartItemResponse> items) {
        return items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
