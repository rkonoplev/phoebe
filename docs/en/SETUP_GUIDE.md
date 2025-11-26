# Setup and Deployment Guide

This guide is your main entry point for deploying the project.
It describes all possible scenarios: from migrating an existing site to a clean installation.

> For daily work with an already configured project, please refer to the **[Quick Start Guide](./QUICK_START.md)**.

---

## Requirements

-   **Docker**: Required to run the environment.
-   **Git**: For cloning the repository.
-   **Make**: (Recommended) For executing commands from the `Makefile`.
    -   **macOS**:
        -   **Method 1 (Recommended)**: Install the Xcode Command Line Tools by running `xcode-select --install`. This is a lightweight installation and does not require the full Xcode IDE.
        -   **Method 2 (Homebrew)**: If you use Homebrew, you can install `make` with the command `brew install make`.
    -   **Linux (Debian/Ubuntu)**: Install with `sudo apt-get install build-essential`.
    -   **Windows**: Can be installed using Chocolatey (`choco install make`) or by using the Windows Subsystem for Linux (WSL).
-   **Java (JDK) 21+**: (Optional) Only required for the **hybrid development mode** (`make run-hybrid`), which is convenient for backend developers. For frontend development or testing, a local JDK is not needed.

---

## Initial Setup

Before choosing a scenario, perform these two steps:

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/rkonoplev/phoebe.git
    cd phoebe
    ```

2.  **Create the environment file**:
    ```bash
    cp .env.dev.example .env.dev
    ```
    In most cases, you will not need to modify the `.env.dev` file.

---

## Choosing a Deployment Scenario

- **[Scenario 1: Quick Start for a New Developer (Recommended)](#scenario-1-quick-start-for-a-new-developer-recommended)**
  - **Goal**: To get the project running locally as quickly as possible with a full, ready-to-use database that has already been migrated.

- **[Scenario 2: Clean Installation (New Site)](#scenario-2-clean-installation-new-site)**
  - **Goal**: To launch the project without historical data to start populating a brand-new site from scratch.

- **[Scenario 3: Backing Up and Transferring the Project](#scenario-3-backing-up-and-transferring-the-project)**
  - **Goal**: To save the current state of your local database for transfer to another machine.

- **[Scenario 4: Deploying to Production](#scenario-4-deploying-to-production)**
  - **Goal**: To deploy the application to a live server for public access.

---

### Scenario 1: Quick Start for a New Developer (Recommended)

This process is fully automated and is the standard way to get started with the project.

1.  **Complete** the [Initial Project Setup](#initial-project-setup) steps.

2.  **Choose your development mode and run the command**:

    -   **For Backend Developers (Hybrid Mode):**
        This mode requires a local JDK but provides the best IDE integration.
        ```bash
        make run
        ```
        *(This is an alias for `make run-hybrid`)*
        *Alternative command (without Makefile):*
        ```bash
        docker compose -f docker-compose.base.yml -f docker-compose.hybrid.yml up --build
        ```

    -   **For Frontend Developers and Testers (Production-Like Mode):**
        This mode requires only Docker.
        ```bash
        make run-prod
        ```
        *Alternative command (without Makefile):*
        ```bash
        docker compose -f docker-compose.base.yml -f docker-compose.prod.yml up --build
        ```

**Result:** The `Makefile` or `docker compose` will run the necessary files. Docker will bring up all services (database, backend, frontend), and Flyway will automatically create the schema and populate it with data.

---

### Scenario 2: Clean Installation (New Site)

This scenario is for starting from scratch.

1.  **Disable the data migration.**
    - Navigate to `backend/src/main/resources/db/migration/common/`.
    - Find the script responsible for inserting data (e.g., `V3__insert_sample_data.sql`).
    - **Rename** it by changing the prefix from `V` to `_V` (e.g., `_V3__insert_sample_data.sql`).

2.  **Run the project** in any mode:
    ```bash
    make run
    ```
    *Alternative command (without Makefile):*
    ```bash
    docker compose -f docker-compose.base.yml -f docker-compose.hybrid.yml up --build
    ```
    *(Or use `docker-compose.prod.yml` instead of `docker-compose.hybrid.yml`)*

**Result:** Flyway will create all tables but will skip the data migration. You will get a clean database.

---

### Scenario 3: Backing Up and Transferring the Project

This process is for creating full snapshots of your local database.

- All steps and commands are described in detail in the **[Docker Volume Migration Guide for MySQL Data](./VOLUME_MIGRATION_GUIDE.md)**.

---

### Scenario 4: Deploying to Production

This scenario covers moving the application from local development to a live environment.
It involves server setup, secret management, and using Docker for deployment.

All detailed instructions, environment requirements, and configuration examples are described in a separate guide:

- **➡️ [Production Deployment Guide](./PRODUCTION_GUIDE.md)**

---

## Frontend Debugging

If the backend is running correctly (accessible at `http://localhost:8080`) but you are
experiencing issues with the frontend, you can run it manually for debugging.

### 1. Ensure the Backend is Running
Start the backend and database using `make run` or `make run-prod`.

### 2. Launch the Chosen Frontend Manually

- **For the Next.js Frontend:**
  1.  Navigate to the directory: `cd frontends/nextjs`
  2.  Install dependencies: `npm install`
  3.  Start the application: `npm run dev`
  The application will be available at `http://localhost:3000`.

### 3. Check Frontend Logs

-   **If launched via `make`:**
    ```bash
    docker compose logs -f phoebe-nextjs
    ```
-   **If launched manually:**
    Logs will be displayed directly in the terminal where you ran `npm run dev`.
