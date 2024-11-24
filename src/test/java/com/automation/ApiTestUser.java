package com.automation;

import com.automation.config.Config;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ApiTestUser {
    private int userId;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = Config.BASE_URL;
    }

    @Test(priority = 1)
    public void testPostUser() {
        String requestBody = "{\n" +
                "  \"name\": \"John Doe\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"email\": \"john.doe" + System.currentTimeMillis() + "@example.com\",\n" +
                "  \"status\": \"active\"\n" +
                "}";

        Response response = given()
                .header("Authorization", "Bearer " + Config.AUTH_TOKEN)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .body("name", equalTo("John Doe"))
                .body("gender", equalTo("male"))
                .body("status", equalTo("active"))
                .extract().response();

        userId = response.path("id");
        System.out.println("Created User ID: " + userId);
    }

    @Test(priority = 2)
    public void testGetUser() {
        given()
                .header("Authorization", "Bearer " + Config.AUTH_TOKEN)
                .when()
                .get("/users/" + userId)
                .then()
                .statusCode(200)
                .body("id", equalTo(userId))
                .body("name", equalTo("John Doe"))
                .body("gender", equalTo("male"))
                .body("status", equalTo("active"));
    }

    @Test(priority = 3)
    public void testUpdateUser() {
        String updateBody = "{\n" +
                "  \"name\": \"John Updated\",\n" +
                "  \"status\": \"inactive\"\n" +
                "}";

        given()
                .header("Authorization", "Bearer " + Config.AUTH_TOKEN)
                .contentType(ContentType.JSON)
                .body(updateBody)
                .when()
                .put("/users/" + userId)
                .then()
                .statusCode(200)
                .body("id", equalTo(userId))
                .body("name", equalTo("John Updated"))
                .body("status", equalTo("inactive"));
    }

    @Test(priority = 4)
    public void testDeleteUser() {
        given()
                .header("Authorization", "Bearer " + Config.AUTH_TOKEN)
                .when()
                .delete("/users/" + userId)
                .then()
                .statusCode(204);
        given()
                .header("Authorization", "Bearer " + Config.AUTH_TOKEN)
                .when()
                .get("/users/" + userId)
                .then()
                .statusCode(404);
    }
}