package de.mpg.mpdl.inge.service.pubman.impl;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import de.mpg.mpdl.inge.db.repository.OrcidAuthorizationRepository;
import de.mpg.mpdl.inge.model.db.valueobjects.OrcidAuthorizationDbVO;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.util.PropertyReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class OrcidServiceTest {

  private HttpServer mockServer;
  private int serverPort;
  private OrcidServiceImpl orcidService;
  private final AtomicReference<CapturedRequest> lastCapturedRequest = new AtomicReference<>();
  private int mockResponseStatus = 200;
  private String mockResponseBody = "";
  private final Map<String, OrcidAuthorizationDbVO> repoStorage = new HashMap<>();

  private static class CapturedRequest {
    String method;
    String acceptHeader;
    String contentTypeHeader;
    String body;
    Map<String, String> formParams = new HashMap<>();
  }

  @Before
  public void setUp() throws Exception {
    repoStorage.clear();
    mockResponseStatus = 200;
    mockResponseBody = "{\n" + "  \"access_token\":\"f5af9f51-07e6-4332-8f1a-c0c11c1e3728\",\n" + "  \"token_type\":\"bearer\",\n"
        + "  \"refresh_token\":\"f725f747-3a65-49f6-a231-3e8944ce464d\",\n" + "  \"expires_in\":631138518,\n"
        + "  \"scope\":\"/read-limited\",\n" + "  \"name\":\"Sofia Garcia\",\n" + "  \"orcid\":\"0000-0001-2345-6789\"\n" + "}";

    mockServer = HttpServer.create(new InetSocketAddress(0), 0);
    serverPort = mockServer.getAddress().getPort();

    mockServer.createContext("/oauth/token", new HttpHandler() {
      @Override
      public void handle(HttpExchange exchange) throws IOException {
        CapturedRequest captured = new CapturedRequest();
        captured.method = exchange.getRequestMethod();
        captured.acceptHeader = exchange.getRequestHeaders().getFirst("Accept");
        captured.contentTypeHeader = exchange.getRequestHeaders().getFirst("Content-Type");

        InputStream is = exchange.getRequestBody();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
          baos.write(buffer, 0, len);
        }
        captured.body = baos.toString(StandardCharsets.UTF_8);

        String[] pairs = captured.body.split("&");
        for (String pair : pairs) {
          int idx = pair.indexOf("=");
          if (idx > 0) {
            String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
            String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
            captured.formParams.put(key, value);
          }
        }

        lastCapturedRequest.set(captured);

        byte[] responseBytes = mockResponseBody.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(mockResponseStatus, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
      }
    });

    mockServer.start();

    System.setProperty(PropertyReader.ORCID_URL, "http://localhost:" + serverPort);
    System.setProperty(PropertyReader.ORCID_TOKEN_URL, "/oauth/token");
    System.setProperty(PropertyReader.ORCID_CLIENT_ID, "TEST-CLIENT-ID");
    System.setProperty(PropertyReader.ORCID_REDIRECT_URL_REST, "http://localhost:" + serverPort + "/rest");
    System.setProperty(PropertyReader.ORCID_REDIRECT_URL_URL, "/orcid/createOrcidAuthentication");
    System.setProperty(PropertyReader.ORCID_EMAIL_MAILSERVERNAME, "localhost");
    System.setProperty(PropertyReader.ORCID_EMAIL_SENDER_FOR_BIBO, "test@mpg.de");
    System.setProperty(PropertyReader.ORCID_EMAIL_SUBJECT_FOR_BIBO, "Test Subject");
    System.setProperty(PropertyReader.ORCID_EMAIL_TEXT_FOR_BIBO, "Test Text");

    OrcidAuthorizationRepository mockRepository = createMockRepository();
    AuthorizationService mockAuthService = null;

    orcidService = new OrcidServiceImpl(mockAuthService, mockRepository, null);
  }

  private OrcidAuthorizationRepository createMockRepository() {
    return (OrcidAuthorizationRepository) Proxy.newProxyInstance(OrcidAuthorizationRepository.class.getClassLoader(),
        new Class<?>[] {OrcidAuthorizationRepository.class}, (proxy, method, args) -> {
          if ("findBySecret".equals(method.getName())) {
            return repoStorage.get((String) args[0]);
          }
          if ("saveAndFlush".equals(method.getName()) || "save".equals(method.getName())) {
            OrcidAuthorizationDbVO entity = (OrcidAuthorizationDbVO) args[0];
            if (entity.getSecret() != null) {
              repoStorage.put(entity.getSecret(), entity);
            }
            return entity;
          }
          return null;
        });
  }

  @After
  public void tearDown() {
    if (mockServer != null) {
      mockServer.stop(0);
    }
    System.clearProperty(PropertyReader.ORCID_URL);
    System.clearProperty(PropertyReader.ORCID_TOKEN_URL);
    System.clearProperty(PropertyReader.ORCID_CLIENT_ID);
    System.clearProperty(PropertyReader.ORCID_REDIRECT_URL_REST);
    System.clearProperty(PropertyReader.ORCID_REDIRECT_URL_URL);
    System.clearProperty(PropertyReader.ORCID_EMAIL_MAILSERVERNAME);
    System.clearProperty(PropertyReader.ORCID_EMAIL_SENDER_FOR_BIBO);
    System.clearProperty(PropertyReader.ORCID_EMAIL_SUBJECT_FOR_BIBO);
    System.clearProperty(PropertyReader.ORCID_EMAIL_TEXT_FOR_BIBO);
  }

  @Test
  public void testRequestTokenAndOrcidSuccess() throws Exception {
    OrcidAuthorizationDbVO vo = new OrcidAuthorizationDbVO();
    vo.setSecret("test-secret-123");
    vo.setRedirectUri("https://qa.pure.mpdl.mpg.de/rest/orcid/createOrcidAuthentication?secret=test-secret-123");

    OrcidAuthorizationDbVO result = orcidService.requestTokenAndOrcid(vo, "654321");

    assertNotNull(result);
    assertEquals("f5af9f51-07e6-4332-8f1a-c0c11c1e3728", result.getAccessToken());
    assertEquals("bearer", result.getTokenType());
    assertEquals("f725f747-3a65-49f6-a231-3e8944ce464d", result.getRefreshToken());
    assertEquals("631138518", result.getExpiresIn());
    assertEquals("/read-limited", result.getScope());
    assertEquals("Sofia Garcia", result.getName());
    assertEquals("0000-0001-2345-6789", result.getOrcid());
    assertEquals(OrcidAuthorizationDbVO.Status.TOKEN_RECEIVED, result.getStatus());
    assertNotNull(result.getDateTokenReceived());

    CapturedRequest captured = lastCapturedRequest.get();
    assertNotNull(captured);
    assertEquals("POST", captured.method);
    assertEquals("application/json", captured.acceptHeader);
    assertTrue(captured.contentTypeHeader.startsWith("application/x-www-form-urlencoded"));
    assertEquals("TEST-CLIENT-ID", captured.formParams.get("client_id"));
    assertEquals("test-secret-123", captured.formParams.get("client_secret"));
    assertEquals("authorization_code", captured.formParams.get("grant_type"));
    assertEquals("654321", captured.formParams.get("code"));
    assertEquals("https://qa.pure.mpdl.mpg.de/rest/orcid/createOrcidAuthentication?secret=test-secret-123",
        captured.formParams.get("redirect_uri"));
  }

  @Test
  public void testRequestTokenAndOrcidSingleParam() throws Exception {
    OrcidAuthorizationDbVO vo = new OrcidAuthorizationDbVO();
    vo.setSecret("test-secret-456");
    vo.setCodeReceived("112233");
    vo.setRedirectUri("https://qa.pure.mpdl.mpg.de/rest/orcid/createOrcidAuthentication?secret=test-secret-456");

    OrcidAuthorizationDbVO result = orcidService.requestTokenAndOrcid(vo, "112233");

    assertNotNull(result);
    assertEquals("f5af9f51-07e6-4332-8f1a-c0c11c1e3728", result.getAccessToken());
    assertEquals("0000-0001-2345-6789", result.getOrcid());

    CapturedRequest captured = lastCapturedRequest.get();
    assertEquals("112233", captured.formParams.get("code"));
  }

  @Test
  public void testRequestTokenAndOrcidHttpError() {
    mockResponseStatus = 400;
    mockResponseBody = "{\"error\":\"invalid_grant\",\"error_description\":\"Invalid authorization code\"}";

    OrcidAuthorizationDbVO vo = new OrcidAuthorizationDbVO();
    vo.setSecret("test-secret-789");
    vo.setRedirectUri("https://qa.pure.mpdl.mpg.de/rest/orcid/createOrcidAuthentication?secret=test-secret-789");
    try {
      orcidService.requestTokenAndOrcid(vo, "bad-code");
      fail("Should have thrown TechnicalException");
    } catch (TechnicalException e) {
      assertTrue(e.getMessage().contains("400"));
      assertTrue(e.getMessage().contains("invalid_grant"));
    } catch (Exception e) {
      fail("Unexpected exception: " + e);
    }
  }
}
