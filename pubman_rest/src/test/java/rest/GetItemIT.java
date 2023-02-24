package rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import util.TestBase;
import util.TestDataManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;

public class GetItemIT {

    private static RequestSpecification requestSpecification;
    private static final String BASE_PATH = "/items";

    @BeforeAll
    public static void initSpec() {
        requestSpecification = TestBase.initRequestSpecification(BASE_PATH);
    }

    @Test
    public void testGetItem() throws IOException, JSONException {
        //Given
        String token = TestDataManager.login();

        String requestBody = TestDataManager.createItem();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(requestBody);
        String itemId = jsonNode.get("objectId").asText();

        //When
        Response response = given().spec(requestSpecification).header("Authorization", token).when().get(itemId).then().statusCode(200)
                .contentType(ContentType.JSON).extract().response();

        //Then
        String responseBody = response.getBody().asString();
        String expectedResponse = Files.readString(Paths.get("src/test/resources/createdItemResponse.json"), StandardCharsets.UTF_8);
        CustomComparator ignoreAttributes = new CustomComparator(JSONCompareMode.LENIENT, new Customization("creationDate", (o1, o2) -> true),
                new Customization("lastModificationDate", (o1, o2) -> true), new Customization("latestVersion.modificationDate", (o1, o2) -> true),
                new Customization("latestVersion.objectId", (o1, o2) -> true), new Customization("modificationDate", (o1, o2) -> true),
                new Customization("objectId", (o1, o2) -> true));
        JSONAssert.assertEquals(expectedResponse, responseBody, ignoreAttributes);

        //TODO: Extract or Add finally:
        TestDataManager.deleteItem(responseBody);
    }

    @Test
    public void testGetItemNoAuthorizationToken() throws IOException, JSONException {
        //Given
        String requestBody = TestDataManager.createItem();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(requestBody);
        String itemId = jsonNode.get("objectId").asText();

        //When
        Response response =
                given().spec(requestSpecification).when().get(itemId).then().statusCode(404).contentType(ContentType.JSON).extract().response();

        //Then
        String responseBody = response.getBody().asString();
        String expectedResponse = Files.readString(Paths.get("src/test/resources/noAuthorizationTokenResponse.json"), StandardCharsets.UTF_8);
        CustomComparator ignoreTimestampAttribute =
                new CustomComparator(JSONCompareMode.LENIENT, new Customization("timestamp", (o1, o2) -> true),
                        //TODO: Rework returned messages & exception in productive code
                        new Customization("exception", (o1, o2) -> true));
        JSONAssert.assertEquals(expectedResponse, responseBody, ignoreTimestampAttribute);

        //TODO: Extract or Add finally:
        TestDataManager.deleteItem(requestBody);
    }

}
