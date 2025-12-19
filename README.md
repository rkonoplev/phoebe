[Русская версия](./README_RU.md)

# Phoebe CMS — Open Source Headless CMS on Java & Spring Boot

[![Java CI with Gradle](https://github.com/rkonoplev/phoebe/actions/workflows/gradle-ci.yml/badge.svg)](
https://github.com/rkonoplev/phoebe/actions/workflows/gradle-ci.yml)  
[![codecov](https://codecov.io/gh/rkonoplev/phoebe/graph/badge.svg)](https://codecov.io/gh/rkonoplev/phoebe)  
![GitHub](https://img.shields.io/github/license/rkonoplev/phoebe)

> **Phoebe CMS** is a flexible, open-source headless content management system engineered for
> high-performance content delivery and modern development workflows.

**Phoebe CMS** is a modern, API-first headless CMS built with **Java** and the **Spring Boot 3.2.8** framework.
It provides a flexible REST API for content delivery, making it an ideal backend for **Angular, React, Vue**,
or any other frontend technology.

## 📚 Documentation

Comprehensive documentation covering installation, development, API usage, and deployment.

- **[📖 English Documentation](docs/en/)** - Complete guides and technical specifications.
- **[📖 Русская документация](docs/ru/)** - Полная документация на русском языке.

## 🎯 Why Headless?

Headless architecture is the modern standard for professional organizations that need:

- **Design Control**: Complete freedom over frontend design and user experience.
- **Multi-Platform Publishing**: Deliver content to websites, mobile apps, and any other digital
  platform.
- **Third-Party Integrations**: Easily connect analytics, marketing, and other tools.
- **Scalability**: An API-first approach built for future growth.
- **Flexibility**: Avoid being locked into a single frontend solution.

## 🏗️ Hybrid Architecture

Phoebe follows a **Hybrid Headless** approach:

```
phoebe/
├── backend/          ← Spring Boot API (headless core)
├── frontends/        ← Optional reference frontend implementations
│   ├── angular/
│   └── nextjs/
└── docs/             ← Documentation: "use our implementations or build your own"
```

**Benefits:**
- **Professional Teams**: Use only the API for maximum flexibility.
- **Startups & Small Teams**: Deploy everything "out of the box" using a provided reference implementation.
- **No Vendor Lock-in**: You're not tied to our reference frontends, but we provide them as fully functional starting points.

## 🚀 Quick Start

**Requirements:**
- JDK 21+ (optional, for hybrid development mode)
- Docker & Docker Compose
- Git
- Gradle 8.x is used. Build is Gradle 9–ready.

The fastest way to get the application running:
```bash
# 1. Clone the repository
git clone https://github.com/rkonoplev/phoebe.git
cd phoebe

# 2. Create the environment file (defaults are fine)
cp .env.dev.example .env.dev

# 3. Run everything!
# For backend developers (requires local JDK for full IDE support):
make run
# For frontend developers (only Docker is needed):
make run-prod
```
The API is now available at `http://localhost:8080` and Swagger UI at `http://localhost:8080/swagger-ui/index.html`.

For more detailed instructions on setup, migration, and daily development, please see the comprehensive **[Setup Guide](docs/en/SETUP_GUIDE.md)** and the **[Developer Guide](docs/en/DEVELOPER_GUIDE.md)**.

## 🔧 Key Features

- **Headless API**: Complete REST API for content management.
- **Content Management**: Full CRUD operations for any content type.
- **Taxonomy System**: Categories and tags with flexible filtering.
- **User Management**: Role-based access (ADMIN, EDITOR).
- **Security**: Spring Security with configurable authentication.
- **Performance**: High-performance caching with Caffeine (3.1.8).
- **Rate Limiting**: IP-based protection with Bucket4j (8.15.0).
- **Multi-Database Support**: Works with MySQL 8.3.0 and PostgreSQL 12+.
- **Automated DB Migrations**: Database schema is managed and versioned with Flyway (11.19.1).
- **Modern Testing**: Comprehensive test suite using **Testcontainers (2.0.2)** for reliable, isolated testing.
- **CI/CD**: GitHub Actions pipeline with automated testing and code quality checks.
- **Archived Migration History**: Includes legacy scripts from the original Drupal 6 migration for historical context.

## 🧰 Helper Tools

For easier database management and inspection during local development, Adminer is included:

- **Adminer (Database Management UI)**: Access at [http://localhost:8081](http://localhost:8081)
  - **System**: `MySQL`
  - **Server**: `phoebe-mysql`
  - **Username**: `root`
  - **Password**: `root`
  - **Database**: `phoebe_db`

## 🌟 Use Cases

### For Development Teams & Agencies
- Build custom frontends with any modern framework (React, Vue, Angular, etc.).
- Integrate content into existing mobile applications.
- Power digital experiences, from websites to IoT devices.
- Connect analytics and advertising platforms seamlessly.

### For Businesses & Organizations
- Use a reference implementation for a quick and robust website deployment.
- Customize one of the provided reference implementations (Angular or Next.js) to match your brand.
- Scale up to custom solutions as your organization grows.

## 🛠️ Technology Stack

- **Backend**: [Java 21+](https://www.oracle.com/java/technologies/javase/21-relnote-issues.html), [Spring Boot 3.2.8](https://spring.io/projects/spring-boot), [Spring Security](https://spring.io/projects/spring-security)
- **Database**: [MySQL 8.3.0](https://www.mysql.com/), [PostgreSQL 12+](https://www.postgresql.org/)
- **DB Migration**: [Flyway 11.19.1](https://flywaydb.org/)
- **Mapping**: [MapStruct 1.6.3](https://mapstruct.org/)
- **Caching**: [Caffeine 3.1.8](https://github.com/ben-manes/caffeine)
- **Rate Limiting**: [Bucket4j 8.15.0](https://github.com/bucket4j/bucket4j)
- **Code Quality**: [Checkstyle 10.12.1](https://checkstyle.sourceforge.io/), [PMD 6.55.0](https://pmd.github.io/)
- **Documentation**: [OpenAPI/Swagger 2.8.14](https://swagger.io/)
- **Testing**: [JUnit 5.x](https://junit.org/junit5/), **[Testcontainers 2.0.2](https://testcontainers.com/)**, Mockito
- **CI/CD**: [GitHub Actions](https://github.com/features/actions)
- **Reference Implementations**: [Angular](https://angular.io/) & [Next.js](https://nextjs.org/)

## 📄 License

MIT License - see [LICENSE](LICENSE) for details.

## Legal Notice

This project is distributed under the MIT License.
All source code is authored and reviewed by [Roman Konoplev](https://en.wikipedia.org/wiki/Roman_Konoplev).
The codebase has been verified to comply with open-source licensing standards. AI tools may have assisted in some parts.

---

**Phoebe CMS** - Empowering modern development with headless flexibility and professional-grade features.
