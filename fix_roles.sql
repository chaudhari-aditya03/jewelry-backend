-- Diagnostic query to check all role values in the database
-- Run this in your MySQL client (MySQL Workbench, HeidiSQL, phpMyAdmin, etc.)

USE jewelry_shop;

-- Check ALL users and their exact role values
SELECT id, email, full_name, role, CHAR_LENGTH(role) as role_length, is_active FROM users;

-- Check if there are any non-uppercase role values
SELECT COUNT(*) as lowercase_roles FROM users WHERE role NOT IN ('ADMIN', 'USER');

-- Fix any lowercase or incorrect role values
UPDATE users SET role = 'ADMIN' WHERE LOWER(role) = 'admin' AND role != 'ADMIN';
UPDATE users SET role = 'USER' WHERE LOWER(role) = 'user' AND role != 'USER';

-- Show final state
SELECT id, email, full_name, role, is_active FROM users;
