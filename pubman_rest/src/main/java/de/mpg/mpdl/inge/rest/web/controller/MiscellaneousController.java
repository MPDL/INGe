package de.mpg.mpdl.inge.rest.web.controller;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.rest.web.exceptions.NotFoundException;
import de.mpg.mpdl.inge.rest.web.spring.AuthCookieToHeaderFilter;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.aa.IpListProvider;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.FileService;
import de.mpg.mpdl.inge.service.util.GenrePropertiesProvider;
import de.mpg.mpdl.inge.util.PropertyReader;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

  private static final String DATA = "data";
  private static final String GENRE = "genre";

  private static final String SITEMAP_PATH = System.getProperty(PropertyReader.JBOSS_HOME_DIR) + "/standalone/data/sitemap/";
  private static final String SITEMAP_FILE_PATH = "/{sitemapFile:.+}";
  private static final String Sitemap_VAR = "sitemapFile";

  private final AuthorizationService authorizationService;

  private final RestTemplate restTemplate;

  @Autowired
  @Qualifier("mpgJsonIpListProvider")
  private IpListProvider ipListProvider;

  @Autowired
  private FileService fileService;

  public MiscellaneousController(AuthorizationService authorizationService, RestTemplate restTemplate) {
    this.authorizationService = authorizationService;
    this.restTemplate = restTemplate;
  }

  //TODO: pubman.properties
  //TODO: token
  //TODO: checkData
  @RequestMapping(value = "/callAiApi", method = RequestMethod.GET)
  public ResponseEntity<String> callAiApi(@RequestParam(DATA) String data) {

    logger.info("Calling Ai API");
    Request request = prepareRequest(data);
    logger.info("RequestBody: " + request.requestBody());

    HttpEntity<String> entity = new HttpEntity<>(request.requestBody(), request.headers());

    LocalDateTime start = LocalDateTime.now();
    ResponseEntity<String> response = restTemplate.exchange(request.url(), HttpMethod.POST, entity, String.class);
    LocalDateTime end = LocalDateTime.now();
    Duration duration = Duration.between(start, end);
    logger.info("Dauer: " + duration.toMinutes() + " Minuten und " + duration.toSecondsPart() + " Sekunden.");

    String responseBody = response.getBody();
    logger.info("ResponseBody:" + responseBody);

    JSONArray authors = parseResult(responseBody);

    return new ResponseEntity<>(authors.toString(), HttpStatus.OK);
  }

  @RequestMapping(value = "/getGenreProperties", method = RequestMethod.GET)
  public ResponseEntity<String> getGenreProperties( //
      @RequestHeader(AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestParam(GENRE) MdsPublicationVO.Genre genre) throws AuthenticationException, IngeApplicationException {

    this.authorizationService.getUserAccountFromToken(token);
    checkGenre(genre, "genre");

    JSONObject json = GenrePropertiesProvider.getGenreProperties(genre);

    return new ResponseEntity<>(json.toString(), HttpStatus.OK);
  }

  @RequestMapping(value = "/getIpList", method = RequestMethod.GET)
  public ResponseEntity<Collection<IpListProvider.IpRange>> getIpList( //
      @RequestHeader(AuthCookieToHeaderFilter.AUTHZ_HEADER) String token) throws AuthenticationException, IngeApplicationException {

    this.authorizationService.getUserAccountFromToken(token);
    Collection<IpListProvider.IpRange> ipList = this.ipListProvider.getAll();

    return new ResponseEntity<>(ipList, HttpStatus.OK);
  }

  @RequestMapping(value = SITEMAP_FILE_PATH, method = RequestMethod.GET)
  public ResponseEntity<Resource> getSitemap( //
      @PathVariable(Sitemap_VAR) String sitemapFile, //
      HttpServletResponse response) throws NotFoundException, IngeTechnicalException {

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
      @RequestHeader(AuthCookieToHeaderFilter.AUTHZ_HEADER) String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException {

    this.fileService.regenerateThumbnails(token);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  /// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  /// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private void checkGenre(MdsPublicationVO.Genre genre, String name) throws IngeApplicationException {
    if (null == genre) {
      throw new IngeApplicationException("The genre " + name + " must not be empty");
    }
  }

  private JSONArray parseResult(String responseBody) {
    Pattern pattern = Pattern.compile("```(.*?)```", Pattern.DOTALL);
    Matcher matcher = pattern.matcher(responseBody);

    String extractedJson = null;
    if (matcher.find()) {
      extractedJson = matcher.group(1);
      logger.info("Extracted json:" + extractedJson);
      extractedJson = extractedJson.replace("\\n", "");
      extractedJson = extractedJson.replace("\\\"", "\"");
    }
    logger.info("Extracted json after replacements:" + extractedJson);

    JSONArray authors = new JSONArray(extractedJson);

    return authors;
  }

  private Request prepareRequest(String data) {
    String url = "https://chat-ai.academiccloud.de/v1/chat/completions";
    String model = "meta-llama-3.1-8b-instruct";
    String prompt = "Please create a CSL json of the following authors (family, given) copied from a research paper:";
    Integer temperature = 0;

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setBearerAuth("707e6ff2cbbdcd151e92ca2212df2268");

    JSONObject systemRole = new JSONObject();
    systemRole.put("role", "system");
    systemRole.put("content", "You are a helpful assistant");

    JSONObject userRole = new JSONObject();
    userRole.put("role", "user");
    userRole.put("content", prompt + " " + data);

    JSONArray messages = new JSONArray();
    messages.put(systemRole);
    messages.put(userRole);

    JSONObject json = new JSONObject();
    json.put("model", model);
    json.put("messages", messages);
    json.put("temperature", temperature);

    String requestBody = json.toString();

    Request request = new Request(url, headers, requestBody);
    return request;
  }

  private record Request(String url, HttpHeaders headers, String requestBody) {}
}
