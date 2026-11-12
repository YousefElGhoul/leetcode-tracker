<!--
---
portfolio:
  title: "LeetCode Tracker"
  subtitle: "Persistent Coding Activity API and Dashboard"
  type:
    - Backend
    - Web
    - Tooling
  status: "Maintained"
  time: "2026"

  description: "A Spring Boot backend that combines live LeetCode statistics with persistent daily activity tracking, account authentication, ownership-aware operations, and a PostgreSQL-backed heatmap, paired with a personal dashboard that shows difficulty progress and a GitHub-style activity calendar as part of a larger custom browser homepage system."

  skills:
    - Java
    - Spring Boot
    - Spring Security
    - REST APIs
    - JWT
    - PostgreSQL
    - Spring Data JPA
    - Hibernate
    - Flyway
    - Docker
    - JUnit
    - GitHub Actions
    - Google Cloud Run
    - GraphQL
    - JavaScript
    - HTML
    - CSS
    - GitHub Pages

  live: "https://yousefelghoul.dev/leetcode-tracker/"
  slug: "leetcode-tracker-api"
---
-->

# LeetCode Tracker

A LeetCode progress API and the dashboard that uses it.

| Part | Path | Stack | Live |
|---|---|---|---|
| API | [`be/leetcode-tracker`](be/leetcode-tracker) | Java 21, Spring Boot, PostgreSQL, Docker, Cloud Run | [Swagger UI](https://leetcode-tracker-api.yousefelghoul.me/swagger-ui/index.html) |
| Dashboard | [`fe`](fe) | HTML, CSS, JavaScript, GitHub Pages | [yousefelghoul.dev/leetcode-tracker](https://yousefelghoul.dev/leetcode-tracker/) |

## How it fits together

The dashboard calls `GET /api/v1/tracker?username=...` to show solved counts per difficulty and the current month's heatmap, and `POST /api/v1/visit` when you open LeetCode from it. The API fetches live statistics from LeetCode's GraphQL endpoint, stores daily snapshots in PostgreSQL, and uses JWT accounts so only the owner can clear their heatmap.

## Deployment

Each part deploys by itself when its folder changes on `main`:

- `be/leetcode-tracker/**` runs [`deploy-cloud-run.yml`](.github/workflows/deploy-cloud-run.yml): tests, Docker Hub image push, Cloud Run deploy.
- `fe/**` runs [`deploy-pages.yml`](.github/workflows/deploy-pages.yml): publishes the folder to GitHub Pages.

See each folder's README for setup and details.

## License

[MIT](LICENSE.md)
