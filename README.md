# Task Management API

Task Management API is a Spring Boot service with JWT authentication.  
It stores **users in PostgreSQL** and **tasks in MongoDB**.

## Stack

- Java 25
- Spring Boot 4.1
- Spring Security + OAuth2 Resource Server (JWT)
- Spring Data JPA + Flyway (PostgreSQL)
- Spring Data MongoDB
- Maven
- Docker / Docker Compose

## What it does

- Register and authenticate users
- Issue JWT tokens
- Create, read, update, and delete tasks
- Filter tasks by status/priority
- Return paged task lists

## Project layout

```text
.
├── src/main/java/com/homework/task_management
│   ├── configuration
│   ├── controller
│   ├── dto
│   ├── model
│   ├── repository
│   └── service
├── src/main/resources
│   ├── application.yml
│   └── db/migration
├── docker-compose.yml
├── Dockerfile
└── pom.xml
```

## Configuration

The app reads values from environment variables (via `application.yml`):

- `MONGODB_URI`
- `POSTGRES_URL`
- `POSTGRES_USER`
- `POSTGRES_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRATION` (seconds)

Docker Compose also expects env files in `.secrets/`:

- `.secrets/app.env`
- `.secrets/mongodb.env`
- `.secrets/postgres.env`

Do not commit `.secrets/` or real credentials.

## Run with Docker Compose

1. Create secrets folder and env files:

```bash
mkdir -p .secrets
```

2. Fill `.secrets/*.env` with your local values.

3. Start services:

```bash
docker compose up --build
```

4. API base URL:

```text
http://localhost:8080
```

Stop services:

```bash
docker compose down
```

Remove services + volumes:

```bash
docker compose down -v
```

## Run locally (without Docker)

You need Java 25 and running PostgreSQL + MongoDB instances, then:

```bash
./mvnw spring-boot:run
```

## Authentication flow

1. `POST /api/v1/auth/register`
2. `POST /api/v1/auth/login` to receive `{ "token": "..." }`
3. Use token for task endpoints:

```text
Authorization: Bearer <token>
```

## API

### Auth

- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`

Register request:

```json
{
  "email": "user@example.com",
  "password": "strong-password"
}
```

Login request:

```json
{
  "email": "user@example.com",
  "password": "strong-password"
}
```

### Tasks

Base path: `/api/v1/tasks` (JWT required)

- `POST /api/v1/tasks`
- `GET /api/v1/tasks`
- `GET /api/v1/tasks/{id}`
- `PUT /api/v1/tasks/{id}`
- `DELETE /api/v1/tasks/{id}`

Create task request:

```json
{
  "title": "Learn Spring Security",
  "description": "Finish JWT auth integration",
  "priority": "MEDIUM"
}
```

Notes:

- `status` is set to `TODO` on create.
- `priority` values: `LOW`, `MEDIUM`, `HIGH`.
- `status` values: `TODO`, `IN_PROGRESS`, `DONE`.

Update task request:

```json
{
  "title": "Learn Spring Security",
  "description": "JWT auth complete",
  "status": "IN_PROGRESS",
  "priority": "HIGH"
}
```

List/filter/pagination example:

```text
GET /api/v1/tasks?status=TODO&priority=HIGH&page=0&size=10&sort=createdAt,desc
```

## Data and migrations

- MongoDB keeps task documents (`tasks` collection).
- PostgreSQL keeps users (`users` table).
- Flyway migration: `V1__create_users_table.sql`.

## License

Educational/demo project.
