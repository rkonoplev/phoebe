> [Back to Documentation Contents](./README.md)

# Development History and Improvements

This document tracks the history of significant improvements, current tasks, and potential future directions for the project.

---

## Completed Tasks and Architectural Decisions

This section serves as a changelog, documenting key implemented features and refactorings.

### Architecture & Design
- **Layered Architecture**: A clear separation between Controller, Service, and Repository layers has been
  implemented.
- **Single Responsibility Principle**: Logic is separated by domain (news, roles, authorization).
- **DTO Pattern**: Data Transfer Objects are used for all API requests and responses.
- **Centralized Authorization**: Access control logic has been extracted into a dedicated `AuthorizationService`.
- **Automated Mapping**: MapStruct is integrated for automatic conversion between DTOs and entities.

### Security
- **Authentication & Authorization**: Spring Security is integrated with Basic Authentication.
- **Role-Based Access Control (RBAC)**: A system with roles (ADMIN, EDITOR) and granular permissions is
  implemented.
- **Endpoint Protection**: Administrative APIs are secured and require appropriate roles.
- **Next.js Vulnerability Fixed**: Updated the `next` package to patch a critical RCE (Remote Code Execution)
  vulnerability.

### Performance
- **Caching**: Method-level caching (`@Cacheable`) is implemented using Caffeine for frequently accessed data.
- **Rate Limiting**: API flood protection is implemented using Bucket4j.
- **Transaction Optimization**: All read-only service methods are annotated with `@Transactional(readOnly = true)`.
- **N+1 Query Prevention**: Added `@EntityGraph` annotations for eager loading of related entities.
- **SQL Logging and Diagnostics**: Configured detailed SQL logging with formatting and parameter tracing in `local` profile.
- **Batch Operations**: Configured grouping of INSERT/UPDATE operations to reduce database round-trips.

### Database
- **Data Access**: The project currently uses the blocking **Spring Data JPA** stack for simplicity and
  reliability.
- **Migration Management**: The database schema is version-controlled using Flyway.
- **Migration Compatibility Fix**: Resolved an issue in the `V11` migration to support MySQL syntax
  (replacing `||` with `CONCAT()`), ensuring cross-database compatibility.
- **MySQL as Single Database**: The project uses MySQL for all environments to ensure consistency.

### Code Quality & CI/CD
- **Static Analysis**: Checkstyle and PMD are configured and integrated to maintain code quality.
- **Test Coverage**: JaCoCo is integrated for code coverage analysis.
- **Test Structure Refactoring**: A full separation of tests into `unit/` and `integration/` directories has
  been completed.
- **Gradle Configuration**: `build.gradle` is configured to run unit and integration tests separately.
- **Unified Testcontainers Architecture**: Migrated to a single `BaseIntegrationTest` class.
- **Simplified CI Pipeline**: Removed Docker Compose dependency from CI, using Testcontainers everywhere.
- **Project Cleanup**: Removed obsolete files (`Task`, root `docker-compose.yml`) from the repository.
- **Improved Docker Workflow**: Added a `make run-no-cache` command to force the rebuilding of Docker images
  without cache, resolving issues with stale files.

---

## In Progress

- No active tasks.

---

## Potential Enhancements and Future Directions

This section outlines potential improvements and architectural vectors that could be considered for the
project's future development. The ideas are grouped by priority to form a hypothetical roadmap.

### Security (High Priority)

- **OAuth 2.0 + JWT**: Transition from Basic Auth to a token-based authentication system.
  - **Purpose**: To implement a modern, secure, and scalable authentication mechanism.
  - **Components**: OAuth 2.0 grant flows, JWT generation and validation, token-based endpoint protection,
    refresh tokens, and updated login/logout procedures.

- **Two-Factor Authentication (2FA)**: Add a second layer of security for administrative roles.
  - **Purpose**: To enhance security for accounts with elevated privileges (e.g., ADMIN, EDITOR).
  - **Components**: Time-based One-Time Password (TOTP) generation and validation, integration with
    authenticator apps, and UI/UX for 2FA setup and confirmation.

### Functionality (Medium Priority)

- **File Uploads**: Implement support for managing images and other media.
  - **Purpose**: To allow users to upload files directly through the API.
  - **Components**: File storage service (e.g., local, S3, or MinIO), API endpoints for upload and retrieval.

- **Search Functionality**: Develop a search system for public and administrative parts of the application.
  - **Purpose**: To provide users with a fast and effective way to find content.
  - **Phase 1 (Angular & Next.js)**: Implement a basic, server-side search. The backend will expose a new API
    endpoint that uses SQL `LIKE` or `ILIKE` to search through article titles and content.
  - **Phase 2 (Next.js - Perspective)**: For the Next.js frontend, consider implementing a client-side
    search using **Pagefind**. This tool creates a static search index during the build process, allowing for
    instant search without any load on the backend API.

- **Webhooks**: Develop a system for event-driven notifications.
  - **Purpose**: To notify external services of specific events within the application.
  - **Components**: A mechanism for managing webhook subscriptions and dispatching event payloads.

- **Telegram Integration**: Automatically post article previews to a Telegram channel.
  - **Purpose**: To expand content distribution and provide timely updates to subscribers.
  - **Components**: A Telegram Bot client, a service to listen for article creation/publication events,
    message formatting, and secure storage for the bot token.

### Architecture & Performance (Low Priority)

- **Reactive Stack Migration**: Consider migrating public-facing, high-traffic endpoints to a non-blocking stack
  (Spring WebFlux and R2DBC) to maximize scalability.

### Implementation Sequence

1.  **Finalize Business Logic and API**: Complete and stabilize core application features.
2.  **Implement OAuth 2.0 + JWT**: Establish the primary authentication and authorization mechanism.
3.  **Update CI/CD and Deployment Configuration**: Ensure the deployment process is compatible with the new
    auth system.
4.  **Implement 2FA**: Add two-factor authentication for administrative roles as an additional security layer.

### Supporting Infrastructure and Services

- **Email Service (e.g., SMTP, Mailgun)**: Required for user notifications, registration confirmation, and
  2FA/password recovery.
- **File Storage (e.g., S3, MinIO)**: For storing user-uploaded content like images and documents.
- **Monitoring and Logging (Grafana Cloud)**: Centralized application monitoring (Prometheus) and log
  aggregation (Loki), which is especially useful in a production environment.

### Reference Frontend Implementations

- **Status**: Currently in a basic structure phase. Full component implementation and API integration are
  required.
- **Next.js Frontend**: Implemented and ready for use.
- **Dependency**: Full implementation of the frontends will proceed after the final backend debugging,
  configuration, and verification.