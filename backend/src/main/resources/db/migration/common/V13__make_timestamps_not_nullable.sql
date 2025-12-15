-- V13: Make Timestamps Not Nullable
-- This script updates the content table to ensure created_at and updated_at are not nullable,
-- aligning the schema with the entity validation rules.

-- First, update existing null values to a sensible default (e.g., the publication_date)
-- to avoid errors when applying the NOT NULL constraint.
UPDATE content
SET created_at = publication_date,
    updated_at = publication_date
WHERE created_at IS NULL
   OR updated_at IS NULL;

-- Now, alter the table to enforce the NOT NULL constraint.
-- The exact syntax can vary between database systems (e.g., MySQL vs. PostgreSQL).
-- This syntax is generally compatible with MySQL.
ALTER TABLE content
    MODIFY COLUMN created_at DATETIME NOT NULL,
    MODIFY COLUMN updated_at DATETIME NOT NULL;
