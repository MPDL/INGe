package de.mpg.mpdl.inge.rest.web.controller;

import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.model.db.valueobjects.OrcidAuthorizationDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.rest.web.spring.AuthCookieToHeaderFilter;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.OrcidService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orcid")
@Tag(name = "Orcid")
public class OrcidRestController {

  private static final String CONE_ID_AUTHOR = "coneIdAuthor";
  private static final String ORCID_AUTHOR = "orcidAuthor";
  private static final String NAME_AUTHOR = "nameAuthor";
  private static final String EMAIL_BIBO = "emailBibo";
  private static final String EMAIL_AUTHOR = "emailAuthor";
  private static final String SECRET = "secret";
  private static final String CODE = "code";
  private static final String ERROR_DESCRIPTION = "error_description";
  private static final String DATE_MODIFIED = "dateModified";

  private final OrcidService orcidService;

  public OrcidRestController(OrcidService orcidService) {
    this.orcidService = orcidService;
  }

  @RequestMapping(value = "/sendEmailLink", method = RequestMethod.POST)
  public ResponseEntity<OrcidAuthorizationDbVO> sendEmailLink( //
      @RequestHeader(AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestParam(CONE_ID_AUTHOR) String coneIdAuthor, //
      @RequestParam(ORCID_AUTHOR) String orcidAuthor, //
      @RequestParam(NAME_AUTHOR) String nameAuthor, //
      @RequestParam(EMAIL_BIBO) String emailBibo, //
      @RequestParam(EMAIL_AUTHOR) String emailAuthor) //
      throws AuthenticationException, IngeApplicationException, AuthorizationException, TechnicalException, ValidationException {

    OrcidAuthorizationDbVO orcidAuthorizationDbVO =
        this.orcidService.sendEmailLink(token, coneIdAuthor, orcidAuthor, nameAuthor, emailBibo, emailAuthor);

    return new ResponseEntity<>(orcidAuthorizationDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/createOrcidAuthentication", method = RequestMethod.GET)
  public ResponseEntity<OrcidAuthorizationDbVO> createOrcidAuthentication( //
      @RequestParam(SECRET) String secret, //
      @RequestParam(value = CODE, required = false) String code, //
      @RequestParam(value = ERROR_DESCRIPTION, required = false) String errorDescription) //
      throws TechnicalException, AuthenticationException, IngeApplicationException {

    OrcidAuthorizationDbVO orcidAuthorizationDbVO = this.orcidService.createOrcidAuthorization(secret, code, errorDescription);

    return new ResponseEntity<>(orcidAuthorizationDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/doOrcidQueryAndConvert", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> doOrcidQueryAndConvert( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token, //
      @RequestParam(CONE_ID_AUTHOR) String coneIdAuthor, //
      @RequestParam(value = DATE_MODIFIED, required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date dateModified) //
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    String orcidJson = this.orcidService.doOrcidQueryAndConvert(coneIdAuthor, dateModified, token);

    return new ResponseEntity<>(orcidJson, HttpStatus.OK);
  }

  @RequestMapping(value = "/getVerifiedOrcidAuthorizations", method = RequestMethod.GET)
  public ResponseEntity<Void> getVerifiedOrcidAuthorizations()
      throws AuthenticationException, AuthorizationException, IngeApplicationException, IngeTechnicalException, TechnicalException {

    this.orcidService.getVerifiedOrcidAuthorizations();

    return new ResponseEntity<>(HttpStatus.OK);
  }

}
