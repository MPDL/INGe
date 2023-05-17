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
import util.TestBase;
import util.TestDataManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;

public class DeleteItemIT {

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
        String requestBody = Files.readString(Paths.get("src/test/resources/deleteItemRequest.json"), StandardCharsets.UTF_8);
        String createdItemBody = TestDataManager.createItem();
        String itemId = this.objectMapper.readTree(createdItemBody).get("objectId").asText();

        //When
        given().spec(requestSpecification).contentType(ContentType.JSON).header("Authorization", token).body(requestBody).when().delete(itemId)
                .then().statusCode(200);

        //Then
        Response response =
                given().spec(requestSpecification).when().get(itemId).then().statusCode(404).contentType(ContentType.JSON).extract().response();
    }

    @ParameterizedTest
    @ValueSource(strings = {"JournalArticleTemplate.json", "ConferencePaperTemplate.json", "BookChapterTemplate.json", "TalkTemplate.json", "ThesisTemplate.json", "PosterTemplate.json"})
    void testDeleteItem(String input) throws IOException, JSONException {
        //Given
        String token = TestDataManager.login();
        String requestBody = Files.readString(Paths.get("src/test/resources/templates/" + input), StandardCharsets.UTF_8);
        String createdItemBody = TestDataManager.createItem(requestBody);
        String itemId = this.objectMapper.readTree(createdItemBody).get("objectId").asText();

        //When
        given().spec(requestSpecification).contentType(ContentType.JSON).header("Authorization", token).body(requestBody).when().delete(itemId)
                .then().statusCode(200);

        //Then
        Response response =
                given().spec(requestSpecification).when().get(itemId).then().statusCode(404).contentType(ContentType.JSON).extract().response();
    }

}
