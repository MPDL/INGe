package rest;

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
import static org.assertj.core.api.Assertions.assertThat;

class LoginIT {

    private static RequestSpecification requestSpecification;
    private static final String BASE_PATH = "/login";

    @BeforeAll
    static void initSpec() {
        requestSpecification = TestBase.initRequestSpecification(BASE_PATH);
    }

    @Test
    void testLogin() throws JSONException {
        //Given
        String credentials = TestBase.USERNAME + ":" + TestBase.PASSWORD;

        //When
        Response response = given().spec(requestSpecification).body(credentials).when().post().then().statusCode(200).extract().response();

        //Then
        String headerToken = response.getHeader("token");
        assertThat(headerToken).isNotEmpty();

        String body = response.getBody().asString();
        assertThat(body).isEmpty();
    }

    @Test
    void testLoginNoBody() throws IOException, JSONException {
        //Given

        //When
        Response response = given().spec(requestSpecification).when().post().then().statusCode(400).extract().response();

        //Then
        boolean headerToken = response.getHeaders().hasHeaderWithName("token");
        assertThat(headerToken).isFalse();

        String responseBody = response.getBody().asString();
        String expectedResponseBody = Files.readString(Paths.get("src/test/resources/loginNoBodyResponse.json"), StandardCharsets.UTF_8);
        String[] ignoreFields = {"timestamp", "message", "exception"};
        //TODO: Rework returned messages & exception in productive code
        AssertJsonWrapper.assertEquals(expectedResponseBody, responseBody, ignoreFields);
    }

    @Test
    void testLoginWrongCredentials() throws IOException, JSONException {
        //Given
        String credentials = TestBase.USERNAME + ":" + "WrongPassword";

        //When
        Response response = given().spec(requestSpecification).body(credentials).when().post().then().statusCode(401).extract().response();

        //Then
        boolean headerToken = response.getHeaders().hasHeaderWithName("token");
        assertThat(headerToken).isFalse();

        String responseBody = response.getBody().asString();
        String expectedResponseBody =
                Files.readString(Paths.get("src/test/resources/loginWrongCredentialsResponse.json"), StandardCharsets.UTF_8);
        String[] ignoreFields = {"timestamp", "exception"};
        //TODO: Rework returned messages & exception in productive code
        AssertJsonWrapper.assertEquals(expectedResponseBody, responseBody, ignoreFields);
    }

}
