# Pastebin Lite

Pastebin Lite is a lightweight Pastebin-like application built using Spring Boot.
It allows users to create text pastes, generate shareable links, and view pastes
with optional expiry constraints such as time-to-live (TTL) and view limits.

This project was developed as part of a take-home assignment and is designed to
pass automated functional and service-level tests.

---

## Features

- Create a paste containing arbitrary text
- Generate a shareable URL for each paste
- View paste content via browser (HTML)
- Optional constraints:
    - Time-based expiry (TTL)
    - View-count limit
- Pastes become unavailable once any constraint is triggered
- REST APIs return JSON responses
- Browser view renders content safely (XSS protection)

---

## Tech Stack

- Java 17
- Spring Boot
- MongoDB (MongoDB Atlas – free tier)
- Maven

---

## API Endpoints

### Health Check

GET /api/healthz

Response:

{
  "ok": true
}
---
### Create Paste
POST /api/pastes

Request Body:

{
"content": "Hello World",
"ttl_seconds": 60,
"max_views": 5
}


Response:

{
"id": "abc123",
"url": "https://<domain>/p/abc123"
}
---

### Fetch Paste (API)
GET /api/pastes/{id}

Response:

{
  "content": "Hello World",
  "remaining_views": 4,
  "expires_at": "2026-01-01T00:00:00.000Z"
}
---
### View Paste (HTML)
GET /p/{id}

Returns an HTML page containing the paste content

If the paste is unavailable (expired, view limit exceeded, or not found),
the endpoint returns HTTP 404

Running Locally

---
## Prerequisites
Java 17 or higher
Maven
MongoDB (local instance or MongoDB Atlas)
---
### Steps
Clone the repository

git clone <your-repository-url>
cd pastebin-lite
Set environment variables
export MONGO_URI="mongodb+srv://<username>:<password>@<cluster>/pastebin"
Run the application


mvn spring-boot:run
Access the application : http://localhost:8080
---
### Persistence Layer
MongoDB is used as the persistence layer.
For deployed environments, a MongoDB Atlas free (M0) cluster is used to ensure
data persistence across requests.
---
### Deployment
The application is deployed using Render (Free Tier)

MongoDB connection details are provided via environment variables

Application domain is configured using environment variables

No in-memory storage is used

---
### Deterministic Time for Testing
If the environment variable TEST_MODE=1 is set, the application uses the
x-test-now-ms request header as the current time for expiry logic instead of
system time. This enables deterministic automated testing.


### Author
Sai Likhitha