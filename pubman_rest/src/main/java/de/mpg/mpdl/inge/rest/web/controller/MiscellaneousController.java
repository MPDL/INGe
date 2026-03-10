package de.mpg.mpdl.inge.rest.web.controller;

import de.mpg.mpdl.inge.cone_cache.ConeCache;
import de.mpg.mpdl.inge.es.dao.impl.ContextDaoImpl;
import de.mpg.mpdl.inge.es.dao.impl.OrganizationDaoImpl;
import de.mpg.mpdl.inge.es.dao.impl.PubItemDaoImpl;
import de.mpg.mpdl.inge.es.dao.impl.UserAccountDaoImpl;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.rest.web.exceptions.NotFoundException;
import de.mpg.mpdl.inge.rest.web.spring.AuthCookieToHeaderFilter;
import de.mpg.mpdl.inge.rest.web.util.UtilServiceBean;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.aa.IpListProvider;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.service.pubman.FileService;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;
import de.mpg.mpdl.inge.service.util.GenrePropertiesProvider;
import de.mpg.mpdl.inge.util.PropertyReader;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/miscellaneous")
@Tag(name = "Miscellaneous")
public class MiscellaneousController {

  private static final Logger logger = LogManager.getLogger(MiscellaneousController.class);

  private static final String GENRE = "genre";

  private static final String SITEMAP_PATH = System.getProperty(PropertyReader.JBOSS_HOME_DIR) + "/standalone/data/sitemap/";
  private static final String SITEMAP_FILE_PATH = "/{sitemapFile:.+}";
  private static final String Sitemap_VAR = "sitemapFile";

  private static final String OPENAI_URL = PropertyReader.getProperty(PropertyReader.INGE_OPENAI_URL);
  private static final String OPENAI_MODEL = PropertyReader.getProperty(PropertyReader.INGE_OPENAI_MODEL);
  private static final String OPENAI_PROMPT = PropertyReader.getProperty(PropertyReader.INGE_OPENAI_PROMPT);
  private static final String OPENAI_TOKEN = PropertyReader.getProperty(PropertyReader.INGE_OPENAI_TOKEN);
  private static final Integer OPENAI_TEMPERATURE = Integer.valueOf(PropertyReader.getProperty(PropertyReader.INGE_OPENAI_TEMPERATURE));

  private final UtilServiceBean utilServiceBean;

  private final RestTemplate restTemplate;

  @Autowired
  @Qualifier("mpgJsonIpListProvider")
  private IpListProvider ipListProvider;

  @Autowired
  private FileService fileService;

  @Autowired
  private PubItemService pubItemService;

  @Autowired
  private ContextService contextService;

  @Autowired
  private OrganizationService ouService;

  @Autowired
  private UserAccountService userAccountService;

  @Autowired
  private AuthorizationService aaService;

  public MiscellaneousController(UtilServiceBean utilServiceBean, RestTemplate restTemplate) {
    this.utilServiceBean = utilServiceBean;
    this.restTemplate = restTemplate;
  }

  @RequestMapping(value = "/callAiApi", method = RequestMethod.POST)
  public ResponseEntity<String> callAiApi( //
      @RequestHeader(AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestBody String data) //
      throws AuthenticationException, IngeApplicationException {

    this.utilServiceBean.checkUser(token);
    UtilServiceBean.checkData(data);

    logger.info("Calling Ai API");
    Request preparedRequest = prepareRequest(data);
    logger.info("RequestBody: " + preparedRequest.requestBody());

    HttpEntity<String> entity = new HttpEntity<>(preparedRequest.requestBody(), preparedRequest.headers());

    LocalDateTime start = LocalDateTime.now();
    ResponseEntity<String> response = restTemplate.exchange(preparedRequest.url, HttpMethod.POST, entity, String.class);
    LocalDateTime end = LocalDateTime.now();
    Duration duration = Duration.between(start, end);
    logger.info("Dauer: " + duration.toMinutes() + " Minuten und " + duration.toSecondsPart() + " Sekunden.");

    String responseBody = response.getBody();
    logger.info("ResponseBody:" + responseBody);

    try {
      String authors = parseResult(responseBody, data);
      return new ResponseEntity<>(authors, HttpStatus.OK);
    } catch (Exception e) {
      throw new IngeApplicationException("Adding failed: please try again or contact your administrator");
    }
  }

  @RequestMapping(value = "/getGenreProperties", method = RequestMethod.GET)
  public ResponseEntity<String> getGenreProperties( //
      @RequestHeader(AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestParam(GENRE) MdsPublicationVO.Genre genre) //
      throws AuthenticationException, IngeApplicationException {

    this.utilServiceBean.checkUser(token);
    UtilServiceBean.checkData(genre);

    JSONObject json = GenrePropertiesProvider.getGenreProperties(genre);

    return new ResponseEntity<>(json.toString(), HttpStatus.OK);
  }

  @RequestMapping(value = "/getIpList", method = RequestMethod.GET)
  public ResponseEntity<Collection<IpListProvider.IpRange>> getIpList( //
      @RequestHeader(AuthCookieToHeaderFilter.AUTHZ_HEADER) String token) //
      throws AuthenticationException, IngeApplicationException {

    this.utilServiceBean.checkUser(token);
    Collection<IpListProvider.IpRange> ipList = this.ipListProvider.getAll();

    return new ResponseEntity<>(ipList, HttpStatus.OK);
  }

  @RequestMapping(value = SITEMAP_FILE_PATH, method = RequestMethod.GET)
  public ResponseEntity<Resource> getSitemap( //
      @PathVariable(Sitemap_VAR) String sitemapFile, //
      HttpServletResponse response) //
      throws NotFoundException, IngeTechnicalException {

    try {
      // Basispfad, in dem sich die XML-Dateien befinden
      Path basePath = Paths.get(SITEMAP_PATH);
      // Vollständiger Pfad zur angeforderten Datei
      Path filePath = basePath.resolve(sitemapFile);
      Resource resource = new UrlResource(filePath.toUri());

      // Stellen Sie sicher, dass die Datei existiert und lesbar ist
      if (resource.exists() || resource.isReadable()) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(resource);
      }
    } catch (Exception e) {
      throw new IngeTechnicalException(e);
    }

    throw new NotFoundException();
  }

  @RequestMapping(value = "/regenerateThumbnails", method = RequestMethod.GET)
  public ResponseEntity<?> regenerateThumbnails( //
      @RequestHeader(AuthCookieToHeaderFilter.AUTHZ_HEADER) String token) //
      throws AuthenticationException, IngeTechnicalException {

    this.fileService.regenerateThumbnails(token);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/reindex", method = RequestMethod.GET)
  public ResponseEntity<?> reindex(@RequestParam(name = "index", required = true) String index,
      @RequestParam(name = "id", required = false) String id, @RequestHeader(AuthCookieToHeaderFilter.AUTHZ_HEADER) String token) //
      throws AuthenticationException, IngeApplicationException, IngeTechnicalException {

    this.aaService.checkLoginRequiredWithRole(token, GrantVO.PredefinedRoles.SYSADMIN.frameworkValue());

    if (PubItemDaoImpl.indexName.equals(index)) {
      if (id == null || id.isEmpty()) {
        this.pubItemService.reindexAll(token);
      } else {
        this.pubItemService.reindex(id, token);
      }
    }

    else if (ContextDaoImpl.indexName.equals(index)) {
      if (id == null || id.isEmpty()) {
        this.contextService.reindexAll(token);
      } else {
        this.contextService.reindex(id, token);
      }
    } else if (OrganizationDaoImpl.indexName.equals(index)) {
      if (id == null || id.isEmpty()) {
        this.ouService.reindexAll(token);
      } else {
        this.ouService.reindex(id, token);
      }

    } else if (UserAccountDaoImpl.indexName.equals(index)) {
      if (id == null || id.isEmpty()) {
        this.userAccountService.reindexAll(token);
      } else {
        this.userAccountService.reindex(id, token);
      }

    } else if ("all".equals(index)) {
      this.pubItemService.reindexAll(token);
      this.contextService.reindexAll(token);
      this.ouService.reindexAll(token);
      this.userAccountService.reindexAll(token);
    } else {
      throw new IngeApplicationException("Index name " + index + " is unknown");
    }
    return new ResponseEntity<>("Index started for index " + index, HttpStatus.OK);
  }

  @RequestMapping(value = "/serviceInfo", method = RequestMethod.GET)
  public ResponseEntity<?> serviceInfo() throws IngeTechnicalException {
    String appVersion = null;
    try {
      InputStream is = getClass().getResourceAsStream("/META-INF/MANIFEST.MF");
      if (is != null) {
        java.util.jar.Manifest manifest = new java.util.jar.Manifest(is);
        appVersion = manifest.getMainAttributes().getValue("Implementation-Version");

        // Debug: log what we found in the manifest
        if (appVersion == null) {
          logger.info("Implementation-Version not found. Available attributes:");
          // manifest.getMainAttributes().forEach((k, v) -> logger.info(k + ": " + v));
          manifest.getEntries().forEach((entryName, attributes) -> {
            logger.info("Entry: " + entryName);
            attributes.forEach((k, v) -> logger.info("  " + k + ": " + v));
          });
        }
      } else {
        logger.warn("Manifest file not found at /META-INF/MANIFEST.MF");

        // Fallback: try to get version from package
        Package pkg = getClass().getPackage();
        if (pkg != null && pkg.getImplementationVersion() != null) {
          appVersion = pkg.getImplementationVersion();
          logger.info("Retrieved Implementation-Version from Package: " + appVersion);
        }
      }
    } catch (Exception e) {
      logger.error("Error reading manifest file for service info REST endpoint", e);
    }
    return new ResponseEntity<>(appVersion, HttpStatus.OK);
  }

  @RequestMapping(value = "/refreshConeCache", method = RequestMethod.PUT, produces = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<String> refreshConeCache(@RequestHeader(AuthCookieToHeaderFilter.AUTHZ_HEADER) String token)
      throws AuthenticationException, IngeTechnicalException {

    this.aaService.checkLoginRequiredWithRole(token, GrantVO.PredefinedRoles.SYSADMIN.frameworkValue());

    try {
      logger.info("REST: CONE-Cache refresh task starts...");
      ConeCache.refreshCache();
      String message = "REST: CONE-Cache refresh task finished.";
      logger.info(message);
      return new ResponseEntity<>(message, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Error in CONE Cache Refresh: ", e);
      throw new IngeTechnicalException("Error in CONE Cache Refresh", e);
    }
  }

  /// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  /// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private String parseResult(String responseBody, String stringToCheck) {
    logger.info("Parsing result from AI response:\n", responseBody);
    // Parse the JSON response
    JSONObject jsonObject = new JSONObject(responseBody);

    // Get the first choices[0].message.content as JSONObject
    JSONObject content = new JSONObject(jsonObject.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content"));

    // Get the authors array
    JSONArray authors = content.getJSONArray("authors");

    // Normalize the string once outside the loop
    String normalizedStringToCheck = stringToCheck.replace("\\r\\n", " ").replace("\\n", " ").replace("\\r", " ").replace("\\t", " ")
        .replace("\\f", " ").replace("\\v", " ").replaceAll("[\\r\\n\\t\\f\\v\\u00A0]+", " ").trim();
    // Remove any remaining control characters (ASCII 0-31 and 127)
    normalizedStringToCheck = normalizedStringToCheck.replaceAll("[\\x00-\\x1F\\x7F]", "");

    for (int i = authors.length() - 1; i >= 0; i--) {
      JSONObject author = authors.getJSONObject(i);
      logger.info("Checking Author [ " + i + "]: " + author.toString());

      String family = author.getString("family");

      // Simple check: if family name exists in the original string, keep it
      // This is more robust than trying to match exact patterns
      boolean familyExists = normalizedStringToCheck.toLowerCase().contains(family.toLowerCase());

      if (!familyExists) {
        authors.remove(i);
        logger.info("Hallucination-Author removed '" + author.getString("family") + ", " + author.getString("given") + "' successfully.");
      }
    }
    return authors.toString();
  }

  private Request prepareRequest(String data) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setBearerAuth(OPENAI_TOKEN);

    JSONObject systemRole = new JSONObject();
    systemRole.put("role", "system");
    systemRole.put("content", "You are a metadata expert");

    JSONObject userRole = new JSONObject();
    userRole.put("role", "user");
    userRole.put("content", OPENAI_PROMPT + " " + data);

    JSONArray messages = new JSONArray();
    messages.put(systemRole);
    messages.put(userRole);

    JSONObject json = new JSONObject();
    json.put("model", OPENAI_MODEL);
    json.put("messages", messages);
    json.put("temperature", OPENAI_TEMPERATURE);

    String requestBody = json.toString();

    Request request = new Request(OPENAI_URL, headers, requestBody);
    return request;
  }

  private record Request(String url, HttpHeaders headers, String requestBody) {}

}
