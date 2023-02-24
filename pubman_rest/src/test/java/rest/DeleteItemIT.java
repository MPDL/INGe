package rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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

    @BeforeAll
    public static void initSpec() {
        requestSpecification = TestBase.initRequestSpecification(BASE_PATH);
    }

    @Test
    public void testDeleteItem() throws IOException, JSONException {
        //Given
        String token = TestDataManager.login();

        String requestBody = Files.readString(Paths.get("src/test/resources/deleteItemRequest.json"), StandardCharsets.UTF_8);

        String createdItemBody = TestDataManager.createItem();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(createdItemBody);
        String itemId = jsonNode.get("objectId").asText();

        //When
        given().spec(requestSpecification).contentType(ContentType.JSON).header("Authorization", token).body(requestBody).when().delete(itemId)
                .then().statusCode(200);

        //Then
        Response response =
                given().spec(requestSpecification).when().get(itemId).then().statusCode(404).contentType(ContentType.JSON).extract().response();

    }

}
