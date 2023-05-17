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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void initSpec() {
        requestSpecification = TestBase.initRequestSpecification(BASE_PATH);
    }

    @Test
    void testCreateItem() throws IOException, JSONException {
        //Given
        String token = TestDataManager.login();
        String baseRequestBody = Files.readString(Paths.get("src/test/resources/itemRequest.json"), StandardCharsets.UTF_8);
        JsonNode requestNode = this.objectMapper.readTree(baseRequestBody);
        ((ObjectNode) requestNode.path("metadata")).put("title", "REST Assured Test Title 1");
        String requestBody = requestNode.toString();

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

    @ParameterizedTest
    @ValueSource(strings = {"JournalArticleTemplate.json", "ConferencePaperTemplate.json", "BookChapterTemplate.json", "TalkTemplate.json", "ThesisTemplate.json", "PosterTemplate.json"})
    void testCreateItem(String input) throws IOException, JSONException {
        //Given
        String token = TestDataManager.login();
        String baseRequestBody = Files.readString(Paths.get("src/test/resources/templates/" + input), StandardCharsets.UTF_8);
        JsonNode requestNode = this.objectMapper.readTree(baseRequestBody);
        //((ObjectNode) requestNode.path("metadata")).put("title", "REST Assured Test - " + input);
        //TODO: Add/Change: context + creator/modifier in the json file!?!
        String requestBody = requestNode.toString();

        //When
        Response response = given().spec(requestSpecification).contentType(ContentType.JSON).header("Authorization", token).body(requestBody)
                .when().post().then().statusCode(201).contentType(ContentType.JSON).extract().response();

        //Then
        String responseBody = response.getBody().asString();
        String[] ignoreFields = {"creationDate", "lastModificationDate", "latestVersion.modificationDate", "latestVersion.objectId", "modificationDate", "objectId"};
        AssertJsonWrapper.assertEquals(baseRequestBody, responseBody, ignoreFields);

        //TODO: Extract or Add finally:
        TestDataManager.deleteItem(responseBody);
    }

    @Test
    void testCreateItemNoAuthorizationToken() throws IOException, JSONException {
        //Given
        String token = "";
        String requestBody = Files.readString(Paths.get("src/test/resources/itemRequest.json"), StandardCharsets.UTF_8);

        //When
        Response response = given().spec(requestSpecification).contentType(ContentType.JSON).header("Authorization", token).body(requestBody)
                .when().post().then().statusCode(401).contentType(ContentType.JSON).extract().response();

        //Then
        String responseBody = response.getBody().asString();
        String expectedResponseBody = Files.readString(Paths.get("src/test/resources/emptyAuthorizationTokenResponse.json"), StandardCharsets.UTF_8);
        String[] ignoreFields = {"timestamp", "exception", "message", "cause.exception", "cause.message"};
        //TODO: Rework returned messages & exception in productive code
        AssertJsonWrapper.assertEquals(expectedResponseBody, responseBody, ignoreFields);
    }

}
