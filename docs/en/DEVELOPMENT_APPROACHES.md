# Local Development Approaches

This document describes the two main approaches to setting up and working with the project on a local machine. Understanding these approaches will help you choose the most convenient workflow.

## Table of Contents
- [Approach 1: Hybrid Development (Current Setup)](#approach-1-hybrid-development-current-setup)
  - [How It Works](#how-it-works)
  - [Required Software](#required-software)
  - [Advantages and Disadvantages](#advantages-and-disadvantages)
- [Approach 2: Fully Containerized Development](#approach-2-fully-containerized-development)
  - [Multi-stage Build Concept](#multi-stage-build-concept)
  - [Required Software](#required-software-1)
  - [Advantages and Disadvantages](#advantages-and-disadvantages-1)
- [Practical Steps to Switch to a Multi-stage Build](#practical-steps-to-switch-to-a-multi-stage-build)
  - [Changing the Backend Dockerfile](#changing-the-backend-dockerfile)
  - [Changing `docker-compose.yml`](#changing-docker-composeyml)
- [Configuring IntelliJ IDEA for Docker](#configuring-intellij-idea-for-docker)

---

## Approach 1: Hybrid Development (Current Setup)

This is the default approach used in the project. It combines the convenience of working in a local IDE with the runtime isolation of Docker.

### How It Works
1.  You write code on your computer in IntelliJ IDEA.
2.  When you run the `make run` command, Docker takes the **entire source code** of your backend.
3.  Inside a Docker container, which already has a full JDK, Gradle is launched. It compiles your code and starts the application.
4.  Your local IDE (IntelliJ IDEA) uses a locally installed JDK for code analysis, autocompletion, navigation, and, importantly, for **running unit tests**, which is very fast and convenient.

Thus, the **compilation and execution** of the application happen in Docker, while **code analysis and unit testing** happen on your local machine.

### Required Software
-   **Docker Desktop**: For running containers.
-   **Java (JDK)**: Required for IntelliJ IDEA to work correctly (code analysis, autocompletion) and for running unit tests locally.
-   **IntelliJ IDEA**: Your development environment.

### Advantages and Disadvantages

| Advantages | Disadvantages |
|:---|:---|
| ✅ **Fast Unit Tests**: You can run unit tests directly from the IDE with a single click, without Docker. | ❌ **Requires Local JDK**: You need to install and maintain Java on your machine. |
| ✅ **Full IDE Support**: All "smart" IDE features work out of the box. | ❌ **Potential Discrepancies**: The Java version in the IDE and in the Docker container might differ, which could theoretically lead to issues. |
| ✅ **Consistent Runtime Environment**: The final application always runs in the same Docker environment. | ❌ **Slow First Start**: On the first `make run`, Docker downloads Gradle and all dependencies, which can take time. |

---

## Approach 2: Fully Containerized Development

This approach aims to **completely eliminate local dependencies** like Java. The only tool on your machine is Docker.

### Multi-stage Build Concept
The Docker image is built in two stages:

1.  **"Builder" Stage**:
    -   A temporary Docker image with a full set of tools is used: **JDK** and **Gradle**.
    -   The source code is copied into this image.
    -   Compilation happens inside it, creating an executable `.jar` file.

2.  **"Runner" Stage**:
    -   A new, minimalist image is created, containing only the Java runtime environment — **JRE**.
    -   **Only the single `.jar` file** is copied from the "Builder" stage.
    -   All the "clutter" (source code, JDK, Gradle) is left behind and does not get into the final image.

The result is a small, fast, and secure image, and you don't need anything on your machine except Docker.

### Required Software
-   **Docker Desktop**: The only required dependency.
-   **IntelliJ IDEA**: Your development environment (with the Docker plugin).

### Advantages and Disadvantages

| Advantages | Disadvantages |
|:---|:---|
| ✅ **No Local Java Needed**: Your computer stays "clean". | ❌ **IDE Setup is More Complex**: Running tests or debugging from the IDE requires configuring it to execute commands inside Docker. |
| ✅ **Maximum Consistency**: The build and runtime environments are 100% identical for all developers and for CI/CD. | ❌ **Slower Development Cycle?**: Running tests via Docker might be slower than running them locally from the IDE. |
| ✅ **Optimized Images**: The final images are smaller and more secure. | ❌ **Requires Learning**: You need to understand how IDE integration with Docker works. |

---

## Practical Steps to Switch to a Multi-stage Build

### Changing the Backend Dockerfile
You would need to replace the content of your `Dockerfile.dev` (or create a new `Dockerfile`) with the following:

```dockerfile
# --- STAGE 1: BUILD ---
# Use an official Gradle image with a JDK as a builder stage
# Use an alias "builder" for this stage
FROM gradle:8.7-jdk21 AS builder

# Set the working directory
WORKDIR /home/gradle/src

# Copy the entire project context to the build stage
COPY . .

# Run the Gradle build to create the executable jar
# The --no-daemon flag is recommended for CI/CD and containerized environments
RUN gradle :backend:build --no-daemon

# --- STAGE 2: RUN ---
# Use a lightweight JRE image for the final stage
FROM openjdk:21-jre-slim

# Set the working directory in the final container
WORKDIR /app

# Magic happens here:
# Copy ONLY the built jar from the "builder" stage into the final image
COPY --from=builder /home/gradle/src/backend/build/libs/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# The command to run the application
ENTRYPOINT ["java","-jar","/app/app.jar"]
```

### Changing `docker-compose.yml`
In your `docker-compose.yml` (or `docker-compose.override.yml`), you would need to change the `build` section for the `phoebe-app` service to point to the new Dockerfile and build context.

**Example:**
```yaml
services:
  phoebe-app:
    build:
      context: ../../ # Context is now the project root
      dockerfile: backend/Dockerfile # Path to your new Dockerfile
    ports:
      - "8080:8080"
    # ... other settings
```
The `make run` command (which calls `docker compose up --build`) will automatically use the multi-stage build after these changes.

---

## Configuring IntelliJ IDEA for Docker

When switching to a fully containerized approach, you need to "teach" the IDE to work with Docker.

1.  **Connect Docker**:
    -   Ensure the "Docker" plugin is enabled in IntelliJ IDEA.
    -   Open the `Services` window (`View -> Tool Windows -> Services`).
    -   Click `+` and select `Docker Connection`. The IDE should automatically find your Docker Desktop.

2.  **Run the Project from the IDE**:
    -   Instead of using the terminal, you can create a `Docker-compose` run configuration.
    -   `Run -> Edit Configurations... -> + -> Docker -> Docker-compose`.
    -   Specify the path to your `docker-compose.yml` file.
    -   Now you can start and stop the entire project directly from the IDE.

3.  **Running Tests (The Hardest Part)**:
    -   **Option A (Simple)**: Run tests from the terminal by executing a command inside the running container:
        ```bash
        docker compose exec phoebe-app ./gradlew test
        ```
    -   **Option B (Advanced)**: Configure a test configuration in the IDE that does the same thing. This requires more in-depth setup of `Run/Debug Configurations`.

This document should give you a complete overview of the two approaches and help you choose the best path for your project.