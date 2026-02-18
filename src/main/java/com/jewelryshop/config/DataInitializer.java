package com.jewelryshop.config;

import com.jewelryshop.entity.*;
import com.jewelryshop.repository.CategoryRepository;
import com.jewelryshop.repository.ProductRepository;
import com.jewelryshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("🔄 Starting data initialization...");
        
        // Initialize Users
        initializeUsers();
        
        // Initialize Categories
        List<Category> categories = initializeCategories();
        
        // Initialize Products
        initializeProducts(categories);
        
        log.info("✅ Data initialization completed successfully!");
    }

    private void initializeUsers() {
        // Create or update Admin User
        User admin = userRepository.findByEmail("admin@gmail.com")
                .orElse(new User());
        
        admin.setFullName("Admin User");
        admin.setEmail("admin@gmail.com");
        admin.setPassword(passwordEncoder.encode("admin@2005"));
        admin.setPhone("9876543210");
        admin.setRole(User.Role.ADMIN);
        admin.setIsActive(true);
        admin.setEmailVerified(true);
        
        if (admin.getId() == null) {
            userRepository.save(admin);
            log.info("✅ Admin user created: admin@gmail.com");
        } else {
            userRepository.save(admin);
            log.info("✅ Admin user password reset: admin@gmail.com");
        }

        // Create or update Regular User
        User regularUser = userRepository.findByEmail("aditya@gmail.com")
                .orElse(new User());
        
        regularUser.setFullName("Aditya Kumar");
        regularUser.setEmail("aditya@gmail.com");
        regularUser.setPassword(passwordEncoder.encode("aditya@2005"));
        regularUser.setPhone("9123456780");
        regularUser.setRole(User.Role.USER);
        regularUser.setIsActive(true);
        regularUser.setEmailVerified(true);
        
        if (regularUser.getId() == null) {
            userRepository.save(regularUser);
            log.info("✅ Regular user created: aditya@gmail.com");
        } else {
            userRepository.save(regularUser);
            log.info("✅ Regular user password reset: aditya@gmail.com");
        }
    }

    private List<Category> initializeCategories() {
        List<Category> categories = new ArrayList<>();
        
        if (categoryRepository.count() == 0) {
            // Create categories
            Category rings = new Category();
            rings.setName("Rings");
            rings = categoryRepository.save(rings);
            categories.add(rings);

            Category necklaces = new Category();
            necklaces.setName("Necklaces");
            necklaces = categoryRepository.save(necklaces);
            categories.add(necklaces);

            Category earrings = new Category();
            earrings.setName("Earrings");
            earrings = categoryRepository.save(earrings);
            categories.add(earrings);

            Category bracelets = new Category();
            bracelets.setName("Bracelets");
            bracelets = categoryRepository.save(bracelets);
            categories.add(bracelets);

            log.info("✅ Categories created: Rings, Necklaces, Earrings, Bracelets");
        } else {
            categories = categoryRepository.findAll();
            log.info("ℹ️ Categories already exist");
        }
        
        return categories;
    }

    private void initializeProducts(List<Category> categories) {
        if (productRepository.count() == 0 && !categories.isEmpty()) {
            // Product 1: Diamond Solitaire Ring
            Product product1 = createProduct(
                "Diamond Solitaire Ring",
                "RING-DS-001",
                "Elegant 18K white gold solitaire ring featuring a brilliant 1-carat diamond with VS1 clarity and F color grade. Perfect for engagements and special occasions.",
                new BigDecimal("45000.00"),
                new BigDecimal("42000.00"),
                15,
                categories.get(0), // Rings
                true,
                "https://images.unsplash.com/photo-1605100804763-247f67b3557e?w=500"
            );
            productRepository.save(product1);

            // Product 2: Pearl Necklace
            Product product2 = createProduct(
                "Classic Pearl Necklace",
                "NECK-CPL-002",
                "Timeless freshwater pearl necklace with 7-8mm pearls on a 14K gold clasp. Features 18-inch length suitable for all occasions.",
                new BigDecimal("28000.00"),
                new BigDecimal("25000.00"),
                20,
                categories.get(1), // Necklaces
                true,
                "https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?w=500"
            );
            productRepository.save(product2);

            // Product 3: Diamond Stud Earrings
            Product product3 = createProduct(
                "Diamond Stud Earrings",
                "EAR-DS-003",
                "Beautiful pair of diamond stud earrings set in platinum. Each earring features a 0.5-carat round brilliant diamond with excellent cut.",
                new BigDecimal("65000.00"),
                new BigDecimal("60000.00"),
                12,
                categories.get(2), // Earrings
                false,
                "https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?w=500"
            );
            productRepository.save(product3);

            // Product 4: Gold Chain Bracelet
            Product product4 = createProduct(
                "Gold Chain Bracelet",
                "BRAC-GC-004",
                "Sophisticated 22K gold chain bracelet with intricate design. Weighs approximately 15 grams and features secure lobster clasp.",
                new BigDecimal("38000.00"),
                new BigDecimal("35000.00"),
                18,
                categories.get(3), // Bracelets
                false,
                "https://images.unsplash.com/photo-1611591437281-460bfbe1220a?w=500"
            );
            productRepository.save(product4);

            // Product 5: Emerald Ring
            Product product5 = createProduct(
                "Emerald Gemstone Ring",
                "RING-EM-005",
                "Stunning emerald ring featuring a 2-carat natural Colombian emerald surrounded by micro-pave diamonds in 18K yellow gold setting.",
                new BigDecimal("72000.00"),
                new BigDecimal("68000.00"),
                8,
                categories.get(0), // Rings
                true,
                "https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=500"
            );
            productRepository.save(product5);

            log.info("✅ 5 products created successfully");
        } else {
            log.info("ℹ️ Products already exist");
        }
    }

    private Product createProduct(String name, String sku, String description, 
                                  BigDecimal price, BigDecimal discountPrice,
                                  Integer stockQuantity, Category category,
                                  Boolean isFeatured, String imageUrl) {
        Product product = new Product();
        product.setName(name);
        product.setSku(sku);
        product.setDescription(description);
        product.setPrice(price);
        product.setDiscountPrice(discountPrice);
        product.setStockQuantity(stockQuantity);
        product.setCategory(category);
        product.setIsActive(true);
        product.setIsFeatured(isFeatured);

        // Add product image
        ProductImage productImage = new ProductImage();
        productImage.setProduct(product);
        productImage.setImageUrl(imageUrl);
        productImage.setIsPrimary(true);
        
        List<ProductImage> images = new ArrayList<>();
        images.add(productImage);
        product.setImages(images);

        return product;
    }
}
