## 🐳 Running with Docker

You can run the eCard Management System and its PostgreSQL database using Docker Compose for a streamlined, reproducible setup.

### Requirements
- **Docker** (latest recommended)
- **Docker Compose** (v2+)

### Environment Variables
Create a `.env` file in the project root or in `./eCard` with the following variables:
```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-db:5432/ecard_db
SPRING_DATASOURCE_USERNAME=ecard_user
SPRING_DATASOURCE_PASSWORD=securepassword
JWT_SECRET=your-512-bit-secret-key
SPRING_MAIL_USERNAME=your-email@domain.com
SPRING_MAIL_PASSWORD=your-email-password
```

> **Note:** The database host should be `postgres-db` (the service name in Docker Compose).

### Build & Run
From the project root, start the services:
```bash
docker compose up --build
```
This will build the Java Spring Boot app (using Java 21 and Maven 3.9+) and start both the API and PostgreSQL database containers.

### Exposed Ports
- **eCard API**: [http://localhost:8080](http://localhost:8080)
- **PostgreSQL**: `localhost:5432` (for local development)

### Service Details
- **java-ecard**: Runs the Spring Boot application (Java 21, built with Maven). Exposes port `8080`.
- **postgres-db**: PostgreSQL 15 database. Exposes port `5432` and persists data in a Docker volume (`pgdata`).

### Special Notes
- The application runs as a non-root user inside the container for improved security.
- Health checks are enabled for the PostgreSQL service.
- If you need to customize database credentials or mail settings, update the `.env` file accordingly.

---
