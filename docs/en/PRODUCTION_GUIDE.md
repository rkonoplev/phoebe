# Production Deployment Guide

This document describes the main principles and steps for deploying the application in a production environment.

---

## Core Principles

- **Immutable Artifacts**: Use Docker images as the unit of deployment. Do not make changes to running containers.
- **Configuration Management**: All configuration (ports, credentials, URLs) must be passed through environment variables.
- **Secret Management**: Never store secrets (passwords, tokens) in code or Docker images. Use your environment's tools (Docker Secrets, Kubernetes Secrets, hosting provider environment variables).
- **Database**: In production, an external, managed database (e.g., AWS RDS, DigitalOcean Managed Database, or similar) should be used. It is not recommended to run the database in a Docker container on the same host as the application without a proper backup and failover setup.

---

## Production Configuration

### Spring Profile
In a production environment, always use the `prod` profile.
```bash
SPRING_PROFILES_ACTIVE=prod
```
This profile activates settings from `application-prod.yml`, which:
- Disables detailed SQL logging.
- Sets `ddl-auto: validate` to prevent accidental schema changes.
- Expects all sensitive data to be passed via environment variables.

### Docker Compose for Production
For production deployment, it is recommended to use `docker-compose.prod.yml` in conjunction with `docker-compose.base.yml`.
This allows using the same modular structure as for local development, but with production settings.

**Example `docker-compose.prod.yml` (with production settings):**
```yaml
# docker-compose.prod.yml
services:
  phoebe-app:
    build:
      context: .
      dockerfile: Dockerfile.multistage # Use the multi-stage build
    container_name: phoebe-app
    restart: always
    depends_on:
      phoebe-mysql:
        condition: service_healthy
    environment:
      SPRING_PROFILES_ACTIVE: prod # Activate production profile
      DB_HOST: your_external_db_host # Specify external DB host
      # ... other environment variables for production
    ports:
      - "8080:8080"
    # In production, do not mount source code, only the built JAR
    # volumes:
    #   - ./backend:/app/backend
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 8
      start_period: 120s # Time for application startup

  nextjs-app:
    build:
      context: ./frontends/nextjs
      dockerfile: Dockerfile # Use the same Dockerfile for the frontend
    container_name: phoebe-nextjs
    restart: always
    depends_on:
      - phoebe-app
    ports:
      - "3000:3000"
    # In production, do not mount source code, only the built artifact
    # volumes:
    #   - ./frontends/nextjs:/app
    #   - /app/node_modules
    #   - /app/.next
```
**Note:** In a production environment, `phoebe-mysql` from `docker-compose.base.yml` should be replaced with an external database. This is achieved by overriding environment variables for `phoebe-app` or by using a different `docker-compose.base.yml` for production.

---

## Future Topics for This Guide

### 1. Deployment Scenarios

-   **Deployment to a Virtual Private Server (VPS)**
    -   **Content**: Basic setup of a Linux VPS (Ubuntu/CentOS), installing Docker and Docker Compose, setting up Nginx as a reverse proxy with SSL (Let's Encrypt), configuring firewall, running the application with Docker Compose.
    -   **Related Documents**: Could link to a more detailed "VPS Deployment Guide".
-   **Deployment to Cloud Platforms (AWS, DigitalOcean, Heroku)**
    -   **Content**: Overview of relevant services (e.g., AWS EC2/ECS/Fargate, RDS; DigitalOcean Droplets/App Platform/Managed Databases; Heroku Dynos/Postgres), basic setup steps, considerations for managed services vs. self-managed.
    -   **Related Documents**: Could link to specific "AWS Deployment Guide", "DigitalOcean Deployment Guide", etc.

### 2. CI/CD for Automated Deployment

-   **Building and Publishing Docker Images to GitHub Packages**
    -   **Content**: Configuring GitHub Actions to build the production Docker image (with versioning, e.g., `phoebe-cms:1.0.0`), tagging it, and pushing it to GitHub Container Registry (GHCR) or Docker Hub.
    -   **Related Documents**: Could link to "CI/CD Guide" for general CI setup.
-   **Configuring GitHub Actions to Deploy to a Server**
    -   **Content**: Using GitHub Actions to SSH into a VPS, pull the latest Docker image, and restart the application (e.g., `docker compose pull && docker compose up -d`). Considerations for zero-downtime deployments.
    -   **Related Documents**: Could link to "CI/CD Guide" or a dedicated "Automated Deployment Guide".

### 3. Monitoring and Logging

-   **Setting Up Log Aggregation**
    -   **Content**: Configuring the application to output logs in a structured format (e.g., JSON), using Docker's logging drivers (e.g., `json-file`, `syslog`, `gelf`), integrating with external log management systems (e.g., ELK Stack, Grafana Loki, Logtail).
    -   **Related Documents**: Could link to a "Logging Strategy Guide".
-   **Integration with Monitoring Systems (Prometheus, Grafana)**
    -   **Content**: Exposing application metrics (Spring Boot Actuator), setting up Prometheus to scrape metrics, configuring Grafana dashboards for visualization, basic alert rules.
    -   **Related Documents**: Could link to a "Monitoring Guide".

### 4. Backup and Recovery

-   **Database Backup Strategies**
    -   **Content**: Automated daily/weekly backups for managed databases, `mysqldump` for self-managed databases, storing backups securely (e.g., S3).
    -   **Related Documents**: Could link to "Database Backup Guide".
-   **Disaster Recovery Plan**
    -   **Content**: Steps to restore the application and database from backups in case of a major outage, RTO (Recovery Time Objective) and RPO (Recovery Point Objective) considerations.
    -   **Related Documents**: Could link to a "Disaster Recovery Plan".
