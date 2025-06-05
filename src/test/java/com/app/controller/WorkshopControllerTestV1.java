package com.app.controller;

import com.app.config.TestConfig;
import com.app.config.TestUtil;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class WorkshopControllerTestV1 {

    @BeforeClass
    public void setup() {
        // Configure REST Assured
        RestAssured.baseURI = TestConfig.getRestAssuredBaseUri();
        RestAssured.port = TestConfig.getRestAssuredPort();
        RestAssured.basePath = TestConfig.getRestAssuredBasePath();

        if (TestConfig.shouldLogAllRequests()) {
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        }
    }

    @Test
    void givenNoAuth_whenGetUpcomingWorkshops_thenReturn200() {
        given()
                .when()
                .get(TestConfig.getUpcomingWorkshopsEndpoint())
                .then()
                .statusCode(200);
    }

    @Test
    void givenAdminAuth_whenGetAllWorkshops_thenReturn200() {
        given()
                .auth().oauth2(TestUtil.getAdminAuthToken())
                .when()
                .get(TestConfig.getWorkshopsEndpoint())
                .then()
                .statusCode(200);
    }

    @Test
    void givenUserAuth_whenGetAllWorkshops_thenReturn403() {
        given()
                .auth().oauth2(TestUtil.getUserAuthToken())
                .when()
                .get(TestConfig.getWorkshopsEndpoint())
                .then()
                .statusCode(403);
    }

    @Test
    void givenNoAuth_whenGetAllWorkshops_thenReturn401() {
        given()
                .when()
                .get(TestConfig.getWorkshopsEndpoint())
                .then()
                .statusCode(401);
    }

    @Test
    void givenAdminAuth_whenGetWorkshopByCode_thenReturn200() {
        String workshopCode = "WS_100";
        Response response = given()
                .auth().oauth2(TestUtil.getAdminAuthToken())
                .when()
                .get(TestConfig.getWorkshopByCodeEndpoint(workshopCode));

        response.then().statusCode(200);
        String responseCode = response.jsonPath().getString("code");
        Assert.assertEquals(responseCode, workshopCode);
    }

    @Test
    void givenInvalidWorkshopCode_whenGetWorkshop_thenReturn400() {
        String invalidWorkshopCode = "INVALID_CODE";
        given()
                .auth().oauth2(TestUtil.getAdminAuthToken())
                .when()
                .get(TestConfig.getWorkshopByCodeEndpoint(invalidWorkshopCode))
                .then()
                .statusCode(400);
    }

    @Test
    void givenValidWorkshopRequest_whenAdminAuthenticated_thenReturn201() {
        String requestBody = """
            {
                "code": "WS_500",
                "name": "Advanced Testing Workshop",
                "description": "Learn advanced testing techniques",
                "startDate": "2024-12-01T09:00:00",
                "endDate": "2024-12-03T17:00:00",
                "capacity": 30,
                "price": 299.99,
                "location": "Virtual"
            }
            """;

        given()
                .auth().oauth2(TestUtil.getAdminAuthToken())
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post(TestConfig.getWorkshopsEndpoint())
                .then()
                .statusCode(201);
    }

    @Test
    void givenInvalidWorkshopRequest_whenAdminAuthenticated_thenReturn400() {
        // Missing required fields
        String invalidRequestBody = "{\"name\": \"Incomplete Workshop\"}";

        given()
                .auth().oauth2(TestUtil.getAdminAuthToken())
                .contentType("application/json")
                .body(invalidRequestBody)
                .when()
                .post(TestConfig.getWorkshopsEndpoint())
                .then()
                .statusCode(400);
    }

    @Test
    void givenDuplicateWorkshopCode_whenCreateWorkshop_thenReturn400() {
        String duplicateWorkshopRequest = """
            {
                "code": "WS_100",
                "name": "Duplicate Workshop",
                "description": "This should fail",
                "startDate": "2024-12-01T09:00:00",
                "endDate": "2024-12-03T17:00:00",
                "capacity": 30,
                "price": 199.99,
                "location": "Virtual"
            }
            """;

        given()
                .auth().oauth2(TestUtil.getAdminAuthToken())
                .contentType("application/json")
                .body(duplicateWorkshopRequest)
                .when()
                .post(TestConfig.getWorkshopsEndpoint())
                .then()
                .statusCode(400);
    }

    @Test
    void givenDeleteWorkshopRequest_whenAdminAuthenticated_thenReturn200() {
        String workshopCode = "WS_500";
        Response response = given()
                .auth().oauth2(TestUtil.getAdminAuthToken())
                .when()
                .delete(TestConfig.getWorkshopByCodeEndpoint(workshopCode));

        String responseBody = response.getBody().asString();
        System.out.println("Delete Workshop API Response: " + responseBody);

        response.then().statusCode(200);
        Assert.assertEquals(responseBody, "Workshop deleted successfully :" + workshopCode);
    }

    @Test
    void givenUpdateWorkshopRequest_whenAdminAuthenticated_thenReturn200() {
        String workshopCode = "WS_100";
        String updateRequestBody = """
            {
                "name": "Updated Workshop Name",
                "description": "Updated description",
                "capacity": 25
            }
            """;

        Response response = given()
                .auth().oauth2(TestUtil.getAdminAuthToken())
                .contentType("application/json")
                .body(updateRequestBody)
                .when()
                .put(TestConfig.getWorkshopByCodeEndpoint(workshopCode));

        response.then().statusCode(200);
        String updatedName = response.jsonPath().getString("name");
        Assert.assertEquals(updatedName, "Updated Workshop Name");
    }
}
