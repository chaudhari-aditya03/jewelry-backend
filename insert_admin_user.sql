-- Manual Admin User Creation for Jewelry E-Commerce
-- Run this in your MySQL client (MySQL Workbench, HeidiSQL, phpMyAdmin, etc.)

USE jewelry_shop;

-- =====================================================
-- STEP 1: Clean up any existing admin users (optional)
-- =====================================================
-- DELETE FROM users WHERE email IN ('admin@gmail.com', 'admin@jewelryshop.com');

-- =====================================================
-- STEP 2: Insert Admin User
-- =====================================================
-- Email: admin@jewelryshop.com
-- Password: admin123
-- 
-- This BCrypt hash was generated for the password "admin123"

INSERT INTO users (full_name, email, password, phone, role, is_active, email_verified, created_at, updated_at)
VALUES (
    'Super Admin',
    'admin@jewelryshop.com',
    '$2a$10$EblZqNptyYVCLV/g3ZqQAOK7gF7vMXL4ekF5svpXv9RhDKfXa1JCW',
    '1234567890',
    'ADMIN',
    1,
    0,
    NOW(),
    NOW()
);

-- =====================================================
-- STEP 3: Verify the Admin User was Created
-- =====================================================
SELECT id, email, full_name, role, is_active, email_verified, created_at 
FROM users 
WHERE role = 'ADMIN';

-- =====================================================
-- LOGIN CREDENTIALS:
-- =====================================================
-- Email: admin@jewelryshop.com
-- Password: admin123
--
-- =====================================================
-- TROUBLESHOOTING: If login still fails
-- =====================================================
-- 1. Check the user exists:
--    SELECT * FROM users WHERE email = 'admin@jewelryshop.com';
--
-- 2. Generate a new BCrypt hash using the PasswordEncoderUtil class:
--    cd backend
--    mvn exec:java -Dexec.mainClass="com.jewelryshop.util.PasswordEncoderUtil"
--
-- 3. Update the password with the new hash:
--    UPDATE users 
--    SET password = 'YOUR_NEW_BCRYPT_HASH_HERE' 
--    WHERE email = 'admin@jewelryshop.com';
--
-- =====================================================
