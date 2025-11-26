> [Back to Documentation Contents](./README.md)

# Docker Guide

> For definitions of key terms and technologies, please refer to the **[Glossary](./GLOSSARY.md)**.

## Table of Contents
- [Docker Philosophy in the Project](#docker-philosophy-in-the-project)
- [Docker Compose File Structure](#docker-compose-file-structure)
  - [`docker-compose.base.yml`](#docker-composebaseyml)
  - [`docker-compose.hybrid.yml`](#docker-composehybridyml)
  - [`docker-compose.prod.yml`](#docker-composeprodyml)
- [Choosing a Development Mode](#choosing-a-development-mode)
- [Direct Usage of Docker Compose](#direct-usage-of-docker-compose)
- [Building for Production](#building-for-production)
- [Data and Cache Management](#data-and-cache-management)
- [Troubleshooting](#troubleshooting)

---

## Docker Philosophy in the Project

The project uses Docker to create a consistent, isolated, and easy-to-use development environment. The main goal
is to minimize the number of dependencies that need to be installed on a developer's local machine.

Ideally, all you need is **Docker Desktop**.

The project offers two main operating modes to meet the needs of both backend and frontend developers.

---

## Docker Compose File Structure

Instead of a single monolithic `docker-compose.yml`, we use a modular approach, splitting the configuration
into several files. This allows for flexible combination of services for different scenarios.

### `docker-compose.base.yml`
- **Purpose**: A base file that defines common services and resources for all modes.
- **Contents**:
  - The `phoebe-mysql` database service.
  - Named volumes for data persistence: `mysql_data`, `gradle_cache`, `node_modules`, `nextjs_cache`.

### `docker-compose.hybrid.yml`
- **Purpose**: Describes the **hybrid development mode**.
- **For whom**: For backend developers.
- **How it works**:
  - Uses `Dockerfile.dev` to build the backend.
  - The backend source code is mounted into the container (`volumes`), allowing Spring Boot DevTools to instantly
    pick up changes in Java code.
  - **Requires a local JDK** for full IDE support (code analysis, running unit tests).

### `docker-compose.prod.yml`
- **Purpose**: Describes the **production-like development mode**.
- **For whom**: For frontend developers, testers, and CI/CD.
- **How it works**:
  - Uses `Dockerfile.multistage` to create a lightweight, optimized backend image.
  - Only the compiled `.jar` file is included in the final image, with no source code.
  - **Does not require a local JDK**.

---

## Choosing a Development Mode

Modes are managed via the `Makefile`, which automatically substitutes the necessary `docker-compose` files.

- **Hybrid Mode (default):**
  ```bash
  make run  # or make run-hybrid
  ```
  This command will execute: `docker compose -f docker-compose.base.yml -f docker-compose.hybrid.yml up --build`

- **Production-Like Mode:**
  ```bash
  make run-prod
  ```
  This command will execute: `docker compose -f docker-compose.base.yml -f docker-compose.prod.yml up --build`

> For a more detailed comparison of the modes, refer to **[Local Development Approaches](./DEVELOPMENT_APPROACHES.md)**.

---

## Direct Usage of Docker Compose

If you prefer to work without `Makefile`, you can run `docker compose` directly, specifying the required
configuration files with the `-f` flag.

- **Running in Hybrid Mode:**
  ```bash
  docker compose -f docker-compose.base.yml -f docker-compose.hybrid.yml up --build
  ```

- **Running in Production-Like Mode:**
  ```bash
  docker compose -f docker-compose.base.yml -f docker-compose.prod.yml up --build
  ```

- **Stopping:**
  ```bash
  # It's sufficient to stop with one of the scenarios to shut everything down
  docker compose -f docker-compose.base.yml -f docker-compose.hybrid.yml down
  ```

---

## Building for Production

To create the final Docker image that will be deployed to a server, `Dockerfile.multistage` is used.

1.  **Ensure your code is committed**, as the build will take place in a clean environment.

2.  **Run the build command from the project root:**
    ```bash
    docker build -t phoebe-cms:latest -f Dockerfile.multistage .
    ```
    - `-t phoebe-cms:latest`: Sets the name and tag for your image. Versioning is recommended (e.g., `phoebe-cms:1.0.0`).
    - `-f Dockerfile.multistage`: Explicitly specifies which Dockerfile to use.
    - `.`: The build context (the entire project).

3.  **Run the built image:**
    ```bash
    # Example of running with environment variables
    docker run -d -p 8080:8080 \
      -e SPRING_PROFILES_ACTIVE=prod \
      -e DB_HOST=your_db_host \
      -e DB_USER=your_db_user \
      -e DB_PASSWORD=your_db_password \
      phoebe-cms:latest
    ```

---

## Data and Cache Management

The project uses named volumes to persist data between runs.

- **`mysql_data`**: Stores your MySQL database data.
- **`gradle_cache`**: Stores the downloaded Gradle and all its dependencies. This significantly speeds up
  subsequent backend builds.
- **`node_modules`**: Stores JavaScript dependencies for the frontend.
- **`nextjs_cache`**: Stores the Next.js build cache.

To completely reset the environment, including all this data, use the command:
```bash
make reset
```
**Warning:** This command will permanently delete your local database.

---

## Troubleshooting

### Forcing a Full Rebuild

Sometimes, Docker's cache can become stale, especially after switching branches or making significant changes
to configuration files (`docker-compose.*.yml`, `Dockerfile`, etc.). If the application fails to start with an
error that seems related to old code or configuration (like a `Flyway` migration conflict that you've already
fixed), you may need to force a full rebuild.

The `--force-recreate` flag tells Docker Compose to recreate all containers, ensuring that all your latest
changes are applied.

1.  **Stop all containers:**
    ```bash
    make stop
    ```

2.  **Run the project with a forced recreate:**
    ```bash
    docker compose -f docker-compose.base.yml -f docker-compose.hybrid.yml up --build --force-recreate
    ```

**Note:** You do not need to do this every time. This is a troubleshooting step for when `make run` does not
seem to be picking up your changes. After this one-time command, you can go back to using the regular `make run`.