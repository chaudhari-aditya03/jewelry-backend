package com.jewelryshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String sku;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Integer stockQuantity;
    private Long categoryId;
    private String categoryName;
    private Boolean isActive;
    private Boolean isFeatured;
    private List<ProductImageResponse> images = new ArrayList<>();
    private List<ProductVariantResponse> variants = new ArrayList<>();
    private Double averageRating;
    private Long reviewCount;
    private LocalDateTime createdAt;
}
