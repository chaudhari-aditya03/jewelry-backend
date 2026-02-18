-- Update email verification status for admin user
-- Run this in your MySQL client (MySQL Workbench, HeidiSQL, phpMyAdmin, etc.)

USE jewelry_shop;

-- Check current admin user status
SELECT id, email, full_name, role, email_verified, is_active FROM users WHERE id = 1;

-- Update email_verified to 0 (false) for admin user
UPDATE users SET email_verified = 0 WHERE id = 1;

-- Verify the change
SELECT id, email, full_name, role, email_verified, is_active FROM users WHERE id = 1;
