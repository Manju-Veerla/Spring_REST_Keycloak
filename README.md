# Workshop Service API

A Spring Boot REST API for managing workshops and registrations.

## Features

- Manage workshops (CRUD)
- Register users for workshops
- JWT-based authentication (Keycloak Resource Server)
- OpenAPI (Swagger) documentation

## Tech Stack / Prerequisites

- Java 17+
- Spring Boot
- PostgreSQL
- Maven
- Keycloak as Auth server
- Docker

## Getting Started

### Installation
1. Clone the repository:
 ```bash
   git clone https://gitlab.com/manju.veerla/keycloak.git
   ```
2. Navigate to the project directory:  
   ```bash 
    cd workshop-service
   ```
   
3. Install dependencies:
   ```bash
   mvn install
   ```
   

### Running the Application
To run the application, use the following command:  
```bash
mvn clean install
mvn spring-boot:run
```

(or)

```bash
docker-compose up --build -d
docker-compose down (# to stop the containers)
```

### API Documentation

- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Keycloak Configuration (If not using docker-compose)
1. Start Keycloak server using Docker:
   ```bash
   docker run --name local_keycloak -p 8081:8080 -e KC_BOOTSTRAP_ADMIN_USERNAME=admin -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:latest start-dev
    ```
2. Access Keycloak admin console at [http://localhost:8081/admin/master/console/](http://localhost:8081/admin/master/console/) with username `admin` and password `admin`.
3. Import the Keycloak configuration JSON file provided in the `keycloak` directory into your Keycloak server.  
    - File Name: `workshop-realm.json`  
   (or)  
    **Manually create the realm and client:**
   - Realm: `workshop`
   - Client ID: `workshop-service`
   - Access Type: `confidential`
   - Create Roles : `admin`, `user`
   - Create Users : `admin`, `user`
### PostgreSQL Configuration
1. Create a PostgreSQL database named `workshop_local_db` with user 'admin' and password 'admin'.
2. GRANT ALL PRIVILEGES ON DATABASE workshop_db to admin;
3. ALTER DATABASE workshop_db OWNER TO admin;
4. SQL Scripts : Initial sql scripts are provided in the `init.sql` file.

### Endpoints
 PUBLIC endpoint: (No authentication required)
- `GET /api/v1/workshops/upcoming` - List upcoming workshops

 ADMIN endpoints (Requires authentication as admin):
- `GET /api/v1/workshops` - List all workshops
- `GET /api/v1/workshops/{id}` - Get workshop by ID
- `POST /api/v1/workshops` - Create a new workshop
- `PUT /api/v1/workshops/{id}` - Update a workshop by ID
- `DELETE /api/v1/workshops/{id}` - Delete a workshop by ID
- `GET /api/v1/registrations` - List all registrations
- `GET /api/v1/registrations/{id}` - Get registration by ID  
- `DELETE /api/v1/registrations/{id}` - Delete registration by ID  

USER endpoints (Requires authentication as user):
- `POST /api/v1/registrations` - Register for a workshop
- `GET /api/v1//user/registrations` - List all registrations for the authenticated user
### Authentication

All endpoints (except whitelisted) require a Bearer JWT token (Generated from keycloak users).

### Testing
Postman collection is provided in the `.postman` directory. You can import it into Postman to test the API endpoints.
- Postman File name : `workshop-service.postman_collection.json`

### Postman screenshots
[Check the screenshots for Testing APIs](./.postman/Testing.docx).

## License
This project is licensed under the MIT License. See the LICENSE file for more details.

