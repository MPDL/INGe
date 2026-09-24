package de.mpg.mpdl.inge.rest.web.controller;

import de.mpg.mpdl.inge.model.db.valueobjects.OrcidAuthorizationDbVO;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.rest.web.spring.AuthCookieToHeaderFilter;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.OrcidService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
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
      throws AuthenticationException, IngeApplicationException, AuthorizationException, TechnicalException {

    OrcidAuthorizationDbVO orcidAuthorizationDbVO =
        this.orcidService.sendEmailLink(token, coneIdAuthor, orcidAuthor, nameAuthor, emailBibo, emailAuthor);

    return new ResponseEntity<>(orcidAuthorizationDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/createOrcidAuthentication", method = RequestMethod.GET)
  public ResponseEntity<?> createOrcidAuthentication( //
      @RequestParam(SECRET) String secret, //
      @RequestParam(CODE) String code) throws TechnicalException, AuthenticationException, IngeApplicationException {

    OrcidAuthorizationDbVO orcidAuthorizationDbVO = this.orcidService.createOrcidAuthorization(secret, code);

    return new ResponseEntity<>(HttpStatus.OK);
  }

}
