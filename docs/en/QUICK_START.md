> [Back to Documentation Contents](./README.md)
>
> **[Developer Guide](./DEVELOPER_GUIDE.md)**: A detailed description of the full development workflow
> for developers, including IDE setup, testing, and CI/CD integration.

> For definitions of key terms and technologies, please refer to the **[Glossary](./GLOSSARY.md)**.

# Quick Start Guide

## Table of Contents

- [Recommended Quick Start (via Makefile)](#recommended-quick-start-via-makefile)
- [Choosing a Development Mode](#choosing-a-development-mode)
- [Architecture Overview: Backend and Frontend](#architecture-overview-backend-and-frontend)
- [Most Frequent Commands (Cheat Sheet)](#most-frequent-commands-cheat-sheet)
- [Managing a Running Project](#managing-a-running-project)
- [How to Update Code in Docker?](#how-to-update-code-in-docker)
- [Advanced Build Scenarios](#advanced-build-scenarios)
- [Direct Project Management via Docker Compose](#direct-project-management-via-docker-compose)
- [Manual Control and Debugging of Docker Containers](#manual-control-and-debugging-of-docker-containers)
- [Main Development Commands](#main-development-commands)
- [Troubleshooting](#troubleshooting)
- [Additional Documentation](#additional-documentation)

Brief instructions for developers for **daily work** with an already configured project.

> **Important**: For **initial project setup**, please follow the detailed
> [Setup Guide](./SETUP_GUIDE.md).

---

## Recommended Quick Start (via Makefile)

The entire primary workflow is built around the `Makefile` for simplicity. This is the easiest way to get started.

- **Run the project (default mode)**: `make run`
- **Run all tests**: `make all-tests`
- **Stop the project**: `make stop`

> For a complete list of commands and descriptions, refer to
> **[Testing and Development with Makefile](./TESTING_WITH_MAKEFILE.md)**.

---

## Choosing a Development Mode

The project offers two launch modes to suit your tasks.

- **`make run` (or `make run-hybrid`)**: **Hybrid Mode**. Ideal for backend developers. Requires a local JDK for full IDE support but allows instant code changes.
- **`make run-prod`**: **Production-Like Mode**. Ideal for frontend developers and testers. Requires nothing but Docker.

> A detailed comparison of the approaches is available in the **[Local Development Approaches](./DEVELOPMENT_APPROACHES.md)** document.

---

## Architecture Overview: Backend and Frontend

When you run the project, two main services are started:

- **Backend** (`phoebe-app`, port `8080`)
  - This is a Spring Boot application that handles the API, logic, database, and authorization.
  - It is accessible at: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

- **Frontend** (`phoebe-nextjs`, port `3000`)
  - This is a reference frontend application (e.g., Next.js or Angular)—a visual interface that
    communicates with the backend via the API.
  - It is accessible at: [http://localhost:3000](http://localhost:3000)
  - Login credentials (usernames and passwords) for different roles are described in the file
    **[DATABASE_GUIDE.md](./DATABASE_GUIDE.md)**.

---

## Most Frequent Commands (Cheat Sheet)

| Goal | Command |
|:---|:---|
| 🚀 **Start the project in the morning** (for backend dev) | `make run` |
| 🌐 **Start the project in the morning** (for frontend dev) | `make run-prod` |
| 🛑 **Stop in the evening** | `make stop` |
| 📋 **View backend logs** | `docker compose logs -f phoebe-app` |
| 💣 **Delete everything, including the database** | `make reset` |

---

## Managing a Running Project

### What happens in the terminal when starting?
When you run a command, for example `make run`, Docker Compose starts all containers and "attaches" their logs directly to your terminal. You will see the running logs of `phoebe-app`, `phoebe-mysql`, `phoebe-nextjs`, and so on.

This is normal and useful during debugging — you can immediately see if something went wrong.

### How to exit the terminal without stopping services?
There are three options:

| Action | What it does | Command |
|:---|:---|:---|
| Detach, but leave everything running | Containers continue to run in the background | Press `Ctrl + p`, then `Ctrl + q` |
| Stop everything and exit | Containers are shut down | Press `Ctrl + C` |
| Force stop Docker Compose | Same as above, but guaranteed | If the terminal is "frozen", type `make stop` in a new window |

### Start and end of the workday
- **In the morning**: Make sure Docker Desktop (or Docker Engine) is running. Navigate to the project folder and start the project in your desired mode.
- **In the evening**: Execute `make stop`. This will correctly stop the containers, preserving data.
- **Full reset**: If you want to wipe everything, including the database, use `make reset`.

---

## How to update code in Docker?

- **Quick Update (with cache)**: `make run` or `make run-prod`.
  This command uses `docker compose up --build`. It quickly rebuilds only the parts that have changed. Use it 99% of the time for daily development.

> A detailed explanation of all commands and scenarios can be found in the **[Docker Guide](./DOCKER_GUIDE.md)**.

---

## Advanced Build Scenarios

### `make` Command Cheat Sheet

| Goal | When to use | What it does |
|:---|:---|:---|
| `make run` / `make run-hybrid` | For backend development | Rebuilds and starts the project in hybrid mode. |
| `make run-prod` | For frontend development | Rebuilds and starts the project in production-like mode. |
| `make stop` | To stop | Stops and preserves data. |
| `make reset` | When you want to start over | Full "reset" of containers and volumes. |
| `make boot` | For debugging the backend locally without Docker | Starts Spring Boot directly. |
| `make test` / `make all-tests` | To run tests | Testcontainers / unit+integration. |

---

## Direct Project Management via Docker Compose (without Makefile)

For direct control, you can use the `-f` flag to specify the required files.

- **Hybrid Mode:**
  ```bash
  docker compose -f docker-compose.base.yml -f docker-compose.hybrid.yml up --build
  ```
- **Production-Like Mode:**
  ```bash
  docker compose -f docker-compose.base.yml -f docker-compose.prod.yml up --build
  ```

---

## Manual Control and Debugging of Docker Containers

Although `Makefile` provides convenient commands for most tasks, sometimes finer control over individual Docker services is required. This is especially relevant during debugging.

### Starting and stopping individual services
You can manage each service defined in `docker-compose.yml` individually:
```bash
# Start a specific service (and its dependencies)
docker compose up -d <service_name>

# Stop a specific service
docker compose stop <service_name>

# Restart a specific service
docker compose restart <service_name>
```

### Rebuilding and restarting a single service
If you have made changes to the code or Dockerfile of a specific service and want to rebuild it without affecting others, use the following command:
```bash
docker compose up --build -d <service_name>
```

---

## Main Development Commands

All commands are executed from the `backend/` directory.

### Running tests
```bash
./gradlew clean test
```

### Building the project
```bash
./gradlew build
```

### Code quality check
```bash
./gradlew checkstyleMain checkstyleTest
```

---

## Troubleshooting

### Tests do not run (MySQL conflict)
- Check that `spring.profiles.active: local` is **not** present in `application.yml`
- Flyway should be disabled in `application-test.yml`
- Run tests with a clean build: `./gradlew clean test`

### Database reset
```bash
make reset
```

### Port issues
- MySQL: port 3306 (may conflict with local MySQL)
- Spring Boot: port 8080
- Stop local services or change ports in `.env.dev`

---

## Additional Documentation

- **[Setup Guide](./SETUP_GUIDE.md)**: Step-by-step instructions for the first launch.
- **[Developer Guide](./DEVELOPER_GUIDE.md)**: Detailed IDE setup and workflow description.
- **[Project Overview](./PROJECT_OVERVIEW.md)**: Full information about architecture and technologies.
- **[Docker Guide](./DOCKER_GUIDE.md)**: Advanced Docker usage.
- **[Local Development Approaches](./DEVELOPMENT_APPROACHES.md)**: Comparison of hybrid and fully containerized approaches.
