# Use Eclipse Temurin JDK 17 as base image
FROM eclipse-temurin:17-jdk-alpine AS build

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skip tests for faster builds)
RUN ./mvnw clean package -DskipTests

# Production stage - use JRE for smaller image
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy built jar from build stage
COPY --from=build /app/target/jewelry-ecommerce-1.0.0.jar app.jar

# Expose port
EXPOSE 8080

# Set JVM options for production
ENV JAVA_OPTS="-Xms256m -Xmx512m -Dspring.profiles.active=prod"

# Important: Set these environment variables when deploying to Render:
# - DATABASE_URL (MySQL connection string)
# - DATABASE_USERNAME (Database user)
# - DATABASE_PASSWORD (Database password)
# - JWT_SECRET (Secure random string, min 256 bits)
# - RAZORPAY_KEY_ID (Razorpay public key)
# - RAZORPAY_KEY_SECRET (Razorpay secret key)
# - CORS_ALLOWED_ORIGINS (e.g., https://jewelryeshop.vercel.app)
# - SERVER_PORT (default: 8080)
# - SPRING_PROFILES_ACTIVE (default: prod)

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/health || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
