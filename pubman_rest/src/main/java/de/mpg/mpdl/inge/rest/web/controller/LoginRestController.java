package de.mpg.mpdl.inge.rest.web.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;

@RestController
@RequestMapping("login")
public class LoginRestController {

  private final String TOKEN_HEADER = "Token";
  private final String AUTHZ_HEADER = "Authorization";

  private UserAccountService userSvc;

  @Autowired
  public LoginRestController(UserAccountService userSvc) {
    this.userSvc = userSvc;
  }

  @RequestMapping(path = "", method = POST, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<?> login(@RequestBody String credendials, HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    String username = credendials.split(":")[0];
    String password = credendials.split(":")[1];
    String token = userSvc.login(username, password, request, response);
    if (token != null && !token.isEmpty()) {
      HttpHeaders headers = new HttpHeaders();
      headers.add(TOKEN_HEADER, token);
      return new ResponseEntity<>(headers, HttpStatus.OK);
    }
    return null;
  }

  @RequestMapping(path = "/who", method = GET, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<AccountUserDbVO> getUser(@RequestHeader(value = AUTHZ_HEADER) String token)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    AccountUserDbVO user = userSvc.get(token);
    return new ResponseEntity<AccountUserDbVO>(user, HttpStatus.OK);
  }

}
