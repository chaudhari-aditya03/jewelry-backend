package com.jewelryshop.controller;

import com.jewelryshop.dto.ApiResponse;
import com.jewelryshop.dto.CouponRequest;
import com.jewelryshop.entity.Coupon;
import com.jewelryshop.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Coupon", description = "Coupon management APIs")
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/admin/coupons")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all coupons (Admin)")
    public ResponseEntity<ApiResponse<List<Coupon>>> getAllCoupons() {
        List<Coupon> coupons = couponService.getAllCoupons();
        return ResponseEntity.ok(ApiResponse.success(coupons));
    }

    @PostMapping("/admin/coupons")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create coupon (Admin)")
    public ResponseEntity<ApiResponse<Coupon>> createCoupon(@Valid @RequestBody CouponRequest request) {
        Coupon coupon = couponService.createCoupon(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Coupon created successfully", coupon));
    }

    @PostMapping("/coupons/apply")
    @Operation(summary = "Apply coupon code")
    public ResponseEntity<ApiResponse<Map<String, Object>>> applyCoupon(
            @RequestParam String code,
            @RequestParam BigDecimal orderAmount) {
        BigDecimal discount = couponService.applyCoupon(code, orderAmount);
        
        Map<String, Object> response = new HashMap<>();
        response.put("discountAmount", discount);
        response.put("finalAmount", orderAmount.subtract(discount));
        
        return ResponseEntity.ok(ApiResponse.success("Coupon applied successfully", response));
    }

    @DeleteMapping("/admin/coupons/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete coupon (Admin)")
    public ResponseEntity<ApiResponse<Void>> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.ok(ApiResponse.success("Coupon deleted successfully", null));
    }
}
