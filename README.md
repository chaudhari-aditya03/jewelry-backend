# Jewelry E-Commerce Platform - Backend API

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.2-brightgreen.svg)
![Java](https://img.shields.io/badge/Java-17-orange.svg)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)
![Maven](https://img.shields.io/badge/Maven-3.8+-red.svg)

A production-ready RESTful API for a Jewelry E-Commerce platform built with Spring Boot 3, featuring JWT authentication, comprehensive order management, payment integration, and admin analytics.

## 🚀 Features

### Core Modules
- ✅ **Authentication & Authorization** - JWT-based security with role-based access control
- ✅ **User Management** - Profile management, soft delete, admin controls
- ✅ **Product Catalog** - Products with variants, images, categories, and reviews
- ✅ **Shopping Cart** - Add, update, remove items with stock validation
- ✅ **Order Management** - Complete order lifecycle with status tracking
- ✅ **Payment Integration** - Razorpay payment gateway integration
- ✅ **Coupon System** - Discount coupons with validation
- ✅ **Reviews & Ratings** - Product reviews with average rating calculation
- ✅ **Wishlist** - Save favorite products
- ✅ **Analytics Dashboard** - Sales reports and business metrics
- ✅ **Address Management** - Multiple delivery addresses per user

### Technical Features
- 🔐 **JWT Authentication** - Secure token-based authentication
- 🛡️ **Spring Security** - Role-based authorization (USER, ADMIN)
- 📊 **Pagination & Sorting** - Efficient data retrieval
- 🔍 **Search & Filter** - Product search and category filtering
- ✅ **Validation** - Request validation with custom error messages
- 🌐 **CORS Configuration** - Cross-origin resource sharing
- 📝 **API Documentation** - Swagger/OpenAPI 3.0
- 🗄️ **Database Auditing** - Automatic timestamp management
- 🔄 **Transaction Management** - ACID compliance with @Transactional
- ⚠️ **Global Exception Handling** - Consistent error responses

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.8+
- MySQL 8.0+
- Git

## 🛠️ Technology Stack

| Technology | Version | Purpose |
|-----------|---------|---------|
| Spring Boot | 3.2.2 | Framework |
| Spring Security | 6.x | Authentication & Authorization |
| Spring Data JPA | 3.x | Data Access Layer |
| MySQL | 8.0 | Database |
| Hibernate | 6.x | ORM |
| JWT (jjwt) | 0.12.3 | Token Generation |
| Lombok | Latest | Boilerplate Reduction |
| ModelMapper | 3.2.0 | DTO Mapping |
| Razorpay SDK | 1.4.6 | Payment Gateway |
| SpringDoc OpenAPI | 2.3.0 | API Documentation |

## 📁 Project Structure

```
com.jewelryshop
├── config                  # Configuration classes
│   ├── SecurityConfig      # Security & JWT configuration
│   ├── CorsConfig          # CORS settings
│   ├── ModelMapperConfig   # ModelMapper bean
│   └── SwaggerConfig       # API documentation config
├── controller             # REST API endpoints
│   ├── AuthController
│   ├── UserController
│   ├── CategoryController
│   ├── ProductController
│   ├── CartController
│   ├── OrderController
│   ├── PaymentController
│   ├── ReviewController
│   ├── WishlistController
│   ├── AddressController
│   └── AnalyticsController
├── service                # Business logic interfaces
│   └── impl              # Service implementations
├── repository            # Data access layer
├── entity                # JPA entities
├── dto                   # Data Transfer Objects
├── security              # Security components
│   ├── JwtAuthenticationFilter
│   ├── JwtAuthenticationEntryPoint
│   ├── CustomUserDetails
│   └── CustomUserDetailsService
├── exception             # Custom exceptions
│   ├── ResourceNotFoundException
│   ├── BadRequestException
│   └── GlobalExceptionHandler
└── util                  # Utility classes
    └── JwtUtil
```

## 🗄️ Database Schema

The application uses 14 database tables with proper relationships:

- **users** - User accounts with roles
- **categories** - Product categories with subcategories support
- **products** - Product catalog
- **product_images** - Multiple images per product
- **product_variants** - Size/color variants
- **cart** - Shopping carts
- **cart_items** - Cart line items
- **addresses** - User delivery addresses
- **coupons** - Discount coupons
- **orders** - Order headers
- **order_items** - Order line items
- **payments** - Payment transactions
- **reviews** - Product reviews
- **wishlist** - Saved products

## ⚙️ Installation & Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd backend
```

### 2. Configure MySQL Database
```sql
CREATE DATABASE jewelry_shop;
```

### 3. Update Application Properties
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/jewelry_shop
spring.datasource.username=your_username
spring.datasource.password=your_password

# JWT Secret (Generate a secure secret)
jwt.secret=your_jwt_secret_key_here
jwt.expiration=86400000

# Razorpay Credentials
razorpay.key.id=your_razorpay_key_id
razorpay.key.secret=your_razorpay_key_secret
```

### 4. Build the Project
```bash
mvn clean install
```

### 5. Run the Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

### Swagger UI
Once the application is running, access the interactive API documentation:
```
http://localhost:8080/swagger-ui.html
```

### API Endpoints Overview

#### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user
- `GET /api/auth/me` - Get current user

#### User Management
- `GET /api/users/profile` - Get user profile
- `PUT /api/users/update` - Update profile
- `DELETE /api/users/delete` - Delete account
- `GET /api/admin/users` - List all users (Admin)
- `PUT /api/admin/users/block/{id}` - Block/unblock user (Admin)

#### Categories
- `GET /api/categories` - Get all categories
- `POST /api/admin/categories` - Create category (Admin)
- `PUT /api/admin/categories/{id}` - Update category (Admin)
- `DELETE /api/admin/categories/{id}` - Delete category (Admin)

#### Products
- `GET /api/products` - Get all products (paginated)
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products/search?keyword=` - Search products
- `GET /api/products/category/{id}` - Filter by category
- `GET /api/products/featured` - Get featured products
- `POST /api/admin/products` - Create product (Admin)
- `PUT /api/admin/products/{id}` - Update product (Admin)
- `DELETE /api/admin/products/{id}` - Delete product (Admin)

#### Shopping Cart
- `GET /api/cart` - Get user cart
- `POST /api/cart/add` - Add item to cart
- `PUT /api/cart/update` - Update cart item
- `DELETE /api/cart/remove/{productId}` - Remove item
- `DELETE /api/cart/clear` - Clear cart

#### Orders
- `POST /api/orders/place` - Place new order
- `GET /api/orders/my` - Get user orders
- `GET /api/orders/{id}` - Get order details
- `PUT /api/orders/cancel/{id}` - Cancel order
- `GET /api/admin/orders` - Get all orders (Admin)
- `PUT /api/admin/orders/status/{id}` - Update order status (Admin)

#### Payment
- `POST /api/payment/create` - Create Razorpay order
- `POST /api/payment/verify` - Verify payment
- `POST /api/payment/refund` - Process refund (Admin)

#### Reviews
- `POST /api/reviews` - Add review
- `PUT /api/reviews/{id}` - Update review
- `DELETE /api/reviews/{id}` - Delete review
- `GET /api/reviews/product/{productId}` - Get product reviews

#### Wishlist
- `GET /api/wishlist` - Get wishlist
- `POST /api/wishlist/add?productId=` - Add to wishlist
- `DELETE /api/wishlist/remove/{productId}` - Remove from wishlist

#### Addresses
- `GET /api/addresses` - Get user addresses
- `POST /api/addresses` - Create address
- `PUT /api/addresses/{id}` - Update address
- `DELETE /api/addresses/{id}` - Delete address

#### Coupons
- `POST /api/admin/coupons` - Create coupon (Admin)
- `POST /api/coupons/apply` - Apply coupon code

#### Analytics (Admin)
- `GET /api/admin/analytics/summary` - Dashboard summary
- `GET /api/admin/analytics/monthly?year=2026` - Monthly sales report

## 🔐 Authentication

### Register a User
```bash
POST /api/auth/register
Content-Type: application/json

{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "phone": "1234567890"
}
```

### Login
```bash
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```

Response:
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "user": {
      "id": 1,
      "fullName": "John Doe",
      "email": "john@example.com",
      "role": "USER"
    }
  },
  "timestamp": "2026-02-09T10:30:00"
}
```

### Using JWT Token
Include the token in the Authorization header:
```bash
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## 🧪 Testing

### Using cURL
```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Test User","email":"test@example.com","password":"password123"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'

# Get Products (with token)
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 📊 Response Format

All API responses follow a consistent structure:

### Success Response
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "timestamp": "2026-02-09T10:30:00"
}
```

### Error Response
```json
{
  "success": false,
  "message": "Error description",
  "data": null,
  "timestamp": "2026-02-09T10:30:00"
}
```

### Validation Error Response
```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "email": "Email should be valid",
    "password": "Password must be at least 6 characters"
  },
  "timestamp": "2026-02-09T10:30:00"
}
```

## 🔒 Security Features

- **Password Encryption**: BCrypt hashing
- **JWT Tokens**: Secure, stateless authentication
- **Role-Based Access**: USER and ADMIN roles
- **CORS**: Configurable cross-origin access
- **SQL Injection Protection**: Parameterized queries via JPA
- **XSS Protection**: Input validation and sanitization

## 🎯 Business Logic

### Order Placement Flow
1. Validate cart items and stock availability
2. Apply coupon discount (if provided)
3. Create order with address snapshot
4. Create order items
5. Reduce product stock
6. Clear user cart
7. Return order confirmation

### Stock Management
- Stock is reduced when order is placed
- Stock is restored when order is cancelled
- Validation prevents overselling

### Soft Delete
- Users and products use soft delete (deletedAt timestamp)
- Prevents data loss and maintains referential integrity

## 🚀 Deployment

### Building for Production
```bash
mvn clean package -DskipTests
```

### Running the JAR
```bash
java -jar target/jewelry-ecommerce-1.0.0.jar
```

### Docker Deployment (Optional)
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/jewelry-ecommerce-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## 📝 Environment Variables

For production deployment, use environment variables:
```bash
SPRING_DATASOURCE_URL=jdbc:mysql://production-db:3306/jewelry_shop
SPRING_DATASOURCE_USERNAME=${DB_USERNAME}
SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}
JWT_SECRET=${JWT_SECRET}
RAZORPAY_KEY_ID=${RAZORPAY_KEY_ID}
RAZORPAY_KEY_SECRET=${RAZORPAY_KEY_SECRET}
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the Apache License 2.0

## 👥 Support

For support, email support@jewelryshop.com or create an issue in the repository.

## 🎉 Acknowledgments

- Spring Boot Team
- Spring Security Team
- Razorpay Payment Gateway
- All contributors

---

**Built with ❤️ using Spring Boot 3**
