package com.jewelryshop.service.impl;

import com.jewelryshop.dto.CouponRequest;
import com.jewelryshop.entity.Coupon;
import com.jewelryshop.exception.BadRequestException;
import com.jewelryshop.exception.ResourceNotFoundException;
import com.jewelryshop.repository.CouponRepository;
import com.jewelryshop.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    @Override
    @Transactional
    public Coupon createCoupon(CouponRequest request) {
        log.info("Creating new coupon: {}", request.getCode());

        if (couponRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Coupon code already exists");
        }

        Coupon coupon = new Coupon();
        coupon.setCode(request.getCode().toUpperCase());
        coupon.setDiscountType(request.getDiscountType());
        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setMinOrderAmount(request.getMinOrderAmount());
        coupon.setExpiryDate(request.getExpiryDate());
        coupon.setIsActive(request.getIsActive());

        Coupon savedCoupon = couponRepository.save(coupon);
        log.info("Coupon created successfully: {}", savedCoupon.getCode());

        return savedCoupon;
    }

    @Override
    public Coupon validateCoupon(String code, BigDecimal orderAmount) {
        log.info("Validating coupon: {}", code);

        Coupon coupon = couponRepository.findByCodeAndIsActiveTrue(code.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found or inactive"));

        if (coupon.getExpiryDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Coupon has expired");
        }

        if (coupon.getMinOrderAmount() != null && orderAmount.compareTo(coupon.getMinOrderAmount()) < 0) {
            throw new BadRequestException("Minimum order amount not met. Required: " + coupon.getMinOrderAmount());
        }

        log.info("Coupon validated successfully: {}", code);
        return coupon;
    }

    @Override
    public BigDecimal applyCoupon(String code, BigDecimal orderAmount) {
        Coupon coupon = validateCoupon(code, orderAmount);

        BigDecimal discount;
        if (coupon.getDiscountType() == Coupon.DiscountType.PERCENT) {
            discount = orderAmount.multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100));
        } else {
            discount = coupon.getDiscountValue();
        }

        // Ensure discount doesn't exceed order amount
        if (discount.compareTo(orderAmount) > 0) {
            discount = orderAmount;
        }

        log.info("Coupon applied. Discount amount: {}", discount);
        return discount;
    }

    @Override
    public List<Coupon> getAllCoupons() {
        log.info("Fetching all coupons");
        return couponRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteCoupon(Long id) {
        log.info("Deleting coupon with id: {}", id);
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));
        couponRepository.delete(coupon);
        log.info("Coupon deleted successfully");
    }
}
