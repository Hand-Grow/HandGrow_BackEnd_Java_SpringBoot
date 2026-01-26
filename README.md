# HandGrow Backend

This repository contains the backend service for the HandGrow application. It is a Spring Boot application built with Java and Gradle, providing a robust and secure API.

## Features

*   **User Management:** API endpoints for managing user accounts (e.g., creation, retrieval).
*   **RESTful API:** A well-structured REST API for various functionalities.
*   **JWT Authentication & Authorization:** Secure access to API endpoints using JSON Web Tokens.
*   **Redis Integration:** Utilizes Redis for caching, session management, or other data storage needs.
*   **OpenAPI Documentation:** Automatically generated API documentation for easy understanding and integration.

## Technologies Used

*   **Java 17+**
*   **Spring Boot**
*   **Gradle**
*   **Spring Security** (with JWT)
*   **Redis**
*   **Lombok** (Assumed based on common Spring Boot practices)
*   **PostgreSQL/MySQL/H2** (Assumed, please specify actual database)

## Getting Started

### Prerequisites

*   Java Development Kit (JDK) 17 or higher
*   Gradle (usually bundled with Spring Boot projects)
*   A running Redis instance
*   A running database instance (e.g., PostgreSQL, MySQL), with necessary credentials

### Build the Project

Clone the repository and build the project using Gradle:

```bash
git clone https://github.com/your-username/handgrow-backend.git
cd handgrow-backend
./gradlew clean build
```

### Configuration

The application can be configured via `src/main/resources/application.yaml`. Key configurations include:

*   **Server Port:**
    ```yaml
    server:
      port: 8080
    ```
*   **Database Connection:**
    ```yaml
    spring:
      datasource:
        url: jdbc:postgresql://localhost:5432/handgrow_db
        username: your_db_user
        password: your_db_password
        driver-class-name: org.postgresql.Driver
    ```
*   **Redis Connection:**
    ```yaml
    spring:
      redis:
        host: localhost
        port: 6379
        password: your_redis_password # if applicable
    ```
*   **JWT Secret:**
    ```yaml
    jwt:
      secret: yourSuperSecretKeyWhichShouldBeAtLeast256BitsLong
      expiration: 86400000 # 24 hours in milliseconds
    ```

### Run the Application

You can run the application using the Spring Boot Gradle plugin:

```bash
./gradlew bootRun
```

Alternatively, you can run the generated JAR file:

```bash
java -jar build/libs/handgrow-backend-0.0.1-SNAPSHOT.jar # Adjust version as needed
```

The API will be available at `http://localhost:8080` (or your configured port).

## API Documentation

Access the API documentation (Swagger UI) at:

`http://localhost:8080/swagger-ui.html`

## Contributing

Please read `CONTRIBUTING.md` (if available) for details on our code of conduct, and the process for submitting pull requests to us.

## License

This project is licensed under the MIT License - see the `LICENSE` file for details.
