-- Migration V3: Insert sample data for testing News Platform APIs
-- Provides initial roles, users, taxonomy term, and a sample news article.
-- Location: backend/src/main/resources/db/migration/V3__insert_sample_data.sql

-- Insert roles
INSERT INTO roles (id, name) VALUES
  (1, 'ADMIN'),
  (2, 'EDITOR')
AS new_roles
ON DUPLICATE KEY UPDATE name = new_roles.name;

-- Insert sample user with BCrypt password hash for 'admin'
INSERT INTO users (id, username, email, password, active) VALUES
  (100, 'admin', 'admin@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P8jF4l3q4R4J8C', 1)
AS new_user
ON DUPLICATE KEY UPDATE email = new_user.email, password = new_user.password;

-- Insert taxonomy category
INSERT INTO terms (id, name, vocabulary) VALUES
  (200, 'General', 'category')
AS new_term
ON DUPLICATE KEY UPDATE name = new_term.name;

-- Insert news article authored by user 100 in category 200
INSERT INTO content (id, title, body, teaser, publication_date, author_id,
                     created_at, updated_at, published)
VALUES
  (300, 'Hello World News',
   'This is the body of the first article.',
   'This is the teaser/summary.',
   NOW(), 100, NOW(), NOW(), 1)
AS new_content
ON DUPLICATE KEY UPDATE title = new_content.title;

-- Link news 300 with category 200
INSERT INTO content_terms (content_id, term_id) VALUES
  (300, 200)
AS new_content_term
ON DUPLICATE KEY UPDATE term_id = new_content_term.term_id;
