# Task Manager REST API

Task Manager REST API is a Java backend project built with Spring Boot, Spring Data JPA, PostgreSQL, and Maven. The API supports creating, reading, updating, and deleting task records through REST endpoints.

## Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Maven
- JUnit

## Project Structure

- `controller`: REST endpoints for task operations
- `service`: business logic for CRUD flows
- `repository`: Spring Data JPA persistence layer
- `model`: JPA entity and enum types
- `dto`: request and response payload classes
- `exception`: global exception handling and error responses

## Task Model

Each task includes:

- `id`
- `title`
- `description`
- `status`
- `createdAt`
- `updatedAt`

Supported `status` values:

- `TODO`
- `IN_PROGRESS`
- `DONE`

## Prerequisites

- Java 21
- PostgreSQL 16 or another compatible PostgreSQL version

## Database Setup

Create the database:

```sql
CREATE DATABASE task_manager;
```

Create the application user:

```sql
CREATE USER taskapp WITH PASSWORD 'yflyi';
GRANT ALL PRIVILEGES ON DATABASE task_manager TO taskapp;
```

The application currently uses these settings from `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/task_manager
spring.datasource.username=taskapp
spring.datasource.password=yflyi
```

Hibernate is configured with `spring.jpa.hibernate.ddl-auto=update`, so the `tasks` table is created automatically when the application starts.

## Run the Application

Start the API with the Maven wrapper:

```bash
./mvnw spring-boot:run
```

The API runs on:

```text
http://localhost:8080
```

## Run the Tests

Run the JUnit test suite with:

```bash
./mvnw test
```

The current integration tests cover:

- successful task creation
- successful task retrieval by id
- not found response for a missing task id
- validation failure for invalid task input

## API Endpoints

Base path:

```text
/api/tasks
```

### Create Task

- Method: `POST`
- Path: `/api/tasks`

Request body:

```json
{
  "title": "Finish backend project",
  "description": "Complete the task manager API",
  "status": "TODO"
}
```

Response:

- `201 Created`

### Get All Tasks

- Method: `GET`
- Path: `/api/tasks`

Response:

- `200 OK`

### Get Task By Id

- Method: `GET`
- Path: `/api/tasks/{id}`

Response:

- `200 OK` if the task exists
- `404 Not Found` if the task does not exist

### Update Task

- Method: `PUT`
- Path: `/api/tasks/{id}`

Request body:

```json
{
  "title": "Finish backend project",
  "description": "Add the remaining documentation",
  "status": "IN_PROGRESS"
}
```

Response:

- `200 OK` if the task exists
- `404 Not Found` if the task does not exist

### Delete Task

- Method: `DELETE`
- Path: `/api/tasks/{id}`

Response:

- `204 No Content` if the task exists
- `404 Not Found` if the task does not exist

## Validation Rules

- `title` is required
- `title` must be 100 characters or fewer
- `description` must be 1000 characters or fewer
- `status` is required

## Error Response Format

The API returns a structured error response for validation failures and missing resources:

```json
{
  "timestamp": "2026-03-17T14:46:44.062981701",
  "status": 404,
  "error": "Not Found",
  "message": "Task with id 999999 was not found",
  "path": "/api/tasks/999999",
  "details": null
}
```

Validation failures include field-level messages in `details`:

```json
{
  "timestamp": "2026-03-17T14:46:41.289647967",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/tasks",
  "details": [
    "title: Title is required"
  ]
}
```
