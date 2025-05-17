# Workshop Service API

A Spring Boot REST API for managing workshops and registrations.

## Features

- Manage workshops (CRUD)
- Register users for workshops
- JWT-based authentication (OAuth2 Resource Server)
- OpenAPI (Swagger) documentation

## Tech Stack / Prerequisites

- Java 17+
- Spring Boot
- Spring Security (JWT)
- JPA/Hibernate
- PostgreSQL
- Maven
- Docker

## Getting Started

### Installation
1. Clone the repository:
   git clone <repository-url>
   
2. Navigate to the project directory:
   cd workshop-service
   
3. Install dependencies:
   mvn install
   

### Running the Application
To run the application, use the following command: <br>
- mvn clean install
- mvn spring-boot:run

(or)

- docker-compose up --build -d
- docker-compose down # to stop the containers

### API Documentation

- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- OpenAPI YAML: See `postman.yaml` or `/v3/api-docs`

### Example Endpoints

- `GET /api/v1/workshops` - List all workshops
- `POST /api/v1/workshops` - Create a new workshop
- `GET /api/v1/registrations` - List all registrations

### Authentication

All endpoints (except whitelisted) require a Bearer JWT token (Generated from keycloak users).

### Testing
Postman collection is provided in the `.postman` directory. You can import it into Postman to test the API endpoints.
- Postman File name : `workshop-service.postman_collection.json`
- SQL Scripts : Initial sql scripts are provided in the `init.sql` file.

### Database Configuration
The application uses PostgreSQL as the database. You can configure the database connection in `application.properties`.

## License
This project is licensed under the MIT License. See the LICENSE file for more details.

