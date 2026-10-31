# Movies API

A Spring Boot REST API that serves movie, showtime and seat data for the **Cinefc** iOS project.

> **Purpose and scope**
>
> This API was built **specifically** as the backend for a Proof of Concept (PoC) developed at the Apple Developer Academy. It exists to support the Swift project [**Cinefc**](https://github.com/MrSampaio/Cinefc), which explores how **App Clips** can be launched from **NFC tags** and then fetch information from a server.
>
> It is intentionally small, has no authentication and is not designed or hardened for production use. If you are not working on the Cinefc PoC, this repository is probably not what you are looking for.

---

## Table of Contents

1. [Related Project](#related-project)
2. [How It Fits Together](#how-it-fits-together)
3. [Tech Stack](#tech-stack)
4. [Getting Started](#getting-started)
5. [Configuration](#configuration)
6. [API Reference](#api-reference)
7. [Error Handling](#error-handling)
8. [Data Model](#data-model)
9. [Seed Data](#seed-data)
10. [Using the API from the Cinefc Swift Project](#using-the-api-from-the-cinefc-swift-project)
11. [Known Limitations](#known-limitations)
12. [Project Structure](#project-structure)

---

## Related Project

| Project | Description | Repository |
|---|---|---|
| **Cinefc** (Swift) | iOS app and App Clip PoC that reads NFC tags and displays movie information | https://github.com/MrSampaio/Cinefc |
| **Movies API** (this repo) | Spring Boot backend that the Cinefc app consumes | https://github.com/MrSampaio/Movies-Api |

The JSON contracts in this API are designed to match the Codable models in the Cinefc project (`MovieDetails`, `Showtime` and `Seat`). The database is named `cinefc` for the same reason.

---

## How It Fits Together

The intended PoC flow is:

```
NFC tag
   |  (encodes an App Clip invocation URL that identifies a movie)
   v
iPhone reads the tag and launches the App Clip
   |
   v
App Clip extracts the movie id and calls this API
   |
   |  GET /api/cinema/movies/{id}
   v
Movies API (Spring Boot)  <---->  MySQL (database: cinefc)
   |
   v
App Clip renders title, synopsis, cover and showtimes
   |
   |  user taps a showtime
   |  GET /api/cinema/showtimes/{id}/seats
   v
App Clip renders the seat map
```

Each NFC tag is meant to be tied to one movie, so reading the tag is enough to know which `id` to request.

---

## Tech Stack

| Component | Version / Detail |
|---|---|
| Language | Java 26 |
| Framework | Spring Boot 4.1.2 |
| Web layer | Spring Web MVC (`spring-boot-starter-webmvc`) |
| Persistence | Spring Data JPA (Hibernate) |
| Database | MySQL (via MySQL Connector/J) |
| Boilerplate reduction | Lombok |
| Developer tooling | Spring Boot DevTools |
| Build tool | Maven (Maven Wrapper included) |

---

## Getting Started

### Prerequisites

- JDK 26 (the version declared in `pom.xml`)
- A MySQL server running on `localhost:3306`
- No local Maven installation is needed; the Maven Wrapper (`mvnw`) is included

### 1. Clone the repository

```bash
git clone https://github.com/MrSampaio/Movies-Api.git
cd Movies-Api
```

### 2. Create and seed the database

The schema and sample data live in `src/main/java/com/movies/api/database/script.sql`. Run it against your MySQL server:

```bash
mysql -u root -p < src/main/java/com/movies/api/database/script.sql
```

This creates the `cinefc` database, the three tables (`movie`, `showtime`, `showtime_seat`) and inserts one movie, two showtimes and 72 seats.

The application runs Hibernate with `ddl-auto=validate`, which means it **does not create tables for you**. If the schema is missing or does not match the entities, the application will fail at startup.

### 3. Configure the database connection

Edit `src/main/resources/application.properties` with your own MySQL credentials. See [Configuration](#configuration).

### 4. Run the API

macOS / Linux:

```bash
./mvnw spring-boot:run
```

Windows:

```bash
mvnw.cmd spring-boot:run
```

The API starts on **http://localhost:8080**.

### 5. Verify it works

```bash
curl http://localhost:8080/api/cinema/movies/1
```

You should receive the seeded movie as JSON.

### Running the tests

```bash
./mvnw test
```

The only test is a Spring context-load test, so it needs a reachable MySQL instance with the `cinefc` schema already created.

---

## Configuration

`src/main/resources/application.properties`:

```properties
spring.application.name=api
spring.datasource.url=jdbc:mysql://localhost:3306/cinefc
spring.datasource.username=root
spring.datasource.password=<your-mysql-password>
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=validate
```

| Property | Purpose |
|---|---|
| `spring.datasource.url` | JDBC URL of the MySQL database. Defaults to the local `cinefc` database. |
| `spring.datasource.username` | MySQL user. |
| `spring.datasource.password` | MySQL password. |
| `spring.jpa.hibernate.ddl-auto` | Set to `validate`: Hibernate only checks that the schema matches the entities. |

Any property can also be overridden with environment variables, which avoids keeping credentials in the file:

```bash
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=your-mysql-password
./mvnw spring-boot:run
```

To run on a different port, add `server.port=9090` (or set `SERVER_PORT=9090`).

---

## API Reference

**Base URL (local):** `http://localhost:8080/api/cinema`

**Conventions**

- All requests and responses use JSON (`application/json`).
- There is no authentication, no pagination and no API versioning.
- Date-times are returned as ISO-8601 strings **without a timezone** (for example `2026-11-13T15:00:00`). They represent the local time of the cinema.
- CORS is not configured. This is not an issue for native iOS clients, but browser-based clients on another origin would be blocked.

### Endpoint Summary

| Method | Path | Description | Success | Error |
|---|---|---|---|---|
| `GET` | `/api/cinema/movies/{id}` | Movie details with its showtimes. This is the route the App Clip calls after reading an NFC tag. | `200 OK` | `404 Not Found` |
| `GET` | `/api/cinema/showtimes/{id}/seats` | Seat map for a showtime. Called when the user taps a showtime. | `200 OK` | none |
| `POST` | `/api/cinema/movies` | Creates a new movie. | `201 Created` | `500` on missing fields |

---

### GET /api/cinema/movies/{id}

Returns the details of one movie together with all of its showtimes.

**Path parameters**

| Name | Type | Description |
|---|---|---|
| `id` | `long` | Movie identifier. This is the value an NFC tag should resolve to. |

**Response body (`MovieDetailsDTO`)**

| Field | Type | Description |
|---|---|---|
| `id` | number | Movie identifier |
| `title` | string | Movie title |
| `synopsis` | string | Movie synopsis |
| `coverUrl` | string | URL of the cover image |
| `showtimes` | array of `ShowtimeDTO` | Showtimes of this movie (empty array if none) |

`ShowtimeDTO`:

| Field | Type | Description |
|---|---|---|
| `id` | number | Showtime identifier. Use it to request the seat map. |
| `startTime` | string | Start date and time, ISO-8601, no timezone |

**Example request**

```bash
curl http://localhost:8080/api/cinema/movies/1
```

**Example response** `200 OK`

```json
{
  "id": 1,
  "title": "Todo Mundo em Pânico",
  "synopsis": "Vinte e seis anos depois de escaparem de um assassino mascarado suspeitosamente familiar, o Quarteto Fantástico está de volta à mira do criminoso, e nenhuma propriedade intelectual de filmes de terror está a salvo.",
  "coverUrl": "https://www.themoviedb.org/t/p/w600_and_h900_face/y9yJd2qIIwhZcllHeKHsz5eRvNr.jpg",
  "showtimes": [
    { "id": 1, "startTime": "2026-11-13T15:00:00" },
    { "id": 2, "startTime": "2026-11-13T20:30:00" }
  ]
}
```

**Example error** `404 Not Found` (no movie with that id)

```bash
curl -i http://localhost:8080/api/cinema/movies/999
```

```
HTTP/1.1 404
Content-Type: application/json
```

See [Error Handling](#error-handling) for the body format.

**Notes**

- The order of `showtimes` is not explicitly defined by the API. If your client needs chronological order, sort by `startTime` on the client side.

---

### GET /api/cinema/showtimes/{id}/seats

Returns every seat of a showtime, including whether each seat is still available.

**Path parameters**

| Name | Type | Description |
|---|---|---|
| `id` | long | Showtime identifier (the `id` of a `ShowtimeDTO` returned by the movie endpoint) |

**Response body**: an array of `SeatDTO`

| Field | Type | Description |
|---|---|---|
| `id` | number | Seat identifier |
| `rowLetter` | string | Row letter, a single character (`A` to `F` in the seed data) |
| `seatNumber` | number | Seat number within the row |
| `isAvailable` | boolean | `true` if the seat is free, `false` if it is taken |

**Example request**

```bash
curl http://localhost:8080/api/cinema/showtimes/1/seats
```

**Example response** `200 OK`

The seeded showtimes have 36 seats each (a 6 by 6 grid). The first three seats of showtime 1 are shown here:

```json
[
  { "id": 1, "rowLetter": "A", "seatNumber": 1, "isAvailable": true },
  { "id": 2, "rowLetter": "A", "seatNumber": 2, "isAvailable": false },
  { "id": 3, "rowLetter": "A", "seatNumber": 3, "isAvailable": false }
]
```

**Notes**

- If the showtime does not exist or has no seats, the API returns `200 OK` with an empty array (`[]`), not a 404.
- Seat order is not explicitly defined. Group and sort by `rowLetter` and `seatNumber` on the client when rendering a seat map.
- This endpoint is read-only. There is currently no endpoint to reserve or release a seat.

---

### POST /api/cinema/movies

Creates a new movie.

**Request headers**

```
Content-Type: application/json
```

**Request body (`CreateMovieDTO`)**

| Field | Type | Required | Description |
|---|---|---|---|
| `title` | string | yes | Movie title (max 255 characters) |
| `synopsis` | string | yes | Movie synopsis |
| `coverUrl` | string | yes | URL of the cover image (max 500 characters) |

**Response body (`MovieDTO`)**

| Field | Type | Description |
|---|---|---|
| `id` | number | Generated identifier of the new movie |
| `title` | string | Saved title |
| `synopsis` | string | Saved synopsis |
| `coverUrl` | string | Saved cover URL |

**Example request**

```bash
curl -X POST http://localhost:8080/api/cinema/movies \
  -H "Content-Type: application/json" \
  -d '{
    "title": "A Festa da Salsicha",
    "synopsis": "Os alimentos de um supermercado acreditam que o momento em que são escolhidos pelos clientes é a ida para o paraíso. Porém, uma salsicha chamada Frank descobre a terrível e sangrenta verdade sobre o que os humanos realmente fazem com eles.",
    "coverUrl": "https://media.themoviedb.org/t/p/w600_and_h900_face/l8XFkTzWxObRLFqYlgZ1UBgvw5r.jpg"
  }'
```

**Example response** `201 Created`

```json
{
  "id": 2,
  "title": "A Festa da Salsicha",
  "synopsis": "Os alimentos de um supermercado acreditam que o momento em que são escolhidos pelos clientes é a ida para o paraíso. Porém, uma salsicha chamada Frank descobre a terrível e sangrenta verdade sobre o que os humanos realmente fazem com eles.",
  "coverUrl": "https://media.themoviedb.org/t/p/w600_and_h900_face/l8XFkTzWxObRLFqYlgZ1UBgvw5r.jpg"
}
```

**Notes**

- The returned `id` is the value you should encode in the NFC tag URL for this movie.
- A newly created movie has **no showtimes and no seats**. `GET /api/cinema/movies/{id}` will return `"showtimes": []` until you add showtimes directly in the database. There is no endpoint for creating showtimes or seats. See the SQL example below.
- The request body is not validated. Omitting a required field results in a database constraint violation and a `500 Internal Server Error`.

**Adding showtimes and seats to a new movie (SQL)**

```sql
USE cinefc;

-- Showtime for movie id 2
INSERT INTO showtime (movie_id, start_time) VALUES (2, '2026-11-14 18:00:00');
SET @showtime_id = LAST_INSERT_ID();

-- Seats for that showtime (repeat for each seat)
INSERT INTO showtime_seat (showtime_id, row_letter, seat_number, is_available) VALUES
(@showtime_id, 'A', 1, true),
(@showtime_id, 'A', 2, true),
(@showtime_id, 'A', 3, true);
```

---

## Error Handling

| Status | When it happens |
|---|---|
| `200 OK` | Successful read |
| `201 Created` | Movie successfully created |
| `400 Bad Request` | The path id is not a number (for example `/movies/abc`) or the request body is not valid JSON |
| `404 Not Found` | `GET /movies/{id}` with an id that does not exist. The service throws a `ResponseStatusException` with the message `Movie not found.` |
| `500 Internal Server Error` | Unexpected errors, including `POST /movies` with a missing required field |

Error bodies follow Spring Boot's default error format. A typical `404` looks like this:

```json
{
  "timestamp": "2026-10-31T14:05:12.345+00:00",
  "status": 404,
  "error": "Not Found",
  "path": "/api/cinema/movies/999"
}
```

By default Spring Boot does not include the exception message in the response body, so clients should rely on the HTTP status code rather than on a message string.

---

## Data Model

Three tables, matching the JPA entities `Movie`, `Showtime` and `ShowtimeSeat`:

```
movie                      showtime                    showtime_seat
-----                      --------                    -------------
id          BIGINT PK      id          BIGINT PK       id            BIGINT PK
title       VARCHAR(255)   movie_id    BIGINT FK  -->  showtime_id   BIGINT FK
synopsis    TEXT           start_time  DATETIME        row_letter    CHAR(1)
cover_url   VARCHAR(500)                               seat_number   INT
                                                       is_available  BOOLEAN (default TRUE)

movie 1 ---- * showtime 1 ---- * showtime_seat
```

| Entity | Table | Relationship |
|---|---|---|
| `Movie` | `movie` | One movie has many showtimes |
| `Showtime` | `showtime` | Many showtimes belong to one movie (`movie_id`) |
| `ShowtimeSeat` | `showtime_seat` | Many seats belong to one showtime (`showtime_id`) |

Entities are never returned directly. The service layer maps them to DTOs (`MovieDTO`, `MovieDetailsDTO`, `ShowtimeDTO`, `SeatDTO`), so the JSON contract stays independent from the persistence model.

---

## Seed Data

`script.sql` inserts the following sample data so the PoC works out of the box:

| Entity | Content |
|---|---|
| Movie | id `1`, "Todo Mundo em Pânico" |
| Showtimes | id `1` at `2026-11-13 15:00:00` and id `2` at `2026-11-13 20:30:00`, both for movie 1 |
| Seats | 36 seats per showtime (rows `A` to `F`, seats `1` to `6`). Some seats are marked as taken (`is_available = false`) for testing. |

The sample content is in Portuguese, since it comes from a Brazilian cinema context.

---

## Using the API from the Cinefc Swift Project

The [Cinefc](https://github.com/MrSampaio/Cinefc) project mirrors this API's DTOs with Codable models:

| Swift model | API DTO |
|---|---|
| `MovieDetails` | `MovieDetailsDTO` |
| `Showtime` | `ShowtimeDTO` |
| `Seat` | `SeatDTO` |

The networking layer lives in `Cinefc/Network/` (`CinemaModels.swift` and `MoviesApiService.swift`). At the moment the service fetches a movie by id from `http://localhost:8080/api/cinema/movies/{id}`.

Fetching a movie:

```swift
func fetchMovie(id: Int) async throws -> MovieDetails {
    let url = URL(string: "http://localhost:8080/api/cinema/movies/\(id)")!
    let (data, response) = try await URLSession.shared.data(from: url)

    guard let http = response as? HTTPURLResponse, http.statusCode == 200 else {
        throw URLError(.badServerResponse)
    }
    return try JSONDecoder().decode(MovieDetails.self, from: data)
}
```

Fetching the seats of a showtime (same approach, using the existing `Seat` model):

```swift
func fetchSeats(showtimeId: Int) async throws -> [Seat] {
    let url = URL(string: "http://localhost:8080/api/cinema/showtimes/\(showtimeId)/seats")!
    let (data, _) = try await URLSession.shared.data(from: url)
    return try JSONDecoder().decode([Seat].self, from: data)
}
```

**Practical notes for the client**

- `startTime` is a plain string in the Swift model, formatted like `2026-11-13T15:00:00`. It carries no timezone, so parse it with a `DateFormatter` set to the cinema's local time rather than with `ISO8601DateFormatter` defaults.
- On the iOS Simulator, `localhost` points to the Mac running the API. On a physical device, replace it with the Mac's local network IP address.
- App Transport Security restricts plain HTTP traffic, and App Clip invocation URLs are HTTPS URLs. Beyond local testing, the API will need to be deployed behind HTTPS.

---

## Known Limitations

This is a PoC, and the following gaps are deliberate or accepted trade-offs:

- No authentication or authorization.
- No input validation on `POST /movies`.
- No endpoint to list movies, to update or delete movies, or to create showtimes and seats. Those records must be inserted via SQL.
- No seat reservation endpoint. `isAvailable` is read-only through the API.
- Unknown showtime ids return an empty seat list instead of a `404`.
- No pagination, caching or rate limiting.
- No CORS configuration.
- Database credentials are configured in `application.properties`. Use environment variables or a secrets manager for anything beyond local development.

---

## Project Structure

```
Movies-Api
|-- pom.xml
|-- mvnw / mvnw.cmd
`-- src
    |-- main
    |   |-- java/com/movies/api
    |   |   |-- ApiApplication.java          Application entry point
    |   |   |-- controller
    |   |   |   `-- CinemaController.java    REST endpoints under /api/cinema
    |   |   |-- service
    |   |   |   `-- CinemaService.java       Business logic and entity-to-DTO mapping
    |   |   |-- repository
    |   |   |   |-- MovieRepository.java
    |   |   |   |-- ShowtimeRepository.java        findByMovieId
    |   |   |   `-- ShowtimeSeatRepository.java    findByShowtimeId
    |   |   |-- entity
    |   |   |   |-- Movie.java
    |   |   |   |-- Showtime.java
    |   |   |   `-- ShowtimeSeat.java
    |   |   |-- dto
    |   |   |   |-- CreateMovieDTO.java
    |   |   |   |-- MovieDTO.java
    |   |   |   |-- MovieDetailsDTO.java
    |   |   |   |-- ShowtimeDTO.java
    |   |   |   `-- SeatDTO.java
    |   |   `-- database
    |   |       `-- script.sql               Schema and seed data
    |   `-- resources
    |       `-- application.properties
    `-- test/java/com/movies/api
        `-- ApiApplicationTests.java         Context-load test
```

---

## Developed for Apple Developer Academy Challenge 🍎

This API were developed by me,([@MrSampaio](https://github.com/MrSampaio)), as part of the Apple Developer Academy PoC together with the [Cinefc](https://github.com/MrSampaio/Cinefc) Swift project.
