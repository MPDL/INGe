package rest;

import com.fasterxml.jackson.databind.ObjectMapper;
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

class DeleteItemIT {

    private static RequestSpecification requestSpecification;
    private static final String BASE_PATH = "/items";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void initSpec() {
        requestSpecification = TestBase.initRequestSpecification(BASE_PATH);
    }

    @Test
    void testDeleteItem() throws IOException, JSONException {
        //Given
        String token = TestDataManager.login();
        //TODO: Why is the body required for delete in productive code
        String requestBody = Files.readString(Paths.get("src/test/resources/deleteItemRequest.json"), StandardCharsets.UTF_8);
        String createdItemBody = TestDataManager.createItem();
        String itemId = this.objectMapper.readTree(createdItemBody).get("objectId").asText();

        //When
        given().spec(requestSpecification).contentType(ContentType.JSON).header("Authorization", token).body(requestBody).when().delete(itemId)
                .then().statusCode(200);

        //Then
        Response getItemResponse = given().spec(requestSpecification).header("Authorization", token).when().get(itemId).then().statusCode(404).contentType(ContentType.JSON).extract().response();
        String getItemResponseBody = getItemResponse.getBody().asString();
        String expectedResponseBody = Files.readString(Paths.get("src/test/resources/notFoundResponse.json"), StandardCharsets.UTF_8);
        String[] ignoreFields = {"timestamp", "exception"};
        AssertJsonWrapper.assertEquals(expectedResponseBody, getItemResponseBody, ignoreFields);
    }

    @ParameterizedTest
    @ValueSource(strings = {"JournalArticleTemplate.json", "ConferencePaperTemplate.json", "BookChapterTemplate.json", "TalkTemplate.json", "ThesisTemplate.json", "PosterTemplate.json"})
    void testDeleteItem(String input) throws IOException, JSONException {
        //Given
        String token = TestDataManager.login();
        String requestBody = Files.readString(Paths.get("src/test/resources/deleteItemRequest.json"), StandardCharsets.UTF_8);
        String baseBody = Files.readString(Paths.get("src/test/resources/templates/" + input), StandardCharsets.UTF_8);
        String createdResponseBody = TestDataManager.createItem(baseBody);
        String itemId = this.objectMapper.readTree(createdResponseBody).get("objectId").asText();

        //When
        given().spec(requestSpecification).contentType(ContentType.JSON).header("Authorization", token).body(requestBody).when().delete(itemId)
                .then().statusCode(200);

        //Then
        Response getItemResponse = given().spec(requestSpecification).header("Authorization", token).when().get(itemId).then().statusCode(404).contentType(ContentType.JSON).extract().response();
        String getItemResponseBody = getItemResponse.getBody().asString();
        String expectedResponseBody = Files.readString(Paths.get("src/test/resources/notFoundResponse.json"), StandardCharsets.UTF_8);
        String[] ignoreFields = {"timestamp", "exception"};
        AssertJsonWrapper.assertEquals(expectedResponseBody, getItemResponseBody, ignoreFields);
    }

}
