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
import static org.assertj.core.api.Assertions.assertThat;

public class UpdateItemIT {

    private static RequestSpecification requestSpecification;
    private static final String BASE_PATH = "/items";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void initSpec() {
        requestSpecification = TestBase.initRequestSpecification(BASE_PATH);
    }

    @Test
    void testUpdateItem() throws IOException, JSONException {
        //Given
        String token = TestDataManager.login();
        String baseRequestBody = TestDataManager.createItem();
        JsonNode requestNode = this.objectMapper.readTree(baseRequestBody);
        ((ObjectNode) requestNode.path("metadata")).put("title", "REST Assured Test Title 1 - Updated");
        String requestBody = requestNode.toString();
        String itemId = requestNode.get("objectId").asText();

        //When
        Response response = given().spec(requestSpecification).contentType(ContentType.JSON).header("Authorization", token).body(requestBody)
                .when().put(itemId).then().statusCode(200).contentType(ContentType.JSON).extract().response();

        //Then
        String responseBody = response.getBody().asString();
        String[] ignoreFields = {"lastModificationDate", "latestVersion.modificationDate", "modificationDate"};
        AssertJsonWrapper.assertEquals(requestBody, responseBody, ignoreFields);

        JsonNode responseBodyNode = objectMapper.readTree(responseBody);
        assertThat(responseBodyNode.get("modificationDate").toString()).isNotEqualToIgnoringCase(requestNode.get("modificationDate").toString());
        assertThat(responseBodyNode.get("lastModificationDate").toString())
                .isNotEqualToIgnoringCase(requestNode.get("lastModificationDate").toString());
        assertThat(responseBodyNode.get("latestVersion").path("modificationDate").toString())
                .isNotEqualToIgnoringCase(requestNode.get("latestVersion").path("modificationDate").toString());

        //TODO: Extract or Add finally:
        TestDataManager.deleteItem(responseBody);
    }

    @ParameterizedTest
    @ValueSource(strings = {"JournalArticleTemplate.json", "ConferencePaperTemplate.json", "BookChapterTemplate.json", "TalkTemplate.json", "ThesisTemplate.json", "PosterTemplate.json"})
    void testUpdateItem(String input) throws IOException, JSONException {
        //Given
        String token = TestDataManager.login();
        String baseBody = Files.readString(Paths.get("src/test/resources/templates/" + input), StandardCharsets.UTF_8);
        String createdResponseBody = TestDataManager.createItem(baseBody);
        JsonNode requestNode = this.objectMapper.readTree(createdResponseBody);
        ((ObjectNode) requestNode.path("metadata")).put("title", "REST Assured Test Title 1 - " + input + " Updated");
        String requestBody = requestNode.toString();
        String itemId = requestNode.get("objectId").asText();

        //When
        Response response = given().spec(requestSpecification).contentType(ContentType.JSON).header("Authorization", token).body(requestBody)
                .when().put(itemId).then().statusCode(200).contentType(ContentType.JSON).extract().response();

        //Then
        String responseBody = response.getBody().asString();
        String[] ignoreFields = {"lastModificationDate", "latestVersion.modificationDate", "modificationDate"};
        AssertJsonWrapper.assertEquals(requestBody, responseBody, ignoreFields);

        JsonNode responseBodyNode = objectMapper.readTree(responseBody);
        assertThat(responseBodyNode.get("modificationDate").toString()).isNotEqualToIgnoringCase(requestNode.get("modificationDate").toString());
        assertThat(responseBodyNode.get("lastModificationDate").toString())
                .isNotEqualToIgnoringCase(requestNode.get("lastModificationDate").toString());
        assertThat(responseBodyNode.get("latestVersion").path("modificationDate").toString())
                .isNotEqualToIgnoringCase(requestNode.get("latestVersion").path("modificationDate").toString());

        //TODO: Extract or Add finally:
        TestDataManager.deleteItem(responseBody);
    }

}
