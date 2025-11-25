> [Back to Documentation Contents](./README.md) | [Quick Start Guide](./QUICK_START.md) | [Developer Guide](./DEVELOPER_GUIDE.md)

# Testing and Development with Makefile

## Table of Contents

- [Quick Reference](#quick-reference)
- [Choosing a Development Mode](#choosing-a-development-mode)
- [Development Workflows](#development-workflows)
  - [1. Full Stack Development](#1-full-stack-development)
  - [2. Backend-Only Development](#2-backend-only-development)
  - [3. Testing Workflows](#3-testing-workflows)
    - [Quick Integration Tests](#quick-integration-tests)
    - [Full Test Suite](#full-test-suite)
    - [Code Quality Checks](#code-quality-checks)
- [Environment Requirements](#environment-requirements)
- [Configuration Details](#configuration-details)
- [Troubleshooting](#troubleshooting)
- [Best Practices](#best-practices)
- [Integration with IDEs](#integration-with-ides)
- [CI/CD Integration](#cicd-integration)

This guide explains how to use the Makefile commands for development and testing workflows.

## Quick Reference

| Command | Description | Use Case |
|:---|:---|:---|
| `make run-hybrid` | **(Default)** Start in Hybrid Mode. | For backend developers (requires local JDK). |
| `make run-prod` | Start in Production-Like Mode. | For frontend developers (only Docker needed). |
| `make run` | Alias for `make run-hybrid`. | Quick start in the default mode. |
| `make stop` | Stop the project. | Clean shutdown. |
| `make reset` | **Delete all containers and DB data**. | Complete environment reset. |
| `make test` | Run integration tests (Testcontainers). | Quick test feedback. |
| `make all-tests` | Run all tests (unit + integration). | Full validation. |
| `make boot` | Start backend locally. | Development with local MySQL. |
| `make clean` | Clean build artifacts. | Fresh start. |
| `make lint` | Run static analysis. | Code quality checks. |
| `make coverage` | Generate test coverage. | Coverage reports. |

## Choosing a Development Mode

The project supports two launch modes for local development to suit different tasks.

### Mode 1: Hybrid (`make run-hybrid` or `make run`)
- **For whom:** Ideal for **backend developers** actively working on Java code.
- **How it works:** The code is compiled inside a Docker container, but `volumes` allow changes to be picked up instantly. Requires a **locally installed JDK** for full IDE support (code analysis, running unit tests).
- **When to use:** When you are writing Java code and want to quickly run unit tests directly from your IDE.

### Mode 2: Production-Like (`make run-prod`)
- **For whom:** Ideal for **frontend developers, testers,** or for final checks.
- **How it works:** Uses a multi-stage build to create a lightweight, self-contained backend image. **No local JDK is required.**
- **When to use:** When you just need a working backend and do not plan to make changes to the Java code.

> A detailed comparison of the approaches is available in the **[Local Development Approaches](./DEVELOPMENT_APPROACHES.md)** document.

## Development Workflows

### 1. Full Stack Development
For developing both frontend and backend with live reload:

```bash
# For backend developers (default mode)
make run

# For frontend developers (no local Java required)
make run-prod
```

**Access Services:**
- API: [http://localhost:8080](http://localhost:8080)
- Swagger: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- Frontend: [http://localhost:3000](http://localhost:3000) (if available)

> **Hint**: Login credentials (usernames and passwords) for different roles are described
> in the **[DATABASE_GUIDE.md](./DATABASE_GUIDE.md)** file.

```bash
# Stop when done
make stop
```

### 2. Backend-Only Development
For API development without frontend:

```bash
# Option A: With Docker Compose (recommended)
make run

# Option B: Local development (requires local MySQL)
make boot
```

### 3. Testing Workflows

#### Quick Integration Tests
```bash
# Run integration tests with Testcontainers
make test
```
- Uses Testcontainers (no Docker Compose needed)
- Starts fresh MySQL container for each test run
- Fast feedback loop

#### Full Test Suite
```bash
# Run all tests (unit + integration)
make all-tests
```
- Comprehensive validation
- Includes code coverage
- Use before commits/PRs

#### Code Quality Checks
```bash
# Run static analysis
make lint

# Generate coverage report
make coverage
```

## Environment Requirements
- **For `make run-hybrid`**: Docker and Java (JDK).
- **For `make run-prod`**: Only Docker.
- **For `make test` and `make all-tests`**: Docker (for Testcontainers) and Java (JDK) to run Gradle.
- **For `make boot`**: Local MySQL and Java (JDK).

## Configuration Details

### Docker Compose (`make run-*`)
- **MySQL**: Persistent data in Docker volume.
- **Backend**: In hybrid mode (`run-hybrid`), hot reload is used with Spring Boot DevTools.
- **Frontend**: Live reload (if configured).

### Testcontainers (`make test`)
- **MySQL**: Fresh container per test run.
- **Isolation**: Each test gets a clean database.
- **Performance**: Optimized for CI/CD.

### Local Development (`make boot`)
- **MySQL**: Your local installation.
- **Flexibility**: Direct database access.
- **Speed**: No container overhead.

## Troubleshooting

### Build and Cache Issues
If the application behaves unexpectedly after code changes (especially in `build.gradle`), try `make reset` to completely clean the environment. **Warning: this will delete your DB data.**

### Docker Issues
```bash
# If containers are stuck
make stop
docker system prune -f

# Restart
make run
```

### Test Issues
```bash
# Clean and retry
make clean
make test
```

### Local MySQL Issues
```bash
# Check MySQL status
brew services list | grep mysql

# Start MySQL (macOS with Homebrew)
brew services start mysql

# Create database
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS phoebe_db;"
```

## Best Practices

1. **Use `make test` for quick feedback** during development.
2. **Use `make all-tests` before commits** for full validation.
3. **Use `make run-hybrid` (or `make run`) for backend development.**
4. **Use `make run-prod` for frontend development or testing.**
5. **Always `make stop`** to clean up Docker resources.

## Integration with IDEs

### IntelliJ IDEA
- Run configurations can use `make` commands.
- Terminal integration: `Tools > Terminal`.
- External tools: `Tools > External Tools`.

### VS Code
- Tasks can be configured to run `make` commands.
- Integrated terminal supports `make`.
- Extensions available for Makefile syntax.

## CI/CD Integration

The GitHub Actions workflow uses Testcontainers directly:
```yaml
- name: Run tests
  run: cd backend && ./gradlew clean build integrationTest jacocoTestReport --no-daemon
```

This is equivalent to running `make all-tests` locally.