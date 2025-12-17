# MRP – Development Protocol

## 1. Technical Steps & Architecture Decisions

- Implemented a pure HTTP server using `com.sun.net.httpserver.HttpServer`
- No frameworks like Spring or ASP.NET were used (requirement fulfilled)
- Layered architecture:
    - Handler layer (HTTP endpoints)
    - Service layer (business logic)
    - Repository layer (PostgreSQL via JDBC)
- JSON serialization handled with Jackson
- PostgreSQL runs in Docker and is accessed via JDBC

## 2. REST API Design

- Endpoints implemented according to the provided OpenAPI specification
- HTTP methods, paths, headers and status codes follow REST principles
- Authentication via token-based authorization using HTTP `Authorization: Bearer <token>`

## 3. Authentication & Authorization

- Users can register and login
- On login, a token is generated and stored server-side
- All protected endpoints validate the token before execution

## 4. Model Classes

Implemented model classes:
- User
- MediaEntry
- Rating

Each model represents a core domain concept of the MRP system.

## 5. Unit Test Coverage

- JUnit 5 used for unit testing
- Core business logic tested (e.g. validation, rating limits, authorization checks)
- Repositories are mocked where appropriate
- Goal was to test logic, not HTTP or database connectivity

## 6. Integration Testing

- A Postman collection is included
- All relevant endpoints can be tested:
    - Register
    - Login
    - Media CRUD
    - Ratings

## 7. Problems Encountered & Solutions

- HTTP routing was implemented manually → solved using a central router
- Token handling without framework required manual validation → implemented a TokenService
- Database connection issues → fixed by Docker Compose and correct JDBC configuration

## 8. Time Tracking (Estimate)

- Project setup & architecture: 5h
- HTTP server & routing: 6h
- Authentication: 3h
- Database integration: 5h
- Testing & Postman: 3h
- Documentation: 1h

Total: ~23 hours

## 9. Git History

- Git was used continuously during development
- History documents incremental development steps
