-- Fix Admin Password Hash
-- Run this SQL in your MySQL client

USE jewelry_shop;

-- Update admin password to: admin123
UPDATE users 
SET password = '$2a$10$N.zmdr9A6P9gfb3a3xHzBuiKcfb3xBUKjm5x1gJp0YVzDzLxKxG2i'
WHERE email = 'admin@jewelryshop.com';

-- Verify the update
SELECT email, role, is_active FROM users WHERE email = 'admin@jewelryshop.com';

-- Login with: admin@jewelryshop.com / admin123
