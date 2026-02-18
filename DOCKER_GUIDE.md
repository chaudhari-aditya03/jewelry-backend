# Docker Setup and Testing Guide

## Test Docker Build Locally (Optional but Recommended)

### Prerequisites
- Docker Desktop installed ([Download](https://www.docker.com/products/docker-desktop))
- Docker running

### Build Docker Image

```powershell
# From backend directory
cd backend

# Build the image
docker build -t jewelry-backend:latest .
```

### Run Container Locally

```powershell
# Run with environment variables
docker run -p 8080:8080 `
  -e SPRING_PROFILES_ACTIVE=dev `
  -e DATABASE_URL=jdbc:mysql://host.docker.internal:3306/jewelry_ecommerce `
  -e DATABASE_USERNAME=root `
  -e DATABASE_PASSWORD=your_password `
  -e JWT_SECRET=your_secret_here `
  -e CORS_ALLOWED_ORIGINS=https://jewelryeshop.vercel.app,http://localhost:5173 `
  jewelry-backend:latest
```

### Test the Container

```powershell
# Check health
curl http://localhost:8080/api/health

# Should return:
# {"status":"UP","service":"Jewelry E-Commerce API",...}
```

### Stop Container

```powershell
# List running containers
docker ps

# Stop container
docker stop <container_id>
```

---

## Docker Compose (Alternative for Local Dev)

Create `docker-compose.yml` in backend directory:

```yaml
version: '3.8'

services:
  backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - DATABASE_URL=jdbc:mysql://mysql:3306/jewelry_ecommerce
      - DATABASE_USERNAME=root
      - DATABASE_PASSWORD=password
      - JWT_SECRET=your_secret_here
      - CORS_ALLOWED_ORIGINS=https://jewelryeshop.vercel.app,http://localhost:5173
    depends_on:
      - mysql

  mysql:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=password
      - MYSQL_DATABASE=jewelry_ecommerce
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```

Run with:
```powershell
docker-compose up
```

---

## Dockerfile Explanation

### Multi-Stage Build

1. **Build Stage** (`eclipse-temurin:17-jdk-alpine`)
   - Uses JDK to compile the application
   - Maven builds the JAR file
   - Results in `jewelry-ecommerce-1.0.0.jar`

2. **Production Stage** (`eclipse-temurin:17-jre-alpine`)
   - Uses lightweight JRE (no compiler needed)
   - Copies only the JAR file
   - Smaller final image (~200MB vs ~400MB)

### Security Features

- **Non-root user**: Runs as `spring` user (not root)
- **Alpine Linux**: Minimal attack surface
- **Health check**: Monitors application status

### Environment Variables

Set via Render dashboard or `-e` flag:
- `SPRING_PROFILES_ACTIVE` - Spring profile (prod/dev)
- `DATABASE_URL` - MySQL connection string
- `DATABASE_USERNAME` - DB user
- `DATABASE_PASSWORD` - DB password
- `JWT_SECRET` - JWT signing key
- `CORS_ALLOWED_ORIGINS` - Allowed frontend URLs

---

## Troubleshooting

### Build Fails

**Issue**: Maven dependencies download timeout
**Solution**: 
```dockerfile
# Add to Dockerfile before dependency download
RUN apk add --no-cache curl
```

### Port Already in Use

**Issue**: Port 8080 already taken
**Solution**:
```powershell
# Stop local backend
# Or use different port
docker run -p 8081:8080 ...
```

### Health Check Fails

**Issue**: Container unhealthy
**Solution**:
```powershell
# Check logs
docker logs <container_id>

# Access container
docker exec -it <container_id> sh
wget http://localhost:8080/api/health
```

### Database Connection Fails

**Issue**: Can't connect to MySQL
**Solution**:
- Use `host.docker.internal` instead of `localhost` on Windows/Mac
- Or use Docker network
- Check MySQL is accessible from container

---

## Deploy to Render

Once Docker build works locally:

1. Push code to GitHub
2. Render will auto-detect Dockerfile
3. Follow [RENDER_DEPLOYMENT.md](RENDER_DEPLOYMENT.md)
4. Render builds and deploys automatically

---

## Useful Commands

```powershell
# Build image
docker build -t jewelry-backend .

# Run container
docker run -p 8080:8080 jewelry-backend

# View logs
docker logs <container_id>

# Stop container
docker stop <container_id>

# Remove container
docker rm <container_id>

# Remove image
docker rmi jewelry-backend

# List images
docker images

# List containers
docker ps -a

# Clean up unused resources
docker system prune
```

---

## Performance Tips

### Optimize Build Time

1. **Layer caching**: Dependencies cached separately from code
2. **Multi-stage build**: Smaller final image
3. **Maven offline**: Dependencies downloaded once

### Reduce Image Size

Current optimizations:
- ✅ Alpine Linux (small base)
- ✅ JRE instead of JDK
- ✅ Multi-stage build
- ✅ Only JAR copied to final image

Result: ~200MB final image

---

## Next Steps

1. ✅ Test locally with Docker
2. ✅ Push to GitHub
3. ✅ Deploy to Render (uses this Dockerfile)
4. ✅ Monitor logs in Render dashboard

For production deployment, see [RENDER_DEPLOYMENT.md](RENDER_DEPLOYMENT.md)
