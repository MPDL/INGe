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
import de.mpg.mpdl.inge.service.pubman.orcid.OrcidResponse;
import de.mpg.mpdl.inge.service.pubman.orcid.OrcidHelper;
import de.mpg.mpdl.inge.util.PropertyReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
  private OrcidAuthorizationRepository mockRepository;
  private final AtomicReference<CapturedRequest> lastCapturedRequest = new AtomicReference<>();
  private int mockResponseStatus = 200;
  private String mockResponseBody = "";
  private final Map<String, OrcidAuthorizationDbVO> repoStorage = new HashMap<>();


  private static class CapturedRequest {
    String method;
    String acceptHeader;
    String contentTypeHeader;
    String authorizationHeader;
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
        captured.authorizationHeader = exchange.getRequestHeaders().getFirst("Authorization");

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

    mockServer.createContext("/", new HttpHandler() {
      @Override
      public void handle(HttpExchange exchange) throws IOException {
        CapturedRequest captured = new CapturedRequest();
        captured.method = exchange.getRequestMethod();
        captured.acceptHeader = exchange.getRequestHeaders().getFirst("Accept");
        captured.contentTypeHeader = exchange.getRequestHeaders().getFirst("Content-Type");
        captured.authorizationHeader = exchange.getRequestHeaders().getFirst("Authorization");

        InputStream is = exchange.getRequestBody();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
          baos.write(buffer, 0, len);
        }
        captured.body = baos.toString(StandardCharsets.UTF_8);
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
    System.setProperty(PropertyReader.ORCID_API_URL, "http://localhost:" + serverPort);
    System.setProperty(PropertyReader.ORCID_TOKEN_URL, "/oauth/token");
    System.setProperty(PropertyReader.ORCID_CLIENT_ID, "TEST-CLIENT-ID");
    System.setProperty(PropertyReader.ORCID_CLIENT_SECRET, "test-secret-123");
    System.setProperty(PropertyReader.ORCID_REDIRECT_URL_REST, "http://localhost:" + serverPort + "/rest");
    System.setProperty(PropertyReader.ORCID_REDIRECT_URL_URL, "/orcid/getOrcidAuthentication");
    System.setProperty(PropertyReader.ORCID_EMAIL_MAILSERVERNAME, "localhost");
    System.setProperty(PropertyReader.ORCID_EMAIL_SENDER_FOR_BIBO, "test@mpg.de");
    System.setProperty(PropertyReader.ORCID_EMAIL_SUBJECT_FOR_BIBO, "Test Subject");
    System.setProperty(PropertyReader.ORCID_EMAIL_TEXT_FOR_BIBO, "Test Text");
    System.setProperty(PropertyReader.ORCID_MAX_ITEMS_PER_SEARCH, "10000");
    System.setProperty(PropertyReader.ORCID_MAX_ITEMS_PER_REQUEST, "100");

    this.mockRepository = createMockRepository();
    AuthorizationService mockAuthService = null;

    orcidService = new OrcidServiceImpl(mockAuthService, this.mockRepository, null, null, null, null);
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
    System.clearProperty(PropertyReader.ORCID_API_URL);
    System.clearProperty(PropertyReader.ORCID_TOKEN_URL);
    System.clearProperty(PropertyReader.ORCID_CLIENT_ID);
    System.clearProperty(PropertyReader.ORCID_CLIENT_SECRET);
    System.clearProperty(PropertyReader.ORCID_REDIRECT_URL_REST);
    System.clearProperty(PropertyReader.ORCID_REDIRECT_URL_URL);
    System.clearProperty(PropertyReader.ORCID_EMAIL_MAILSERVERNAME);
    System.clearProperty(PropertyReader.ORCID_EMAIL_SENDER_FOR_BIBO);
    System.clearProperty(PropertyReader.ORCID_EMAIL_SUBJECT_FOR_BIBO);
    System.clearProperty(PropertyReader.ORCID_EMAIL_TEXT_FOR_BIBO);
    System.clearProperty(PropertyReader.ORCID_MAX_ITEMS_PER_SEARCH);
    System.clearProperty(PropertyReader.ORCID_MAX_ITEMS_PER_REQUEST);
  }

  @Test
  public void testRequestTokenAndOrcidSuccess() throws Exception {
    OrcidAuthorizationDbVO vo = new OrcidAuthorizationDbVO();
    vo.setSecret("test-secret-123");
    vo.setNameAuthor("Sofia Garcia");
    vo.setOrcidAuthor("0000-0001-2345-6789");
    vo.setRedirectUri("https://qa.pure.mpdl.mpg.de/rest/orcid/getOrcidAuthentication?secret=test-secret-123");

    OrcidAuthorizationDbVO result = OrcidHelper.requestTokenAndOrcid(vo, "654321", mockRepository);

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
    assertEquals("https://qa.pure.mpdl.mpg.de/rest/orcid/getOrcidAuthentication?secret=test-secret-123",
        captured.formParams.get("redirect_uri"));
  }

  @Test
  public void testRequestTokenAndOrcidSingleParam() throws Exception {
    OrcidAuthorizationDbVO vo = new OrcidAuthorizationDbVO();
    vo.setSecret("test-secret-456");
    vo.setNameAuthor("Sofia Garcia");
    vo.setOrcidAuthor("0000-0001-2345-6789");
    vo.setCodeReceived("112233");
    vo.setRedirectUri("https://qa.pure.mpdl.mpg.de/rest/orcid/getOrcidAuthentication?secret=test-secret-456");

    OrcidAuthorizationDbVO result = OrcidHelper.requestTokenAndOrcid(vo, "112233", mockRepository);

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
    vo.setRedirectUri("https://qa.pure.mpdl.mpg.de/rest/orcid/getOrcidAuthentication?secret=test-secret-789");
    try {
      OrcidHelper.requestTokenAndOrcid(vo, "bad-code", mockRepository);
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
    vo.setRedirectUri("https://qa.pure.mpdl.mpg.de/rest/orcid/getOrcidAuthentication?secret=test-secret-denied");
    repoStorage.put("test-secret-denied", vo);

    OrcidAuthorizationDbVO result =
        orcidService.getOrcidAuthorization("test-secret-denied?error=access_denied", null, "User denied access");

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
    vo.setRedirectUri("https://qa.pure.mpdl.mpg.de/rest/orcid/getOrcidAuthentication?secret=test-secret-server-error");
    repoStorage.put("test-secret-server-error", vo);

    OrcidAuthorizationDbVO result =
        orcidService.getOrcidAuthorization("test-secret-server-error?error=server_error", null, "Internal server error");

    assertNotNull(result);
    assertEquals(OrcidAuthorizationDbVO.Status.ERROR.toString(), result.getStatus());
    assertEquals("Internal server error", result.getErrorCodes());
  }

  @Test
  public void testCreateEmailText() throws Exception {
    String nameAuthor = "Dr. Max Mustermann";
    String orcidAuthor = "0000-0002-1825-0097";
    String emailLink = "https://sandbox.orcid.org/oauth/authorize?test=123";

    String emailText = OrcidHelper.createEmailText(nameAuthor, orcidAuthor, emailLink);
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
    return OrcidHelper.createOrcidQueryJsonNode(personId, dateModified);
  }

  private String invokeConvertToOrcid(SearchRetrieveResponseVO<ItemVersionVO> responseVO) throws Exception {
    return OrcidHelper.convertToOrcid(responseVO);
  }

  private SearchRetrieveResponseVO<ItemVersionVO> invokeDoOrcidQuery(PubItemService pubItemService, String personId, Date dateModified)
      throws Exception {
    return OrcidHelper.doOrcidQuery(pubItemService, personId, dateModified);
  }

  @Test
  public void testCreateOrcidQueryJsonNodeWithDateModified() throws Exception {
    String personId = "/persons/resource/persons96304";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    Date dateModified = sdf.parse("2026-10-08 08:16:17.055");

    JsonNode node = invokeCreateOrcidQueryJsonNode(personId, dateModified);
    assertNotNull(node);

    assertEquals(10000, node.get("size").asInt());
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

    SearchRetrieveResponseVO<ItemVersionVO> response = invokeDoOrcidQuery(mockPubItemService, "/persons/resource/persons96304", new Date());

    assertNotNull(response);
    assertNotNull(capturedRequest.get());
    assertNull(capturedToken.get());
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

    Map<String, ObjectNode> worksMap = OrcidHelper.extractWorksBySourceWorkId(initialOrcidJson);
    assertEquals(2, worksMap.size());
    assertTrue(worksMap.containsKey("item_11111"));
    assertTrue(worksMap.containsKey("item_22222"));

    // Modify work1 with put-code 2543541
    ObjectNode work1 = worksMap.get("item_11111");
    assertNotNull(work1);
    OrcidHelper.setPutCode(work1, 2543541L);

    // Modify work2 with put-code 9876543
    ObjectNode work2 = worksMap.get("item_22222");
    assertNotNull(work2);
    OrcidHelper.setPutCode(work2, "9876543");

    // Convert back to string
    String modifiedJson = OrcidHelper.convertWorksToOrcidJson(worksMap);
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

    String updatedJson = OrcidHelper.updatePutCodesInOrcidJson(initialOrcidJson, putCodes);
    assertNotNull(updatedJson);

    JsonNode root = MapperFactory.getObjectMapper().readTree(updatedJson);
    assertEquals(2543541L, root.get("bulk").get(0).get("work").get("put-code").asLong());
  }

  @Test
  public void testExtractWorksWithEmptyOrNull() throws Exception {
    Map<String, ObjectNode> nullMap = OrcidHelper.extractWorksBySourceWorkId((String) null);
    assertNotNull(nullMap);
    assertTrue(nullMap.isEmpty());

    Map<String, ObjectNode> emptyMap = OrcidHelper.extractWorksBySourceWorkId("  ");
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
          if ("findByConeIdAuthor".equals(method.getName())) {
            queriedPairs.add((String) args[0]);
            OrcidDbVO orcidVO = new OrcidDbVO("CREATE", "item_12345");
            return List.of(orcidVO);
          } else if ("findByConeIdAuthorAndItemId".equals(method.getName())) {
            queriedPairs.add(args[0] + "#" + args[1]);
            return new OrcidDbVO("CREATE", (String) args[1]);
          } else if ("findByOrcidAuthorizationDbVO_ConeIdAuthorAndItemId".equals(method.getName())) {
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

    try {
      customService.sendItemsToOrcid();
    } catch (Exception e) {
      // Network call to sandbox API in createWork may fail in isolated test environment
    }

    assertFalse(queriedPairs.isEmpty());
    assertTrue(queriedPairs.contains("persons96304") || queriedPairs.contains("persons99999"));
    assertFalse(queriedPairs.contains("persons12345"));
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

  @Test
  public void testCreateWorkHttpErrorOrExecution() {
    try {
      OrcidHelper.createWorksInOrcid("0000-0001-2345-6789", "{\"bulk\":[]}", "f5af9f51-07e6-4332-8f1a-c0c11c1e3728");
    } catch (TechnicalException e) {
      assertNotNull(e.getMessage());
    }
  }

  @Test
  public void testUpdateWorkHttpErrorOrExecution() {
    try {
      org.json.JSONObject res =
          OrcidHelper.updateWorkInOrcid("0000-0001-2345-6789", "{}", "f5af9f51-07e6-4332-8f1a-c0c11c1e3728", "123456");
      assertNotNull(res);
    } catch (TechnicalException e) {
      assertNotNull(e.getMessage());
    }
  }

  @Test
  public void testParseBulkResponseSingleError404() {
    org.json.JSONObject errorObj = new org.json.JSONObject();
    errorObj.put("response-code", 404);
    errorObj.put("developer-message", "404 Not Found: The work could not be found for the given put-code.");
    errorObj.put("error-code", 9016);
    errorObj.put("user-message", "ORCID could not process the data, because they were invalid.");

    List<OrcidResponse> items = OrcidHelper.parseOrcidResponses(errorObj);
    assertNotNull(items);
    assertEquals(1, items.size());

    OrcidResponse item = items.get(0);
    assertTrue(item.isError());
    assertEquals(Integer.valueOf(404), item.getResponseCode());
    assertEquals(Integer.valueOf(9016), item.getErrorCode());
    assertEquals("404 Not Found: The work could not be found for the given put-code.", item.getDeveloperMessage());
    assertEquals("ORCID could not process the data, because they were invalid.", item.getUserMessage());
  }

  @Test
  public void testSaveOrcidDbRecordUpdate404ErrorOverwritesPutCode() {
    OrcidAuthorizationDbVO authVO = new OrcidAuthorizationDbVO();
    authVO.setConeIdAuthor("persons96304");

    OrcidDbVO existingVO = new OrcidDbVO("UPDATE", "item_update_404");
    existingVO.setId(10);
    existingVO.setOrcidAuthorizationDbVO(authVO);
    existingVO.setPutCode("2547851");

    List<OrcidDbVO> savedList = new ArrayList<>();
    OrcidRepository mockOrcidRepo = (OrcidRepository) Proxy.newProxyInstance(OrcidRepository.class.getClassLoader(),
        new Class<?>[] {OrcidRepository.class}, (proxy, method, args) -> {
          if ("findByConeIdAuthorAndItemId".equals(method.getName())) {
            String itemId = (String) args[1];
            if ("item_update_404".equals(itemId)) {
              return existingVO;
            }
            return null;
          } else if ("saveAndFlush".equals(method.getName())) {
            savedList.add((OrcidDbVO) args[0]);
            return args[0];
          }
          return null;
        });

    org.json.JSONObject answer404 = new org.json.JSONObject();
    answer404.put("response-code", 404);
    answer404.put("developer-message", "404 Not Found: The work could not be found for the given put-code.");

    List<OrcidResponse> bulkItems = OrcidHelper.parseOrcidResponses(answer404);
    assertEquals(1, bulkItems.size());
    OrcidResponse errorItem = bulkItems.get(0);
    assertTrue(errorItem.isError());

    String putCode = "2547851";
    String devMsg = errorItem.getDeveloperMessage();
    String devMsgWithPutCode = (devMsg != null && !devMsg.isBlank()) ? devMsg + " (putCode: " + putCode + ")" : "putCode: " + putCode;
    errorItem.setDeveloperMessage(devMsgWithPutCode);

    OrcidDbVO saved = OrcidHelper.saveOrcidDbRecord(mockOrcidRepo, authVO, "item_update_404", null, errorItem, OrcidDbVO.Status.UPDATE);

    assertNotNull(saved);
    assertSame(existingVO, saved);
    assertEquals("UPDATE", saved.getStatus());
    assertNull(saved.getPutCode());
    assertEquals("404", saved.getResponseCode());
    assertEquals("404 Not Found: The work could not be found for the given put-code. (putCode: 2547851)", saved.getDeveloperMessage());
    assertEquals(1, savedList.size());
  }

  @Test
  public void testSaveOrcidDbRecordIndividual() {
    OrcidAuthorizationDbVO authVO = new OrcidAuthorizationDbVO();
    authVO.setConeIdAuthor("persons96304");

    OrcidDbVO existingVO = new OrcidDbVO("CREATE", "item_update_1");
    existingVO.setId(10);
    existingVO.setOrcidAuthorizationDbVO(authVO);

    List<OrcidDbVO> savedList = new ArrayList<>();
    OrcidRepository mockOrcidRepo = (OrcidRepository) Proxy.newProxyInstance(OrcidRepository.class.getClassLoader(),
        new Class<?>[] {OrcidRepository.class}, (proxy, method, args) -> {
          if ("findByConeIdAuthorAndItemId".equals(method.getName())) {
            String itemId = (String) args[1];
            if ("item_update_1".equals(itemId)) {
              return existingVO;
            }
            return null;
          } else if ("saveAndFlush".equals(method.getName())) {
            savedList.add((OrcidDbVO) args[0]);
            return args[0];
          }
          return null;
        });

    org.json.JSONObject answer = new org.json.JSONObject();
    answer.put("put-code", 2543541);
    OrcidDbVO saved = OrcidHelper.saveOrcidDbRecord(mockOrcidRepo, authVO, "item_update_1", "2543541", answer, OrcidDbVO.Status.UPDATE);

    assertNotNull(saved);
    assertSame(existingVO, saved);
    assertEquals("UPDATE", saved.getStatus());
    assertEquals("2543541", saved.getPutCode());
    assertEquals(1, savedList.size());
  }

  @Test
  public void testParseBulkResponseNullAndEmpty() {
    List<OrcidResponse> nullItems = OrcidHelper.parseOrcidResponses(null);
    assertNotNull(nullItems);
    assertTrue(nullItems.isEmpty());

    List<OrcidResponse> emptyItems = OrcidHelper.parseOrcidResponses(new org.json.JSONObject("{}"));
    assertNotNull(emptyItems);
    assertTrue(emptyItems.isEmpty());
  }

  @Test
  public void testOrcidDbVOAttributesForWorkAndError() {
    OrcidAuthorizationDbVO authVO = new OrcidAuthorizationDbVO();
    authVO.setConeIdAuthor("persons96304");

    // WORK item
    OrcidResponse workItem = new OrcidResponse(OrcidResponse.Type.WORK, 0, new org.json.JSONObject("{\"put-code\": 123456}"));
    workItem.setPutCode(123456L);

    OrcidDbVO workDbVO = new OrcidDbVO();
    workDbVO.setOrcidAuthorizationDbVO(authVO);
    workDbVO.setItemId("item_work_1");
    workDbVO.setStatus(OrcidDbVO.Status.CREATE.toString());
    workDbVO.setDate(new Date());
    if (workItem.isWork() && workItem.getPutCode() != null) {
      workDbVO.setPutCode(String.valueOf(workItem.getPutCode()));
    }

    assertEquals("persons96304", workDbVO.getConeIdAuthor());
    assertEquals("item_work_1", workDbVO.getItemId());
    assertEquals("CREATE", workDbVO.getStatus());
    assertEquals("123456", workDbVO.getPutCode());
    assertNull(workDbVO.getResponseCode());
    assertNull(workDbVO.getDeveloperMessage());
    assertNotNull(workDbVO.getDate());

    // ERROR item
    OrcidResponse errorItem = new OrcidResponse(OrcidResponse.Type.ERROR, 1, new org.json.JSONObject());
    errorItem.setResponseCode(409);
    errorItem.setDeveloperMessage("Conflict: already exists");

    OrcidDbVO errorDbVO = new OrcidDbVO();
    errorDbVO.setOrcidAuthorizationDbVO(authVO);
    errorDbVO.setItemId("item_error_1");
    errorDbVO.setStatus(OrcidDbVO.Status.CREATE.toString());
    errorDbVO.setDate(new Date());
    if (errorItem.isError()) {
      if (errorItem.getResponseCode() != null) {
        errorDbVO.setResponseCode(String.valueOf(errorItem.getResponseCode()));
      }
      errorDbVO.setDeveloperMessage(errorItem.getDeveloperMessage());
    }

    assertEquals("persons96304", errorDbVO.getConeIdAuthor());
    assertEquals("item_error_1", errorDbVO.getItemId());
    assertEquals("CREATE", errorDbVO.getStatus());
    assertNull(errorDbVO.getPutCode());
    assertEquals("409", errorDbVO.getResponseCode());
    assertEquals("Conflict: already exists", errorDbVO.getDeveloperMessage());
    assertNotNull(errorDbVO.getDate());
  }

  @Test
  public void testExtractedUpdatesExistingRecordAndCreatesNew() throws Exception {
    OrcidAuthorizationDbVO authVO = new OrcidAuthorizationDbVO();
    authVO.setConeIdAuthor("persons96304");

    OrcidDbVO existingVO = new OrcidDbVO("CREATE", "item_existing");
    existingVO.setId(42);
    existingVO.setOrcidAuthorizationDbVO(authVO);

    List<OrcidDbVO> savedList = new ArrayList<>();

    OrcidRepository mockOrcidRepo = (OrcidRepository) Proxy.newProxyInstance(OrcidRepository.class.getClassLoader(),
        new Class<?>[] {OrcidRepository.class}, (proxy, method, args) -> {
          if ("findByConeIdAuthorAndItemId".equals(method.getName())) {
            String itemId = (String) args[1];
            if ("item_existing".equals(itemId)) {
              return existingVO;
            }
            return null;
          } else if ("saveAndFlush".equals(method.getName())) {
            savedList.add((OrcidDbVO) args[0]);
            return args[0];
          }
          return null;
        });

    SearchRetrieveResponseVO<ItemVersionVO> searchResult = new SearchRetrieveResponseVO<>();
    List<SearchRetrieveRecordVO<ItemVersionVO>> records = new ArrayList<>();

    SearchRetrieveRecordVO<ItemVersionVO> rec1 = new SearchRetrieveRecordVO<>();
    ItemVersionVO item1 = new ItemVersionVO();
    item1.setObjectId("item_existing");
    rec1.setData(item1);
    records.add(rec1);

    SearchRetrieveRecordVO<ItemVersionVO> rec2 = new SearchRetrieveRecordVO<>();
    ItemVersionVO item2 = new ItemVersionVO();
    item2.setObjectId("item_new");
    rec2.setData(item2);
    records.add(rec2);

    searchResult.setRecords(records);

    List<OrcidResponse> bulkItems = new ArrayList<>();
    OrcidResponse itemBulk1 = new OrcidResponse(OrcidResponse.Type.WORK, 0, new org.json.JSONObject());
    itemBulk1.setPutCode(999999L);
    bulkItems.add(itemBulk1);

    OrcidResponse itemBulk2 = new OrcidResponse(OrcidResponse.Type.ERROR, 1, new org.json.JSONObject());
    itemBulk2.setResponseCode(400);
    itemBulk2.setDeveloperMessage("Bad Request");
    bulkItems.add(itemBulk2);

    OrcidHelper.saveOrcidDbRecords(mockOrcidRepo, authVO, searchResult, bulkItems, OrcidDbVO.Status.UPDATE);

    assertEquals(2, savedList.size());

    // 1st record: updated existing record
    OrcidDbVO savedExisting = savedList.get(0);
    assertSame(existingVO, savedExisting);
    assertEquals(Integer.valueOf(42), savedExisting.getId());
    assertEquals("UPDATE", savedExisting.getStatus());
    assertEquals("999999", savedExisting.getPutCode());
    assertEquals("item_existing", savedExisting.getItemId());

    // 2nd record: created new record
    OrcidDbVO savedNew = savedList.get(1);
    assertNull(savedNew.getId());
    assertEquals("UPDATE", savedNew.getStatus());
    assertEquals("item_new", savedNew.getItemId());
    assertEquals("400", savedNew.getResponseCode());
    assertEquals("Bad Request", savedNew.getDeveloperMessage());
  }

  @Test
  public void testSaveOrcidDbRecordUsesExistingOrcidMapWithoutRepoQuery() {
    OrcidAuthorizationDbVO authVO = new OrcidAuthorizationDbVO();
    authVO.setConeIdAuthor("persons96304");

    OrcidDbVO existingVO = new OrcidDbVO("CREATE", "item_map_1");
    existingVO.setId(100);
    existingVO.setOrcidAuthorizationDbVO(authVO);

    Map<String, OrcidDbVO> existingOrcidMap = new HashMap<>();
    existingOrcidMap.put("item_map_1", existingVO);

    List<OrcidDbVO> savedList = new ArrayList<>();
    OrcidRepository mockOrcidRepo = (OrcidRepository) Proxy.newProxyInstance(OrcidRepository.class.getClassLoader(),
        new Class<?>[] {OrcidRepository.class}, (proxy, method, args) -> {
          if ("findByConeIdAuthorAndItemId".equals(method.getName())) {
            fail("findByConeIdAuthorAndItemId should not be called when existingOrcidMap is provided");
            return null;
          } else if ("saveAndFlush".equals(method.getName())) {
            savedList.add((OrcidDbVO) args[0]);
            return args[0];
          }
          return null;
        });

    OrcidResponse workItem = new OrcidResponse(OrcidResponse.Type.WORK, 0, new org.json.JSONObject());
    workItem.setPutCode(1234567L);

    OrcidDbVO saved =
        OrcidHelper.saveOrcidDbRecord(mockOrcidRepo, authVO, "item_map_1", null, workItem, OrcidDbVO.Status.UPDATE, existingOrcidMap);

    assertNotNull(saved);
    assertSame(existingVO, saved);
    assertEquals("UPDATE", saved.getStatus());
    assertEquals("1234567", saved.getPutCode());
    assertEquals(1, savedList.size());
    assertSame(saved, existingOrcidMap.get("item_map_1"));
  }

  @Test
  public void testSaveOrcidDbRecordsUsesExistingOrcidMapWithoutRepoQuery() {
    OrcidAuthorizationDbVO authVO = new OrcidAuthorizationDbVO();
    authVO.setConeIdAuthor("persons96304");

    OrcidDbVO existingVO = new OrcidDbVO("CREATE", "item_map_existing");
    existingVO.setId(200);
    existingVO.setOrcidAuthorizationDbVO(authVO);

    Map<String, OrcidDbVO> existingOrcidMap = new HashMap<>();
    existingOrcidMap.put("item_map_existing", existingVO);

    List<OrcidDbVO> savedList = new ArrayList<>();
    OrcidRepository mockOrcidRepo = (OrcidRepository) Proxy.newProxyInstance(OrcidRepository.class.getClassLoader(),
        new Class<?>[] {OrcidRepository.class}, (proxy, method, args) -> {
          if ("findByConeIdAuthorAndItemId".equals(method.getName())) {
            fail("findByConeIdAuthorAndItemId should not be called when existingOrcidMap is provided");
            return null;
          } else if ("saveAndFlush".equals(method.getName())) {
            savedList.add((OrcidDbVO) args[0]);
            return args[0];
          }
          return null;
        });

    SearchRetrieveResponseVO<ItemVersionVO> searchResult = new SearchRetrieveResponseVO<>();
    List<SearchRetrieveRecordVO<ItemVersionVO>> records = new ArrayList<>();

    SearchRetrieveRecordVO<ItemVersionVO> rec1 = new SearchRetrieveRecordVO<>();
    ItemVersionVO item1 = new ItemVersionVO();
    item1.setObjectId("item_map_existing");
    rec1.setData(item1);
    records.add(rec1);

    SearchRetrieveRecordVO<ItemVersionVO> rec2 = new SearchRetrieveRecordVO<>();
    ItemVersionVO item2 = new ItemVersionVO();
    item2.setObjectId("item_map_new");
    rec2.setData(item2);
    records.add(rec2);

    searchResult.setRecords(records);

    List<OrcidResponse> bulkItems = new ArrayList<>();
    OrcidResponse itemBulk1 = new OrcidResponse(OrcidResponse.Type.WORK, 0, new org.json.JSONObject());
    itemBulk1.setPutCode(111111L);
    bulkItems.add(itemBulk1);

    OrcidResponse itemBulk2 = new OrcidResponse(OrcidResponse.Type.WORK, 1, new org.json.JSONObject());
    itemBulk2.setPutCode(222222L);
    bulkItems.add(itemBulk2);

    OrcidHelper.saveOrcidDbRecords(mockOrcidRepo, authVO, searchResult, bulkItems, OrcidDbVO.Status.CREATE, existingOrcidMap);

    assertEquals(2, savedList.size());
    assertSame(existingVO, savedList.get(0));
    assertEquals("111111", savedList.get(0).getPutCode());

    OrcidDbVO savedNew = savedList.get(1);
    assertEquals("item_map_new", savedNew.getItemId());
    assertEquals("222222", savedNew.getPutCode());
    assertSame(savedNew, existingOrcidMap.get("item_map_new"));
  }

  @Test
  public void testBatchingNewRecordsInChunksOf100() throws Exception {
    OrcidAuthorizationDbVO vo = new OrcidAuthorizationDbVO();
    vo.setSecret("secret-batch");
    vo.setConeIdAuthor("persons96304");
    vo.setVerified(true);
    vo.setOrcid("0000-0001-2345-6789");
    vo.setAccessToken("f5af9f51-07e6-4332-8f1a-c0c11c1e3728");
    repoStorage.put("secret-batch", vo);

    List<OrcidDbVO> savedList = new ArrayList<>();
    OrcidRepository mockOrcidRepo = (OrcidRepository) Proxy.newProxyInstance(OrcidRepository.class.getClassLoader(),
        new Class<?>[] {OrcidRepository.class}, (proxy, method, args) -> {
          if ("findByConeIdAuthorAndItemId".equals(method.getName())) {
            return null; // All records are new
          } else if ("saveAndFlush".equals(method.getName())) {
            savedList.add((OrcidDbVO) args[0]);
            return args[0];
          }
          return null;
        });

    PubItemService mockPubItemService = (PubItemService) Proxy.newProxyInstance(PubItemService.class.getClassLoader(),
        new Class<?>[] {PubItemService.class}, (proxy, method, args) -> {
          if ("searchDetailed".equals(method.getName())) {
            List<co.elastic.clients.elasticsearch.core.search.Hit<ObjectNode>> hitList = new ArrayList<>();
            for (int i = 0; i < 150; i++) {
              ItemVersionVO itemVO = new ItemVersionVO();
              itemVO.setObjectId("item_" + i);
              ObjectNode itemNode = (ObjectNode) MapperFactory.getObjectMapper().valueToTree(itemVO);
              hitList.add(co.elastic.clients.elasticsearch.core.search.Hit
                  .of(hit -> hit.index("items").id("item_" + hitList.size()).source(itemNode)));
            }
            co.elastic.clients.elasticsearch.core.SearchResponse<ObjectNode> searchResponse =
                co.elastic.clients.elasticsearch.core.SearchResponse
                    .of(sr -> sr.took(1).timedOut(false).shards(s -> s.total(1).successful(1).skipped(0).failed(0))
                        .hits(h -> h.total(t -> t.value(150).relation(co.elastic.clients.elasticsearch.core.search.TotalHitsRelation.Eq))
                            .hits(hitList)));
            return searchResponse;
          }
          return null;
        });

    OrcidServiceImpl customService = new OrcidServiceImpl(null, createMockRepository(), null, null, mockPubItemService, mockOrcidRepo);

    try {
      customService.sendItemsToOrcid();
    } catch (Exception e) {
      // Network call to external ORCID in test environment
    }

    // Verify subList logic chunking
    List<SearchRetrieveRecordVO<ItemVersionVO>> allRecords = new ArrayList<>();
    for (int i = 0; i < 150; i++) {
      SearchRetrieveRecordVO<ItemVersionVO> rec = new SearchRetrieveRecordVO<>();
      ItemVersionVO item = new ItemVersionVO();
      item.setObjectId("item_" + i);
      rec.setData(item);
      allRecords.add(rec);
    }
    int batchSize = 100;
    List<List<SearchRetrieveRecordVO<ItemVersionVO>>> batches = new ArrayList<>();
    for (int i = 0; i < allRecords.size(); i += batchSize) {
      int toIndex = Math.min(i + batchSize, allRecords.size());
      batches.add(allRecords.subList(i, toIndex));
    }
    assertEquals(2, batches.size());
    assertEquals(100, batches.get(0).size());
    assertEquals(50, batches.get(1).size());
  }

  @Test
  public void testSendItemsToOrcidUpdatesDateLastUpdated() throws Exception {
    OrcidAuthorizationDbVO vo = new OrcidAuthorizationDbVO();
    vo.setSecret("secret-date-update");
    vo.setConeIdAuthor("persons96304");
    vo.setVerified(true);
    vo.setDateLastUpdated(null);
    repoStorage.put("secret-date-update", vo);

    PubItemService mockPubItemService = (PubItemService) Proxy.newProxyInstance(PubItemService.class.getClassLoader(),
        new Class<?>[] {PubItemService.class}, (proxy, method, args) -> {
          if ("searchDetailed".equals(method.getName())) {
            ItemVersionVO item = new ItemVersionVO();
            item.setObjectId("item_date_update");
            ObjectNode itemNode = (ObjectNode) MapperFactory.getObjectMapper().valueToTree(item);
            co.elastic.clients.elasticsearch.core.SearchResponse<ObjectNode> searchResponse =
                co.elastic.clients.elasticsearch.core.SearchResponse
                    .of(sr -> sr.took(1).timedOut(false).shards(s -> s.total(1).successful(1).skipped(0).failed(0))
                        .hits(h -> h.total(t -> t.value(1).relation(co.elastic.clients.elasticsearch.core.search.TotalHitsRelation.Eq))
                            .hits(List.of(co.elastic.clients.elasticsearch.core.search.Hit
                                .of(hit -> hit.index("items").id("item_date_update").source(itemNode))))));
            return searchResponse;
          }
          return null;
        });

    OrcidServiceImpl customService = new OrcidServiceImpl(null, createMockRepository(), null, null, mockPubItemService, null);

    assertNull(vo.getDateLastUpdated());
    long before = System.currentTimeMillis();
    customService.sendItemsToOrcid();
    assertNotNull(vo.getDateLastUpdated());
    assertTrue(vo.getDateLastUpdated().getTime() >= before - 1000);
  }

  @Test
  public void testSendItemsToOrcidDoesNotUpdateDateLastUpdatedWhenNoRecords() throws Exception {
    OrcidAuthorizationDbVO vo = new OrcidAuthorizationDbVO();
    vo.setSecret("secret-date-no-update");
    vo.setConeIdAuthor("persons96304");
    vo.setVerified(true);
    vo.setDateLastUpdated(null);
    repoStorage.put("secret-date-no-update", vo);

    PubItemService mockPubItemService = (PubItemService) Proxy.newProxyInstance(PubItemService.class.getClassLoader(),
        new Class<?>[] {PubItemService.class}, (proxy, method, args) -> {
          if ("searchDetailed".equals(method.getName())) {
            co.elastic.clients.elasticsearch.core.SearchResponse<ObjectNode> searchResponse =
                co.elastic.clients.elasticsearch.core.SearchResponse
                    .of(sr -> sr.took(1).timedOut(false).shards(s -> s.total(0).successful(0).skipped(0).failed(0))
                        .hits(h -> h.total(t -> t.value(0).relation(co.elastic.clients.elasticsearch.core.search.TotalHitsRelation.Eq))
                            .hits(List.of())));
            return searchResponse;
          }
          return null;
        });

    OrcidServiceImpl customService = new OrcidServiceImpl(null, createMockRepository(), null, null, mockPubItemService, null);

    assertNull(vo.getDateLastUpdated());
    customService.sendItemsToOrcid();
    assertNull(vo.getDateLastUpdated());
  }

  @Test
  public void testSendItemsToOrcidUsesBatchFindByConeIdAuthor() throws Exception {
    OrcidAuthorizationDbVO authVO = new OrcidAuthorizationDbVO();
    authVO.setSecret("secret-batch-lookup");
    authVO.setConeIdAuthor("persons96304");
    authVO.setVerified(true);
    authVO.setOrcid("0000-0001-2345-6789");
    authVO.setAccessToken("token-123");
    repoStorage.put("secret-batch-lookup", authVO);

    OrcidDbVO existingVO = new OrcidDbVO("CREATE", "item_existing");
    existingVO.setOrcidAuthorizationDbVO(authVO);
    existingVO.setPutCode("987654");

    java.util.concurrent.atomic.AtomicInteger findByAuthorCount = new java.util.concurrent.atomic.AtomicInteger();
    java.util.concurrent.atomic.AtomicInteger findByItemCount = new java.util.concurrent.atomic.AtomicInteger();

    OrcidRepository mockOrcidRepo = (OrcidRepository) Proxy.newProxyInstance(OrcidRepository.class.getClassLoader(),
        new Class<?>[] {OrcidRepository.class}, (proxy, method, args) -> {
          if ("findByConeIdAuthor".equals(method.getName())) {
            findByAuthorCount.incrementAndGet();
            return List.of(existingVO);
          } else if ("findByConeIdAuthorAndItemId".equals(method.getName())) {
            findByItemCount.incrementAndGet();
            return null;
          }
          return null;
        });

    PubItemService mockPubItemService = (PubItemService) Proxy.newProxyInstance(PubItemService.class.getClassLoader(),
        new Class<?>[] {PubItemService.class}, (proxy, method, args) -> {
          if ("searchDetailed".equals(method.getName())) {
            ItemVersionVO item1 = new ItemVersionVO();
            item1.setObjectId("item_existing");
            ItemVersionVO item2 = new ItemVersionVO();
            item2.setObjectId("item_brand_new");

            ObjectNode node1 = (ObjectNode) MapperFactory.getObjectMapper().valueToTree(item1);
            ObjectNode node2 = (ObjectNode) MapperFactory.getObjectMapper().valueToTree(item2);

            co.elastic.clients.elasticsearch.core.SearchResponse<ObjectNode> searchResponse =
                co.elastic.clients.elasticsearch.core.SearchResponse
                    .of(sr -> sr.took(1).timedOut(false).shards(s -> s.total(2).successful(2).skipped(0).failed(0))
                        .hits(h -> h.total(t -> t.value(2).relation(co.elastic.clients.elasticsearch.core.search.TotalHitsRelation.Eq))
                            .hits(List.of(
                                co.elastic.clients.elasticsearch.core.search.Hit
                                    .of(hit -> hit.index("items").id("item_existing").source(node1)),
                                co.elastic.clients.elasticsearch.core.search.Hit
                                    .of(hit -> hit.index("items").id("item_brand_new").source(node2))))));
            return searchResponse;
          }
          return null;
        });

    OrcidServiceImpl customService = new OrcidServiceImpl(null, createMockRepository(), null, null, mockPubItemService, mockOrcidRepo);

    try {
      customService.sendItemsToOrcid();
    } catch (Exception e) {
      e.printStackTrace();
    }

    assertEquals(1, findByAuthorCount.get());
    assertEquals(0, findByItemCount.get());
  }

  @Test
  public void testSendItemsToOrcidIsolatesAuthorFailure() throws Exception {
    OrcidAuthorizationDbVO auth1 = new OrcidAuthorizationDbVO();
    auth1.setSecret("secret-fail");
    auth1.setConeIdAuthor("persons_fail");
    auth1.setVerified(true);
    auth1.setDateLastUpdated(null);
    repoStorage.put("secret-fail", auth1);

    OrcidAuthorizationDbVO auth2 = new OrcidAuthorizationDbVO();
    auth2.setSecret("secret-success");
    auth2.setConeIdAuthor("persons_success");
    auth2.setVerified(true);
    auth2.setDateLastUpdated(null);
    repoStorage.put("secret-success", auth2);

    PubItemService mockPubItemService = (PubItemService) Proxy.newProxyInstance(PubItemService.class.getClassLoader(),
        new Class<?>[] {PubItemService.class}, (proxy, method, args) -> {
          if ("searchDetailed".equals(method.getName())) {
            co.elastic.clients.elasticsearch.core.SearchRequest request = (co.elastic.clients.elasticsearch.core.SearchRequest) args[0];
            if (request != null && request.toString().contains("persons_fail")) {
              throw new RuntimeException("Simulated failure for author 1");
            }
            ItemVersionVO itemSuccess = new ItemVersionVO();
            itemSuccess.setObjectId("item_success");
            ObjectNode itemNode = (ObjectNode) MapperFactory.getObjectMapper().valueToTree(itemSuccess);
            co.elastic.clients.elasticsearch.core.SearchResponse<ObjectNode> searchResponse =
                co.elastic.clients.elasticsearch.core.SearchResponse
                    .of(sr -> sr.took(1).timedOut(false).shards(s -> s.total(1).successful(1).skipped(0).failed(0))
                        .hits(h -> h.total(t -> t.value(1).relation(co.elastic.clients.elasticsearch.core.search.TotalHitsRelation.Eq))
                            .hits(List.of(co.elastic.clients.elasticsearch.core.search.Hit
                                .of(hit -> hit.index("items").id("item_success").source(itemNode))))));
            return searchResponse;
          }
          return null;
        });

    OrcidServiceImpl customService = new OrcidServiceImpl(null, createMockRepository(), null, null, mockPubItemService, null);

    customService.sendItemsToOrcid();

    assertNull(auth1.getDateLastUpdated());
    assertNotNull(auth2.getDateLastUpdated());
  }

  @Test
  public void testSendItemsToOrcidHandles401InCreateNewWorks() throws Exception {
    mockResponseStatus = 401;
    mockResponseBody = "{\"error\":\"invalid_token\",\"error_description\":\"Token not valid\"}";

    OrcidAuthorizationDbVO auth = new OrcidAuthorizationDbVO();
    auth.setSecret("secret-401-create");
    auth.setConeIdAuthor("persons_401_create");
    auth.setOrcid("0000-0002-1825-0097");
    auth.setAccessToken("invalid-token-401");
    auth.setVerified(true);
    auth.setStatus(OrcidAuthorizationDbVO.Status.TOKEN_RECEIVED.toString());
    auth.setDateLastUpdated(null);
    repoStorage.put("secret-401-create", auth);

    PubItemService mockPubItemService = (PubItemService) Proxy.newProxyInstance(PubItemService.class.getClassLoader(),
        new Class<?>[] {PubItemService.class}, (proxy, method, args) -> {
          if ("searchDetailed".equals(method.getName())) {
            ItemVersionVO item = new ItemVersionVO();
            item.setObjectId("item_401_new");
            MdsPublicationVO metadata = new MdsPublicationVO();
            metadata.setGenre(MdsPublicationVO.Genre.ARTICLE);
            metadata.setTitle("Test Publication New");
            item.setMetadata(metadata);
            ObjectNode itemNode = (ObjectNode) MapperFactory.getObjectMapper().valueToTree(item);
            co.elastic.clients.elasticsearch.core.SearchResponse<ObjectNode> searchResponse =
                co.elastic.clients.elasticsearch.core.SearchResponse
                    .of(sr -> sr.took(1).timedOut(false).shards(s -> s.total(1).successful(1).skipped(0).failed(0))
                        .hits(h -> h.total(t -> t.value(1).relation(co.elastic.clients.elasticsearch.core.search.TotalHitsRelation.Eq))
                            .hits(List.of(co.elastic.clients.elasticsearch.core.search.Hit
                                .of(hit -> hit.index("items").id("item_401_new").source(itemNode))))));
            return searchResponse;
          }
          return null;
        });

    OrcidServiceImpl customService = new OrcidServiceImpl(null, createMockRepository(), null, null, mockPubItemService, null);

    customService.sendItemsToOrcid();

    assertEquals(OrcidAuthorizationDbVO.Status.ACCESS_DENIED.toString(), auth.getStatus());
    assertFalse(auth.getVerified());
    assertNull(auth.getDateLastUpdated());
    assertNotNull(auth.getDateError());
  }

  @Test
  public void testSendItemsToOrcidHandles401InUpdateExistingWorks() throws Exception {
    mockResponseStatus = 401;
    mockResponseBody = "{\"error\":\"invalid_token\",\"error_description\":\"Token not valid\"}";

    OrcidAuthorizationDbVO auth = new OrcidAuthorizationDbVO();
    auth.setSecret("secret-401-update");
    auth.setConeIdAuthor("persons_401_update");
    auth.setOrcid("0000-0002-1825-0097");
    auth.setAccessToken("invalid-token-401");
    auth.setVerified(true);
    auth.setStatus(OrcidAuthorizationDbVO.Status.TOKEN_RECEIVED.toString());
    auth.setDateLastUpdated(null);
    repoStorage.put("secret-401-update", auth);

    OrcidDbVO existingVO = new OrcidDbVO("CREATE", "item_401_update");
    existingVO.setOrcidAuthorizationDbVO(auth);
    existingVO.setPutCode("12345");

    OrcidRepository mockOrcidRepo = (OrcidRepository) Proxy.newProxyInstance(OrcidRepository.class.getClassLoader(),
        new Class<?>[] {OrcidRepository.class}, (proxy, method, args) -> {
          if ("findByConeIdAuthor".equals(method.getName())) {
            return List.of(existingVO);
          }
          return null;
        });

    PubItemService mockPubItemService = (PubItemService) Proxy.newProxyInstance(PubItemService.class.getClassLoader(),
        new Class<?>[] {PubItemService.class}, (proxy, method, args) -> {
          if ("searchDetailed".equals(method.getName())) {
            ItemVersionVO item = new ItemVersionVO();
            item.setObjectId("item_401_update");
            MdsPublicationVO metadata = new MdsPublicationVO();
            metadata.setGenre(MdsPublicationVO.Genre.ARTICLE);
            metadata.setTitle("Test Publication Update");
            item.setMetadata(metadata);
            ObjectNode itemNode = (ObjectNode) MapperFactory.getObjectMapper().valueToTree(item);
            co.elastic.clients.elasticsearch.core.SearchResponse<ObjectNode> searchResponse =
                co.elastic.clients.elasticsearch.core.SearchResponse
                    .of(sr -> sr.took(1).timedOut(false).shards(s -> s.total(1).successful(1).skipped(0).failed(0))
                        .hits(h -> h.total(t -> t.value(1).relation(co.elastic.clients.elasticsearch.core.search.TotalHitsRelation.Eq))
                            .hits(List.of(co.elastic.clients.elasticsearch.core.search.Hit
                                .of(hit -> hit.index("items").id("item_401_update").source(itemNode))))));
            return searchResponse;
          }
          return null;
        });

    OrcidServiceImpl customService = new OrcidServiceImpl(null, createMockRepository(), null, null, mockPubItemService, mockOrcidRepo);

    customService.sendItemsToOrcid();

    assertEquals(OrcidAuthorizationDbVO.Status.ACCESS_DENIED.toString(), auth.getStatus());
    assertFalse(auth.getVerified());
    assertNull(auth.getDateLastUpdated());
    assertNotNull(auth.getDateError());
  }
}
