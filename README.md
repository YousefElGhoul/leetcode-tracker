# LeetCode Tracker API

A Spring Boot REST API that fetches and tracks LeetCode statistics for users, including problem-solving progress and daily activity.

## Features

- Fetch LeetCode stats by username via the LeetCode GraphQL API
- Returns solved/total counts broken down by difficulty (Easy, Medium, Hard, All)
- Tracks daily visit activity per user
- Builds a monthly heatmap showing days visited and days solved
- Upserts activity on each stats fetch, comparing against the previous snapshot to determine if problems were solved
- PostgreSQL persistence for heatmap records
- OpenAPI/Swagger documentation available at `/swagger-ui.html`

## Notes

- The first time a username is tracked, a baseline is established without crediting any solved problems — there is no prior snapshot to compare against
- `POST /leet?username=<username>` fetches stats and records activity as a side effect
- `POST /visit?username=<username>` records a visit without fetching stats (no LeetCode API call)
- Requires `DATABASE_URL`, `DB_USERNAME`, and `DB_PASSWORD` environment variables
- Deployed on Fly.io via GitHub Actions
