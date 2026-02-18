package com.jewelryshop.dto;

import com.jewelryshop.entity.Order;
import com.jewelryshop.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private String orderNumber;
    private Long userId;
    private String paymentGateway;
    private String transactionId;
    private BigDecimal amount;
    private Payment.PaymentStatus status;
    private Order.PaymentStatus orderPaymentStatus;
    private LocalDateTime createdAt;
}
