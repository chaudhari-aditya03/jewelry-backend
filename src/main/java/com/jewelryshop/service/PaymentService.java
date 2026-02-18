package com.jewelryshop.service;

import com.jewelryshop.dto.CreatePaymentRequest;
import com.jewelryshop.dto.PaymentResponse;
import com.jewelryshop.dto.VerifyPaymentRequest;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    JSONObject createPaymentOrder(CreatePaymentRequest request) throws Exception;
    boolean verifyPayment(VerifyPaymentRequest request) throws Exception;
    void updatePaymentStatus(Long orderId, String status);
    Page<PaymentResponse> getAllPayments(Pageable pageable);
}
