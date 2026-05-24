# LeetCode Tracker API

A Spring Boot REST API that fetches and tracks LeetCode statistics for users, including problem-solving progress and daily activity. Uses JWT authentication to protect destructive operations.

**Base URL:** `https://leetcode-tracker-api.yousefelghoul.me` (production) / `http://localhost:8080` (local)

## Features

- **LeetCode stats** — fetches solved/total counts broken down by difficulty (Easy, Medium, Hard, All) via the LeetCode GraphQL API
- **Activity tracking** — records daily visits and detects when problems were solved by comparing against the previous snapshot
- **Monthly heatmap** — builds a day-by-day timeline showing which days were visited and which had solved problems
- **JWT authentication** — register and login to obtain a token; required for destructive operations
- **PostgreSQL persistence** — all heatmap records and user credentials stored in a PostgreSQL database
- **OpenAPI documentation** — interactive Swagger UI for exploring and testing endpoints

## Documentation

Interactive API documentation is available via Swagger UI:

- **Production:** [https://leetcode-tracker-api.yousefelghoul.me/swagger-ui/index.html](https://leetcode-tracker-api.yousefelghoul.me/swagger-ui/index.html)
- **Local:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

The OpenAPI 3 spec is also available at `/v3/api-docs`.

## Endpoints

All endpoints are prefixed with `/api/v1`.

### Public (no auth required)

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/tracker?username=` | Fetch LeetCode stats and record activity |
| POST | `/api/v1/visit?username=` | Record a visit without fetching stats |
| GET | `/api/v1/test-header` | Health-check endpoint |
| POST | `/api/v1/auth/register` | Create a new user account |
| POST | `/api/v1/auth/login` | Authenticate and receive a JWT token |

### Protected (JWT required)

| Method | Path | Description |
|--------|------|-------------|
| DELETE | `/api/v1/clear?username=` | Clear heatmap data for a user |

To access protected endpoints, include the JWT token in the `Authorization` header:
```
Authorization: Bearer <your-token>
```

### `GET /api/v1/tracker?username=`

Fetches LeetCode stats for the given username and records a visit. The first time a user is tracked, a baseline is established with no solved problems credited (no prior snapshot to compare against).

**Response `200 OK`**

```json
{
  "progress": {
    "all":    { "solvedNum": 150, "totalNum": 3450 },
    "easy":   { "solvedNum": 80,  "totalNum": 850 },
    "medium": { "solvedNum": 55,  "totalNum": 1500 },
    "hard":   { "solvedNum": 15,  "totalNum": 1100 }
  },
  "heatmap": [
    { "date": "2026-05-01", "visited": true,  "solved": false },
    { "date": "2026-05-02", "visited": false, "solved": false },
    { "date": "2026-05-03", "visited": true,  "solved": true }
  ]
}
```

The `heatmap` array contains daily records for the tracked period, each with a `date`, `visited` flag, and `solved` flag.

### `POST /api/v1/visit?username=`

Records a visit for the given username without fetching fresh LeetCode stats. Useful for tracking page views independently of API calls.

**Response `200 OK`**

Empty body.

### `GET /api/v1/test-header`

Simple health-check endpoint.

**Response `200 OK`**

```
<h1>This is a test</h1>
```

### `POST /api/v1/auth/register`

Creates a new user account.

**Request body**

```json
{
  "username": "johndoe",
  "password": "securepass123"
}
```

**Response `200 OK`**

Empty body.

### `POST /api/v1/auth/login`

Authenticates with your credentials and returns a JWT token.

**Request body**

```json
{
  "username": "johndoe",
  "password": "securepass123"
}
```

**Response `200 OK`**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresIn": 86400,
  "username": "johndoe"
}
```

The token expires after 24 hours (`expiresIn` is in seconds).

### `DELETE /api/v1/clear?username=`

Clears all heatmap data for the given username. Requires JWT authentication.

**Response `200 OK`**

Empty body.

## Authentication Flow

1. **Register** — `POST /api/v1/auth/register` with `{"username": "...", "password": "..."}`
2. **Login** — `POST /api/v1/auth/login` with the same credentials to receive a JWT token
3. **Use** — Include the token in the `Authorization: Bearer <token>` header on protected requests

Tokens expire after 24 hours.

## Errors

Error handling has not been implemented yet — this section is a placeholder for future work.

| HTTP Status | Condition |
|---|---|
| `500 Internal Server Error` | Any unexpected error (default Spring Boot behaviour) |

## Tech Stack

- **Java 21** with **Spring Boot 4.0**
- **Maven** build system
- **PostgreSQL** database with **Hibernate JPA**
- **Springdoc OpenAPI** for Swagger UI documentation
- **JWT (jjwt)** for authentication
- **Docker** multi-stage build (Alpine-based runtime image)
- **Google Cloud Run** deployment via GitHub Actions

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `JWT_SECRET` | — | Base64-encoded secret key for signing JWT tokens |
| `DATASOURCE_URL` | `jdbc:postgresql://127.0.0.1:5432/leetcode` | PostgreSQL connection URL |
| `DB_USERNAME` | `postgres` | Database username |
| `DB_PASSWORD` | `0000` | Database password |

Generate a JWT secret:

```bash
openssl rand -base64 64
```

## Notes

- The first time a username is tracked, a baseline is established without crediting any solved problems — there is no prior snapshot to compare against.
- `DELETE /api/v1/clear?username=` requires authentication. All other endpoints are public.
- Deployed on Google Cloud Run via GitHub Actions.
