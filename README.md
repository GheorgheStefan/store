# Store Application

A **Spring Boot REST API** for managing users and products with comprehensive security features.

**Project Repository:** `GheorgheStefan/store`

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Quick Start](#quick-start)
- [API Documentation](#api-documentation)
- [Security](#security)
- [Database](#database)
- [Development](#development)
- [Testing](#testing)
- [Deployment](#deployment)
- [Troubleshooting](#troubleshooting)

---

## 🎯 Overview
**Tech Stack:**
- **Language:** Java 21
- **Framework:** Spring Boot 4.1.0
- **Security:** Spring Security 6.x + custom `sec-lib` (v0.0.8)
- **Database:** PostgreSQL 16
- **Testing:** JUnit 5 + Mockito
- **Build:** Maven
- **Container:** Docker & Docker Compose

---

## ✨ Features

✅ **User Management**
- User registration with email validation
- Secure login with JWT token generation
- User profile management (view, update, delete)
- Role-based access (USER/ADMIN)

✅ **Product Management**
- Create, read, update, delete products
- Stock management
- Product categorization
- Price and inventory tracking

✅ **Security**
- JWT token-based authentication
- BCrypt password hashing
- Method-level authorization with `@PreAuthorize`
- Request validation with Jakarta Validation
- Custom security exceptions via `sec-lib` -> Handmade repository

✅ **Data Persistence**
- PostgreSQL with Flyway migrations
- JPA/Hibernate ORM
- Transaction management

---

### Project Structure

```
src/
├── main/java/com/air/practice/
│   ├── controller/           # REST endpoints
│   │   ├── UserController.java
│   │   └── ProductController.java
│   ├── service/              # Business logic
│   │   ├── UserService.java
│   │   └── ProductService.java
│   ├── repository/           # Data access (JPA)
│   │   ├── UserRepository.java
│   │   └── ProductRepository.java
│   ├── entity/               # JPA entities
│   │   ├── User.java
│   │   └── Product.java
│   ├── dto/                  # Data Transfer Objects
│   │   ├── users/
│   │   ├── products/
│   │   ├── Role.java
│   │   └── ProductCategory.java
│   ├── mapper/               # MapStruct mappers
│   │   ├── UserMapper.java
│   │   └── ProductMapper.java
│   └── StoreApplication.java # Spring Boot entry point
│
├── resources/
│   ├── application.yml       # Configuration
│   └── db/migration/         # Flyway migrations
│       ├── V1__create_users_table.sql
│       └── V2__create_products_table.sql
│
└── test/java/com/air/practice/
    ├── service/
    │   ├── UserServiceTest.java
    │   ├── UserServiceLoginTest.java
    │   ├── ProductServiceTest.java
    │   └── ...
    ├── controller/
    │   ├── UserControllerTest.java
    │   ├── UserControllerLoginTest.java
    │   ├── ProductControllerTest.java
    │   └── ...
    └── repository/
        ├── UserRepositoryTest.java
        └── ProductRepositoryTest.java
```

---

## 🚀 Quick Start

### Prerequisites

- **Java 21**
- **Maven** 
- **Docker & Docker Compose**
- **Git**

### 1. Clone Repository

```bash
git clone https://github.com/GheorgheStefan/store.git
cd store
```

### 2. Configure Environment Variables

Set environment variables:

```bash
# Database Configuration
POSTGRES_USER=storeuseradmin
POSTGRES_PASSWORD=storepassword123!
POSTGRES_DB=store_db

# Security Configuration
JWT_SECRET=your-super-secret-jwt-key-min-32-chars-long-please
```


### 3. Start PostgreSQL Database

```bash
docker-compose up -d
```

This starts a PostgreSQL 16 container with the configured credentials.

### 4. Build Application

```bash
mvn clean package
```

### 5. Run Application

```bash
mvn spring-boot:run
```

### 6. Verify Application

```bash
# Health check
curl http://localhost:8080/actuator/health

# Register a user
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "password": "SecurePass123!"
  }'
```

---

## 📡 API Documentation

### Authentication Flow

```
1. User Registration (POST /users)
   ↓
2. User Login (POST /users/login) → Returns JWT Token
   ↓
3. Include Token in Authorization Header: Bearer <token>
   ↓
4. Access Protected Endpoints
```

### Base URL

```
http://localhost:8080
```

### Endpoints Summary

#### User Endpoints

| Method | Endpoint | Auth Required | Role Required | Description |
|--------|----------|---------------|---------------|-------------|
| POST | `/users` | ❌ No | - | Register new user |
| POST | `/users/login` | ❌ No | - | Login & get JWT token |
| GET | `/users/{userId}` | ✅ Yes | USER/ADMIN | Get user details |
| GET | `/users` | ✅ Yes | ADMIN | List all users |
| PUT | `/users/{userId}` | ✅ Yes | USER/ADMIN | Update user |
| DELETE | `/users/{userId}` | ✅ Yes | USER/ADMIN | Delete user |

#### Product Endpoints

| Method | Endpoint | Auth Required | Role Required | Description |
|--------|----------|---------------|---------------|-------------|
| POST | `/products` | ✅ Yes | ADMIN | Create product |
| GET | `/products` | ✅ Yes | USER/ADMIN | List products |
| GET | `/products/{productId}` | ✅ Yes | USER/ADMIN | Get product details |
| PUT | `/products/{productId}` | ✅ Yes | ADMIN | Update product |
| PATCH | `/products/{productId}/stock` | ✅ Yes | ADMIN | Update stock |
| DELETE | `/products/{productId}` | ✅ Yes | ADMIN | Delete product |


---

## 🔒 Security

### Overview

The application implements **multi-layered security** with Spring Security and the custom **`sec-lib`** library (version 0.0.8). Security is enforced at authentication, authorization, and application layers.

### Security Architecture

```
┌─────────────────────────────────────────────────────────┐
│ 1. Request Layer (Spring Security Filters)              │
│    - JWT Token Extraction                               │
│    - Token Validation                                   │
│    - Authentication Establishment                       │
└─────────────────────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────────────────────┐
│ 2. Authorization Layer (@PreAuthorize Annotations)      │
│    - Method-level security checks                       │
│    - Role validation (USER, ADMIN)                      │
│    - Custom authorization rules                         │
└─────────────────────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────────────────────┐
│ 3. Business Logic Layer (Service)                       │
│    - Additional authorization checks                    │
│    - Business rule validation                           │
│    - Password verification (BCrypt)                     │
└─────────────────────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────────────────────┐
│ 4. Data Layer (Repository)                              │
│    - JPA parameterized queries (SQL injection safe)     │
│    - Database constraints enforcement                   │
└─────────────────────────────────────────────────────────┘
```

### sec-lib Integration

#### What is sec-lib?

`sec-lib` is a **custom internal security library** maintained by the organization that provides:
- **JWT Token Management:** Generation and validation of stateless authentication tokens
- **Security Configuration:** Pre-configured Spring Security setup
- **Custom Exceptions:** Security-specific exception handling
- **Authentication Gateway:** Centralized authentication logic

**Maven Dependency:**
```xml
<dependency>
    <groupId>com.air</groupId>
    <artifactId>sec-lib</artifactId>
    <version>0.0.8</version>
</dependency>
```

---
## 💾 Database

### Database Diagram

```
┌─────────────────────────────────┐
│         users                   │
├─────────────────────────────────┤
│ id (UUID, PK)                   │
│ first_name (VARCHAR)            │
│ last_name (VARCHAR)             │
│ email (VARCHAR, UNIQUE)         │
│ password (VARCHAR)              │
│ role (VARCHAR, CHECK)           │
│ CONSTRAINTS:                    │
│  - UK: email uniqueness         │
│  - CK: role IN ('USER', 'ADMIN')│
└─────────────────────────────────┘

┌─────────────────────────────────┐
│        products                 │
├─────────────────────────────────┤
│ id (UUID, PK)                   │
│ name (VARCHAR)                  │
│ description (VARCHAR)           │
│ price (NUMERIC)                 │
│ stock (INTEGER)                 │
│ category (VARCHAR)              │
│ CONSTRAINTS:                    │
│  - CK: price >= 0               │
│  - CK: stock >= 0               │
└─────────────────────────────────┘
```

### Migrations

Flyway automatically applies migrations on application startup:

```
migrations/
├── V1__create_users_table.sql     (Users table with role enum)
└── V2__create_products_table.sql  (Products table with validations)
```
---

## 👨‍💻 Development

### Prerequisites

- Java 21 JDK
- Maven 3.9+
- IDE: IntelliJ IDEA, VS Code, or Eclipse
- Docker for local PostgreSQL

---

## 🧪 Testing

### Test Coverage

- **Service Tests:** 27 tests (UserService, ProductService)
- **Controller Tests:** 33 tests (UserController, ProductController)
- **Repository Tests:** 11 tests (UserRepository, ProductRepository)
- **Total:** 70+ tests, all passing ✅
---



## 📚 Additional Resources

- **Spring Boot Documentation:** https://spring.io/projects/spring-boot
- **Spring Security Reference:** https://spring.io/projects/spring-security
- **JWT Best Practices:** https://tools.ietf.org/html/rfc7519
- **PostgreSQL Documentation:** https://www.postgresql.org/docs/
- **Maven Documentation:** https://maven.apache.org/