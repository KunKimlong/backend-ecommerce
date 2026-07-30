# Jongnham E-Commerce — Backend API

Spring Boot REST API powering the Jongnham e-commerce platform. Handles authentication, product management, file assets, RBAC, store settings, and all customer-facing endpoints.

## Tech Stack

| Category        | Technology                                              |
| --------------- | ------------------------------------------------------- |
| Language        | Java 17                                                 |
| Framework       | Spring Boot 3.5.8                                       |
| Security        | Spring Security + JWT (HS512) + Cookie/Bearer Auth      |
| Database        | PostgreSQL + Spring Data JPA / Hibernate                |
| Migrations      | Liquibase (YAML changelogs)                             |
| API Docs        | SpringDoc OpenAPI (Swagger UI)                          |
| DTO Mapping     | ModelMapper                                             |
| Build Tool      | Maven                                                   |
| Utilities       | Lombok, SLF4J                                           |

## Features

- **JWT Authentication** — Login, refresh, logout, and session management
- **User Management** — CRUD for admin employees
- **Role-Based Access Control** — Granular permission strings per endpoint
- **Product Management** — Products with variants, categories, options, option values
- **File Assets** — Upload, store, and serve images for products, banners, users, and stores
- **Banner Management** — Banners with type classification for storefront display
- **Store Settings** — Store profile, logo, and contact info
- **Dashboard** — Aggregate stats (products, categories, banners, users counts)
- **Auditable Entities** — Auto-tracked `createdBy`, `updatedBy`, timestamps via JPA auditing
- **Liquibase Migrations** — Version-controlled schema evolution

## Prerequisites

- Java 17+
- Maven
- PostgreSQL (or Docker for containerized DB)

## Getting Started

### 1. Clone & Configure

```bash
git clone <repo-url>
cd backend-ecommerce
```

Copy `.env.example` to `.env` and fill in your values:

```env
APPLICATION_PORT=168

DB_HOST=localhost
DB_PORT=5432
DB_DATASOURCE=ecommerce
DB_USERNAME=admin
DB_PW=admin

JWT_SECRET_KEY=<base64-encoded-256bit-secret>
JWT_EXPIRATION=43200000

ADMIN_EMAIL=adminkimlong@gmail.com
ADMIN_PASSWORD=admin@123

STORE_NAME=TINH TOM NENH

CORS_URLS=http://localhost:3000,http://localhost:1680,https://shop.tinhtomnenh.uk
```

### 2. Start PostgreSQL

```bash
docker-compose up -d
```

Or use your own PostgreSQL instance. The database will be created automatically if it doesn't exist (ensure the user has `CREATE DATABASE` privileges).

### 3. Run

```bash
mvn spring-boot:run
```

The API starts on `http://localhost:168`. On first launch:
- The Liquibase migration runs all schema changelogs
- The admin user from `ADMIN_EMAIL`/`ADMIN_PASSWORD` is auto-created
- The store from `STORE_NAME` is auto-created

### 4. API Documentation

Swagger UI: [http://localhost:168/swagger-ui.html](http://localhost:168/swagger-ui.html)

## API Endpoints

### Authentication

| Method | Path              | Auth          | Description               |
| ------ | ----------------- | ------------- | ------------------------- |
| POST   | `/api/auth/login` | Public        | Login, returns JWT        |
| POST   | `/api/auth/refresh` | Public      | Refresh access token      |
| GET    | `/api/auth/me`    | Authenticated | Current user + permissions |
| POST   | `/api/auth/logout` | Authenticated | Clear server-side session |

### Users (Admin)

| Method | Path                  | Auth         | Description        |
| ------ | --------------------- | ------------ | ------------------ |
| GET    | `/api/users`          | RBAC         | List all users     |
| POST   | `/api/users`          | RBAC         | Create user        |
| GET    | `/api/users/{id}`     | RBAC         | Get user by ID     |
| PUT    | `/api/users/{id}`     | RBAC         | Update user        |

### Role & Permission Management

| Method | Path                           | Auth | Description                |
| ------ | ------------------------------ | ---- | -------------------------- |
| GET    | `/api/admin/roles`             | RBAC | List all roles             |
| POST   | `/api/admin/roles`             | RBAC | Create role                |
| GET    | `/api/admin/roles/{id}`        | RBAC | Get role by ID             |
| PUT    | `/api/admin/roles/{id}`        | RBAC | Update role                |
| DELETE | `/api/admin/roles/{id}`        | RBAC | Delete role                |
| GET    | `/api/admin/permissions`       | RBAC | List all permissions       |
| GET    | `/api/admin/users/{id}/roles`  | RBAC | Get user's roles           |
| PUT    | `/api/admin/users/{id}/roles`  | RBAC | Assign roles to user       |

### Store Settings

| Method | Path                | Auth | Description              |
| ------ | ------------------- | ---- | ------------------------ |
| GET    | `/api/admin/store`  | RBAC | Get store configuration  |
| PUT    | `/api/admin/store`  | RBAC | Update store settings    |

### Products (Admin)

| Method | Path                                   | Auth | Description               |
| ------ | -------------------------------------- | ---- | ------------------------- |
| GET    | `/api/admin/product`                   | RBAC | List all products         |
| POST   | `/api/admin/product`                   | RBAC | Create product            |
| GET    | `/api/admin/product/{id}`              | RBAC | Get product by ID         |
| PUT    | `/api/admin/product/{id}`              | RBAC | Update product            |
| DELETE | `/api/admin/product/{id}`              | RBAC | Delete product            |
| PUT    | `/api/admin/product-variant/{id}`      | RBAC | Update product variant    |

### Categories, Options, Option Values (Admin)

| Method | Path                  | Auth | Description             |
| ------ | --------------------- | ---- | ----------------------- |
| CRUD   | `/api/category`       | RBAC | Category management     |
| CRUD   | `/api/option`         | RBAC | Product option management |
| CRUD   | `/api/option-value`   | RBAC | Option value management |

### Banners (Admin)

| Method | Path                  | Auth | Description              |
| ------ | --------------------- | ---- | ------------------------ |
| CRUD   | `/api/admin/banner`   | RBAC | Banner management        |
| GET    | `/api/banner-type`    | RBAC | List banner types        |

### Assets

| Method | Path                         | Auth  | Description          |
| ------ | ---------------------------- | ----- | -------------------- |
| POST   | `/api/asset/upload`          | RBAC  | Upload file          |
| GET    | `/api/asset/image/{uuid}`    | Public | Serve file by UUID   |
| GET    | `/api/asset`                 | RBAC  | List all assets      |
| DELETE | `/api/asset/{id}`            | RBAC  | Delete asset         |

### Dashboard

| Method | Path                       | Auth  | Description                       |
| ------ | -------------------------- | ----- | --------------------------------- |
| GET    | `/api/admin/dashboard`     | Public | Aggregate stats (counts, revenue) |

### Storefront (Public)

| Method | Path                     | Auth   | Description             |
| ------ | ------------------------ | ------ | ----------------------- |
| GET    | `/api/store/info`        | Public | Store info (logo, name) |
| GET    | `/api/store/banner`      | Public | Active banners          |
| GET    | `/api/store/category`    | Public | Active categories       |
| GET    | `/api/store/product`     | Public | Products with filters   |

## Authentication & RBAC

1. **Login** — POST `/api/auth/login` returns a JWT in the response body and sets it as an `authToken` cookie.
2. **Authenticate** — The client sends the JWT via `Authorization: Bearer <token>` header or the `authToken` cookie. `JwtFilter` validates every request.
3. **Authorize** — `EndpointPermissions.java` maps each HTTP method+path to a required permission string. The `SecurityConfig` applies these rules.
4. **Auto-Create** — On startup, `AutoCreateAdminConfig` creates the first admin user and `AutoCreateStoreConfig` creates the default store (configured in `.env`).

## Project Structure

```
src/main/java/com/dyc/backendecommerce/
├── BackendEcommerceApplication.java    # Entry point
│
├── auth/                               # Auth controller, service, DTOs
├── user/                               # User/employee management (not yet extracted)
├── role/                               # Role CRUD + user-role assignment
├── permission/                         # Permission listing
├── store/                              # Store settings (admin + public info)
├── product/                            # Products & variants (admin + storefront)
├── category/                           # Category CRUD
├── option/                             # Product options
├── optionvalue/                        # Option values
├── banner/                             # Banners (admin + storefront)
├── banner_type/                        # Banner type classification
├── asset/                              # File upload & storage
├── dashboard/admin/                    # Dashboard statistics
├── test/                               # Health-check endpoint
└── shared/
    ├── config/         # SecurityConfig, JwtFilter, BeanConfig, OpenAPI, auto-create configs
    ├── entity/         # Auditable base class
    ├── enums/          # UserRole, AssetType, Gender, Permission constants
    ├── exception/      # Custom exceptions + GlobalExceptionHandler
    ├── service/        # StorageService (file I/O)
    └── util/           # JwtUtil, ResponseData (paginated response wrapper)
```

## Environment Variables

| Variable            | Description                  | Default            |
| ------------------- | ---------------------------- | ------------------ |
| `APPLICATION_PORT`  | Server port                  | `168`              |
| `DB_HOST`           | PostgreSQL host              | `localhost`        |
| `DB_PORT`           | PostgreSQL port              | `5432`             |
| `DB_DATASOURCE`     | Database name                | `ecommerce`        |
| `DB_USERNAME`       | Database user                | `admin`            |
| `DB_PW`             | Database password            | `admin`            |
| `JWT_SECRET_KEY`    | Base64-encoded HS512 secret  | —                  |
| `JWT_EXPIRATION`    | Token TTL in ms              | `43200000` (12h)   |
| `ADMIN_EMAIL`       | Auto-created admin email     | —                  |
| `ADMIN_PASSWORD`    | Auto-created admin password  | —                  |
| `STORE_NAME`        | Default store name           | —                  |
| `CORS_URLS`         | Comma-separated allowed origins | —               |

## Database Migrations

Liquibase changelogs are in `src/main/resources/db/changelog/`:

- `db.changelog-master.yaml` — entry point
- `v1.0.0.yaml` — initial schema (users, products, categories, roles, permissions, etc.)
- `v1.1.0.yaml`, `v1.2.0.yaml`, `v1.3.0.yaml` — incremental changes

Migration SQL scripts and data seeding scripts are in `src/main/resources/db/sql/`.

## Related

- **Admin Panel** — [`Frontend/jongnhamapplication/`](../Frontend/jongnhamapplication/) — Next.js admin dashboard
- **Customer Storefront** — [`Frontend/Customer-Facing/`](../Frontend/Customer-Facing/) — Next.js storefront
