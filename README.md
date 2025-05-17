# Workshop Service API

A Spring Boot REST API for managing workshops and registrations.

## Features

- Manage workshops (CRUD)
- Register users for workshops
- JWT-based authentication (OAuth2 Resource Server)
- OpenAPI (Swagger) documentation

## Tech Stack

- Java 17+
- Spring Boot
- Spring Security (JWT)
- JPA/Hibernate
- PostgreSQL
- Maven

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven
- PostgreSQL
- Docker

### Installation
1. Clone the repository:
   git clone <repository-url>
   
2. Navigate to the project directory:
   cd workshop-service
   
3. Install dependencies:
   mvn install
   

### Running the Application
To run the application, use the following command:
mvn clean install mvn spring-boot:run

### API Documentation

- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- OpenAPI YAML: See `postman.yaml` or `/v3/api-docs`

### Example Endpoints

- `GET /api/v1/workshops` - List all workshops
- `POST /api/v1/workshops` - Create a new workshop
- `GET /api/v1/registrations` - List all registrations


### Authentication

All endpoints (except whitelisted) require a Bearer JWT token.

## License
This project is licensed under the MIT License. See the LICENSE file for more details.
