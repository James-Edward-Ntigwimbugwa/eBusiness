
```markdown
# eCard Management System

**Digitize Your Business Identity**  
A modern Spring Boot solution for managing digital business cards with secure authentication and seamless sharing capabilities.

## Features
- JWT-secured user authentication
- Full CRUD operations for digital business cards
- Email integration for card sharing
- PostgreSQL database support
- OpenAPI 3.0 documentation for easy API exploration
- RESTful API architecture
- Actuator endpoints for application monitoring
- Lombok to reduce boilerplate code

## Quick Start Guide

### Prerequisites
- **Java 21**  
- **PostgreSQL 15 or later**  
- **Maven 3.9 or later**  
- **SMTP Email Service Credentials**

### Installation Steps

1. **Clone the Repository**  
   Open a terminal and run:
   ```bash
   git clone https://github.com/Jamesnavigator2001/eBusiness.git
   cd eBusiness
   ```

2. **Set Up the Database**  
   Using your preferred SQL client or command line, execute:
   ```sql
   CREATE DATABASE ecard_db;
   CREATE USER ecard_user WITH PASSWORD 'securepassword';
   GRANT ALL PRIVILEGES ON DATABASE ecard_db TO ecard_user;
   ```

3. **Configure the Environment**  
   Create a file named `.env` in the project root with these contents:
   ```properties
   SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/ecard_db
   SPRING_DATASOURCE_USERNAME=ecard_user
   SPRING_DATASOURCE_PASSWORD=securepassword
   JWT_SECRET=your-512-bit-secret-key
   SPRING_MAIL_USERNAME=your-email@domain.com
   SPRING_MAIL_PASSWORD=your-email-password
   ```

4. **Build & Run the Application**  
   In the project root, execute:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

5. **Access API Documentation**  
   After the application starts, open your browser and visit:
   ```
   http://localhost:8080/swagger-ui.html
   ```

## Technology Stack

- **Backend:** Spring Boot 3.3.8
- **Database:** PostgreSQL
- **Security:** JWT Authentication
- **Documentation:** SpringDoc OpenAPI
- **Testing:** Spring Security Test
- **Tooling:** Lombok and Maven

## API Endpoints (Examples)
- **GET /api/cards** – List all digital cards  
- **POST /api/cards** – Create a new card  
- **GET /api/cards/{id}** – Retrieve card details  
- **PUT /api/cards/{id}** – Update card information  
- **DELETE /api/cards/{id}** – Delete a card  
- **POST /api/auth/login** – Authenticate user

## Development Setup

1. **Enable Lombok:** Ensure Lombok is enabled in your IDE.
2. **Configure PostgreSQL:** Update your database connection settings as needed.
3. **Set the Development Profile:**  
   ```bash
   export SPRING_PROFILES_ACTIVE=dev
   ```
4. **Run with the Development Profile:**  
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

## Contribution Guidelines

To contribute:
1. Fork the repository.
2. Create a new feature branch:
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. Commit your changes.
4. Push the branch and open a pull request.

## License

This project is distributed under the MIT License. See the `LICENSE` file for details.

---

**Pro Tip:** Use Postman for API testing and explore the Swagger UI to experiment with endpoints!

**Contact:**  
Email: [jamesedwards2001.tz@gmail.com](mailto:jamesedwards2001.tz@gmail.com)  
Tanzania Phone: +255716521848
```