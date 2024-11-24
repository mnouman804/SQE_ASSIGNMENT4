package com.automation;

import com.automation.config.Config;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class PostApiTest {
    private int userId;
    private int postId;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = Config.BASE_URL;
        // Create a user first since we need a valid user to create posts
        createTestUser();
    }

    private void createTestUser() {
        String userBody = "{\n" +
                "  \"name\": \"Test User\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"email\": \"testuser" + System.currentTimeMillis() + "@example.com\",\n" +
                "  \"status\": \"active\"\n" +
                "}";

        Response response = given()
                .header("Authorization", "Bearer " + Config.AUTH_TOKEN)
                .contentType(ContentType.JSON)
                .body(userBody)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .extract().response();

        userId = response.path("id");
    }

    @Test(priority = 1)
    public void testCreatePost() {
        String postBody = "{\n" +
                "  \"title\": \"Test Post\",\n" +
                "  \"body\": \"This is a test post content.\",\n" +
                "  \"user_id\": " + userId + "\n" +
                "}";

        Response response = given()
                .header("Authorization", "Bearer " + Config.AUTH_TOKEN)
                .contentType(ContentType.JSON)
                .body(postBody)
                .when()
                .post("/users/" + userId + "/posts")
                .then()
                .statusCode(201)
                .body("title", equalTo("Test Post"))
                .body("body", equalTo("This is a test post content."))
                .body("user_id", equalTo(userId))
                .extract().response();

        postId = response.path("id");
        System.out.println("Created Post ID: " + postId);
    }

    @Test(priority = 2)
    public void testGetPost() {
        given()
                .header("Authorization", "Bearer " + Config.AUTH_TOKEN)
                .when()
                .get("/posts/" + postId)
                .then()
                .statusCode(200)
                .body("id", equalTo(postId))
                .body("title", equalTo("Test Post"))
                .body("body", equalTo("This is a test post content."))
                .body("user_id", equalTo(userId));
    }

    @Test(priority = 3)
    public void testUpdatePost() {
        String updateBody = "{\n" +
                "  \"title\": \"Updated Post\",\n" +
                "  \"body\": \"This post has been updated.\"\n" +
                "}";

        given()
                .header("Authorization", "Bearer " + Config.AUTH_TOKEN)
                .contentType(ContentType.JSON)
                .body(updateBody)
                .when()
                .put("/posts/" + postId)
                .then()
                .statusCode(200)
                .body("id", equalTo(postId))
                .body("title", equalTo("Updated Post"))
                .body("body", equalTo("This post has been updated."))
                .body("user_id", equalTo(userId));
    }

    @Test(priority = 4)
    public void testDeletePost() {
        given()
                .header("Authorization", "Bearer " + Config.AUTH_TOKEN)
                .when()
                .delete("/posts/" + postId)
                .then()
                .statusCode(204);

        // Verify post is deleted
        given()
                .header("Authorization", "Bearer " + Config.AUTH_TOKEN)
                .when()
                .get("/posts/" + postId)
                .then()
                .statusCode(404);
    }
}