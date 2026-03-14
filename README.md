# RevPay — User Service

> Manages user profiles, personal and business account information, transaction PINs, and security settings for all RevPay users.

---

## Overview

The User Service owns all **profile and account data** beyond authentication. Once a user registers via the Auth Service, the User Service manages their personal profile, business profile (for business accounts), profile updates, and transaction PIN management. It reads user identity from gateway-injected headers — no JWT parsing needed.

| Property | Value |
|---|---|
| **Service Name** | `user-service` |
| **Internal Port** | `8080` |
| **External Port** | `8082` |
| **Framework** | Spring Boot 3.2.5 + Spring Data JPA |
| **Database** | MySQL — `user_db` |
| **Java Version** | 17 |

---

## Architecture Role

```
API Gateway
  │  X-User-Id, X-User-Email, X-Account-Type headers
  ▼
User Service :8082
  │
  ├── Personal profile CRUD (user_db → personal_profile)
  ├── Business profile CRUD (user_db → business_profile)
  ├── Transaction PIN management
  └── Profile status management
```

---

## API Endpoints

All endpoints require JWT (validated by gateway). User identity comes from `X-User-Id` header.

### Personal Profile

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/users/personal-profile` | Create personal profile |
| `GET` | `/api/users/personal-profile` | Get own personal profile |
| `PUT` | `/api/users/personal-profile` | Update personal profile |

### Business Profile

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/users/business-profile` | Create business profile |
| `GET` | `/api/users/business-profile` | Get own business profile |
| `PUT` | `/api/users/business-profile` | Update business profile |
| `POST` | `/api/users/business-profile/verify` | Submit verification documents |

### Account Management

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/users/set-pin` | Set transaction PIN |
| `PUT` | `/api/users/change-pin` | Change transaction PIN |
| `GET` | `/api/users/profile` | Get combined profile info |
| `GET` | `/api/users/{id}` | Get user by ID (internal use) |

---

## Database Schema (`user_db`)

### `personal_profile`
| Column | Type | Description |
|---|---|---|
| `profile_id` | BIGINT PK | Auto-generated |
| `user_id` | BIGINT | FK to auth users |
| `full_name` | VARCHAR(255) | Display name |
| `dob` | DATE | Date of birth |
| `address` | TEXT | Home address |
| `status` | ENUM | ACTIVE / INACTIVE |

### `business_profile`
| Column | Type | Description |
|---|---|---|
| `business_id` | BIGINT PK | Auto-generated |
| `user_id` | BIGINT | FK to auth users |
| `business_name` | VARCHAR(255) | Registered business name |
| `business_type` | VARCHAR(255) | Type of business |
| `tax_id` | VARCHAR(255) | Tax identification number |
| `address` | TEXT | Business address |
| `status` | ENUM | PENDING / VERIFIED / REJECTED |

---

## Getting Started

### Run with Docker
```bash
docker build -t revpay-user .
docker run -p 8082:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/user_db \
  -e DB_USER=revpay_user \
  -e DB_PASS=revpay_pass \
  -e JWT_SECRET=your-secret \
  -e EUREKA_HOST=eureka-server \
  revpay-user
```

### Run Locally
```bash
./mvnw spring-boot:run
```

### Test — Create Personal Profile
```bash
curl -X POST http://localhost:8082/api/users/personal-profile \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 1" \
  -H "X-User-Email: john@example.com" \
  -H "X-Account-Type: PERSONAL" \
  -d '{
    "fullName": "John Doe",
    "dob": "1995-06-15",
    "address": "123 Main Street, Mumbai"
  }'
```

---

## Configuration

| Environment Variable | Description |
|---|---|
| `SPRING_DATASOURCE_URL` | MySQL JDBC URL for `user_db` |
| `DB_USER` | Database username |
| `DB_PASS` | Database password |
| `JWT_SECRET` | JWT secret (for local filter if applicable) |
| `EUREKA_HOST` | Eureka server hostname |
| `CONFIG_SERVER_HOST` | Config server hostname |

---

## Security

- No JWT parsing — identity provided by gateway headers (`X-User-Id`, `X-User-Email`, `X-Account-Type`)
- Transaction PINs stored as BCrypt hash
- Users can only access their own profile (enforced by `X-User-Id` binding)
- Business profile verification is admin-controlled

---

## Inter-Service Communication

| Called Service | When | Method |
|---|---|---|
| `notification-service` | On profile update | OpenFeign |

---

## Health & Monitoring

| Endpoint | Description |
|---|---|
| `GET /actuator/health` | Service health + DB connectivity |
| `GET /actuator/info` | Service version and metadata |

---

## Key Dependencies

```xml
<dependency>spring-boot-starter-data-jpa</dependency>
<dependency>spring-boot-starter-web</dependency>
<dependency>spring-cloud-starter-netflix-eureka-client</dependency>
<dependency>spring-cloud-starter-openfeign</dependency>
<dependency>mysql-connector-j</dependency>
<dependency>lombok</dependency>
```

---

## Project Structure

```
user-service/
├── src/main/java/com/revpay/userservice/
│   ├── UserServiceApplication.java
│   ├── controller/
│   │   └── UserController.java
│   ├── service/
│   │   └── UserService.java
│   ├── entity/
│   │   ├── PersonalProfile.java
│   │   └── BusinessProfile.java
│   ├── repository/
│   │   ├── PersonalProfileRepository.java
│   │   └── BusinessProfileRepository.java
│   ├── dto/
│   │   ├── PersonalProfileRequest.java
│   │   └── BusinessProfileRequest.java
│   └── feign/
│       └── NotificationServiceClient.java
├── src/main/resources/
│   └── application.properties
├── Dockerfile
└── pom.xml
```

---

## Related Services

- **Auth Service** — creates the user record; User Service creates the profile
- **Notification Service** — notified on profile events via Feign
- **API Gateway** — injects user identity headers before forwarding requests

See the main [RevPay Microservices](https://github.com/Conginzant-Java-FSE/RevPay-Frontend) repository for full architecture.
