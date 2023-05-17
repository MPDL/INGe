package rest;

import com.fasterxml.jackson.databind.ObjectMapper;
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

public class GetItemIT {

    private static RequestSpecification requestSpecification;
    private static final String BASE_PATH = "/items";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void initSpec() {
        requestSpecification = TestBase.initRequestSpecification(BASE_PATH);
    }

    @Test
    void testGetItem() throws IOException, JSONException {
        //Given
        String token = TestDataManager.login();
        String requestBody = TestDataManager.createItem();
        String itemId = this.objectMapper.readTree(requestBody).get("objectId").asText();

        //When
        Response response = given().spec(requestSpecification).header("Authorization", token).when().get(itemId).then().statusCode(200)
                .contentType(ContentType.JSON).extract().response();

        //Then
        String responseBody = response.getBody().asString();
        String expectedResponseBody = Files.readString(Paths.get("src/test/resources/createdItemResponse.json"), StandardCharsets.UTF_8);
        String[] ignoreFields = {"creationDate", "lastModificationDate", "latestVersion.modificationDate", "latestVersion.objectId", "modificationDate", "objectId"};
        AssertJsonWrapper.assertEquals(expectedResponseBody, responseBody, ignoreFields);

        //TODO: Extract or Add finally:
        TestDataManager.deleteItem(responseBody);
    }

    @Test
    void testGetItemNoAuthorizationToken() throws IOException, JSONException {
        //Given
        String requestBody = TestDataManager.createItem();
        String itemId = this.objectMapper.readTree(requestBody).get("objectId").asText();

        //When
        Response response =
                given().spec(requestSpecification).when().get(itemId).then().statusCode(404).contentType(ContentType.JSON).extract().response();

        //Then
        String responseBody = response.getBody().asString();
        String expectedResponseBody = Files.readString(Paths.get("src/test/resources/noAuthorizationTokenResponse.json"), StandardCharsets.UTF_8);
        String[] ignoreFields = {"timestamp", "exception"};
        //TODO: Rework returned messages & exception in productive code
        AssertJsonWrapper.assertEquals(expectedResponseBody, responseBody, ignoreFields);


        //TODO: Extract or Add finally:
        TestDataManager.deleteItem(requestBody);
    }

}
