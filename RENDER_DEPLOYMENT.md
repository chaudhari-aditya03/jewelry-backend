# Render Deployment Configuration

This file contains setup instructions for deploying the backend to Render.

## Prerequisites

1. GitHub account with repository pushed
2. Render account (https://render.com)
3. MySQL database (can use Render PostgreSQL or external MySQL like Railway/PlanetScale)

## Deployment Steps

### Step 1: Create a MySQL Database

**Option A: Using Railway (Recommended for MySQL)**
1. Go to https://railway.app
2. Create a new project
3. Add MySQL service
4. Copy the connection details

**Option B: Using Render PostgreSQL** (Requires code changes)
1. In Render Dashboard, click "New +" → "PostgreSQL"
2. Name: `jewelry-ecommerce-db`
3. Select free tier or paid
4. Copy the Internal Database URL

### Step 2: Deploy Backend on Render

1. **Create New Web Service**
   - Go to Render Dashboard
   - Click "New +" → "Web Service"
   - Connect your GitHub repository
   - Select the repository

2. **Configure Build Settings**
   - **Name**: `jewelry-ecommerce-backend`
   - **Region**: Choose closest to your users
   - **Branch**: `main` (or your deployment branch)
   - **Root Directory**: `backend`
   - **Runtime**: `Java`
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -Dspring.profiles.active=prod -jar target/jewelry-ecommerce-1.0.0.jar`

3. **Configure Environment Variables**
   
   Click "Advanced" → "Add Environment Variable" and add:

   ```
   SPRING_PROFILES_ACTIVE=prod
   
   # Database Configuration
   DATABASE_URL=jdbc:mysql://YOUR_MYSQL_HOST:3306/jewelry_ecommerce?useSSL=true&serverTimezone=UTC
   DATABASE_USERNAME=your_db_username
   DATABASE_PASSWORD=your_db_password
   
   # JWT Secret (Generate using: openssl rand -base64 64)
   JWT_SECRET=your_very_long_secure_jwt_secret_minimum_256_bits
   JWT_EXPIRATION=86400000
   
   # Razorpay
   RAZORPAY_KEY_ID=your_razorpay_key_id
   RAZORPAY_KEY_SECRET=your_razorpay_key_secret
   
   # CORS - Update after deploying frontend
   CORS_ALLOWED_ORIGINS=https://your-app.vercel.app,http://localhost:5173
   
   # Server
   SERVER_PORT=8080
   ```

4. **Set Instance Type**
   - Free tier or Starter ($7/month recommended)

5. **Click "Create Web Service"**

### Step 3: Database Setup

After deployment, you need to initialize the database:

1. **Connect to your database** using MySQL Workbench or CLI
2. **Run the SQL scripts** in this order:
   ```bash
   # From backend directory
   mysql -h YOUR_HOST -u YOUR_USER -p YOUR_DATABASE < insert_admin_user.sql
   mysql -h YOUR_HOST -u YOUR_USER -p YOUR_DATABASE < fix_roles.sql
   mysql -h YOUR_HOST -u YOUR_USER -p YOUR_DATABASE < add_category_description.sql
   ```

### Step 4: Update CORS After Frontend Deployment

1. Once your frontend is deployed to Vercel, copy the URL
2. Update the `CORS_ALLOWED_ORIGINS` environment variable in Render:
   ```
   CORS_ALLOWED_ORIGINS=https://your-app.vercel.app,https://www.your-app.vercel.app
   ```
3. Redeploy the service

## Health Check

Render will check: `https://your-backend.onrender.com/api/health`

Make sure this endpoint exists or remove health check in settings.

## Troubleshooting

### Build Fails
- Check Java version is 17
- Verify pom.xml is correct
- Check build logs in Render dashboard

### Database Connection Issues
- Verify DATABASE_URL format
- Check database is accessible from external connections
- Verify username/password

### CORS Errors
- Make sure CORS_ALLOWED_ORIGINS includes your frontend URL
- Check frontend is using correct backend URL
- Verify no trailing slashes in URLs

## Important Notes

1. **Free Tier**: Render free tier spins down after inactivity (takes 30s to wake up)
2. **Logs**: Access logs in Render Dashboard → Service → Logs
3. **Custom Domain**: Can add custom domain in Render settings
4. **Database Backups**: Set up automatic backups in database settings

## Render URLs

After deployment:
- Backend API: `https://your-service-name.onrender.com`
- API Docs: `https://your-service-name.onrender.com/swagger-ui.html` (disabled in prod)
- Health Check: `https://your-service-name.onrender.com/api/health`
