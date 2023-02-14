package de.mpg.mpdl.inge.rest.web.controller;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.rest.web.spring.AuthCookieToHeaderFilter;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.FileServiceExternal;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Rest controller for components
 * 
 * @author walter
 * 
 */
@RestController
@RequestMapping("/staging")
@Tag(name = "Upload Files / Staging")
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
