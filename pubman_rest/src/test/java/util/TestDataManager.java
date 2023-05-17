package util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;

public abstract class TestDataManager {

    //TODO: Test-User & Context must exist to run the Tests - Creation via API!?

    //TODO: Refactor: Do not use REST Assured to create Test-Data!?

    public static String login() {
        RequestSpecification requestSpecification = TestBase.initRequestSpecification("/login");
        String credentials = TestBase.USERNAME + ":" + TestBase.PASSWORD;

        Response response = given().spec(requestSpecification).body(credentials).when().post().then().statusCode(200).extract().response();

        String headerToken = response.getHeader("token");
        return headerToken;
    }

    public static String createItem() throws IOException, JSONException {
        String requestBody = Files.readString(Paths.get("src/test/resources/itemRequest.json"), StandardCharsets.UTF_8);

        return createItem(requestBody);
    }

    public static String createItem(String requestBody) throws IOException, JSONException {
        RequestSpecification requestSpecification = TestBase.initRequestSpecification("/items");
        String token = TestDataManager.login();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(requestBody);
        //((ObjectNode) jsonNode.path("metadata")).put("title", "REST Assured Test Title 1");
        requestBody = jsonNode.toString();

        Response response = given().spec(requestSpecification).contentType(ContentType.JSON).header("Authorization", token).body(requestBody)
                .when().post().then().statusCode(201).contentType(ContentType.JSON).extract().response();

        String body = response.getBody().asString();
        return body;
    }

    public static void deleteItem(String responseBody) throws IOException {
        RequestSpecification requestSpecification = TestBase.initRequestSpecification("/items");
        String token = TestDataManager.login();

        String requestBody = Files.readString(Paths.get("src/test/resources/deleteItemRequest.json"), StandardCharsets.UTF_8);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String itemId = jsonNode.get("objectId").asText();

        //When
        given().spec(requestSpecification).contentType(ContentType.JSON).header("Authorization", token).body(requestBody).when().delete(itemId)
                .then().statusCode(200);
    }
}
