-- V12: Create tables for Homepage Blocks functionality

-- Create the homepage_settings table to store the current display mode
CREATE TABLE homepage_settings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    mode VARCHAR(255) NOT NULL UNIQUE
);

-- Insert the default mode
INSERT INTO homepage_settings (mode) VALUES ('SIMPLE');

-- Create the home_page_block table
CREATE TABLE home_page_block (
    id INT AUTO_INCREMENT PRIMARY KEY,
    weight INT NOT NULL DEFAULT 0,
    block_type VARCHAR(255) NOT NULL,
    news_count INT,
    show_teaser BOOLEAN,
    title_font_size VARCHAR(255),
    content TEXT
);

-- Create the join table for blocks and taxonomy terms
CREATE TABLE home_page_block_taxonomy_term (
    home_page_block_id INT NOT NULL,
    taxonomy_term_id INT NOT NULL,
    PRIMARY KEY (home_page_block_id, taxonomy_term_id),
    FOREIGN KEY (home_page_block_id) REFERENCES home_page_block(id) ON DELETE CASCADE,
    FOREIGN KEY (taxonomy_term_id) REFERENCES taxonomy_term(id) ON DELETE CASCADE
);
