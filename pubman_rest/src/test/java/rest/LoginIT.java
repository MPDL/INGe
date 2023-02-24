package rest;

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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class LoginIT {

    private static RequestSpecification requestSpecification;
    private static final String BASE_PATH = "/login";

    @BeforeAll
    public static void initSpec() {
        requestSpecification = TestBase.initRequestSpecification(BASE_PATH);
    }

    @Test
    public void testLogin() throws JSONException {
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
    public void testLoginNoBody() throws IOException, JSONException {
        //Given

        //When
        Response response = given().spec(requestSpecification).when().post().then().statusCode(400).extract().response();

        //Then
        boolean headerToken = response.getHeaders().hasHeaderWithName("token");
        assertThat(headerToken).isFalse();

        String responseBody = response.getBody().asString();
        String expectedResponseBody = Files.readString(Paths.get("src/test/resources/loginNoBodyResponse.json"), StandardCharsets.UTF_8);
        CustomComparator ignoreTimestampAttribute =
                new CustomComparator(JSONCompareMode.LENIENT, new Customization("timestamp", (o1, o2) -> true),
                        //TODO: Rework returned messages & exception in productive code
                        new Customization("message", (o1, o2) -> true), new Customization("exception", (o1, o2) -> true));
        JSONAssert.assertEquals(expectedResponseBody, responseBody, ignoreTimestampAttribute);
    }

    @Test
    public void testLoginWrongCredentials() throws IOException, JSONException {
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
        CustomComparator ignoreTimestampAttribute =
                new CustomComparator(JSONCompareMode.LENIENT, new Customization("timestamp", (o1, o2) -> true),
                        //TODO: Rework returned messages & exception in productive code
                        new Customization("exception", (o1, o2) -> true));
        JSONAssert.assertEquals(expectedResponseBody, responseBody, ignoreTimestampAttribute);
    }

}
