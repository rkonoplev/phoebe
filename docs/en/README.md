# Documentation

This directory contains all English project documentation for **Phoebe CMS**.

> **Русская документация**: See [../ru/](../ru/) for Russian documentation.


---

## 📖 Core Documentation

- **[Project Overview](./PROJECT_OVERVIEW.md)**  
  A comprehensive overview of the project for new developers.
- **[Glossary](./GLOSSARY.md)**  
  Definitions of key terms and technologies.
- **[Technical Debt](./TECHNICAL_DEBT.md)**  
  A list of known issues and planned improvements.
- **[Setup Guide](./SETUP_GUIDE.md)**  
  Step-by-step instructions for the initial project setup.
- **[Quick Start Guide](./QUICK_START.md)**  
  Brief instructions for **getting started quickly and performing daily tasks** with an already
  configured project.
- **[Developer Guide](./DEVELOPER.md)**  
  A detailed description of the **full development workflow** for developers, including IDE setup,
  testing, and CI/CD integration.
- **[API Reference](./API_REFERENCE.md)**  
  Endpoint descriptions, `curl` examples, and specifications.

## 🏗️ Architecture & Setup

- **[Local Development Approaches](./DEVELOPMENT_APPROACHES.md)**  
  Describes and compares hybrid and fully containerized development approaches.
- **[Design Principles](./PRINCIPLES.md)**  
  Fundamental design principles (SOLID, KISS).
- **[Design Patterns](./DESIGN_PATTERNS.md)**  
  Architectural patterns used in the project.
- **[Configuration Guide](./CONFIG_GUIDE.md)**  
  Spring profiles matrix, `application-*.yml` files, `.env`, and secrets.
- **[Production Deployment Guide](./PRODUCTION_GUIDE.md)**  
  Principles and steps for deploying to a production environment.
- **[Configuration Files Overview](../../backend/src/main/resources/README.md)**  
  A detailed description of each `application-*.yml` file.
- **[Docker Guide](./DOCKER_GUIDE.md)**  
  Local development with `docker-compose` and production builds.
- **[Dockerfile Optimization Guide](./DOCKERFILE_OPTIMIZATION_GUIDE.md)**: Recommendations for writing
  Dockerfiles for development and production.
- **[ADR: Unifying the Testing Strategy with Testcontainers](./TESTCONTAINERS_EVOLUTION.md)**  
  The Architectural Decision Record for the migration to Testcontainers.
- **[ADR: MySQL-first Strategy Implementation](./MYSQL_STRATEGY_IMPLEMENTATION.md)**  
  The Architectural Decision Record for dropping H2 in favor of MySQL.

## 🧪 Testing

- **[Testing with Makefile](./TESTING_WITH_MAKEFILE.md)**  
  **(Start here)** A practical guide for running all types of tests.
- **[CI/CD Guide](./CI_CD_GUIDE.md)**  
  Describes how tests are executed in the GitHub Actions pipeline.
- **[Input Validation Guide](./VALIDATION_GUIDE.md)**  
  A guide to content validation and sanitization.

## 🔐 Security & Authentication

- **[Authentication Guide](./AUTHENTICATION_GUIDE.md)**  
  Security implementation for the headless API.
- **[Security & Roles](./SECURITY_ROLES.md)**  
  A guide for implementing role-based restrictions.
- **[Rate Limiting Guide](./RATE_LIMITING.md)**  
  IP-based rate limiting implementation.

## 🎨 Frontend Integration

- **[Frontend Specification (Angular)](./FRONTEND_SPEC_ANGULAR.md)**  
  Technical specification for the Angular implementation.
- **[Frontend Specification (Next.js)](./FRONTEND_SPEC_NEXTJS.md)**  
  Technical specification for the Next.js implementation.
- **[Admin Panel Specification](./ADMIN_PANEL_SPEC.md)**  
  Technical requirements for the admin panel UI.
- **[Frontend Frameworks Guide](./FRONTEND_FRAMEWORKS_GUIDE.md)**  
  A comparative overview of frontend frameworks.

## 🗄️ Database & Migration

- **[Database Guide](./DATABASE_GUIDE.md)**  
  Complete database documentation, including schema and migrations.
- **[MySQL Commands Guide](MYSQL_GUIDE.md)**
  Useful commands for working with MySQL in Docker and locally.
- **[Modern Migration Guide](./MODERN_MIGRATION_GUIDE.md)**  
  The modern way to deploy the project with data from a Drupal 6 dump.
- **[Historical Migration Guide](./MIGRATION_DRUPAL6.md)**  
  Describes the old, manual migration process.
- **[Historical Docker Data Backup and Recovery Guide (Drupal 6 Migration Context)](./LEGACY_DOCKER_DATA_RECOVERY_GUIDE_EN.md)**  
  A historical document describing manual backup and recovery processes relevant during the
  early stages of the project.
- **[Docker Volume Migration Guide](./VOLUME_MIGRATION_GUIDE.md)**  
  Guide to transferring Docker Volumes with MySQL data.
- **[Development Guide with Local Database](LOCAL_DB_DEVELOPMENT_GUIDE.md)**
  Setting up development with locally installed MySQL or PostgreSQL without Docker.

## 🛠️ Development Tools

- **[Code Style Setup](./CODE_STYLE_SETUP.md)**  
  A guide for automatic code formatting in IntelliJ IDEA.
- **[Git and Bash Commands Guide](./GIT_BASH_COMMANDS.md)**  
  Practical Git and Bash commands.
- **[Project Codebase Transfer Guide](./PROJECT_TRANSFER_GUIDE.md)**  
  Backup and recovery of the codebase using Git.
- **[Legacy Migration Archives](../../legacy/README.md)**  
  An archive of obsolete scripts and configurations.