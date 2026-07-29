# Task Management Service

A simple task management REST API built with **Spring Boot** and **MongoDB**.

The application provides CRUD operations for managing tasks and uses MongoDB as a persistent data storage.

## Technologies

* Java 25
* Spring Boot 4.1
* Spring Web MVC
* Spring Data MongoDB
* MongoDB 7
* Docker
* Docker Compose
* Maven

## Features

* Create a task
* Retrieve all tasks
* Retrieve a task by ID
* Update a task
* Delete a task
* Persistent MongoDB storage using Docker volumes
* Configuration through environment variables

## Project Structure

```
task-management
│
├── src
│   └── main
│       ├── java
│       └── resources
│           └── application.yml
│
├── secrets
│   ├── app.env
│   └── mongodb.env
│
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Configuration and Secrets

Sensitive configuration values are stored outside of the application code.

The project expects the following secret files:

```
.secrets/
├── app.env
└── mongodb.env
```

### Application secrets

File:

```
.secrets/app.env
```

Content:

```env
MONGODB_URI=mongodb://mongodb:27017/taskdb
```

This variable is used by Spring Boot to configure the MongoDB connection.

---

### MongoDB secrets

File:

```
.secrets/mongodb.env
```

Content:

```env
MONGO_ROOT_USERNAME=change_me
MONGO_ROOT_PASSWORD=change_me
MONGO_DATABASE=taskdb
```

These variables configure the MongoDB container.

> Do not commit the `.secrets` directory to the repository.
> Add it to `.gitignore`:

```
.secrets/
```

For a production environment, secrets should be provided using a secret manager (for example Kubernetes Secrets, Docker Secrets, HashiCorp Vault, AWS Secrets Manager, etc.).

## Running the Application

### Prerequisites

Make sure you have installed:

* Docker
* Docker Compose

Verify installation:

```bash
docker --version
docker-compose --version
```

---

## Start the Application

Create the secrets directory:

```bash
mkdir .secrets
```

Add the required environment files:

```bash
.secrets/app.env
.secrets/mongodb.env
```

Build and start containers:

```bash
docker-compose up --build
```

The application will start:

```
Spring Boot service:
http://localhost:8080

MongoDB:
Running inside Docker container

MongoDB is exposed on the host machine:

mongodb://localhost:27017

For connections between application containers, MongoDB is available through the Docker Compose service name:

mongodb://mongodb:27017/taskdb
```

---

## Stop the Application

Stop containers:

```bash
docker-compose down
```

The MongoDB data will remain stored because the application uses a Docker volume.

To remove containers and database data:

```bash
docker-compose down -v
```

## API Endpoints

Base URL:

```
http://localhost:8080/api/v1/tasks
```

### Create Task

```
POST /tasks
```

Example request:

```json
{
  "title": "Learn Spring Boot",
  "description": "Complete task management project",
  "status": "TODO"
}
```

---

### Get All Tasks

```
GET /tasks
```

---

### Get Task By ID

```
GET /tasks/{id}
```

---

### Update Task

```
PUT /tasks/{id}
```

Example request:

```json
{
  "title": "Learn MongoDB",
  "description": "Understand Docker MongoDB integration",
  "status": "IN_PROGRESS"
}
```

---

### Delete Task

```
DELETE /tasks/{id}
```

## Database Persistence

MongoDB data is stored in a Docker volume:

```yaml
volumes:
  - mongodb-data:/data/db
```

This means that restarting containers will not remove stored tasks.

The data will only be deleted when removing volumes:

```bash
docker-compose down -v
```

## Useful Docker Commands

View running containers:

```bash
docker ps
```

View application logs:

```bash
docker logs task-service
```

Access MongoDB container:

```bash
docker exec -it task-mongodb mongosh
```

List MongoDB collections:

```javascript
show collections
```

View tasks:

```javascript
db.tasks.find().pretty()
```

## License

This project is created for educational and demonstration purposes.
