package de.mpg.mpdl.inge.service.pubman.impl;

import co.elastic.clients.elasticsearch.core.SearchRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import de.mpg.mpdl.inge.db.repository.OrcidAuthorizationRepository;
import de.mpg.mpdl.inge.db.repository.OrcidRepository;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.db.valueobjects.OrcidAuthorizationDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.OrcidDbVO;
import de.mpg.mpdl.inge.model.util.MapperFactory;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.util.PropertyReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    System.setProperty(PropertyReader.ORCID_CLIENT_SECRET, "test-secret-123");
    System.setProperty(PropertyReader.ORCID_REDIRECT_URL_REST, "http://localhost:" + serverPort + "/rest");
    System.setProperty(PropertyReader.ORCID_REDIRECT_URL_URL, "/orcid/createOrcidAuthentication");
    System.setProperty(PropertyReader.ORCID_EMAIL_MAILSERVERNAME, "localhost");
    System.setProperty(PropertyReader.ORCID_EMAIL_SENDER_FOR_BIBO, "test@mpg.de");
    System.setProperty(PropertyReader.ORCID_EMAIL_SUBJECT_FOR_BIBO, "Test Subject");
    System.setProperty(PropertyReader.ORCID_EMAIL_TEXT_FOR_BIBO, "Test Text");

    OrcidAuthorizationRepository mockRepository = createMockRepository();
    AuthorizationService mockAuthService = null;

    orcidService = new OrcidServiceImpl(mockAuthService, mockRepository, null, null);
  }

  private OrcidAuthorizationRepository createMockRepository() {
    return (OrcidAuthorizationRepository) Proxy.newProxyInstance(OrcidAuthorizationRepository.class.getClassLoader(),
        new Class<?>[] {OrcidAuthorizationRepository.class}, (proxy, method, args) -> {
          if ("findBySecret".equals(method.getName())) {
            return repoStorage.get((String) args[0]);
          }
          if ("findByVerifiedTrue".equals(method.getName())) {
            List<OrcidAuthorizationDbVO> list = new ArrayList<>();
            for (OrcidAuthorizationDbVO vo : repoStorage.values()) {
              if (Boolean.TRUE.equals(vo.getVerified())) {
                list.add(vo);
              }
            }
            return list;
          }
          if ("findByVerified".equals(method.getName())) {
            Boolean flag = (Boolean) args[0];
            List<OrcidAuthorizationDbVO> list = new ArrayList<>();
            for (OrcidAuthorizationDbVO vo : repoStorage.values()) {
              if (flag != null && flag.equals(vo.getVerified())) {
                list.add(vo);
              }
            }
            return list;
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
    System.clearProperty(PropertyReader.ORCID_CLIENT_SECRET);
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
    vo.setNameAuthor("Sofia Garcia");
    vo.setOrcidAuthor("0000-0001-2345-6789");
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
    assertEquals(OrcidAuthorizationDbVO.Status.TOKEN_RECEIVED.toString(), result.getStatus());
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
    vo.setNameAuthor("Sofia Garcia");
    vo.setOrcidAuthor("0000-0001-2345-6789");
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

  @Test
  public void testCreateOrcidAuthorizationAccessDenied() throws Exception {
    OrcidAuthorizationDbVO vo = new OrcidAuthorizationDbVO();
    vo.setSecret("test-secret-denied");
    vo.setNameAuthor("Sofia Garcia");
    vo.setOrcidAuthor("0000-0001-2345-6789");
    vo.setRedirectUri("https://qa.pure.mpdl.mpg.de/rest/orcid/createOrcidAuthentication?secret=test-secret-denied");
    repoStorage.put("test-secret-denied", vo);

    OrcidAuthorizationDbVO result =
        orcidService.createOrcidAuthorization("test-secret-denied?error=access_denied", null, "User denied access");

    assertNotNull(result);
    assertEquals(OrcidAuthorizationDbVO.Status.ACCESS_DENIED.toString(), result.getStatus());
    assertEquals(OrcidAuthorizationDbVO.Status.ACCESS_DENIED.toString(), result.getErrorCodes());
  }

  @Test
  public void testCreateOrcidAuthorizationOtherError() throws Exception {
    OrcidAuthorizationDbVO vo = new OrcidAuthorizationDbVO();
    vo.setSecret("test-secret-server-error");
    vo.setNameAuthor("Sofia Garcia");
    vo.setOrcidAuthor("0000-0001-2345-6789");
    vo.setRedirectUri("https://qa.pure.mpdl.mpg.de/rest/orcid/createOrcidAuthentication?secret=test-secret-server-error");
    repoStorage.put("test-secret-server-error", vo);

    OrcidAuthorizationDbVO result =
        orcidService.createOrcidAuthorization("test-secret-server-error?error=server_error", null, "Internal server error");

    assertNotNull(result);
    assertEquals(OrcidAuthorizationDbVO.Status.ERROR.toString(), result.getStatus());
    assertEquals("Internal server error", result.getErrorCodes());
  }

  @Test
  public void testCreateEmailText() throws Exception {
    String nameAuthor = "Dr. Max Mustermann";
    String orcidAuthor = "0000-0002-1825-0097";
    String emailLink = "https://sandbox.orcid.org/oauth/authorize?test=123";

    String emailText = orcidService.createEmailText(nameAuthor, orcidAuthor, emailLink);
    System.out.println(emailText);

    assertNotNull(emailText);
    assertTrue(emailText.contains("Sehr geehrte(r) Dr. Max Mustermann"));
    assertTrue(emailText.contains("0000-0002-1825-0097"));
    assertTrue(emailText.contains(
        "Klicken Sie auf diesen Link: <a href=\"https://sandbox.orcid.org/oauth/authorize?test=123\">https://sandbox.orcid.org/oauth/authorize?test=123</a>"));
    assertFalse(emailText.contains("$NAME_AUTHOR"));
    assertFalse(emailText.contains("$ORCID_AUTHOR"));
    assertFalse(emailText.contains("$EMAIL_LINK"));
  }

  private JsonNode invokeCreateOrcidQueryJsonNode(String personId, Date dateModified) throws Exception {
    Method method = OrcidServiceImpl.class.getDeclaredMethod("createOrcidQueryJsonNode", String.class, Date.class);
    method.setAccessible(true);
    try {
      return (JsonNode) method.invoke(orcidService, personId, dateModified);
    } catch (InvocationTargetException e) {
      if (e.getCause() instanceof Exception) {
        throw (Exception) e.getCause();
      }
      throw e;
    }
  }

  private String invokeConvertToOrcid(SearchRetrieveResponseVO<ItemVersionVO> responseVO) throws Exception {
    Method method = OrcidServiceImpl.class.getDeclaredMethod("convertToOrcid", SearchRetrieveResponseVO.class);
    method.setAccessible(true);
    try {
      return (String) method.invoke(orcidService, responseVO);
    } catch (InvocationTargetException e) {
      if (e.getCause() instanceof Exception) {
        throw (Exception) e.getCause();
      }
      throw e;
    }
  }

  @SuppressWarnings("unchecked")
  private SearchRetrieveResponseVO<ItemVersionVO> invokeDoOrcidQuery(OrcidServiceImpl targetService, String personId, Date dateModified,
      String token) throws Exception {
    Method method = OrcidServiceImpl.class.getDeclaredMethod("doOrcidQuery", String.class, Date.class, String.class);
    method.setAccessible(true);
    try {
      return (SearchRetrieveResponseVO<ItemVersionVO>) method.invoke(targetService, personId, dateModified, token);
    } catch (InvocationTargetException e) {
      if (e.getCause() instanceof Exception) {
        throw (Exception) e.getCause();
      }
      throw e;
    }
  }

  @Test
  public void testCreateOrcidQueryJsonNodeWithDateModified() throws Exception {
    String personId = "/persons/resource/persons96304";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    Date dateModified = sdf.parse("2026-10-08 08:16:17.055");

    JsonNode node = invokeCreateOrcidQueryJsonNode(personId, dateModified);
    assertNotNull(node);

    assertEquals(25, node.get("size").asInt());
    assertEquals(0, node.get("from").asInt());
    assertFalse(node.has("runtime_mappings"));
    assertFalse(node.has("aggs"));

    // Check sort
    JsonNode sort = node.get("sort");
    assertNotNull(sort);
    assertTrue(sort.isArray());
    assertEquals("desc", sort.get(0).get("lastModificationDate").get("order").asText());

    // Check query
    JsonNode must = node.get("query").get("bool").get("must");
    assertNotNull(must);
    assertTrue(must.isArray());
    assertEquals(3, must.size());

    // Must 0: term person identifier
    assertEquals(personId, must.get(0).get("term").get("metadata.creators.person.identifier.id").asText());

    // Must 1: range dateModified (timestamp in ms)
    assertEquals(dateModified.getTime(), must.get(1).get("range").get("modificationDate").get("gte").asLong());

    // Must 2: bool state
    JsonNode should = must.get(2).get("bool").get("should");
    assertNotNull(should);
    JsonNode innerMust = should.get(0).get("bool").get("must");
    assertEquals("RELEASED", innerMust.get(0).get("term").get("versionState").asText());
    JsonNode innerMustNot = should.get(0).get("bool").get("must_not");
    assertEquals("WITHDRAWN", innerMustNot.get(0).get("term").get("publicState").asText());
  }

  @Test
  public void testCreateOrcidQueryJsonNodeWithoutDateModified() throws Exception {
    String personId = "/persons/resource/persons96304";

    JsonNode node = invokeCreateOrcidQueryJsonNode(personId, null);
    assertNotNull(node);

    JsonNode must = node.get("query").get("bool").get("must");
    assertEquals(2, must.size());
    assertEquals(personId, must.get(0).get("term").get("metadata.creators.person.identifier.id").asText());
    assertTrue(must.get(1).has("bool"));
  }

  @Test
  public void testCreateOrcidQueryJsonNodeWithFullConeUrl() throws Exception {
    String fullUrl = "https://qa.pure.mpdl.mpg.de/cone/persons/resource/persons96304";

    JsonNode node = invokeCreateOrcidQueryJsonNode(fullUrl, null);
    assertNotNull(node);

    JsonNode must = node.get("query").get("bool").get("must");
    assertEquals("/persons/resource/persons96304", must.get(0).get("term").get("metadata.creators.person.identifier.id").asText());
  }

  @Test(expected = IngeApplicationException.class)
  public void testCreateOrcidQueryJsonNodeMissingPersonId() throws Exception {
    invokeCreateOrcidQueryJsonNode(null, new Date());
  }

  @Test(expected = IngeApplicationException.class)
  public void testCreateOrcidQueryJsonNodeEmptyPersonId() throws Exception {
    invokeCreateOrcidQueryJsonNode("   ", new Date());
  }

  @Test
  public void testDoOrcidQueryAndConvertWithDate() throws Exception {
    AtomicReference<SearchRequest> capturedRequest = new AtomicReference<>();
    AtomicReference<String> capturedToken = new AtomicReference<>();

    PubItemService mockPubItemService = (PubItemService) Proxy.newProxyInstance(PubItemService.class.getClassLoader(),
        new Class<?>[] {PubItemService.class}, (proxy, method, args) -> {
          if ("searchDetailed".equals(method.getName())) {
            capturedRequest.set((SearchRequest) args[0]);
            capturedToken.set((String) args[2]);
            return null;
          }
          return null;
        });

    OrcidServiceImpl customOrcidService = new OrcidServiceImpl(null, null, null, null, mockPubItemService);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    Date date = sdf.parse("2026-10-08 08:16:17.055");
    String result = customOrcidService.doOrcidQueryAndConvert("/persons/resource/persons96304", date, "test-token");

    assertNotNull(capturedRequest.get());
    assertEquals("test-token", capturedToken.get());
    assertEquals(25, capturedRequest.get().size().intValue());
    assertEquals(0, capturedRequest.get().from().intValue());
    assertNotNull(result);
    JsonNode root = MapperFactory.getObjectMapper().readTree(result);
    assertTrue(root.has("bulk"));
    assertEquals(0, root.get("bulk").size());
  }

  @Test
  public void testDoOrcidQueryAndConvertWithStringDate() throws Exception {
    AtomicReference<SearchRequest> capturedRequest = new AtomicReference<>();
    AtomicReference<String> capturedToken = new AtomicReference<>();

    PubItemService mockPubItemService = (PubItemService) Proxy.newProxyInstance(PubItemService.class.getClassLoader(),
        new Class<?>[] {PubItemService.class}, (proxy, method, args) -> {
          if ("searchDetailed".equals(method.getName())) {
            capturedRequest.set((SearchRequest) args[0]);
            capturedToken.set((String) args[2]);
            return null;
          }
          return null;
        });

    OrcidServiceImpl customOrcidService = new OrcidServiceImpl(null, null, null, null, mockPubItemService);
    String result = customOrcidService.doOrcidQueryAndConvert("/persons/resource/persons96304", new Date(), "test-token");

    assertNotNull(capturedRequest.get());
    assertEquals("test-token", capturedToken.get());
    assertNotNull(result);
    JsonNode root = MapperFactory.getObjectMapper().readTree(result);
    assertTrue(root.has("bulk"));
    assertEquals(0, root.get("bulk").size());
  }

  @Test(expected = IngeApplicationException.class)
  public void testDoOrcidQueryAndConvertMissingPersonId() throws Exception {
    orcidService.doOrcidQueryAndConvert(null, new Date(), null);
  }

  @Test(expected = IngeApplicationException.class)
  public void testDoOrcidQueryAndConvertEmptyPersonId() throws Exception {
    orcidService.doOrcidQueryAndConvert("   ", new Date(), null);
  }

  @Test
  public void testDoOrcidQueryReturnsSearchRetrieveResponseVO() throws Exception {
    AtomicReference<SearchRequest> capturedRequest = new AtomicReference<>();
    AtomicReference<String> capturedToken = new AtomicReference<>();

    PubItemService mockPubItemService = (PubItemService) Proxy.newProxyInstance(PubItemService.class.getClassLoader(),
        new Class<?>[] {PubItemService.class}, (proxy, method, args) -> {
          if ("searchDetailed".equals(method.getName())) {
            capturedRequest.set((SearchRequest) args[0]);
            capturedToken.set((String) args[2]);
            return null;
          }
          return null;
        });

    OrcidServiceImpl customOrcidService = new OrcidServiceImpl(null, null, null, null, mockPubItemService);
    SearchRetrieveResponseVO<ItemVersionVO> response =
        invokeDoOrcidQuery(customOrcidService, "/persons/resource/persons96304", new Date(), "test-token");

    assertNotNull(response);
    assertNotNull(capturedRequest.get());
    assertEquals("test-token", capturedToken.get());
  }

  @Test
  public void testConvertToOrcidWithSearchRetrieveResponseVO() throws Exception {
    SearchRetrieveResponseVO<ItemVersionVO> responseVO = new SearchRetrieveResponseVO<>();
    List<SearchRetrieveRecordVO<ItemVersionVO>> records = new ArrayList<>();

    ItemVersionVO item = new ItemVersionVO();
    item.setObjectId("item_12345");
    MdsPublicationVO metadata = new MdsPublicationVO();
    metadata.setGenre(MdsPublicationVO.Genre.ARTICLE);
    metadata.setTitle("ORCID Test Publication");
    metadata.setDatePublishedInPrint("2026-10-15");

    PersonVO person = new PersonVO();
    person.setGivenName("Max");
    person.setFamilyName("Planck");
    person.setOrcid("0000-0001-2345-6789");
    CreatorVO creator = new CreatorVO(person, CreatorVO.CreatorRole.AUTHOR);
    metadata.getCreators().add(creator);

    item.setMetadata(metadata);

    SearchRetrieveRecordVO<ItemVersionVO> record = new SearchRetrieveRecordVO<>();
    record.setData(item);
    records.add(record);
    responseVO.setRecords(records);

    String orcidJson = invokeConvertToOrcid(responseVO);
    assertNotNull(orcidJson);

    JsonNode root = MapperFactory.getObjectMapper().readTree(orcidJson);
    assertTrue(root.has("bulk"));
    JsonNode bulk = root.get("bulk");
    assertTrue(bulk.isArray());
    assertEquals(1, bulk.size());

    JsonNode work = bulk.get(0).get("work");
    assertEquals("journal-article", work.get("type").asText());
    assertEquals("ORCID Test Publication", work.get("title").get("title").get("value").asText());
    assertEquals("2026", work.get("publication-date").get("year").get("value").asText());

    JsonNode contributors = work.get("contributors").get("contributor");
    assertEquals(1, contributors.size());
    assertEquals("Max Planck", contributors.get(0).get("credit-name").get("value").asText());
    assertEquals("https://orcid.org/0000-0001-2345-6789", contributors.get(0).get("contributor-orcid").get("uri").asText());
  }

  @Test
  public void testExtractWorksBySourceWorkIdAndModifyPutCode() throws Exception {
    SearchRetrieveResponseVO<ItemVersionVO> responseVO = new SearchRetrieveResponseVO<>();
    List<SearchRetrieveRecordVO<ItemVersionVO>> records = new ArrayList<>();

    ItemVersionVO item1 = new ItemVersionVO();
    item1.setObjectId("item_11111");
    MdsPublicationVO meta1 = new MdsPublicationVO();
    meta1.setGenre(MdsPublicationVO.Genre.ARTICLE);
    meta1.setTitle("Work One");
    item1.setMetadata(meta1);
    SearchRetrieveRecordVO<ItemVersionVO> rec1 = new SearchRetrieveRecordVO<>();
    rec1.setData(item1);
    records.add(rec1);

    ItemVersionVO item2 = new ItemVersionVO();
    item2.setObjectId("item_22222");
    MdsPublicationVO meta2 = new MdsPublicationVO();
    meta2.setGenre(MdsPublicationVO.Genre.BOOK);
    meta2.setTitle("Work Two");
    item2.setMetadata(meta2);
    SearchRetrieveRecordVO<ItemVersionVO> rec2 = new SearchRetrieveRecordVO<>();
    rec2.setData(item2);
    records.add(rec2);

    responseVO.setRecords(records);
    String initialOrcidJson = invokeConvertToOrcid(responseVO);

    Map<String, ObjectNode> worksMap = orcidService.extractWorksBySourceWorkId(initialOrcidJson);
    assertEquals(2, worksMap.size());
    assertTrue(worksMap.containsKey("item_11111"));
    assertTrue(worksMap.containsKey("item_22222"));

    // Modify work1 with put-code 2543541
    ObjectNode work1 = worksMap.get("item_11111");
    assertNotNull(work1);
    orcidService.setPutCode(work1, 2543541L);

    // Modify work2 with put-code 9876543
    ObjectNode work2 = worksMap.get("item_22222");
    assertNotNull(work2);
    orcidService.setPutCode(work2, "9876543");

    // Convert back to string
    String modifiedJson = orcidService.convertWorksToOrcidJson(worksMap);
    assertNotNull(modifiedJson);

    JsonNode root = MapperFactory.getObjectMapper().readTree(modifiedJson);
    assertTrue(root.has("bulk"));
    JsonNode bulk = root.get("bulk");
    assertEquals(2, bulk.size());

    assertEquals(2543541L, bulk.get(0).get("work").get("put-code").asLong());
    assertEquals(9876543L, bulk.get(1).get("work").get("put-code").asLong());
  }

  @Test
  public void testUpdatePutCodesInOrcidJson() throws Exception {
    SearchRetrieveResponseVO<ItemVersionVO> responseVO = new SearchRetrieveResponseVO<>();
    List<SearchRetrieveRecordVO<ItemVersionVO>> records = new ArrayList<>();

    ItemVersionVO item = new ItemVersionVO();
    item.setObjectId("item_99999");
    MdsPublicationVO meta = new MdsPublicationVO();
    meta.setGenre(MdsPublicationVO.Genre.ARTICLE);
    meta.setTitle("Work Nine");
    item.setMetadata(meta);
    SearchRetrieveRecordVO<ItemVersionVO> rec = new SearchRetrieveRecordVO<>();
    rec.setData(item);
    records.add(rec);

    responseVO.setRecords(records);
    String initialOrcidJson = invokeConvertToOrcid(responseVO);

    Map<String, Long> putCodes = new HashMap<>();
    putCodes.put("item_99999", 2543541L);

    String updatedJson = orcidService.updatePutCodesInOrcidJson(initialOrcidJson, putCodes);
    assertNotNull(updatedJson);

    JsonNode root = MapperFactory.getObjectMapper().readTree(updatedJson);
    assertEquals(2543541L, root.get("bulk").get(0).get("work").get("put-code").asLong());
  }

  @Test
  public void testExtractWorksWithEmptyOrNull() throws Exception {
    Map<String, ObjectNode> nullMap = orcidService.extractWorksBySourceWorkId((String) null);
    assertNotNull(nullMap);
    assertTrue(nullMap.isEmpty());

    Map<String, ObjectNode> emptyMap = orcidService.extractWorksBySourceWorkId("  ");
    assertNotNull(emptyMap);
    assertTrue(emptyMap.isEmpty());
  }

  @Test
  public void testGetVerifiedOrcidAuthorizations() throws Exception {
    OrcidAuthorizationDbVO vo1 = new OrcidAuthorizationDbVO();
    vo1.setSecret("secret-1");
    vo1.setConeIdAuthor("persons96304");
    vo1.setVerified(true);
    repoStorage.put("secret-1", vo1);

    OrcidAuthorizationDbVO vo2 = new OrcidAuthorizationDbVO();
    vo2.setSecret("secret-2");
    vo2.setConeIdAuthor("persons12345");
    vo2.setVerified(false);
    repoStorage.put("secret-2", vo2);

    OrcidAuthorizationDbVO vo3 = new OrcidAuthorizationDbVO();
    vo3.setSecret("secret-3");
    vo3.setConeIdAuthor("persons99999");
    vo3.setVerified(true);
    repoStorage.put("secret-3", vo3);

    List<String> queriedPairs = new ArrayList<>();
    OrcidRepository mockOrcidRepo = (OrcidRepository) Proxy.newProxyInstance(OrcidRepository.class.getClassLoader(),
        new Class<?>[] {OrcidRepository.class}, (proxy, method, args) -> {
          if ("findByConeIdAuthorAndItemId".equals(method.getName())
              || "findByOrcidAuthorizationDbVO_ConeIdAuthorAndItemId".equals(method.getName())) {
            queriedPairs.add(args[0] + "#" + args[1]);
            OrcidDbVO orcidVO = new OrcidDbVO("CREATE", (String) args[1]);
            List<OrcidDbVO> list = new ArrayList<>();
            list.add(orcidVO);
            return list;
          }
          return null;
        });

    PubItemService mockPubItemService = (PubItemService) Proxy.newProxyInstance(PubItemService.class.getClassLoader(),
        new Class<?>[] {PubItemService.class}, (proxy, method, args) -> {
          if ("searchDetailed".equals(method.getName())) {
            ItemVersionVO itemVO = new ItemVersionVO();
            itemVO.setObjectId("item_12345");
            ObjectNode itemNode = (ObjectNode) MapperFactory.getObjectMapper().valueToTree(itemVO);
            co.elastic.clients.elasticsearch.core.SearchResponse<ObjectNode> searchResponse =
                co.elastic.clients.elasticsearch.core.SearchResponse
                    .of(sr -> sr.took(1).timedOut(false).shards(s -> s.total(1).successful(1).skipped(0).failed(0))
                        .hits(h -> h.total(t -> t.value(1).relation(co.elastic.clients.elasticsearch.core.search.TotalHitsRelation.Eq))
                            .hits(List.of(co.elastic.clients.elasticsearch.core.search.Hit
                                .of(hit -> hit.index("items").id("item_12345").source(itemNode))))));
            return searchResponse;
          }
          return null;
        });

    OrcidServiceImpl customService = new OrcidServiceImpl(null, createMockRepository(), null, null, mockPubItemService, mockOrcidRepo);

    Method method = OrcidServiceImpl.class.getDeclaredMethod("getVerifiedOrcidAuthorizations");
    method.setAccessible(true);
    method.invoke(customService);

    assertFalse(queriedPairs.isEmpty());
    assertTrue(queriedPairs.contains("persons96304#item_12345") || queriedPairs.contains("/persons96304#item_12345"));
  }

  @Test
  public void testOrcidDbVOAndRepositoryQuery() {
    OrcidAuthorizationDbVO authVO = new OrcidAuthorizationDbVO();
    authVO.setConeIdAuthor("persons96304");

    OrcidDbVO orcidVO = new OrcidDbVO("CREATE", "item_12345");
    orcidVO.setOrcidAuthorizationDbVO(authVO);
    orcidVO.setPutCode("2543541");
    orcidVO.setResponseCode("201");
    orcidVO.setDeveloperMessage("Created successfully");

    assertEquals("persons96304", orcidVO.getConeIdAuthor());
    assertEquals("item_12345", orcidVO.getItemId());
    assertEquals(authVO, orcidVO.getOrcidAuthorizationDbVO());
    assertEquals("2543541", orcidVO.getPutCode());
    assertEquals("201", orcidVO.getResponseCode());
    assertEquals("Created successfully", orcidVO.getDeveloperMessage());
  }
}
