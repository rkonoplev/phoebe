> [Back to Documentation Contents](./README.md)
>
> **[Quick Start Guide](./QUICK_START.md)**: Brief instructions for getting started quickly and
> performing daily tasks with an already configured project.

# Developer Guide – Local Workflow and CI/CD

This document explains how to work on the project locally (IntelliJ IDEA, Gradle, Docker)
and what will be automatically executed in GitHub Actions (CI/CD).

> For definitions of key terms and technologies, please refer to the **[Glossary](./GLOSSARY.md)**.

---

## Table of Contents
- [Initial Project Setup](#initial-project-setup)
- [Choosing a Development Mode](#choosing-a-development-mode)
- [Daily Local Workflow](#daily-local-workflow)
- [Setting Up Code Auto-formatter](#setting-up-code-auto-formatter)
- [Before Pushing to GitHub](#before-pushing-to-github)
- [What CI/CD Does (on GitHub Actions)](#what-cicd-does-on-github-actions)
- [Summary](#summary)
- [Database Migrations (Flyway)](#database-migrations-flyway)

---

## Initial Project Setup

For the first-time setup of the project, please refer to the **[Setup Guide](./SETUP_GUIDE.md)**.

---

## Choosing a Development Mode

The project supports two launch modes for local development. Choose the one that best suits your current task.

### Mode 1: Hybrid (`make run` or `make run-hybrid`)
- **For whom:** Ideal for **backend developers** actively working on Java code.
- **Requirements:** Docker and a locally installed JDK.
- **Advantages:** Allows you to quickly run unit tests and use all code analysis features directly in your IDE.

### Mode 2: Production-Like (`make run-prod`)
- **For whom:** Ideal for **frontend developers, testers,** or for final checks before a commit.
- **Requirements:** Only Docker.
- **Advantages:** Does not require Java to be installed on your local machine, providing a "clean" environment identical to CI/CD.

> A detailed comparison of the approaches is available in the **[Local Development Approaches](./DEVELOPMENT_APPROACHES.md)** document.

---

## Daily Local Workflow

### Recommended Process (via Makefile)
The entire primary workflow is built around the `Makefile` for simplicity.

- **Run the full environment** (DB + backend + frontend):
  ```bash
  # For backend development (default mode)
  make run

  # For frontend development
  make run-prod
  ```
- **Run all tests** (unit + integration):
  ```bash
  make all-tests
  ```
- **Stop the environment**:
  ```bash
  make stop
  ```
> For a complete list of commands, see **[Testing and Development with Makefile](./TESTING_WITH_MAKEFILE.md)**.

### Alternative/Low-Level Launch
The `make` commands are wrappers around `docker-compose` and `gradlew`. You can use them directly.

#### Gradle Commands by Complexity Level

**Quick Checks (without Docker):**
```bash
cd backend
./gradlew test              # Unit tests only
./gradlew checkstyleMain    # Code style check for src/main/java
./gradlew checkstyleTest    # Code style check for src/test/java
./gradlew checkstyle        # Code style check for all code
```

**Full Checks (require Docker):**
```bash
cd backend
./gradlew integrationTest   # Integration tests only (Testcontainers auto-starts MySQL)
./gradlew check             # All tests + Checkstyle + PMD (without JAR build)
./gradlew build             # Full build: check + JAR creation
```

**Run Application:**
```bash
cd backend
./gradlew bootRun           # Requires local MySQL or docker-compose up mysql
```

#### What Each Command Requires

| Command | Docker | Application Running | What It Does |
|---------|--------|---------------------|-------------|
| `test` | ❌ | ❌ | Unit tests |
| `checkstyle*` | ❌ | ❌ | Code style checks |
| `integrationTest` | ✅ | ❌ | Integration tests (Testcontainers) |
| `check` | ✅ | ❌ | Tests + linters |
| `build` | ✅ | ❌ | check + JAR build |
| `bootRun` | ✅* | ❌ | Run application |

*for `bootRun` you need a DB (via `docker-compose up mysql` or locally)

#### Testcontainers - Automatic Database Management

Integration tests use **Testcontainers**, which:
- Automatically downloads and starts a MySQL container
- Starts Spring Boot in test context
- Runs tests with a real database
- Stops and removes the container after tests

**No need** to manually start the application or database for integration tests!

---

## Setting Up Code Auto-formatter

The project uses a consistent Java code formatting style with a line length of **120 characters**.
The configuration is located in `.idea/codeStyles/` and is automatically applied when you open the project.

- **Check settings**: `File → Settings → Editor → Code Style → Scheme = "Project"`.
- **Format code**: `Ctrl+Alt+L` (Win/Linux) or `Cmd+Alt+L` (Mac).
- **Recommended**: Enable format on save: `Settings → Tools → Actions on Save → Reformat code`.

---

## Before Pushing to GitHub

**Recommended Check:**
```bash
make all-tests
```
This command emulates the full CI check.

**Minimum Manual Checks:**
- Code compiles (`./gradlew build`).
- All unit tests pass (`./gradlew test`).
- Integration tests pass (`./gradlew integrationTest`).

---

## What CI/CD Does (on GitHub Actions)

After every `push` or `pull request`, GitHub Actions will automatically perform:
- A full Gradle build (`clean build`).
- Execution of all tests (`integrationTest`, which includes `test`).
- Static code analysis: Checkstyle and PMD (as part of the `build` task).
- Test coverage reporting (JaCoCo) and upload to Codecov.

> **Note**: Secret scanning (GitLeaks) is no longer an automated step in CI and is recommended
> for local execution.

---

## Summary

- You do not need to keep Docker running for regular development (writing code, unit tests).
- It is recommended to run `make all-tests` before pushing to be confident.
- All heavy analysis and integration tests will be additionally verified in CI.

---

## Database Migrations (Flyway)

The project uses Flyway to manage database schema evolution. To support multiple database systems,
the migration scripts are organized into separate directories.

### Directory Structure
- `src/main/resources/db/migration/common/`: Common scripts for all databases.
- `src/main/resources/db/migration/mysql/`: Scripts specific to MySQL.
- `src/main/resources/db/migration/postgresql/`: Scripts specific to PostgreSQL.
