# Jongnham E-Commerce — Backend API

Spring Boot REST API for the Jongnham e-commerce platform. Provides authentication, product management, file assets, and role-based access control.

## Tech Stack

- **Java 17**
- **Spring Boot 3.5.8**
- **Spring Security** — JWT authentication + RBAC
- **Spring Data JPA / Hibernate**
- **PostgreSQL**
- **Liquibase** — Database migrations
- **ModelMapper** — DTO mapping
- **SpringDoc OpenAPI** — API documentation
- **Lombok**

## Features

- **Authentication** — JWT-based login/register with token in cookie
- **User Management** — CRUD for admin users
- **Role & Permission Management** — RBAC with granular authority strings
- **Product Management** — Products, categories, options, option values
- **Asset Service** — File upload, storage, and serving
- **Banner Management** — Banners with type classification
- **Store Settings** — Store profile and logo
- **Auditable Entities** — Auto-track createdBy/updatedBy timestamps

## Getting Started

### Prerequisites

- Java 17+
- Maven
- PostgreSQL

### Database

Create a PostgreSQL database and configure credentials via environment variables or `application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_DATASOURCE}
    username: ${DB_USERNAME}
    password: ${DB_PW}
```

Migrations are handled automatically by Liquibase on startup.

### Run

```bash
mvn spring-boot:run
```

The API runs on `http://localhost:8080` by default.

### API Documentation

Swagger UI available at `http://localhost:8080/swagger-ui.html` (SpringDoc OpenAPI).

## Project Structure

```
src/main/java/com/dyc/backendecommerce/
├── auth/              # Authentication (login, register, me)
├── user/              # Admin user CRUD
├── role/              # Role management
├── permission/        # Permission management
├── store/             # Store settings
│   └── admin/
├── product/           # Product management
│   └── store/         # Customer-facing product endpoints
├── category/          # Product categories
├── option/            # Product options & option values
├── banner/            # Banners
│   └── admin/
├── banner_type/       # Banner type classification
├── asset/             # File upload & storage
└── shared/            # Config, enums, exceptions, utilities
    ├── config/        # SecurityConfig, JWT filter, BeanConfig, OpenAPI
    ├── entity/        # Auditable base classes
    ├── enums/         # UserRole, AssetType, Gender, etc.
    ├── exception/     # Custom exceptions + global handler
    ├── service/       # Shared services (storage)
    └── util/          # JWT util, response helpers
```

## Related

- **Admin Panel**: `Frontend/jongnhamapplication/` — Next.js admin dashboard
- **Customer Storefront**: `Frontend/Customer-Facing/` — Next.js customer-facing store
