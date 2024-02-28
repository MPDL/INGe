
package de.mpg.mpdl.inge.rest.web.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

  private final AuthorizationService authorizationService;

  @Autowired
  @Qualifier("mpgJsonIpListProvider")
  private IpListProvider ipListProvider;

  public MiscellaneousController(AuthorizationService authorizationService) {
    this.authorizationService = authorizationService;
  }

  @RequestMapping(value = "/getIpList", method = RequestMethod.GET)
  public ResponseEntity<Collection<IpListProvider.IpRange>> getIpList( //
      @RequestHeader(AuthCookieToHeaderFilter.AUTHZ_HEADER) String token) throws AuthenticationException, IngeApplicationException {

    this.authorizationService.getUserAccountFromToken(token);
    Collection<IpListProvider.IpRange> ipList = this.ipListProvider.getAll();

    return new ResponseEntity<>(ipList, HttpStatus.OK);
  }
}
