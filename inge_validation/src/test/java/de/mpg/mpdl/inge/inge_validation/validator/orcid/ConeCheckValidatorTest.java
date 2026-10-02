package de.mpg.mpdl.inge.inge_validation.validator.orcid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.baidu.unbiz.fluentvalidator.ComplexResult;
import com.baidu.unbiz.fluentvalidator.FluentValidator;
import com.baidu.unbiz.fluentvalidator.ResultCollectors;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.util.PropertyReader;

public class ConeCheckValidatorTest {

  private HttpServer mockServer;
  private int serverPort;
  private String mockResponseBody;
  private int mockResponseStatus = 200;

  private static final String VALID_PERSON_JSON = "{\n" //
      + "\"id\" : \"https://qa.pure.mpdl.mpg.de/cone/persons/resource/persons96304\",\n" //
      + "\"http_xmlns_com_foaf_0_1_family_name\" : \"Boosen\",\n" //
      + "\"http_purl_org_dc_terms_alternative\" : \"Boosen, M.\",\n" //
      + "\"http_purl_org_dc_elements_1_1_title\" : \"Boosen, Martin\",\n" //
      + "\"http_purl_org_dc_elements_1_1_identifier\" : {\n" //
      + "  \"http_www_w3_org_1999_02_22_rdf_syntax_ns_value\" : \"https://orcid.org/0009-0009-3989-5084\",\n" //
      + "  \"http_www_w3_org_2001_XMLSchema_instance_type\" : \"ORCID\"\n" //
      + "}\n" //
      + "}";

  @Before
  public void setUp() throws Exception {
    mockResponseStatus = 200;
    mockResponseBody = VALID_PERSON_JSON;

    mockServer = HttpServer.create(new InetSocketAddress(0), 0);
    serverPort = mockServer.getAddress().getPort();

    mockServer.createContext("/cone/persons/resource/persons96304", new HttpHandler() {
      @Override
      public void handle(HttpExchange exchange) throws IOException {
        byte[] response = mockResponseBody.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(mockResponseStatus, response.length);
        OutputStream os = exchange.getResponseBody();
        os.write(response);
        os.close();
      }
    });

    mockServer.start();

    System.setProperty(PropertyReader.INGE_CONE_SERVICE_URL, "http://localhost:" + serverPort + "/cone/");
  }

  @After
  public void tearDown() {
    if (mockServer != null) {
      mockServer.stop(0);
    }
    System.clearProperty(PropertyReader.INGE_CONE_SERVICE_URL);
  }

  @Test
  public void testValidConePersonMatching() {
    FluentValidator validator = FluentValidator.checkAll().failOver() //
        .on("persons96304", new ConeCheckValidator("Boosen, Martin", "0009-0009-3989-5084"));

    ComplexResult result = validator.doValidate().result(ResultCollectors.toComplex());
    assertTrue(result.isSuccess());
  }

  @Test
  public void testValidConePersonWithFullUrl() {
    String fullUrl = "http://localhost:" + serverPort + "/cone/persons/resource/persons96304?format=json";
    FluentValidator validator = FluentValidator.checkAll().failOver() //
        .on(fullUrl, new ConeCheckValidator("Boosen, Martin", "https://orcid.org/0009-0009-3989-5084"));

    ComplexResult result = validator.doValidate().result(ResultCollectors.toComplex());
    assertTrue(result.isSuccess());
  }

  @Test
  public void testNameMismatch() {
    FluentValidator validator = FluentValidator.checkAll().failOver() //
        .on("persons96304", new ConeCheckValidator("Mustermann, Max", "0009-0009-3989-5084"));

    ComplexResult result = validator.doValidate().result(ResultCollectors.toComplex());
    assertFalse(result.isSuccess());
    assertEquals(ErrorMessages.ORCID_NAME_AUTHOR_NOT_PROVIDED, result.getErrors().get(0).getErrorMsg());
  }

  @Test
  public void testOrcidMismatch() {
    FluentValidator validator = FluentValidator.checkAll().failOver() //
        .on("persons96304", new ConeCheckValidator("Boosen, Martin", "0000-0002-1825-0097"));

    ComplexResult result = validator.doValidate().result(ResultCollectors.toComplex());
    assertFalse(result.isSuccess());
    assertEquals(ErrorMessages.ORCID_INVALID, result.getErrors().get(0).getErrorMsg());
  }

  @Test
  public void testEmptyConeIdAuthor() {
    FluentValidator validator = FluentValidator.checkAll().failOver() //
        .on("", new ConeCheckValidator("Boosen, Martin", "0009-0009-3989-5084"));

    ComplexResult result = validator.doValidate().result(ResultCollectors.toComplex());
    assertFalse(result.isSuccess());
    assertEquals(ErrorMessages.ORCID_CONE_ID_AUTHOR_NOT_PROVIDED, result.getErrors().get(0).getErrorMsg());
  }

  @Test
  public void testEmptyNameAuthor() {
    FluentValidator validator = FluentValidator.checkAll().failOver() //
        .on("persons96304", new ConeCheckValidator("", "0009-0009-3989-5084"));

    ComplexResult result = validator.doValidate().result(ResultCollectors.toComplex());
    assertFalse(result.isSuccess());
    assertEquals(ErrorMessages.ORCID_NAME_AUTHOR_NOT_PROVIDED, result.getErrors().get(0).getErrorMsg());
  }

  @Test
  public void testEmptyOrcidAuthor() {
    FluentValidator validator = FluentValidator.checkAll().failOver() //
        .on("persons96304", new ConeCheckValidator("Boosen, Martin", ""));

    ComplexResult result = validator.doValidate().result(ResultCollectors.toComplex());
    assertFalse(result.isSuccess());
    assertEquals(ErrorMessages.ORCID_AUTHOR_NOT_PROVIDED, result.getErrors().get(0).getErrorMsg());
  }

  @Test
  public void testHttpServerError() {
    mockResponseStatus = 500;
    mockResponseBody = "Server Error";

    FluentValidator validator = FluentValidator.checkAll().failOver() //
        .on("persons96304", new ConeCheckValidator("Boosen, Martin", "0009-0009-3989-5084"));

    ComplexResult result = validator.doValidate().result(ResultCollectors.toComplex());
    assertFalse(result.isSuccess());
    assertEquals(ErrorMessages.ORCID_CONE_ID_AUTHOR_NOT_PROVIDED, result.getErrors().get(0).getErrorMsg());
  }
}
