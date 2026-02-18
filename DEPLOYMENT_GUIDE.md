# Jewelry E-Commerce Backend - Deployment Guide

## Configuration Architecture

The application uses a multi-layered configuration strategy to support different environments with minimal code changes.

### Configuration Files Structure

```
src/main/resources/
├── application.properties          # Base/default configuration
├── application-dev.properties      # Development environment
├── application-prod.properties     # Production environment
└── db.properties                   # Database-specific configuration
```

---

## Configuration Files Overview

### 1. **application.properties** (Base Configuration)

**Purpose**: Default settings that apply to all environments unless overridden by environment-specific profiles.

**Key Settings**:
- Server port and compression
- Database connection pooling (HikariCP)
- JPA/Hibernate settings
- Pagination defaults
- File upload limits

**When Used**: Always loaded as base configuration.

---

### 2. **db.properties** (Database Configuration)

**Purpose**: Centralized database connection and SQL configuration.

**Contains**:
```properties
# Database Connection
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=...
spring.datasource.username=...
spring.datasource.password=...

# Connection Pool (HikariCP)
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.maximum-pool-size=20
...

# Hibernate Settings
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
...
```

**Usage**: Referenced in `application.properties` for separation of concerns.

---

### 3. **application-dev.properties** (Development Profile)

**Purpose**: Optimized development environment settings.

**Key Differences from Production**:
- ✅ DEBUG logging enabled
- ✅ SQL query display enabled (`show-sql=true`)
- ✅ Swagger UI enabled
- ✅ Smaller connection pool (suitable for single machine)
- ✅ Localhost CORS configuration
- ✅ Development credentials

**Use This When**:
- Local development and testing
- Debugging database issues
- API documentation access needed

**Launch Command**:
```bash
java -jar jewelry-ecommerce-backend.jar --spring.profiles.active=dev
```

Or with Maven:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

---

### 4. **application-prod.properties** (Production Profile)

**Purpose**: Optimized production environment with security and performance.

**Key Differences from Development**:
- ✅ WARNING-level logging only (reduced I/O)
- ✅ SQL queries hidden (security + performance)
- ✅ Swagger UI disabled
- ✅ Larger connection pool (handle concurrent users)
- ✅ Environment variable support for sensitive data
- ✅ HTTPS enforcement settings
- ✅ Security headers enabled

**Use This When**:
- Deploying to staging/production servers
- Running in containerized environments (Docker/Kubernetes)
- Exposed to internet traffic

**Launch Command**:
```bash
java -jar jewelry-ecommerce-backend.jar --spring.profiles.active=prod
```

---

## Environment Variables (Production)

For production deployments, use environment variables instead of hardcoding sensitive data:

### Required Environment Variables

```bash
# Database Configuration
export DATABASE_URL="jdbc:mysql://prod-db-server:3306/jewelry_shop?createDatabaseIfNotExist=false&useSSL=true"
export DATABASE_USERNAME="prod_user"
export DATABASE_PASSWORD="secure_password_here"

# JWT Configuration
export JWT_SECRET="your_256_character_minimum_secret_key_here"

# Razorpay Configuration
export RAZORPAY_KEY_ID="your_razorpay_key_id"
export RAZORPAY_KEY_SECRET="your_razorpay_key_secret"

# CORS Configuration
export ALLOWED_ORIGINS="https://jewelryshop.com,https://www.jewelryshop.com"

# Start application with environment variables
java -jar jewelry-ecommerce-backend.jar --spring.profiles.active=prod
```

---

## Database DDL Auto Behavior

| Profile | DDL Setting | Meaning | Use Case |
|---------|------------|---------|----------|
| dev | `update` | Automatically update schema | Development/testing |
| prod | `validate` | Validate schema only (fail if mismatch) | Production safety |

---

## Connection Pool Configuration

### Development (application-dev.properties)
```properties
minimum-idle=3          # Minimum 3 idle connections
maximum-pool-size=10    # Max 10 total connections
```

### Production (application-prod.properties)
```properties
minimum-idle=10         # Minimum 10 idle connections
maximum-pool-size=30    # Max 30 total connections
```

---

## Logging Levels

### Development
- Root: DEBUG
- Jewelry Shop Packages: DEBUG
- Spring Web: DEBUG
- Spring Security: DEBUG

### Production
- Root: WARN
- Jewelry Shop Packages: INFO
- Spring Web: WARN
- Spring Security: WARN

---

## Swagger/API Documentation Access

| Environment | URL | Status |
|------------|-----|--------|
| Development | `http://localhost:8080/swagger-ui.html` | ✅ Enabled |
| Production | N/A | ❌ Disabled |

---

## CORS Configuration

### Development (Localhost)
```properties
cors.allowed.origins=http://localhost:3000,http://localhost:5173,http://127.0.0.1:3000,http://127.0.0.1:5173
```

### Production (Your Domains)
```properties
cors.allowed.origins=https://jewelryshop.com,https://www.jewelryshop.com
```

---

## Deployment Checklist

### Pre-Deployment
- [ ] Update `application-prod.properties` with production server details
- [ ] Set all required environment variables
- [ ] JWT secret is securely generated (min 256 bits)
- [ ] Database is created and accessible
- [ ] Razorpay credentials are valid
- [ ] SSL certificates are configured (if using HTTPS)

### Build
```bash
# Clean build for production
mvn clean package -DskipTests

# JAR file created at: target/jewelry-ecommerce-backend.jar
```

### Deployment Commands

**Local Development**:
```bash
java -jar jewelry-ecommerce-backend.jar --spring.profiles.active=dev
```

**Staging**:
```bash
java -jar jewelry-ecommerce-backend.jar --spring.profiles.active=dev
```

**Production**:
```bash
export DATABASE_URL="jdbc:mysql://prod-db:3306/jewelry_shop?useSSL=true"
export DATABASE_USERNAME="prod_user"
export DATABASE_PASSWORD="secure_pwd"
export JWT_SECRET="your_secure_key"
export RAZORPAY_KEY_ID="key_id"
export RAZORPAY_KEY_SECRET="key_secret"
export ALLOWED_ORIGINS="https://jewelryshop.com,https://www.jewelryshop.com"

java -jar jewelry-ecommerce-backend.jar --spring.profiles.active=prod
```

---

## Docker Deployment

### Dockerfile Example
```dockerfile
FROM openjdk:17-jdk-slim

COPY target/jewelry-ecommerce-backend.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
```

### Docker Compose Example
```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      DATABASE_URL: jdbc:mysql://mysql:3306/jewelry_shop?useSSL=false
      DATABASE_USERNAME: root
      DATABASE_PASSWORD: password
      JWT_SECRET: ${JWT_SECRET}
      RAZORPAY_KEY_ID: ${RAZORPAY_KEY_ID}
      RAZORPAY_KEY_SECRET: ${RAZORPAY_KEY_SECRET}
    depends_on:
      - mysql

  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: password
      MYSQL_DATABASE: jewelry_shop
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```

---

## Kubernetes Deployment

### ConfigMap (application.properties)
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: jewelry-shop-config
data:
  application-prod.properties: |
    spring.profiles.active=prod
    cors.allowed.origins=https://jewelryshop.com
```

### Secret (Sensitive Data)
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: jewelry-shop-secret
type: Opaque
stringData:
  DATABASE_URL: jdbc:mysql://mysql-service:3306/jewelry_shop
  DATABASE_USERNAME: prod_user
  DATABASE_PASSWORD: <secure_password>
  JWT_SECRET: <secure_jwt_secret>
  RAZORPAY_KEY_ID: <key_id>
  RAZORPAY_KEY_SECRET: <key_secret>
```

---

## Performance Tuning

### Connection Pool Optimization
Adjust based on your expected concurrent users:

```properties
# Light Load (< 100 concurrent): minimum-idle=5, maximum-pool-size=10
# Medium Load (100-500 concurrent): minimum-idle=10, maximum-pool-size=20
# Heavy Load (500+ concurrent): minimum-idle=15, maximum-pool-size=40
```

### Batch Processing
```properties
spring.jpa.properties.hibernate.jdbc.batch_size=30    # Increase for bulk operations
```

### Caching (Optional Future Addition)
```properties
spring.cache.type=redis
spring.redis.host=localhost
spring.redis.port=6379
```

---

## Troubleshooting

### Issue: Database Connection Failed
**Solution**:
1. Verify DATABASE_URL is correct
2. Check DATABASE_USERNAME and PASSWORD
3. Ensure MySQL server is running
4. Check firewall rules

### Issue: Swagger UI Not Accessible
**Solution**:
1. Verify you're using `dev` profile
2. Check if `springdoc.swagger-ui.enabled=true` in profile

### Issue: CORS Errors from Frontend
**Solution**:
1. Add your frontend URL to `cors.allowed.origins`
2. Verify protocol (http vs https)
3. Check proxy configuration

### Issue: JWT Token Validation Failure
**Solution**:
1. Regenerate JWT secret
2. Ensure JWT_SECRET is same across instances
3. Check token expiration time

---

## Monitoring & Health Checks

### Health Endpoint (Production)
```bash
curl http://localhost:8080/actuator/health
```

Response:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    }
  }
}
```

---

## Backup & Recovery

### MySQL Backup
```bash
mysqldump -u root -p jewelry_shop > backup.sql
```

### MySQL Restore
```bash
mysql -u root -p jewelry_shop < backup.sql
```

---

## Security Considerations

1. **Never commit sensitive data** to git
2. **Use environment variables** for secrets
3. **Rotate JWT secrets** periodically
4. **Update dependencies** regularly with `mvn versions:display-dependency-updates`
5. **Enable HTTPS** in production (use reverse proxy like Nginx)
6. **Restrict database access** to application server only
7. **Monitor logs** for suspicious activity

---

## Support & Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [Docker Documentation](https://docs.docker.com/)
