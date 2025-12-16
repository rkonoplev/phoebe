-- Migration V11: Add static/technical pages for footer links
-- Creates page_type vocabulary and sample static pages (About, Contact, Privacy, etc.)
-- These pages use the existing News/Content entity but are marked with page_type taxonomy

-- Insert page_type vocabulary terms
INSERT INTO terms (name, vocabulary) VALUES
  ('about', 'page_type'),
  ('contact', 'page_type'),
  ('privacy', 'page_type'),
  ('terms_of_service', 'page_type'),
  ('advertising', 'page_type'),
  ('editorial_statute', 'page_type'),
  ('community_rules', 'page_type'),
  ('technical_sheet', 'page_type')
AS new_terms
ON DUPLICATE KEY UPDATE name = new_terms.name;

-- Insert static pages (using admin user id=100 as author)
-- About Us page
INSERT INTO content (title, body, teaser, publication_date, author_id, created_at, updated_at, published)
VALUES
  ('About Us',
   '<h2>About Phoebe News</h2><p>Phoebe News is a modern news platform built with cutting-edge technology to deliver the latest news and insights.</p><p>Our mission is to provide accurate, timely, and engaging content to our readers worldwide.</p>',
   'Learn more about Phoebe News and our mission',
   NOW(), 100, NOW(), NOW(), 1)
AS new_about
ON DUPLICATE KEY UPDATE title = new_about.title;

-- Contact page
INSERT INTO content (title, body, teaser, publication_date, author_id, created_at, updated_at, published)
VALUES
  ('Contact Us',
   '<h2>Get in Touch</h2><p>Email: <a href="mailto:contact@phoebe.news">contact@phoebe.news</a></p><p>Phone: +1 (555) 123-4567</p><p>Address: 123 News Street, Media City, MC 12345</p>',
   'Contact information for Phoebe News',
   NOW(), 100, NOW(), NOW(), 1)
AS new_contact
ON DUPLICATE KEY UPDATE title = new_contact.title;

-- Privacy Policy page
INSERT INTO content (title, body, teaser, publication_date, author_id, created_at, updated_at, published)
VALUES
  ('Privacy Policy',
   CONCAT('<h2>Privacy Policy</h2><p>Last updated: ', CURRENT_DATE, '</p><p>We respect your privacy and are committed to protecting your personal data.</p><p>This privacy policy explains how we collect, use, and safeguard your information when you visit our website.</p>'),
   'Our commitment to protecting your privacy',
   NOW(), 100, NOW(), NOW(), 1)
AS new_privacy
ON DUPLICATE KEY UPDATE title = new_privacy.title;

-- Terms of Service page
INSERT INTO content (title, body, teaser, publication_date, author_id, created_at, updated_at, published)
VALUES
  ('Terms of Service',
   '<h2>Terms of Service</h2><p>By accessing and using Phoebe News, you agree to be bound by these terms and conditions.</p><p>Please read these terms carefully before using our services.</p>',
   'Terms and conditions for using Phoebe News',
   NOW(), 100, NOW(), NOW(), 1)
AS new_terms_service
ON DUPLICATE KEY UPDATE title = new_terms_service.title;

-- Advertising page
INSERT INTO content (title, body, teaser, publication_date, author_id, created_at, updated_at, published)
VALUES
  ('Advertise With Us',
   '<h2>Advertising Opportunities</h2><p>Reach millions of engaged readers with Phoebe News advertising solutions.</p><p>Contact our advertising team: <a href="mailto:ads@phoebe.news">ads@phoebe.news</a></p>',
   'Advertising opportunities at Phoebe News',
   NOW(), 100, NOW(), NOW(), 1)
AS new_advertising
ON DUPLICATE KEY UPDATE title = new_advertising.title;

-- Editorial Statute page
INSERT INTO content (title, body, teaser, publication_date, author_id, created_at, updated_at, published)
VALUES
  ('Editorial Statute',
   '<h2>Editorial Statute</h2><p>Our editorial principles guide our journalism and ensure the highest standards of accuracy and integrity.</p>',
   'Our editorial principles and standards',
   NOW(), 100, NOW(), NOW(), 1)
AS new_editorial
ON DUPLICATE KEY UPDATE title = new_editorial.title;

-- Community Rules page
INSERT INTO content (title, body, teaser, publication_date, author_id, created_at, updated_at, published)
VALUES
  ('Community Rules',
   '<h2>Community Guidelines</h2><p>We foster a respectful and constructive community. Please follow these guidelines when participating in discussions.</p>',
   'Guidelines for our community members',
   NOW(), 100, NOW(), NOW(), 1)
AS new_community
ON DUPLICATE KEY UPDATE title = new_community.title;

-- Technical Sheet page
INSERT INTO content (title, body, teaser, publication_date, author_id, created_at, updated_at, published)
VALUES
  ('Technical Information',
   '<h2>Technical Sheet</h2><p>Phoebe News is built with modern technology stack including Java, Spring Boot, Next.js, and more.</p>',
   'Technical information about our platform',
   NOW(), 100, NOW(), NOW(), 1)
AS new_technical
ON DUPLICATE KEY UPDATE title = new_technical.title;

-- Link pages with their page_type terms
-- Note: We need to get the IDs dynamically, so we use subqueries
INSERT INTO content_terms (content_id, term_id)
SELECT c.id, t.id
FROM content c
CROSS JOIN terms t
WHERE c.title = 'About Us' AND t.name = 'about' AND t.vocabulary = 'page_type'
AS new_about_link
ON DUPLICATE KEY UPDATE term_id = new_about_link.term_id;

INSERT INTO content_terms (content_id, term_id)
SELECT c.id, t.id
FROM content c
CROSS JOIN terms t
WHERE c.title = 'Contact Us' AND t.name = 'contact' AND t.vocabulary = 'page_type'
AS new_contact_link
ON DUPLICATE KEY UPDATE term_id = new_contact_link.term_id;

INSERT INTO content_terms (content_id, term_id)
SELECT c.id, t.id
FROM content c
CROSS JOIN terms t
WHERE c.title = 'Privacy Policy' AND t.name = 'privacy' AND t.vocabulary = 'page_type'
AS new_privacy_link
ON DUPLICATE KEY UPDATE term_id = new_privacy_link.term_id;

INSERT INTO content_terms (content_id, term_id)
SELECT c.id, t.id
FROM content c
CROSS JOIN terms t
WHERE c.title = 'Terms of Service' AND t.name = 'terms_of_service' AND t.vocabulary = 'page_type'
AS new_terms_link
ON DUPLICATE KEY UPDATE term_id = new_terms_link.term_id;

INSERT INTO content_terms (content_id, term_id)
SELECT c.id, t.id
FROM content c
CROSS JOIN terms t
WHERE c.title = 'Advertise With Us' AND t.name = 'advertising' AND t.vocabulary = 'page_type'
AS new_ad_link
ON DUPLICATE KEY UPDATE term_id = new_ad_link.term_id;

INSERT INTO content_terms (content_id, term_id)
SELECT c.id, t.id
FROM content c
CROSS JOIN terms t
WHERE c.title = 'Editorial Statute' AND t.name = 'editorial_statute' AND t.vocabulary = 'page_type'
AS new_editorial_link
ON DUPLICATE KEY UPDATE term_id = new_editorial_link.term_id;

INSERT INTO content_terms (content_id, term_id)
SELECT c.id, t.id
FROM content c
CROSS JOIN terms t
WHERE c.title = 'Community Rules' AND t.name = 'community_rules' AND t.vocabulary = 'page_type'
AS new_community_link
ON DUPLICATE KEY UPDATE term_id = new_community_link.term_id;

INSERT INTO content_terms (content_id, term_id)
SELECT c.id, t.id
FROM content c
CROSS JOIN terms t
WHERE c.title = 'Technical Information' AND t.name = 'technical_sheet' AND t.vocabulary = 'page_type'
AS new_tech_link
ON DUPLICATE KEY UPDATE term_id = new_tech_link.term_id;
