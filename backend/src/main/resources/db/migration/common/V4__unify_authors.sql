-- Migration V4: Unify all legacy authors into a single "ImportedAuthor"
-- Purpose: simplify migrated Drupal data -> every old article has the same author
-- Location: backend/src/main/resources/db/migration/V4__unify_authors.sql

-- 1. Insert or ensure a single "ImportedAuthor" user exists
INSERT INTO users (id, username, email, password, active)
VALUES (999, 'imported_author', 'imported@author.local', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P8jF4l3q4R4J8C', 1)
AS new_author
ON DUPLICATE KEY UPDATE email = new_author.email, password = new_author.password;

-- 2. Reassign all news records from any old author to the new ImportedAuthor
UPDATE content
SET author_id = 999;

-- 3. Optionally delete all legacy authors except the ImportedAuthor and your active staff accounts
-- ⚠️ Only do this if you are 100% sure you don't want to preserve old user data
-- DELETE FROM users WHERE id <> 999 AND id NOT IN (SELECT id FROM active_staff_users);

-- Note: Step 3 is commented out for safety - run manually if needed.