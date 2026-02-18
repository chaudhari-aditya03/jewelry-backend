package com.jewelryshop.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class to generate BCrypt password hashes
 * Run this class to generate encoded passwords for manual user creation
 */
public class PasswordEncoderUtil {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        System.out.println("=".repeat(60));
        System.out.println("BCrypt Password Encoder Utility");
        System.out.println("=".repeat(60));
        
        // Generate hash specifically for admin123
        String adminPassword = "admin123";
        String adminHash = encoder.encode(adminPassword);
        
        System.out.println("\n>>> ADMIN PASSWORD <<<");
        System.out.println("Plain Password: " + adminPassword);
        System.out.println("BCrypt Hash:    " + adminHash);
        System.out.println("\nUse this SQL to update/insert admin:");
        System.out.println("UPDATE users SET password = '" + adminHash + "' WHERE email = 'admin@jewelryshop.com';");
        
        System.out.println("\n" + "=".repeat(60));
        
        // Generate hashes for other common passwords
        System.out.println("\nOther common passwords:");
        String[] passwords = {
            "password123",
            "Admin@2026",
            "test123"
        };
        
        for (String password : passwords) {
            String encoded = encoder.encode(password);
            System.out.println("\nPassword: " + password);
            System.out.println("Hash:     " + encoded);
        }
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Copy the BCrypt hash and use it in your SQL statement");
        System.out.println("=".repeat(60));
    }
}
