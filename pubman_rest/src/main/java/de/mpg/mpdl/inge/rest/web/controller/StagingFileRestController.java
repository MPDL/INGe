package de.mpg.mpdl.inge.rest.web.controller;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.rest.web.spring.AuthCookieToHeaderFilter;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.FileServiceExternal;
import io.swagger.annotations.Api;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Rest controller for components
 * 
 * @author walter
 * 
 */
@RestController
@RequestMapping("/staging")
@Api(tags = "Upload Files / Staging")
public class StagingFileRestController {

  private static final Logger logger = Logger.getLogger(StagingFileRestController.class);
  private static final String COMPONENT_NAME_PATH = "/{componentName:.*\\..*}";

  @Autowired
  private FileServiceExternal fileService;



  /**
   * generate a staged File, that can be integrated in PubItems by using the delivered reference
   * 
   * @param componentName
   * @param request
   * @return stagedFileId
   */
  @RequestMapping(path = COMPONENT_NAME_PATH, method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public String createStageComponent(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable String componentName, HttpServletRequest request)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    // HttpHeaders headers = new HttpHeaders();
    // headers.setContentType(MediaType.TEXT_HTML);

    int stagedFileId;
    try {
      stagedFileId = fileService.createStageFile(request.getInputStream(), componentName, token).getId();
    } catch (IOException e) {

      logger.error("Error while opening input stream", e);
      throw new IngeTechnicalException("Error while opening input stream", e);
    }

    return String.valueOf(stagedFileId);
    // return new ResponseEntity<String>(responseBody, headers, HttpStatus.CREATED);
  }


}
