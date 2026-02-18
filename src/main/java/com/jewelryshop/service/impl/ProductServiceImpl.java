package com.jewelryshop.service.impl;

import com.jewelryshop.dto.*;
import com.jewelryshop.entity.*;
import com.jewelryshop.exception.BadRequestException;
import com.jewelryshop.exception.ResourceNotFoundException;
import com.jewelryshop.repository.*;
import com.jewelryshop.service.ProductService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ReviewRepository reviewRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating new product: {}", request.getName());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        Product product = new Product();
        product.setName(request.getName());
        product.setSku(request.getSku() != null ? request.getSku() : generateSku());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(category);
        product.setIsActive(request.getIsActive());
        product.setIsFeatured(request.getIsFeatured());

        Product savedProduct = productRepository.save(product);

        // Add images (supports both new images list and legacy imageUrls)
        List<ProductImage> images = new ArrayList<>();
        int imageCount = 0;
        final int MAX_IMAGES = 4; // Limit to 4 images (1 main + 3 side views)
        
        // First, try to use the new images list (with isPrimary information)
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (var imageReq : request.getImages()) {
                if (imageCount >= MAX_IMAGES) {
                    log.warn("Maximum of {} images allowed. Skipping additional images.", MAX_IMAGES);
                    break; // Enforce limit
                }
                if (imageReq.getImageUrl() != null && !imageReq.getImageUrl().isEmpty()) {
                    ProductImage image = new ProductImage();
                    image.setProduct(savedProduct);
                    image.setImageUrl(imageReq.getImageUrl());
                    image.setImageName(imageReq.getImageName());
                    image.setImagePath(imageReq.getImagePath());
                    image.setFileSize(imageReq.getFileSize());
                    image.setMimeType(imageReq.getMimeType());
                    image.setIsPrimary(imageReq.getIsPrimary() != null ? imageReq.getIsPrimary() : false);
                    image.setUploadedAt(java.time.LocalDateTime.now());
                    images.add(image);
                    imageCount++;
                }
            }
        } 
        // Fall back to legacy imageUrls list
        else if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            for (int i = 0; i < Math.min(request.getImageUrls().size(), MAX_IMAGES); i++) {
                ProductImage image = new ProductImage();
                image.setProduct(savedProduct);
                image.setImageUrl(request.getImageUrls().get(i));
                image.setIsPrimary(i == 0);
                image.setUploadedAt(java.time.LocalDateTime.now());
                images.add(image);
            }
        }
        
        if (!images.isEmpty()) {
            productImageRepository.saveAll(images);
            log.info("Saved {} images for product ID: {}", images.size(), savedProduct.getId());
        }

        // Add variants
        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            List<ProductVariant> variants = request.getVariants().stream()
                    .map(variantReq -> {
                        ProductVariant variant = new ProductVariant();
                        variant.setProduct(savedProduct);
                        variant.setVariantName(variantReq.getVariantName());
                        variant.setAdditionalPrice(variantReq.getAdditionalPrice());
                        variant.setStockQuantity(variantReq.getStockQuantity());
                        return variant;
                    })
                    .collect(Collectors.toList());
            productVariantRepository.saveAll(variants);
        }

        log.info("Product created successfully with ID: {}", savedProduct.getId());
        return getProductById(savedProduct.getId());
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        log.info("Updating product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(category);
        product.setIsActive(request.getIsActive());
        product.setIsFeatured(request.getIsFeatured());

        productRepository.save(product);

        // Update images (supports both new images list and legacy imageUrls)
        // IMPORTANT: Delete old images first and flush to avoid duplicates
        productImageRepository.deleteByProductId(id);
        entityManager.flush(); // Force delete to complete before inserts
        
        log.info("Deleted old images for product ID: {}", id);
        
        List<ProductImage> images = new ArrayList<>();
        int imageCount = 0;
        final int MAX_IMAGES = 4; // Limit to 4 images (1 main + 3 side views)
        
        // First, try to use the new images list (with isPrimary information)
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (var imageReq : request.getImages()) {
                if (imageCount >= MAX_IMAGES) {
                    log.warn("Maximum of {} images allowed. Skipping additional images.", MAX_IMAGES);
                    break; // Enforce limit
                }
                if (imageReq.getImageUrl() != null && !imageReq.getImageUrl().isEmpty()) {
                    ProductImage image = new ProductImage();
                    image.setProduct(product);
                    image.setImageUrl(imageReq.getImageUrl());
                    image.setImageName(imageReq.getImageName());
                    image.setImagePath(imageReq.getImagePath());
                    image.setFileSize(imageReq.getFileSize());
                    image.setMimeType(imageReq.getMimeType());
                    image.setIsPrimary(imageReq.getIsPrimary() != null ? imageReq.getIsPrimary() : false);
                    image.setUploadedAt(java.time.LocalDateTime.now());
                    images.add(image);
                    imageCount++;
                }
            }
        } 
        // Fall back to legacy imageUrls list
        else if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            for (int i = 0; i < Math.min(request.getImageUrls().size(), MAX_IMAGES); i++) {
                ProductImage image = new ProductImage();
                image.setProduct(product);
                image.setImageUrl(request.getImageUrls().get(i));
                image.setIsPrimary(i == 0);
                image.setUploadedAt(java.time.LocalDateTime.now());
                images.add(image);
            }
        }
        
        if (!images.isEmpty()) {
            productImageRepository.saveAll(images);
            log.info("Saved {} new images for product ID: {}", images.size(), id);
        } else {
            log.warn("No images to save for product ID: {}", id);
        }

        // Update variants
        if (request.getVariants() != null) {
            productVariantRepository.deleteByProductId(id);
            List<ProductVariant> variants = request.getVariants().stream()
                    .map(variantReq -> {
                        ProductVariant variant = new ProductVariant();
                        variant.setProduct(product);
                        variant.setVariantName(variantReq.getVariantName());
                        variant.setAdditionalPrice(variantReq.getAdditionalPrice());
                        variant.setStockQuantity(variantReq.getStockQuantity());
                        return variant;
                    })
                    .collect(Collectors.toList());
            productVariantRepository.saveAll(variants);
        }

        log.info("Product updated successfully: {}", id);
        return getProductById(id);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        log.info("Soft deleting product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        product.setDeletedAt(LocalDateTime.now());
        product.setIsActive(false);
        productRepository.save(product);

        log.info("Product soft deleted successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        return mapToProductResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        log.info("Fetching all products with pagination");
        Page<Product> products = productRepository.findAllActiveProducts(pageable);
        return products.map(this::mapToProductResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProductsWithFilters(String search, Long categoryId,
                                                           Double minPrice, Double maxPrice,
                                                           String sortBy, Pageable pageable) {
        log.info("Fetching products with filters - search: {}, categoryId: {}, minPrice: {}, maxPrice: {}, sortBy: {}",
                search, categoryId, minPrice, maxPrice, sortBy);
        
        // Apply sorting
        if (sortBy != null && !sortBy.isEmpty()) {
            pageable = applySorting(sortBy, pageable);
        }
        
        Page<Product> products = productRepository.findWithFilters(search, categoryId, minPrice, maxPrice, pageable);
        return products.map(this::mapToProductResponse);
    }
    
    private Pageable applySorting(String sortBy, Pageable pageable) {
        org.springframework.data.domain.Sort sort;
        switch (sortBy.toLowerCase()) {
            case "price-asc":
                sort = org.springframework.data.domain.Sort.by("price").ascending();
                break;
            case "price-desc":
                sort = org.springframework.data.domain.Sort.by("price").descending();
                break;
            case "rating":
                sort = org.springframework.data.domain.Sort.by("id").descending(); // Placeholder for rating
                break;
            case "newest":
            default:
                sort = org.springframework.data.domain.Sort.by("createdAt").descending();
                break;
        }
        return org.springframework.data.domain.PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        log.info("Searching products with keyword: {}", keyword);
        Page<Product> products = productRepository.searchProducts(keyword, pageable);
        return products.map(this::mapToProductResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
        log.info("Fetching products by category ID: {}", categoryId);
        Page<Product> products = productRepository.findByCategoryId(categoryId, pageable);
        return products.map(this::mapToProductResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getFeaturedProducts(Pageable pageable) {
        log.info("Fetching featured products");
        Page<Product> products = productRepository.findFeaturedProducts(pageable);
        return products.map(this::mapToProductResponse);
    }

    private ProductResponse mapToProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setSku(product.getSku());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setDiscountPrice(product.getDiscountPrice());
        response.setStockQuantity(product.getStockQuantity());
        response.setCategoryId(product.getCategory().getId());
        response.setCategoryName(product.getCategory().getName());
        response.setIsActive(product.getIsActive());
        response.setIsFeatured(product.getIsFeatured());
        response.setCreatedAt(product.getCreatedAt());

        // Map images
        List<ProductImageResponse> images = product.getImages().stream()
                .map(img -> new ProductImageResponse(img.getId(), img.getImageUrl(), img.getIsPrimary()))
                .collect(Collectors.toList());
        response.setImages(images);

        // Map variants
        List<ProductVariantResponse> variants = product.getVariants().stream()
                .map(variant -> new ProductVariantResponse(
                        variant.getId(),
                        variant.getVariantName(),
                        variant.getAdditionalPrice(),
                        variant.getStockQuantity()
                ))
                .collect(Collectors.toList());
        response.setVariants(variants);

        // Get average rating and review count
        Double avgRating = reviewRepository.getAverageRatingByProductId(product.getId());
        Long reviewCount = reviewRepository.countByProductId(product.getId());
        response.setAverageRating(avgRating != null ? avgRating : 0.0);
        response.setReviewCount(reviewCount);

        return response;
    }

    private String generateSku() {
        return "JW-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
