# LeetCode Tracker API

A Spring Boot REST API that fetches and tracks LeetCode statistics for users, including problem-solving progress and daily activity. Uses JWT authentication to protect destructive operations.

## Features

- Fetch LeetCode stats by username via the LeetCode GraphQL API
- Returns solved/total counts broken down by difficulty (Easy, Medium, Hard, All)
- Tracks daily visit activity per user
- Builds a monthly heatmap showing days visited and days solved
- Upserts activity on each stats fetch, comparing against the previous snapshot to determine if problems were solved
- PostgreSQL persistence for heatmap records
- JWT-based authentication for protecting the clear endpoint
- OpenAPI/Swagger documentation available at `/swagger-ui.html`

## Endpoints

### Public (no auth required)

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/tracker?username=` | Fetch LeetCode stats and record activity |
| POST | `/api/v1/visit?username=` | Record a visit without fetching stats |
| GET | `/api/v1/test-header` | Test endpoint |
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

## Authentication Flow

1. **Register** — `POST /api/v1/auth/register` with `{"username": "...", "password": "..."}`
2. **Login** — `POST /api/v1/auth/login` with the same credentials → receive a JWT token
3. **Use** — Include the token in the `Authorization: Bearer <token>` header on protected requests

Tokens expire after 24 hours.

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

- The first time a username is tracked, a baseline is established without crediting any solved problems — there is no prior snapshot to compare against
- `DELETE /api/v1/clear?username=` requires authentication. All other endpoints are public.
- Deployed on Fly.io via GitHub Actions
