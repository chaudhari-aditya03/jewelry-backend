-- Migration script to add description column to categories table

-- Add description column to categories table
ALTER TABLE categories 
ADD COLUMN description TEXT AFTER name;

-- Optional: Update existing categories with default descriptions
UPDATE categories SET description = 'No description available' WHERE description IS NULL;

-- Verify the change
SELECT * FROM categories;
