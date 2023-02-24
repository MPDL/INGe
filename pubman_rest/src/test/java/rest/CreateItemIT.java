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
        CustomComparator ignoreTimestampAttribute =
                new CustomComparator(JSONCompareMode.LENIENT, new Customization("timestamp", (o1, o2) -> true),
                        //TODO: Rework returned messages & exception in productive code
                        new Customization("exception", (o1, o2) -> true), new Customization("message", (o1, o2) -> true),
                        new Customization("cause.exception", (o1, o2) -> true), new Customization("cause.message", (o1, o2) -> true));
        JSONAssert.assertEquals(expectedResponseBody, responseBody, ignoreTimestampAttribute);
    }

}
