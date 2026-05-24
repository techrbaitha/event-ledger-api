# Event Ledger API

A Spring Boot based Event Ledger API for processing financial transaction events with support for:

- Idempotent event ingestion
- Out-of-order event handling
- Balance computation
- Validation and exception handling
- Pagination
- Swagger/OpenAPI
- Dockerized setup
- Integration tests

---

# Tech Stack

- Java 17
- Spring Boot 3
- Spring Data JPA
- H2 Database
- Swagger / OpenAPI
- JUnit 5 + MockMvc
- Docker

---

# Features

## Core Features

- POST transaction events
- Retrieve event by eventId
- Retrieve events by account
- Compute account balance
- Idempotent POST handling
- Chronological event ordering
- Input validation
- Global exception handling

## Bonus Features

- Pagination support
- Dockerized deployment
- Swagger API documentation
- Concurrency-safe duplicate handling

---

# Project Structure

```text
event-ledger-api
│
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── README.md
│
├── src
│   ├── main
│   │   ├── java
│   │   └── resources
│   │
│   └── test
│
└── target
```

---

# Prerequisites

Install:

- Java 17+
- Maven 3+
- Docker (optional)

---

# Build Project

```bash
mvn clean package
```

Build output:

```text
target/event-ledger-api-1.0.0.jar
```

---

# Run Application Locally

```bash
mvn spring-boot:run
```

Application:

```text
http://localhost:8080
```

---

# Swagger UI

API documentation:

```text
http://localhost:8080/swagger-ui.html
```

---

# H2 Database Console

```text
http://localhost:8080/h2-console
```

Use:

```text
JDBC URL:
jdbc:h2:mem:eventdb

Username:
sa

Password:
(blank)
```

---

# Run Tests

```bash
mvn test
```

Expected:

```text
BUILD SUCCESS
```

---

# Run With Docker

Build and start:

```bash
docker compose up --build
```

Swagger:

```text
http://localhost:8080/swagger-ui.html
```

Stop:

```bash
docker compose down
```

---

# API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | /events | Submit transaction event |
| GET | /events/{eventId} | Get event by ID |
| GET | /events | List account events |
| GET | /accounts/{accountId}/balance | Get balance |

---

# Pagination Support

Event listing supports pagination.

Example:

```text
GET /events?account=acct-123&page=0&size=5
```

Defaults:

- page = 0
- size = 10

Events are always returned sorted by:

```text
eventTimestamp ASC
```

---

# Example Event Request

POST `/events`

```json
{
  "eventId": "evt-001",
  "accountId": "acct-123",
  "type": "CREDIT",
  "amount": 150,
  "currency": "USD",
  "eventTimestamp": "2026-05-15T14:02:11Z",
  "metadata": {
    "source": "mainframe-batch",
    "batchId": "B-9042"
  }
}
```

---

# Example Responses

## New Event

Status:

```text
201 Created
```

---

## Duplicate Event

Status:

```text
200 OK
```

Returns original event.

---

## Validation Error

Status:

```text
400 Bad Request
```

Example:

```json
{
  "message": "amount must be greater than 0"
}
```

---

## Event Not Found

Status:

```text
404 Not Found
```

Example:

```json
{
  "message": "Event not found: unknown"
}
```

---

# Design Decisions

## Idempotency

Duplicate submissions are prevented using:

- eventId unique constraint
- Repository duplicate check
- Concurrency-safe exception handling

Duplicate requests:

- do not create new rows
- do not affect balance
- return original event

---

## Out-of-Order Events

Events are stored immutably and retrieved using:

```text
ORDER BY eventTimestamp ASC
```

Arrival order does not affect:

- listing order
- balance accuracy

---

## Balance Computation

Balance is computed dynamically:

```text
sum(CREDIT) - sum(DEBIT)
```

This avoids stale or inconsistent balances.

---

# Test Coverage

Integration tests cover:

- Event creation
- Duplicate event handling
- Event ordering
- Balance calculation
- Validation errors
- 404 handling

Run:

```bash
mvn test
```

---

# Future Improvements

Possible enhancements:

- Cursor-based pagination
- Authentication / Authorization
- Multi-currency balance support
- Observability / Metrics
- Kubernetes deployment