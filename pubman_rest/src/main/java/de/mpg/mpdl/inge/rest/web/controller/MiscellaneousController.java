
package de.mpg.mpdl.inge.rest.web.controller;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.rest.web.exceptions.NotFoundException;
import de.mpg.mpdl.inge.service.pubman.FileService;
import de.mpg.mpdl.inge.service.util.GenrePropertiesProvider;
import de.mpg.mpdl.inge.util.PropertyReader;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.mpg.mpdl.inge.rest.web.spring.AuthCookieToHeaderFilter;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.aa.IpListProvider;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/miscellaneous")
@Tag(name = "Miscellaneous")
public class MiscellaneousController {

  private static final String GENRE = "genre";

  private static final String SITEMAP_PATH = System.getProperty(PropertyReader.JBOSS_HOME_DIR) + "/standalone/data/sitemap/";
  private static final String SITEMAP_FILE_PATH = "/{sitemapFile:.+}";
  private static final String Sitemap_VAR = "sitemapFile";

  private final AuthorizationService authorizationService;

  @Autowired
  @Qualifier("mpgJsonIpListProvider")
  private IpListProvider ipListProvider;

  @Autowired
  private FileService fileService;

  public MiscellaneousController(AuthorizationService authorizationService) {
    this.authorizationService = authorizationService;
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

  private void checkGenre(MdsPublicationVO.Genre genre, String name) throws IngeApplicationException {
    if (null == genre) {
      throw new IngeApplicationException("The genre " + name + " must not be empty");
    }
  }

  @RequestMapping(value = "/regenerateThumbnails", method = RequestMethod.GET)
  public ResponseEntity<Collection<IpListProvider.IpRange>> regenerateThumbnails( //
      @RequestHeader(AuthCookieToHeaderFilter.AUTHZ_HEADER) String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException {

    this.fileService.regenerateThumbnails(token);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
