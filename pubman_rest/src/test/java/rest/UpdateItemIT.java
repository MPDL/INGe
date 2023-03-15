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

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class UpdateItemIT {

    private static RequestSpecification requestSpecification;
    private static final String BASE_PATH = "/items";

    @BeforeAll
    public static void initSpec() {
        requestSpecification = TestBase.initRequestSpecification(BASE_PATH);
    }

    @Test
    public void testUpdateItem() throws IOException, JSONException {
        //Given
        String token = TestDataManager.login();

        String requestBody = TestDataManager.createItem();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(requestBody);
        ((ObjectNode) jsonNode.path("metadata")).put("title", "REST Assured Test Title 1 - Updated");
        requestBody = jsonNode.toString();

        String itemId = jsonNode.get("objectId").asText();

        //When
        Response response = given().spec(requestSpecification).contentType(ContentType.JSON).header("Authorization", token).body(requestBody)
                .when().put(itemId).then().statusCode(200).contentType(ContentType.JSON).extract().response();

        //Then
        String responseBody = response.getBody().asString();
        String expectedResponseBody = requestBody;
        String[] ignoreFields = {"lastModificationDate", "latestVersion.modificationDate", "modificationDate"};
        AssertJsonWrapper.assertEquals(expectedResponseBody, responseBody, ignoreFields);

        JsonNode responseBodyNode = objectMapper.readTree(responseBody);
        assertThat(responseBodyNode.get("modificationDate").toString()).isNotEqualToIgnoringCase(jsonNode.get("modificationDate").toString());
        assertThat(responseBodyNode.get("lastModificationDate").toString())
                .isNotEqualToIgnoringCase(jsonNode.get("lastModificationDate").toString());
        assertThat(responseBodyNode.get("latestVersion").path("modificationDate").toString())
                .isNotEqualToIgnoringCase(jsonNode.get("latestVersion").path("modificationDate").toString());

        //TODO: Extract or Add finally:
        TestDataManager.deleteItem(responseBody);
    }

}
