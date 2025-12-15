-- V12__make_timestamps_not_nullable.sql
-- This migration was originally V12 but was renumbered to V14 due to a version conflict.
-- This script makes created_at and updated_at columns NOT NULL across several tables.
-- It includes UPDATE statements to handle existing NULL values before applying the constraint.

-- Update existing NULL values in channel_settings
UPDATE `channel_settings`
SET
    `created_at` = COALESCE(`created_at`, CURRENT_TIMESTAMP),
    `updated_at` = COALESCE(`updated_at`, CURRENT_TIMESTAMP)
WHERE `created_at` IS NULL OR `updated_at` IS NULL;

ALTER TABLE `channel_settings`
    MODIFY COLUMN `created_at` DATETIME(6) NOT NULL,
    MODIFY COLUMN `updated_at` DATETIME(6) NOT NULL;

-- Update existing NULL values in homepage_block
UPDATE `homepage_block`
SET
    `created_at` = COALESCE(`created_at`, CURRENT_TIMESTAMP),
    `updated_at` = COALESCE(`updated_at`, CURRENT_TIMESTAMP)
WHERE `created_at` IS NULL OR `updated_at` IS NULL;

ALTER TABLE `homepage_block`
    MODIFY COLUMN `created_at` DATETIME(6) NOT NULL,
    MODIFY COLUMN `updated_at` DATETIME(6) NOT NULL;

-- Update existing NULL values in homepage_block_item
UPDATE `homepage_block_item`
SET
    `created_at` = COALESCE(`created_at`, CURRENT_TIMESTAMP),
    `updated_at` = COALESCE(`updated_at`, CURRENT_TIMESTAMP)
WHERE `created_at` IS NULL OR `updated_at` IS NULL;

ALTER TABLE `homepage_block_item`
    MODIFY COLUMN `created_at` DATETIME(6) NOT NULL,
    MODIFY COLUMN `updated_at` DATETIME(6) NOT NULL;

-- Update existing NULL values in permission
UPDATE `permission`
SET
    `created_at` = COALESCE(`created_at`, CURRENT_TIMESTAMP),
    `updated_at` = COALESCE(`updated_at`, CURRENT_TIMESTAMP)
WHERE `created_at` IS NULL OR `updated_at` IS NULL;

ALTER TABLE `permission`
    MODIFY COLUMN `created_at` DATETIME(6) NOT NULL,
    MODIFY COLUMN `updated_at` DATETIME(6) NOT NULL;

-- Update existing NULL values in role
UPDATE `role`
SET
    `created_at` = COALESCE(`created_at`, CURRENT_TIMESTAMP),
    `updated_at` = COALESCE(`updated_at`, CURRENT_TIMESTAMP)
WHERE `created_at` IS NULL OR `updated_at` IS NULL;

ALTER TABLE `role`
    MODIFY COLUMN `created_at` DATETIME(6) NOT NULL,
    MODIFY COLUMN `updated_at` DATETIME(6) NOT NULL;

-- Update existing NULL values in static_page
UPDATE `static_page`
SET
    `created_at` = COALESCE(`created_at`, CURRENT_TIMESTAMP),
    `updated_at` = COALESCE(`updated_at`, CURRENT_TIMESTAMP)
WHERE `created_at` IS NULL OR `updated_at` IS NULL;

ALTER TABLE `static_page`
    MODIFY COLUMN `created_at` DATETIME(6) NOT NULL,
    MODIFY COLUMN `updated_at` DATETIME(6) NOT NULL;

-- Update existing NULL values in user
UPDATE `user`
SET
    `created_at` = COALESCE(`created_at`, CURRENT_TIMESTAMP),
    `updated_at` = COALESCE(`updated_at`, CURRENT_TIMESTAMP)
WHERE `created_at` IS NULL OR `updated_at` IS NULL;

ALTER TABLE `user`
    MODIFY COLUMN `created_at` DATETIME(6) NOT NULL,
    MODIFY COLUMN `updated_at` DATETIME(6) NOT NULL;

-- Update existing NULL values in user_role
UPDATE `user_role`
SET
    `created_at` = COALESCE(`created_at`, CURRENT_TIMESTAMP),
    `updated_at` = COALESCE(`updated_at`, CURRENT_TIMESTAMP)
WHERE `created_at` IS NULL OR `updated_at` IS NULL;

ALTER TABLE `user_role`
    MODIFY COLUMN `created_at` DATETIME(6) NOT NULL,
    MODIFY COLUMN `updated_at` DATETIME(6) NOT NULL;
