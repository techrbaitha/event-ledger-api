# Event Ledger API

Spring Boot Event Ledger API for processing financial transaction events.

## Tech Stack

- Java 17
- Spring Boot 3
- Spring Data JPA
- H2 Database
- Swagger/OpenAPI
- JUnit 5 + MockMvc

---

## Features

- Event ingestion
- Idempotent POST handling
- Out-of-order event support
- Balance computation
- Validation
- Exception handling
- Swagger UI
- Integration tests

---

## Run Application

```bash
mvn spring-boot:run
```

Application:

http://localhost:8080

Swagger:

http://localhost:8080/swagger-ui.html

H2 Console:

http://localhost:8080/h2-console

---

## Run Tests

```bash
mvn test
```

---

## API Endpoints

POST /events

GET /events/{eventId}

GET /events?account={accountId}

GET /accounts/{accountId}/balance

---

## Example Request

```json
{
  "eventId":"evt-001",
  "accountId":"acct-123",
  "type":"CREDIT",
  "amount":150,
  "currency":"USD",
  "eventTimestamp":"2026-05-15T14:02:11Z"
}
```