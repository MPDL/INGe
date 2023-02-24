package util;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class TestBase {

  private static final Properties TEST_DATA_PROPERTIES = initTestDataProperties();
  public static final String BASE_URI = TEST_DATA_PROPERTIES.getProperty("baseURI");
  public static final String USERNAME = TEST_DATA_PROPERTIES.getProperty("username");
  public static final String PASSWORD = TEST_DATA_PROPERTIES.getProperty("password");

  private static Properties initTestDataProperties() {
    Properties testDataProperties = new Properties();
    InputStream testDataInputStream = TestBase.class.getClassLoader().getResourceAsStream("testData.properties");
    try {
      testDataProperties.load(testDataInputStream);
    } catch (IOException e) {
      //TODO Log error
      throw new RuntimeException(e);
    }

    return testDataProperties;
  }

  public static RequestSpecification initRequestSpecification(String basePath) {
    //TODO: Extract in other methode
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

    RequestSpecification requestSpecification = new RequestSpecBuilder()
        //.setContentType(ContentType.JSON)
        .setBaseUri(BASE_URI).setBasePath(basePath)
        //.addFilter(new ResponseLoggingFilter())
        //.addFilter(new RequestLoggingFilter())
        //.addFilter(new ErrorLoggingFilter())
        .build();

    return requestSpecification;
  }

}
