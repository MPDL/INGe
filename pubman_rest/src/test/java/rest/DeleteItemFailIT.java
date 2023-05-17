package rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
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

public class DeleteItemFailIT {

    private static RequestSpecification requestSpecification;
    private static final String BASE_PATH = "/items";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String requestBody;

    @BeforeAll
    static void initSpec() {
        requestSpecification = TestBase.initRequestSpecification(BASE_PATH);
    }

    @AfterEach
    void deleteItems() throws IOException {
        TestDataManager.deleteItem(requestBody);
    }

    @Test
    void testDeleteItemNoAuthorizationToken() throws IOException, JSONException {
        //Given
        requestBody = TestDataManager.createItem();
        String itemId = this.objectMapper.readTree(requestBody).get("objectId").asText();

        //When
        Response response = given().spec(requestSpecification).contentType(ContentType.JSON).body(requestBody).when().delete(itemId)
                .then().statusCode(401).contentType(ContentType.JSON).extract().response();

        //Then
        String responseBody = response.getBody().asString();
        String expectedResponseBody = Files.readString(Paths.get("src/test/resources/noAuthorizationTokenResponse2.json"), StandardCharsets.UTF_8);
        String[] ignoreFields = {"timestamp", "exception"};
        //TODO: Unify different error responses in productive code
        AssertJsonWrapper.assertEquals(expectedResponseBody, responseBody, ignoreFields);
    }

}
