# =======================================================
# Makefile for Phoebe CMS project management
# =======================================================
# This Makefile provides common shortcuts for development,
# testing, building, and cleaning the project environment.
# =======================================================

# Default command when running 'make'
.DEFAULT_GOAL := help

# Define the compose files to be used.
# This allows for easy switching between development modes.
COMPOSE_BASE := -f docker-compose.base.yml
COMPOSE_HYBRID := $(COMPOSE_BASE) -f docker-compose.hybrid.yml
COMPOSE_PROD := $(COMPOSE_BASE) -f docker-compose.prod.yml

# --- Development Modes ---

# Run in HYBRID mode (default).
# This mode is for backend developers. It requires a local JDK for full IDE support.
# It mounts source code for hot-reloading.
run-hybrid:
	docker compose $(COMPOSE_HYBRID) up --build

# Run in PRODUCTION-LIKE mode.
# This mode is for frontend developers, testers, or final checks.
# It uses a multi-stage build and does not require a local JDK.
run-prod:
	docker compose $(COMPOSE_PROD) up --build

# 'run' is now an alias for the default hybrid mode.
run: run-hybrid

# --- Standard Commands ---

# Stop all running containers (keeps database volume).
stop:
	docker compose $(COMPOSE_HYBRID) down
	docker compose $(COMPOSE_PROD) down --remove-orphans

# Reset the entire environment (remove containers + volumes).
# Warning: this deletes your MySQL data volume.
reset:
	docker compose $(COMPOSE_HYBRID) down -v
	docker compose $(COMPOSE_PROD) down -v --remove-orphans

# Force a full rebuild of images without using cache.
# Useful for fixing issues with stale or corrupted Docker cache.
run-no-cache:
	docker compose $(COMPOSE_HYBRID) build --no-cache
	docker compose $(COMPOSE_HYBRID) up

# --- Testing & Analysis ---

# Run integration tests (Testcontainers).
test:
	cd backend && ./gradlew clean integrationTest

# Run all tests (unit + integration).
all-tests:
	cd backend && ./gradlew clean build

# Start backend locally (without Docker, requires local MySQL).
boot:
	cd backend && ./gradlew bootRun

# Clean build artifacts.
clean:
	cd backend && ./gradlew clean

# Run static analysis tools (Checkstyle + PMD).
lint:
	cd backend && ./gradlew checkstyleMain checkstyleTest pmdMain pmdTest

# Generate test coverage report.
coverage:
	cd backend && ./gradlew jacocoTestReport

# --- Help ---

# Show help with available commands.
help:
	@echo "Usage: make [command]"
	@echo ""
	@echo "Development Modes:"
	@echo "  run-hybrid    - (Default) Start in Hybrid Mode. For backend devs, requires local JDK."
	@echo "  run-prod      - Start in Production-Like Mode. For frontend devs, only Docker needed."
	@echo "  run           - Alias for 'run-hybrid'."
	@echo ""
	@echo "Standard Commands:"
	@echo "  stop          - Stop all project containers."
	@echo "  reset         - Stop and remove all containers, volumes, and networks (deletes data)."
	@echo "  run-no-cache  - Force a full rebuild of images without using cache."
	@echo ""
	@echo "Testing & Analysis:"
	@echo "  test          - Run integration tests (uses Testcontainers)."
	@echo "  all-tests     - Run all tests (unit + integration)."
	@echo "  boot          - Start backend locally without Docker (requires local MySQL)."
	@echo "  clean         - Clean Gradle build artifacts."
	@echo "  lint          - Run static analysis tools."
	@echo "  coverage      - Generate test coverage report."
	@echo ""
	@echo "Other:"
	@echo "  help          - Show this help message."

.PHONY: run-hybrid run-prod run stop reset run-no-cache test all-tests boot clean lint coverage help
