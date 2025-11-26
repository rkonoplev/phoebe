> [Back to Documentation Contents](./README.md)
>
> **[MySQL Commands Guide](./MYSQL_GUIDE.md)**: Useful commands for working with MySQL,
> including data manipulation and user account management.

# Database Guide – Phoebe CMS

This comprehensive guide covers the database schema, setup, migration, and troubleshooting for Phoebe CMS.
The schema supports both **clean installations** and **migrated data from Drupal 6**.

## Table of Contents
- [Database Schema](#database-schema)
- [Quick Setup](#quick-setup)
- [Helper Tools](#helper-tools)
- [Migration from Drupal 6 (Historical/Manual Process)](#migration-from-drupal-6-historicalmanual-process)
- [Migration Scripts Reference](#migration-scripts-reference)
- [Spring Boot Migrations](#spring-boot-migrations)
- [Authentication & Access Control](#authentication--access-control)
- [Database Interaction](#database-interaction)
- [Recommendations for Working with Production Databases](#recommendations-for-working-with-production-databases)
- [Troubleshooting](#troubleshooting)
- [Related Documentation](#related-documentation)

---

## Database Schema

### Schema Features

- **User Management**: Authentication with role-based access control
- **Permissions System**: Granular permissions assigned to roles
- **Content Management**: Unified storage for news articles and content
- **Taxonomy System**: Categories and tags with flexible vocabularies
- **Publication Workflow**: Draft/published states with audit trails
- **Migration Support**: Handles legacy Drupal 6 data transformation

### Current Database Schema `phoebe_db` (After All Migrations V1-V10)

```sql
-- ======================================
-- USERS TABLE
-- ======================================
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,              -- BCrypt hashed passwords
    email VARCHAR(255) UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE         -- true = active, false = blocked
);

-- ======================================
-- ROLES TABLE
-- ======================================
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,            -- 'ADMIN', 'EDITOR', etc.
    description VARCHAR(255)                     -- Role description (added in V5)
);

-- ======================================
-- PERMISSIONS TABLE (Added in V5)
-- ======================================
CREATE TABLE permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE            -- 'news:read', 'users:create', etc.
);

-- ======================================
-- USER_ROLES (Many-to-Many)
-- ======================================
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- ======================================
-- ROLE_PERMISSIONS (Many-to-Many, Added in V5)
-- ======================================
CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- ======================================
-- CONTENT TABLE (News Articles)
-- ======================================
CREATE TABLE content (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    body TEXT,                                   -- Full article content
    teaser TEXT,                                 -- Article summary/excerpt
    publication_date DATETIME NOT NULL,
    published BOOLEAN NOT NULL DEFAULT FALSE,    -- Added in V2: draft/published state
    created_at DATETIME,                         -- Audit: creation timestamp
    updated_at DATETIME,                         -- Audit: last modification
    version BIGINT,                              -- Optimistic locking
    author_id BIGINT NOT NULL,                   -- FK to users.id
    FOREIGN KEY (author_id) REFERENCES users(id)
);

-- ======================================
-- TERMS TABLE (Taxonomy)
-- ======================================
CREATE TABLE terms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    vocabulary VARCHAR(100),                     -- 'category', 'tag', etc.
    UNIQUE (name, vocabulary)
);

-- ======================================
-- CONTENT_TERMS (Many-to-Many)
-- ======================================
CREATE TABLE content_terms (
    content_id BIGINT NOT NULL,
    term_id BIGINT NOT NULL,
    PRIMARY KEY (content_id, term_id),
    FOREIGN KEY (content_id) REFERENCES content(id) ON DELETE CASCADE,
    FOREIGN KEY (term_id) REFERENCES terms(id) ON DELETE CASCADE
);
```

### Example Queries

```sql
-- List all published articles with authors
SELECT c.title, c.publication_date, u.username as author
FROM content c
JOIN users u ON c.author_id = u.id
WHERE c.published = TRUE
ORDER BY c.publication_date DESC;

-- Get user permissions through roles
SELECT u.username, p.name as permission
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
JOIN role_permissions rp ON r.id = rp.role_id
JOIN permissions p ON rp.permission_id = p.id
WHERE u.active = TRUE;

-- Find articles by term
SELECT c.title, t.name as term
FROM content c
JOIN content_terms ct ON c.id = ct.content_id
JOIN terms t ON ct.term_id = t.id
WHERE t.name = 'Technology';
```

---

## Quick Setup

### New Installation (Clean Database)
The easiest way to run the project for local development is to use the Makefile.
```bash
# Run the entire project (DB + Backend)
make run
```
On the first run, Flyway will automatically apply all necessary migrations. The default credentials are `admin` / `admin`.

### For Running Tests
**No manual database setup is required.** Integration tests are fully automated with Testcontainers.
```bash
# Run only integration tests
make test
```

### Manual Admin User Creation
If you need to create an admin user manually (e.g., if you disabled V3 migration with test data or for debugging), you can use the script:
```bash
mysql phoebe_db < db_data/create_admin_user.sql
```
*   **Note**: This script creates an `admin` user with password `admin`. It is intended **for development only** and should not be used in production.

---

## Helper Tools

For easier database management and inspection during local development, Adminer is included:

- **Adminer (Database Management UI)**: Access at [http://localhost:8081](http://localhost:8081)
  - **System**: `MySQL`
  - **Server**: `phoebe-mysql` (this is the Docker service name)
  - **Username**: `root`
  - **Password**: `root`
  - **Database**: `phoebe_db`

---

## Migration from Drupal 6 (Historical/Manual Process)

> **Important**: This section describes the *historical and manual process* of migrating data from Drupal 6.
> **For the current and automated deployment of the project with Drupal 6 data, please refer to the [Modern Guide to Migrating Data from Drupal 6](./MODERN_MIGRATION_GUIDE.md).**
> This manual process may be useful for debugging or understanding how the data was transformed.

### Migration Workflow

1.  **Analysis**: `mysql drupal6_db < db_data/detect_custom_fields.sql`
2.  **Main Migration**: `mysql clean_db < db_data/migrate_from_drupal6_universal.sql`
3.  **Fields Migration**: `mysql clean_db < db_data/migrate_cck_fields.sql`
4.  **Cleanup**: `mysql clean_db < db_data/update_migrated_users.sql`

### Post-Migration Credentials

After migration, users will have:
- **Admin**: username `admin`, password `admin`
- **Migrated users**: their original username, password `changeme123`
- **All migrated users must reset passwords on first login**

### Archive Data & Vocabulary System

**Important:** All archive terms from Drupal 6 are fully preserved with their vocabulary classifications.

**Migration ensures:**
- Archive integrity: All existing news-term relationships maintained
- Vocabulary preservation: Original Drupal 6 vocabulary names ("category", "tags", etc.) kept
- No data loss: Complete taxonomy structure migrated
- Future flexibility: New terms can use existing or new vocabulary groupings

---

## Migration Scripts Reference

### Core Scripts

#### `migrate_from_drupal6_universal.sql`
- Main migration from Drupal 6 to the modern schema. Creates UTF8 tables and migrates core data.

#### `migrate_cck_fields.sql`
- Handles Drupal 6 CCK custom fields.

#### `update_migrated_users.sql`
- Post-migration user cleanup, sets temporary passwords.

#### `detect_custom_fields.sql`
- A discovery script for introspecting the Drupal 6 database.

#### `create_admin_user.sql`
- Creates a default admin user for local development.
- **For development only** - password `admin`

---

## Spring Boot Migrations

### Automatic Migrations (Flyway, version 9.22.3)

The application uses Flyway to automatically manage the schema. For multi-DBMS support,
scripts are organized into common and vendor-specific directories.

- `db/migration/common`: Scripts compatible with all supported DBMS.
- `db/migration/mysql`: Scripts for MySQL only.
- `db/migration/postgresql`: Scripts for PostgreSQL only.

#### `ddl-auto` Configuration in Spring JPA
In Spring JPA configuration, the `spring.jpa.hibernate.ddl-auto` property controls Hibernate's behavior for managing the database schema.
-   `validate`: (Recommended for production and with Flyway) Hibernate validates that the database schema matches the entities but does not make changes. If there are discrepancies, the application will not start.
-   `update`: Hibernate attempts to update the database schema to match the entities. **Use with caution**, especially in production, as this can lead to data loss or unpredictable behavior.
-   `none`: Hibernate performs no schema operations.

When using Flyway, it is recommended to set `ddl-auto: validate` or `none`, as Flyway is solely responsible for schema evolution.

| Migration | Purpose | Changes | Location |
|---|---|---|---|
| V1 | Initial schema | Core tables: users, roles, content, terms | `common` |
| V2 | Add `published` field | Added `published` field to `content` table | `common` |
| V3 | Sample data | **Default admin user and test content** | `common` |
| V4 | User unification | Consolidated migrated authors | `common` |
| V5 | Permissions system | Added permissions and role_permissions tables | `common` |
| V6 | Permission descriptions | Added description column to permissions table | `common` |
| V7 | Channel settings | Added channel_settings table for site configuration | `common` |
| V8 | Setup permissions | Populate permissions and assign to roles | `mysql/postgresql` |
| V9 | Add indexes | Performance indexes and unique constraints | `mysql/postgresql` |
| V10 | Site URL field | Added site_url field to channel_settings table | `common` |

### Migration V3 Default Data

**⚠️ Important**: Migration V3 creates default login credentials for the CMS web interface:

**Roles Created:**
- `ADMIN` - Full system access
- `EDITOR` - Content management access

**Default User Created:**
- Username: `admin`
- Password: `admin` (BCrypt hash)
- Email: `admin@example.example.com`
- Role: `ADMIN`

**Test Data:**
- Sample news article
- General category
- Content relationships

---

## Authentication & Access Control

### Default CMS Users & Passwords

**⚠️ Important**: These are passwords for the CMS web interface, not the MySQL database.

#### For Clean Database (New Installation)
After executing migration V3 on a fresh database:

| Username | Password | Role | Email | Purpose |
|---|---|---|---|---|
| `admin` | `admin` | ADMIN | admin@example.com | System administrator |

#### For Migrated Database (From Drupal 6)
After executing migration scripts + `update_migrated_users.sql`:

| Username | Password | Role | Email | Purpose |
|---|---|---|---|---|
| `admin` | `admin` | ADMIN | admin@phoebe.local | System administrator |
| All migrated users | `changeme123` | - | user{id}@migrated.local | Legacy users (must reset) |

### Database Connection Passwords (MySQL)

#### For Dockerized MySQL
If you are using `docker-compose` to run MySQL (recommended method):
- **Host**: `localhost` (or `phoebe-mysql` from other Docker containers)
- **Port**: `3306`
- **User**: `root`
- **Password**: `root`
- **Database**: `phoebe_db`

These credentials are defined in the `.env` file and `docker-compose.yml`.

#### For Local MySQL
If you have MySQL installed directly on your machine (not in Docker), use the credentials you configured during installation. Typically:
- **Host**: `localhost`
- **Port**: `3306`
- **User**: `root`
- **Password**: (your root password)
- **Database**: `phoebe_db` (if you created it)

### Password Security
- All CMS passwords are stored as **BCrypt hashes** (strength 10-12)
- Migrated users **must change password** on first login
- CMS admin password should be changed immediately in production
- Database credentials `root`/`root` are **for development only** and must be changed in production.

### Permission System

The system uses **resource:action** permission naming:

| Permission | Description |
|---|---|
| `news:read` | View news articles |
| `news:create` | Create new articles |
| `news:update` | Edit existing articles |
| `news:delete` | Delete articles |
| `news:publish` | Publish/unpublish articles |
| `users:read` | View user accounts |
| `users:create` | Create new users |
| `users:update` | Edit user accounts |
| `users:delete` | Delete users |
| `roles:*` | Role management permissions |
| `terms:*` | Taxonomy management permissions |

### Default Role Permissions

**ADMIN Role:**
- All permissions (full system access)

**EDITOR Role:**
- `news:read`, `news:create`, `news:update`, `news:publish`
- `terms:read`

---

## Database Interaction

This section describes how to interact with your MySQL database, whether it's in a Docker container or installed locally.
For more detailed information on working with MySQL, please refer to the [MySQL Commands Guide](./MYSQL_GUIDE.md).

### For Dockerized MySQL

If your MySQL database is running in Docker via `docker-compose` (`phoebe-mysql` container), you can access it as follows:

1.  **Connect to the MySQL container:**
    ```bash
    docker exec -it phoebe-mysql mysql -u root -p
    ```
    At the `Enter password:` prompt, type `root`.

2.  **View databases:**
    After connecting to the MySQL client inside the container:
    ```sql
    SHOW DATABASES;
    ```
    You should see `phoebe_db` among others.

3.  **Select a database:**
    ```sql
    USE phoebe_db;
    ```

4.  **View tables:**
    ```sql
    SHOW TABLES;
    ```

5.  **View table schema:**
    ```sql
    DESCRIBE users; -- or any other table
    ```

6.  **Execute queries:**
    ```sql
    SELECT * FROM users;
    SELECT COUNT(*) FROM content;
    ```

7.  **Exit the MySQL client:**
    ```sql
    EXIT;
    ```

### For Local MySQL

If you have MySQL installed directly on your machine (not in Docker), you can use the standard `mysql` client:

1.  **Connect to local MySQL:**
    ```bash
    mysql -u root -p
    ```
    At the `Enter password:` prompt, type the password you set for your local MySQL `root` user.

2.  **View databases:**
    ```sql
    SHOW DATABASES;
    ```

3.  **Select a database:**
    ```sql
    USE phoebe_db; -- Assuming you created a database with this name
    ```

4.  **View tables:**
    ```sql
    SHOW TABLES;
    ```

5.  **View table schema:**
    ```sql
    DESCRIBE users; -- or any other table
    ```

6.  **Execute queries:**
    ```sql
    SELECT * FROM users;
    SELECT COUNT(*) FROM content;
    ```

7.  **Exit the MySQL client:**
    ```sql
    EXIT;
    ```

---

## Recommendations for Working with Production Databases

Working with databases in a production environment requires special attention to security, stability, and auditing. Direct access to a production database from outside (e.g., from a local machine via terminal) is strongly discouraged for routine operations.

### Why is direct access undesirable?

1.  **Security Risks:**
    *   **Credential Leakage:** Direct connection increases the risk of password compromise.
    *   **Unauthorized Access:** Opening ports for direct access creates potential vulnerabilities.
2.  **Human Factor:**
    *   **Errors:** Manually executing commands in production can lead to accidental data deletion or corruption.
    *   **Lack of Testing:** Changes made directly often do not go through standard testing and review processes.
3.  **Lack of Auditing and Control:**
    *   **Difficulty in Tracking:** It is difficult to track who made what changes to the database and when.
    *   **CI/CD Violation:** Direct changes violate the "single source of truth" principle (Git) and can lead to schema desynchronization.

### Recommended Practices

1.  **Automated Migrations (Flyway):**
    *   All database schema changes should be versioned and applied through a migration system (e.g., Flyway). This ensures that the schema always matches the application code version.
    *   Develop and test migrations locally, then integrate them into your CI/CD process.

2.  **Access via Application API:**
    *   All data operations (read, write, update, delete) should be performed through your application's API. This ensures compliance with business logic, validation, and security mechanisms.

3.  **Using Platform Tools:**
    *   If your project is deployed on a cloud platform (virtual servers, PaaS), use the tools it provides for database monitoring and performing limited administrative tasks. These tools are usually more secure and integrated with the auditing system.

4.  **Desktop SQL Clients via SSH Tunnel (for emergencies):**
    *   In rare cases where direct access is required for debugging or emergency fixes, use a desktop SQL client (e.g., DataGrip, MySQL Workbench) with a connection via an **SSH tunnel**. This is significantly more secure than opening the database port directly.

### Connecting to a Production Database via SSH Tunnel

To connect to a remote MySQL database via an SSH tunnel, you will need:
*   SSH access to the server where the database is running.
*   A private SSH key for authentication.
*   The IP address or domain name of the server.
*   Ucredentials for the MySQL database on the remote server.

**Step 1: Create an SSH Tunnel**

Open a terminal on your local machine and execute the command:

```bash
ssh -L 3307:127.0.0.1:3306 user@your_server_ip -i /path/to/your/private_key.pem
```

*   `ssh`: Command to establish an SSH connection.
*   `-L 3307:127.0.0.1:3306`: This option creates a local port forwarding (tunnel).
    *   `3307`: The local port on your machine that will be used for connection (you can choose any free port).
    *   `127.0.0.1`: The IP address that the SSH server will connect to within the remote network (usually `localhost` relative to the remote server where the DB is running).
    *   `3306`: The MySQL port on the remote server.
*   `user@your_server_ip`: The username and IP address/domain name of the remote server.
*   `-i /path/to/your/private_key.pem`: Path to your private SSH key.

After executing this command and successful authentication (you may need to enter a passphrase for the key), the SSH tunnel will be established. Leave this terminal open.

**Step 2: Connect to the Database via the Local Port**

Now that the tunnel is active, you can connect to the remote database using local port `3307` (or the one you specified) on your machine.

Via the MySQL client in the terminal:

```bash
mysql -h 127.0.0.1 -P 3307 -u your_db_user -p your_db_name
```

*   `-h 127.0.0.1`: Indicates that you are connecting to the local host.
*   `-P 3307`: Specifies the local port that forwards traffic through the tunnel.
*   `-u your_db_user`: The database username on the remote server.
*   `-p`: Will prompt for the database user's password.
*   `your_db_name`: The database name on the remote server.

Or via a graphical SQL client (DataGrip, MySQL Workbench):
*   **Host**: `127.0.0.1`
*   **Port**: `3307`
*   **User**: `your_db_user`
*   **Password**: `your_db_password`
*   **Database**: `your_db_name`

**Step 3: Close the SSH Tunnel**

When you are finished working with the database, simply close the terminal where the SSH tunnel was opened (or press `Ctrl+C` in it).

---

## Troubleshooting

### Common Issues

**Migration fails with encoding errors:**
```sql
-- Ensure UTF8 charset
ALTER DATABASE phoebe_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

**Admin user already exists:**
```sql
-- Check existing users
SELECT * FROM users WHERE username = 'admin';
-- Update password if necessary
UPDATE users SET password = '$2a$12$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P8jF4l3q4R4J8C' 
WHERE username = 'admin';
```

**Verify migration success:**
```sql
SELECT COUNT(*) FROM content;   -- Check articles migrated
SELECT COUNT(*) FROM users;     -- Check users migrated
SELECT COUNT(*) FROM terms;     -- Check taxonomy migrated
```

### Database Setup Instructions

#### For New Installation (Clean Database)
The easiest way to run the project for local development is to use the Makefile.
```bash
# Run the entire project (DB + Backend)
make run
```
On the first run, Flyway will automatically apply all necessary migrations. The default credentials are `admin` / `admin`.

#### For Migrated Database (From Drupal 6)
> **Important**: This section describes the *manual process* for setting up a migrated database.
> **For the current and automated deployment of the project with Drupal 6 data, please refer to the [Modern Guide to Migrating Data from Drupal 6](./MODERN_MIGRATION_GUIDE.md).**
> This manual process may be useful for debugging or understanding how the data was transformed.

1.  **Import Drupal 6 data** (if available)
    *   **Note**: For obtaining data from an old Drupal 6 dump, see the **[Historical Docker Data Backup and Recovery Guide (Drupal 6 Migration Context)](./LEGACY_DOCKER_DATA_RECOVERY_GUIDE_EN.md)**.
2.  **Run migration scripts** (if you are using the manual process):
    ```bash
    mysql phoebe_db < db_data/migrate_from_drupal6_universal.sql
    mysql phoebe_db < db_data/update_migrated_users.sql
    ```
3.  **Start the application** (`make run`), which will apply the remaining Flyway migrations.

### Security Checklist

- [ ] Change default admin password
- [ ] Force password reset for all migrated users
- [ ] Review and adjust role permissions
- [ ] Enable HTTPS in production
- [ ] Configure proper database credentials
- [ ] Set up backup procedures

---

## Related Documentation

- [Project Overview](./PROJECT_OVERVIEW.md) - Complete project information
- [Migration Drupal6 Guide](./MIGRATION_DRUPAL6.md) - Detailed migration process
- [Developer Guide](./DEVELOPER_GUIDE.md) - Local development setup
- [Quick Start](./QUICK_START.md) - Brief instructions to get started