package com.jewelryshop.service;

import com.jewelryshop.dto.ProductRequest;
import com.jewelryshop.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
    ProductResponse getProductById(Long id);
    Page<ProductResponse> getAllProducts(Pageable pageable);
    
    Page<ProductResponse> getAllProductsWithFilters(String search, Long categoryId, 
                                                     Double minPrice, Double maxPrice, 
                                                     String sortBy, Pageable pageable);
    Page<ProductResponse> searchProducts(String keyword, Pageable pageable);
    Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable);
    Page<ProductResponse> getFeaturedProducts(Pageable pageable);
}
