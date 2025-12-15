# Docker for Beginners

> This document is intended for developers **who are new to Docker** or want to better understand
> what exactly happens when working with containers within this project.
>
> If you are already confident using `make run`, `docker compose`, and understand the difference
> between a container, image, and volume — you can read this file selectively.

---

## Why is Docker used in this project at all?

Docker allows you to run the project **in the same environment for all developers**, regardless of:

*   Java version,
*   Node.js version,
*   operating system,
*   installed local dependencies.

The idea is simple:

> *You don't configure the environment — you run it.*

In most cases, **the only thing you need to install locally is Docker Desktop**.

---

## Basic Concepts (very important)

### Image

A **Docker image** is a template.

You can think of it as:

*   an ISO image of a system,
*   or an archive with already installed software.

An image:

*   **does not run by itself**,
*   is used as a base for containers.

Examples:

*   `mysql:8.0`
*   `openjdk:21-jdk`
*   `phoebe-cms:latest`

---

### Container

A **container** is a *running instance of an image*.

If an image is a "blueprint," then a container is a "running process."

A container:

*   can be started and stopped,
*   can be removed and recreated,
*   **does not reliably store data by itself**.

When a container is stopped and removed:

*   everything that is not moved to external storage is lost.

---

### Volume

A **Volume** is where Docker stores data **outside the container**.

Used for:

*   databases,
*   build caches,
*   dependencies.

In this project, volumes are used for:

*   `mysql_data` — MySQL data
*   `gradle_cache` — Gradle dependencies
*   `node_modules` — Frontend dependencies
*   `nextjs_cache` — Next.js cache

💡 **Important:**
Removing a container ≠ deleting data.
Data lives in a volume until you explicitly delete it.

---

### Docker Compose

**Docker Compose** is a tool for running **multiple containers at once**.

It reads a `docker-compose.yml` file (or several files) and:

*   starts the necessary services,
*   creates networks,
*   attaches volumes,
*   sets environment variables.

This project uses **multiple compose files** that are combined.

---

## How Docker is used in this project

The project runs in one of two modes:

### 1. Hybrid Mode (for backend development)

*   The database runs in Docker
*   The backend runs in a container
*   Source code is **mounted inside the container**
*   Changes in Java code are picked up automatically

Requires:

*   a locally installed JDK (for the IDE)

To run:

```bash
make run
```

---

### 2. Production-Like Mode

*   Everything runs as in a real production environment
*   Only the compiled `.jar` is in the container
*   No source code inside the container

Used for:

*   frontend development
*   testing
*   CI/CD

To run:

```bash
make run-prod
```

---

## Essential Docker Commands (minimum required)

### View running containers

```bash
docker ps
```

### View all containers (including stopped ones)

```bash
docker ps -a
```

### Stop a container

```bash
docker stop <container_id>
```

### Remove a container

```bash
docker rm <container_id>
```

---

## How to safely "restart everything"

### Recommended method (via Makefile)

```bash
make reset
make run
```

⚠️ **This will delete the project database.**

---

### Manually via Docker Compose

```bash
docker compose down --volumes
docker compose up --build
```

---

## What to do if "Docker broke"

### Symptoms

*   containers do not start
*   changes are not picked up
*   strange errors that weren't there before

### Solution (in order of escalation)

1.  Restart the project:

    ```bash
    make stop
    make run
    ```

2.  Force recreate containers:

    ```bash
    docker compose -f docker-compose.base.yml -f docker-compose.hybrid.yml up --build --force-recreate
    ```

3.  Completely clean Docker (dangerous, deletes data):

    ```bash
    docker system prune
    docker volume prune
    ```

---

## Common beginner mistakes

❌ Remove a container and expect the data to be gone

✔ Data is stored in volumes — they must be removed separately

---

❌ Constantly run `docker system prune`

✔ Use it **only if you understand the consequences**

---

❌ Ignore the Makefile

✔ The Makefile is a safe wrapper around Docker commands

---

## Conclusion

In short:

*   **Image** — template
*   **Container** — running process
*   **Volume** — data
*   **Docker Compose** — orchestrator

If something goes wrong — **don't panic and don't delete Docker first thing**.
In most cases, it's enough to:

```bash
make reset
make run
```

---

> After mastering this document, it is recommended to return to the
> **[Docker Guide](./DOCKER_GUIDE.md)** and use it as the primary project reference.
