package com.app.controller;

import com.app.config.TestConfig;
import com.app.config.TestUtil;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


import static io.restassured.RestAssured.given;

public class RegistrationControllerTestV1 {
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
    void givenAdminAuth_whenGetRegistrations_thenReturn200() {
        given()
                .auth().oauth2(TestUtil.getAdminAuthToken())
                .when()
                .get(TestConfig.getRegistrationsEndpoint())
                .then()
                .statusCode(200);
    }

    @Test
    void givenNoAuth_whenGetRegistrations_thenReturn401() {
        given()
                .auth().none()
                .when()
                .get(TestConfig.getRegistrationsEndpoint())
                .then()
                .statusCode(401);
    }

    @Test
    void givenInvalidAuth_whenGetRegistrations_thenReturn403() {
        given()
                .auth().oauth2("InvalidAUTH")
                .when()
                .get(TestConfig.getRegistrationsEndpoint())
                .then()
                .statusCode(403);
    }

    @Test
    void givenUserAuth_whenGetRegistrations_thenReturn401() {
        given()
                .auth().oauth2(TestUtil.getUserAuthToken())
                .when()
                .get(TestConfig.getRegistrationsEndpoint())
                .then()
                .statusCode(401);
    }


    @Test
    void givenAdminAuth_whenGetRegistrationByWorkshopCode_thenReturn200AndCorrectWorkshop() {
        // Example using the endpoints
        ValidatableResponse response = given()
                .auth().oauth2(TestUtil.getAdminAuthToken())
                .when()
                .get(TestConfig.getRegistrationByWorkshopEndpoint("WS_700"))
                .then();
        // Get the workshopCode as a List since it's an array in the response
        String workshopCode = response.extract().jsonPath().getString("workshopCode[0]");

        Assert.assertEquals(response.extract().statusCode(), 200);
        Assert.assertEquals(workshopCode, "WS_700");
    }

    @Test
    void givenInvalidWorkshopCode_whenGetRegistration_thenReturn400() {
        // Example using the endpoints
        ValidatableResponse response = given()
                .auth().oauth2(TestUtil.getAdminAuthToken())
                .when()
                .get(TestConfig.getRegistrationByWorkshopEndpoint("AAA0"))
                .then();
        System.out.println(response.extract().jsonPath().toString());
        // Get the workshopCode as a List since it's an array in the response
        String workshopCode = response.extract().jsonPath().getString("workshopCode[0]");

        Assert.assertEquals(response.extract().statusCode(), 400);
        Assert.assertNull(workshopCode);
    }

    @Test
    void givenValidRegistrationRequest_whenUserAuthenticated_thenReturn201() {
        given().auth().oauth2(TestUtil.getUserAuthToken())
                .contentType("application/json")
                .body(TestConfig.getRegistrationRequestBody("WS_100", "+49123456789", "EMAIL"))
                .when().post(TestConfig.getRegistrationsEndpoint())
                .then()
                .statusCode(201);
    }

    @Test
    void givenInValidRegistrationRequest_whenUserAuthenticated_thenReturn400() {
        given().auth().oauth2(TestUtil.getUserAuthToken())
                .contentType("application/json")
                .body(TestConfig.getRegistrationRequestBody("WS_700", "+49123456789", "EMAIL"))
                .when().post(TestConfig.getRegistrationsEndpoint())
                .then()
                .statusCode(400);
    }


    @Test
    void givenDuplicateRegistrationRequest_whenUserAuthenticated_thenReturn400() {
        ValidatableResponse response = given().auth().oauth2(TestUtil.getUserAuthToken())
                .contentType("application/json")
                .body(TestConfig.getRegistrationRequestBody("WS_100", "+49123456789", "EMAIL"))
                .when().post(TestConfig.getRegistrationsEndpoint())
                .then()
                .statusCode(400);
        String errorMessage = response.extract().jsonPath().getString("message");
        Assert.assertEquals(errorMessage, "User already registered");
    }

    @Test
    void givenDeleteRegistrationRequest_whenAdminAuthenticated_thenReturnOk() {
        int id = 6;
        Response response = given().auth().oauth2(TestUtil.getAdminAuthToken())
                .when().delete(TestConfig.getRegistrationByIdEndpoint(id));
        String responseBody = response.getBody().asString();
        System.out.println("Delete API Response: " + responseBody);

        response.then().statusCode(200);
        Assert.assertEquals(responseBody, "Registration deleted successfully :" + id);
    }

    @Test
    void givenGetUserRegistrationRequest_whenUserAuthenticated_thenReturn200() {
        given().auth().oauth2(TestUtil.getUserAuthToken())
                .contentType("application/json")
                .when().get(TestConfig.getUserRegistrationsEndpoint())
                .then()
                .statusCode(200);

    }
    }
