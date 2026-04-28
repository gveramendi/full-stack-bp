# Full Stack BP

Full-stack project composed of:

- **full-stack-bp-api** — Spring Boot 3.5 REST API (Java 21, Gradle, JPA, Liquibase, Spring Security).
- **full-stack-bp-ui** — Angular 21 application.
- **PostgreSQL 17** — Database.

## Prerequisites

To run the project with Docker (recommended):

- Docker 24+ and Docker Compose v2.

To run each module locally without Docker:

- Java 21 (JDK)
- Node.js 22 and npm 11
- PostgreSQL 17 (or use the container from the Docker section)

---

## Option 1: Run everything with Docker Compose (recommended)

From the project root:

```bash
docker compose up --build
```

To stop and clean up:

```bash
docker compose down              # Stops containers
docker compose down -v           # Stops and removes the postgres volume
```

---

## Option 2: Run each module locally

### 1. Start PostgreSQL

You can use the Docker Compose service just for the database:

```bash
docker compose up -d postgres
```

### 2. Backend — full-stack-bp-api

```bash
cd full-stack-bp-api
./gradlew bootRun
```

The API will be available at `http://localhost:8080/api`.

Schema and seed data are applied automatically via Liquibase on startup.

### 3. Frontend — full-stack-bp-ui

```bash
cd full-stack-bp-ui
npm install
npm start
```

---

## Additional resources

- **Postman collection**: `docs/BP.postman_collection.json` — import into Postman to try the endpoints.
- **Reference SQL script**: `docs/BaseDatos.sql`.
- **API healthcheck**: `http://localhost:8080/api/actuator/health`.
