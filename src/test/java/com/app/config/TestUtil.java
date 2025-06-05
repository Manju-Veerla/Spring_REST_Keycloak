package com.app.config;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class TestUtil {
    public static String getAdminAuthToken() {
        Response response = given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("grant_type", TestConfig.getGrantType())
                .formParam("client_id", TestConfig.getClientId())
                .formParam("client_secret", TestConfig.getClientSecret())
                .formParam("username", TestConfig.getAuthAdminUsername())
                .formParam("password", TestConfig.getAuthAdminPassword())
                .when()
                .post(TestConfig.getTokenUrl())
                .then()
                .statusCode(200)
                .extract().response();

        return response.jsonPath().getString("access_token");
    }
    public static String getUserAuthToken() {
        Response response = given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("grant_type", TestConfig.getGrantType())
                .formParam("client_id", TestConfig.getClientId())
                .formParam("client_secret", TestConfig.getClientSecret())
                .formParam("username", TestConfig.getAuthUserUsername())
                .formParam("password", TestConfig.getAuthUserPassword())
                .when()
                .post(TestConfig.getTokenUrl())
                .then()
                .statusCode(200)
                .extract().response();

        return response.jsonPath().getString("access_token");
    }

}
