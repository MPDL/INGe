package rest;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import util.AssertJsonWrapper;
import util.TestBase;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;

class CreateItemFailIT {

    private static RequestSpecification requestSpecification;
    private static final String BASE_PATH = "/items";

    @BeforeAll
    static void initSpec() {
        requestSpecification = TestBase.initRequestSpecification(BASE_PATH);
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
