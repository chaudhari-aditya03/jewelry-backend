-- SQL Migration: Add Image Metadata Columns to product_images Table
-- Date: February 13, 2026
-- Purpose: Store additional image metadata (filename, path, size, mime type, upload timestamp)

-- Add new columns to product_images table
ALTER TABLE product_images
ADD COLUMN image_name VARCHAR(255) NULL AFTER image_url,
ADD COLUMN image_path VARCHAR(500) NULL AFTER image_name,
ADD COLUMN file_size BIGINT NULL AFTER image_path,
ADD COLUMN mime_type VARCHAR(100) NULL AFTER file_size,
ADD COLUMN uploaded_at DATETIME NULL AFTER mime_type;

-- Set uploaded_at to current timestamp for existing images
UPDATE product_images SET uploaded_at = NOW() WHERE uploaded_at IS NULL;

-- Create index on product_id for faster queries
CREATE INDEX idx_product_images_product_id ON product_images(product_id);

-- Create index on is_primary for faster filtering
CREATE INDEX idx_product_images_is_primary ON product_images(is_primary);

-- Verify the changes
DESCRIBE product_images;
