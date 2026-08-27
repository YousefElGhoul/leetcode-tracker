# LeetCode Tracker API

A production-minded Spring Boot API for retrieving LeetCode progress and building a persistent daily activity heatmap. It combines LeetCode's GraphQL statistics with local snapshots, account authentication, ownership-aware deletion, validated requests, and a stable JSON error contract.

## Architecture

```text
HTTP client
  -> Spring MVC controllers + Bean Validation
  -> JWT security filter / authenticated principal
  -> authentication, tracker, and heatmap services
  -> PostgreSQL via Spring Data JPA
  -> LeetCode GraphQL via a timeout-bounded RestClient
```

The application is stateless for authentication. PostgreSQL stores account password hashes and heatmap snapshots. Flyway owns schema creation/evolution; Hibernate validates mappings at startup and does not update production tables.

## Features

- Solved and available problem counts for All, Easy, Medium, and Hard
- Current-month heatmap backed by persisted daily records
- Same-day and cross-day solved-problem detection
- JWT registration/login with BCrypt password hashes
- Principal-derived deletion so request parameters cannot target another account
- Request validation and consistent client-facing errors
- Explicit handling for LeetCode timeouts, HTTP failures, GraphQL errors, unknown users, and malformed payloads
- OpenAPI/Swagger documentation
- Unit and MockMvc integration tests with no live LeetCode dependency
- Multi-stage, non-root Docker image and test-gated Cloud Run workflow

## Snapshot Logic

The first successful tracker request stores the current solved count as a baseline and does not infer historical activity. Later requests compare the new count with today's existing snapshot or, for a visit-only record, the latest prior non-null snapshot. An increase marks the day as solved and visited. Once detected, `solved=true` is preserved, and transient upstream count regressions do not lower the stored baseline.

`GET /tracker` writes today's snapshot before reading the heatmap, so the response includes the latest daily state. The heatmap contains persisted records from the first day of the current month through today; it does not synthesize rows for days that were never recorded.

## Tech Stack

- Java 21, Spring Boot 4, Spring MVC, Spring Security
- Spring Data JPA, Hibernate, PostgreSQL, Flyway
- JJWT with HS256 signed bearer tokens
- Spring `RestClient` for LeetCode GraphQL
- Jakarta Bean Validation, Springdoc OpenAPI
- JUnit 6, Mockito, MockMvc, H2 test database
- Maven, Docker, GitHub Actions, Google Cloud Run configuration

## Endpoints

All application endpoints use the `/api/v1` prefix.

| Access | Method | Path | Behavior |
|---|---|---|---|
| Public | `POST` | `/api/v1/auth/register` | Create an account; returns `201` |
| Public | `POST` | `/api/v1/auth/login` | Authenticate and return a JWT |
| Public | `GET` | `/api/v1/tracker?username=alice` | Fetch stats and update today's snapshot |
| Public | `POST` | `/api/v1/visit?username=alice` | Record a visit without fetching LeetCode |
| Protected | `DELETE` | `/api/v1/clear` | Delete only the JWT subject's heatmap; returns `204` |

Swagger UI is available at `/swagger-ui/index.html`; the OpenAPI document is at `/v3/api-docs`.

The tracker and visit endpoints intentionally remain public for the portfolio application's read-first frontend flow, although they persist activity. In a multi-tenant production product, rate limiting and authenticated profile ownership should be added before exposing these write paths broadly.

## Authentication

1. Register an application username matching the heatmap identity you own.
2. Log in with the same credentials.
3. Send `Authorization: Bearer <token>` to protected endpoints.

Passwords are stored only as salted, delegating `{bcrypt}` hashes. JWT signatures and expiration are validated on every authenticated request. Missing, malformed, invalid, and expired tokens receive JSON `401` responses. The default lifetime is 24 hours and is configurable through `JWT_EXPIRY`.

Deletion does not accept a username. The service always uses the authenticated JWT subject, preventing an authenticated caller from changing a parameter to clear another username's records. Registration establishes application-local ownership of that username; it does not cryptographically verify ownership of an external LeetCode profile.

## Error Format

MVC validation, authentication, authorization, conflicts, upstream failures, and unexpected errors use the same shape:

```json
{
  "timestamp": "2026-08-27T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Request validation failed",
  "path": "/api/v1/auth/register",
  "fieldErrors": {
    "password": "Password must be between 8 and 72 characters"
  }
}
```

Common statuses are `400` validation, `401` authentication, `403` authorization, `404` unknown LeetCode user, `409` duplicate registration, `502` LeetCode failure/malformed response, and `500` unexpected server failure. Logs contain request paths and failure categories but never request passwords, bearer tokens, database credentials, or secret values.

## Local Setup

Requirements: Java 21, PostgreSQL, and Docker optionally.

1. Create a PostgreSQL database and user.
2. Export the required configuration:

```bash
export JWT_SECRET="$(openssl rand -base64 64)"
export DATASOURCE_URL="jdbc:postgresql://127.0.0.1:5432/leetcode"
export DB_USERNAME="leetcode"
export DB_PASSWORD="your-local-password"
```

3. Start the API:

```bash
./mvnw spring-boot:run
```

The local base URL is `http://localhost:8080`.

## Environment Variables

| Variable | Required | Default | Description |
|---|---:|---|---|
| `JWT_SECRET` | Yes | None | Base64-encoded HS256 key, at least 32 decoded bytes |
| `JWT_EXPIRY` | No | `PT24H` | ISO-8601 token lifetime |
| `DATASOURCE_URL` | No | `jdbc:postgresql://127.0.0.1:5432/leetcode` | PostgreSQL JDBC URL |
| `DB_USERNAME` | No | `postgres` | Database login |
| `DB_PASSWORD` | Yes | None | Database password |
| `PORT` | No | `8080` | Container HTTP port |
| `LEETCODE_GRAPHQL_BASE_URL` | No | `https://leetcode.com/graphql` | Upstream endpoint override |
| `LEETCODE_GRAPHQL_CONNECT_TIMEOUT` | No | `2s` | Upstream connection timeout |
| `LEETCODE_GRAPHQL_READ_TIMEOUT` | No | `5s` | Upstream response timeout |
| `CORS_ALLOWED_ORIGINS` | No | Project frontend origin | Comma-separated exact origins |
| `CORS_ALLOWED_ORIGIN_PATTERNS` | No | Local/project patterns | Comma-separated origin patterns |

Never commit `.env`, JWT keys, database passwords, tokens, or cloud credentials.

## Database Migrations

Flyway executes SQL from `src/main/resources/db/migration`. `V1__create_tracker_schema.sql` creates fresh `users` and `heatmap` tables, widens password storage for future hash formats, and permits a null solve count for visit-only snapshots. The migration contains no row deletion or table drop.

For a fresh database, start the application and Flyway applies V1 automatically. Hibernate then validates the resulting schema with `ddl-auto=validate`.

For an existing database previously managed by Hibernate `ddl-auto=update`:

1. Back up the database and inspect its columns, constraints, and indexes.
2. Rehearse startup against a copy.
3. Flyway's configured baseline version `0` adopts the non-empty schema, then V1 runs its idempotent creation and non-destructive column adjustments.
4. Confirm the `flyway_schema_history` row and application startup before production rollout.

Do not point a new build at production without this backup/rehearsal. Constraint names generated by older Hibernate versions can differ even when the effective schema is compatible.

## Tests

Tests use an in-memory H2 database and mocked HTTP interactions; they require neither PostgreSQL nor LeetCode credentials.

```bash
./mvnw test
./mvnw verify
```

Coverage includes snapshot/activity behavior, password hashing and duplicate users, stats mapping and malformed upstream data, GraphQL variable transport, registration/login, validation, unauthorized and invalid-token responses, and JWT-subject ownership during deletion.

## Docker

Create local environment values from the documented template:

```bash
cp .env.example .env
# Replace both placeholder secrets in .env
docker compose up --build
```

Compose starts PostgreSQL 16 and the API, waits for database health, and persists data in the `pgdata` volume. PostgreSQL is bound to `127.0.0.1:5432`, not all host interfaces.

To build only the application image:

```bash
docker build -t leetcode-tracker:local .
```

The Dockerfile uses Java 21 in both stages, installs dependencies separately for caching, copies no secrets into the image, and runs the application as the non-root `spring` user. Tests are skipped inside the image build because the CI workflow runs `mvn verify` as a required predecessor.

## API Examples

```bash
curl -i http://localhost:8080/api/v1/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","password":"strong-password"}'

curl -s http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","password":"strong-password"}'

curl -s 'http://localhost:8080/api/v1/tracker?username=alice'

curl -i -X DELETE http://localhost:8080/api/v1/clear \
  -H 'Authorization: Bearer YOUR_TOKEN'
```

Example tracker response:

```json
{
  "progress": {
    "all": {"solvedNum": 150, "totalNum": 3450},
    "easy": {"solvedNum": 80, "totalNum": 850},
    "medium": {"solvedNum": 55, "totalNum": 1500},
    "hard": {"solvedNum": 15, "totalNum": 1100}
  },
  "heatmap": [
    {"date": "2026-08-26", "visited": true, "solved": false},
    {"date": "2026-08-27", "visited": true, "solved": true}
  ]
}
```

## CI/CD and Deployment

GitHub Actions runs `./mvnw verify` on pull requests and pushes to `main`. Only a successful `main` verification allows the workflow to build and push `docker.io/<Docker Hub username>/leetcode-tracker:<commit SHA>`, authenticate with Google Cloud through Workload Identity Federation, and request a Cloud Run deployment. The Docker Hub repository must be public so Cloud Run can pull the image directly.

Required GitHub Actions secrets:

| Secret | Purpose |
|---|---|
| `GCP_PROJECT_ID` | Google Cloud project containing the Cloud Run service |
| `GCP_REGION` | Existing Cloud Run service region |
| `DOCKERHUB_USERNAME` | Docker Hub account that owns the public `leetcode-tracker` repository |
| `DOCKERHUB_TOKEN` | Docker Hub access token used to push images; do not use an account password |
| `CLOUD_RUN_SERVICE_NAME` | Existing Cloud Run service name |
| `WIF_PROVIDER` | Workload Identity Federation provider resource name |
| `WIF_SERVICE_ACCOUNT` | Google service account used by GitHub OIDC |
| `JWT_SECRET` | Base64-encoded application JWT signing key |
| `DATASOURCE_URL` | Production PostgreSQL JDBC URL |
| `DB_USERNAME` | Production database username |
| `DB_PASSWORD` | Production database password |

Deployment is configuration-ready, not claimed as universally verified. Repository owners must provision and configure:

- A public Docker Hub repository named `leetcode-tracker`, an existing Cloud Run service, and a reachable PostgreSQL instance
- GitHub OIDC Workload Identity Federation and least-privileged service-account IAM
- GitHub secrets listed at the top of `.github/workflows/deploy-cloud-run.yml`
- Database network access, TLS requirements, backups, and migration rehearsal
- Preferably Google Secret Manager references instead of long-lived application secrets in revision environment variables

Cloud Run is configured as publicly invokable because the stats endpoints are public; destructive data clearing remains protected by application JWT authentication.
