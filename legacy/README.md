# Legacy Docker Compose and Configuration Files

**WARNING:** This directory contains configuration files that were used during the migration from Drupal 6
to the Spring Boot News Platform. They are not used in current development and are preserved for historical
reference.

[Русская версия](./README_RU.md)

---

## Archived Resources Overview

- **[Transformation Scripts](./archived_migration_scripts/README.md):** SQL scripts that converted raw Drupal data.
- **[Original Drupal 6 Dump](./original_drupal_dump/README.md):** The initial database dump before migration.

---

## File Descriptions

### `docker-compose.yml` (Legacy)
- **Original location**: Project root directory (`/docker-compose.yml`)
- **Description**: This was the single file used to run all services (MySQL, backend, frontend). It did not provide flexibility for different development modes.
- **Status**: OBSOLETE. Replaced by a modular structure with `docker-compose.base.yml`, `docker-compose.hybrid.yml`, and `docker-compose.prod.yml`. Preserved here for historical context.

### `legacy-gradle-ci.yml`
A legacy version of the GitHub Actions workflow file.
- **Key Differences**: Split into multiple `jobs`, manual DB management via `services`, automated `gitleaks` scan.
- **Status**: OBSOLETE. Preserved as an example of the "classic" CI setup.

### `application-ci.yml`
A legacy Spring profile used in CI.
- **Description**: Configured tests to use a fast, in-memory H2 database in MySQL compatibility mode.
- **Status**: OBSOLETE. Replaced by the `integration-test` profile which uses Testcontainers.

### `application-dev.yml`
A legacy Spring profile for dev environments.
- **Description**: Was intended for dev-staging or CI where the MySQL database was provided by an external Docker container (`mysql-dev`).
- **Status**: OBSOLETE. Its role has been consolidated into the `local` profile for local development.

### `DatabaseProperties.java`
Legacy Spring Boot configuration class used during Drupal 6 migration. This file:
- **Original location**: `backend/src/main/java/com/example/phoebe/config/DatabaseProperties.java`
- Defined custom database connection pool settings for migration workload
- Provided extended connection timeouts for large data transfers
- Enabled SQL logging for debugging migration queries
- Used `@ConfigurationProperties(prefix = "app.database")` for configuration binding
- **Status**: OBSOLETE - Migration completed, standard Spring datasource config used

### `Makefile`
Legacy testing utility with API endpoint shortcuts. This file:
- **Original location**: Project root directory (`/Makefile`)
- Provided `make test-news-get`, `make test-news-post` commands for API testing
- Used outdated API endpoints (`/api/news` instead of `/api/public/news`)
- Required manual credential setup via environment variables
- **Status**: OBSOLETE - Replaced by curl examples in API documentation and Postman collections.

### `docker-compose.drupal.yml`
Temporary Docker Compose setup for Drupal 6 data migration. This file:
- **Original location**: Project root directory (`/docker-compose.drupal.yml`)
- Runs MySQL 5.7 for compatibility with legacy Drupal 6 database dumps.
- Exposes MySQL on port 3307 to avoid conflicts with the main application database.
- Auto-loads database dumps from the `./legacy/original_drupal_dump` directory.
- Mounts migration scripts from the `./legacy/archived_migration_scripts` directory.
- Uses a separate volume `mysql_data_drupal6` for isolation.

### `docker-compose.override.yml`
Example of a production override configuration for Docker Compose. This file provides:
- **Original location**: Project root directory (`/docker-compose.override.yml`)
- Production-ready security with Docker secrets for sensitive data.
- Health checks for both database and application services.
- Proper service dependencies and restart policies.
- Secure database configuration without external port exposure.

### `ExampleTest.java`
Legacy placeholder test file from early development. This file:
- **Original location**: `backend/src/test/java/com/example/phoebe/ExampleTest.java`
- Simple JUnit 5 test with `assertTrue(true)` assertion.
- Created as an initial test structure during project setup.
- **Status**: OBSOLETE - Replaced by comprehensive unit and integration tests.

---

## Migration Context

These files were part of the migration and early development process from a legacy Drupal 6 site to the modern Spring Boot News Platform. They include:

- **Migration-specific configurations** (Docker Compose files for Drupal 6 compatibility)
- **Legacy development tools** (Makefile with outdated API endpoints)
- **Migration-era Spring Boot classes** (custom database properties)

They are preserved here for reference and to help developers understand the migration approach and evolution of the project.

### `migrate_volumes.sh` and `rollback_migration.sh`
Docker volume migration scripts used during the news-platform → phoebe renaming. These files:
- **Original location**: Project root directory
- Automated the migration from `news-mysql` to `phoebe-mysql` containers
- Created database backups in `db_dumps/` before migration
- Updated `docker-compose.yml` with new container names
- Fixed MapStruct warnings and Docker build contexts
- **Status**: COMPLETED - Migration successfully executed, containers renamed to phoebe-*
- **Backup created**: `db_dumps/backup_before_migration_*.sql`

---

## Current Development

For current development, use the `Makefile` and the modular `docker-compose.*.yml` files in the project root.