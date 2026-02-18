package com.jewelryshop.service;

import com.jewelryshop.dto.*;
import com.jewelryshop.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderResponse placeOrder(Long userId, PlaceOrderRequest request);
    OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request);
    OrderResponse cancelOrder(Long userId, Long orderId, CancelOrderRequest request);
    OrderResponse getOrderById(Long orderId);
    List<OrderResponse> getUserOrders(Long userId);
    Page<OrderResponse> getAllOrders(Pageable pageable);
}
