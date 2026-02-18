package com.jewelryshop.service.impl;

import com.jewelryshop.dto.CreatePaymentRequest;
import com.jewelryshop.dto.PaymentResponse;
import com.jewelryshop.dto.VerifyPaymentRequest;
import com.jewelryshop.entity.Order;
import com.jewelryshop.entity.Payment;
import com.jewelryshop.exception.BadRequestException;
import com.jewelryshop.exception.ResourceNotFoundException;
import com.jewelryshop.repository.OrderRepository;
import com.jewelryshop.repository.PaymentRepository;
import com.jewelryshop.service.PaymentService;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Override
    public JSONObject createPaymentOrder(CreatePaymentRequest request) throws Exception {
        log.info("Creating Razorpay order for order ID: {}", request.getOrderId());

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", request.getOrderId()));

        try {
            RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", request.getAmount().multiply(BigDecimal.valueOf(100)).intValue());
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", order.getOrderNumber());

            com.razorpay.Order razorpayOrder = razorpay.orders.create(orderRequest);

            // Save payment record
            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setPaymentGateway("Razorpay");
            payment.setTransactionId(razorpayOrder.get("id"));
            payment.setAmount(request.getAmount());
            payment.setStatus(Payment.PaymentStatus.PENDING);
            paymentRepository.save(payment);
            log.info("Razorpay order created successfully: {}",
                    String.valueOf(razorpayOrder.get("id")));
            return razorpayOrder.toJson();

        } catch (RazorpayException e) {
            log.error("Error creating Razorpay order", e);
            throw new BadRequestException("Failed to create payment order: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean verifyPayment(VerifyPaymentRequest request) throws Exception {
        log.info("Verifying Razorpay payment");

        String generatedSignature = generateSignature(
                request.getRazorpayOrderId(),
                request.getRazorpayPaymentId(),
                razorpayKeySecret
        );

        boolean isValid = generatedSignature.equals(request.getRazorpaySignature());

        if (isValid) {
            Payment payment = paymentRepository.findByTransactionId(request.getRazorpayOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

            payment.setStatus(Payment.PaymentStatus.SUCCESS);
            paymentRepository.save(payment);

            Order order = payment.getOrder();
            order.setPaymentStatus(Order.PaymentStatus.PAID);
            orderRepository.save(order);

            log.info("Payment verified and updated successfully");
        } else {
            log.error("Payment verification failed - invalid signature");
        }

        return isValid;
    }

    @Override
    @Transactional
    public void updatePaymentStatus(Long orderId, String status) {
        log.info("Updating payment status for order: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        Order.PaymentStatus paymentStatus;
        try {
            paymentStatus = Order.PaymentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid payment status: " + status);
        }

        order.setPaymentStatus(paymentStatus);
        orderRepository.save(order);

        log.info("Payment status updated successfully");
    }

    @Override
    public Page<PaymentResponse> getAllPayments(Pageable pageable) {
        log.info("Fetching all payments with pagination");
        Page<Payment> payments = paymentRepository.findAll(pageable);
        return payments.map(this::mapToPaymentResponse);
    }

    private PaymentResponse mapToPaymentResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setOrderId(payment.getOrder().getId());
        response.setOrderNumber(payment.getOrder().getOrderNumber());
        response.setUserId(payment.getOrder().getUser().getId());
        response.setPaymentGateway(payment.getPaymentGateway());
        response.setTransactionId(payment.getTransactionId());
        response.setAmount(payment.getAmount());
        response.setStatus(Payment.PaymentStatus.valueOf(payment.getStatus().name()));
        response.setOrderPaymentStatus(Order.PaymentStatus.valueOf(payment.getOrder().getPaymentStatus().name()));
        response.setCreatedAt(payment.getCreatedAt());
        return response;
    }

    private String generateSignature(String orderId, String paymentId, String secret) throws Exception {
        String payload = orderId + "|" + paymentId;
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        byte[] hash = sha256_HMAC.doFinal(payload.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }
}
