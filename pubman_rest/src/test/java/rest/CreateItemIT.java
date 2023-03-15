package rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import util.AssertJsonWrapper;
import util.TestBase;
import util.TestDataManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;

public class CreateItemIT {

    private static RequestSpecification requestSpecification;
    private static final String BASE_PATH = "/items";

    @BeforeAll
    public static void initSpec() {
        requestSpecification = TestBase.initRequestSpecification(BASE_PATH);
    }

    @Test
    public void testCreateItem() throws IOException, JSONException {
        //Given
        String token = TestDataManager.login();

        String requestBody = Files.readString(Paths.get("src/test/resources/itemRequest.json"), StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(requestBody);
        ((ObjectNode) jsonNode.path("metadata")).put("title", "REST Assured Test Title 1");
        requestBody = jsonNode.toString();

        //When
        Response response = given().spec(requestSpecification).contentType(ContentType.JSON).header("Authorization", token).body(requestBody)
                .when().post().then().statusCode(201).contentType(ContentType.JSON).extract().response();

        //Then
        String responseBody = response.getBody().asString();
        String expectedResponseBody = Files.readString(Paths.get("src/test/resources/createdItemResponse.json"), StandardCharsets.UTF_8);
        String[] ignoreFields = {"creationDate", "lastModificationDate", "latestVersion.modificationDate", "latestVersion.objectId", "modificationDate", "objectId"};
        AssertJsonWrapper.assertEquals(expectedResponseBody, responseBody, ignoreFields);

        //TODO: Extract or Add finally:
        TestDataManager.deleteItem(responseBody);
    }

    @Test
    public void testCreateItemNoAuthorizationToken() throws IOException, JSONException {
        //Given
        String token = "";

        String requestBody = Files.readString(Paths.get("src/test/resources/itemRequest.json"), StandardCharsets.UTF_8);

        //When
        Response response = given().spec(requestSpecification).contentType(ContentType.JSON).header("Authorization", token).body(requestBody)
                .when().post().then().statusCode(401).contentType(ContentType.JSON).extract().response();

        //Then
        String responseBody = response.getBody().asString();
        String expectedResponseBody =
                Files.readString(Paths.get("src/test/resources/emptyAuthorizationTokenResponse.json"), StandardCharsets.UTF_8);
        String[] ignoreFields = {"timestamp", "exception", "message", "cause.exception", "cause.message"};
        //TODO: Rework returned messages & exception in productive code
        AssertJsonWrapper.assertEquals(expectedResponseBody, responseBody, ignoreFields);
    }

}
