package de.mpg.mpdl.inge.rest.web.controller;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tika.exception.TikaException;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.rest.web.spring.AuthCookieToHeaderFilter;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.FileServiceExternal;

/**
 * Rest controller for components
 * 
 * @author walter
 * 
 */
@RestController
@RequestMapping("/component")
public class FileRestController {

  private static final Logger logger = Logger.getLogger(FileRestController.class);
  private static final String COMPONENT_ID_PATH = "/{componentId}";
  private static final String COMPONENT_NAME_PATH = "/{componentName:.*\\..*}";
  private static final String COMPONENT_METADATA_PATH = "/metadata";

  @Autowired
  private FileServiceExternal fileService;

  /**
   * Retrieve a file with a given ID
   * 
   * @param componentId
   * @param response
   */
  @RequestMapping(path = COMPONENT_ID_PATH, method = RequestMethod.GET)
  public void getComponentContent(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER,
      required = false) String token, @PathVariable String componentId, HttpServletResponse response)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException,
      IngeApplicationException {
    try {
      OutputStream output = response.getOutputStream();
      response.setContentType(fileService.getFileType(componentId));
      response.setHeader("Content-disposition",
          "attachment; filename=" + fileService.getFileName(componentId));
      fileService.readFile(componentId, output, token);
      output.flush();
      output.close();
    } catch (IOException e) {
      logger.error("could not read file [" + componentId + "]");
      throw new IngeTechnicalException("Error while opening input stream", e);
    }
  }

  /**
   * generate a staged File, that can be integrated in PubItems by using the delivered reference
   * 
   * @param componentName
   * @param request
   * @return stagedFileId
   */
  @RequestMapping(path = COMPONENT_NAME_PATH, method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.CREATED)
  public String createStageComponent(
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable String componentName, HttpServletRequest request)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException,
      IngeApplicationException {

    // HttpHeaders headers = new HttpHeaders();
    // headers.setContentType(MediaType.TEXT_HTML);

    int stagedFileId;
    try {
      stagedFileId =
          fileService.createStageFile(request.getInputStream(), componentName, token).getId();
    } catch (IOException e) {

      logger.error("Error while opening input stream", e);
      throw new IngeTechnicalException("Error while opening input stream", e);
    }

    return String.valueOf(stagedFileId);
    // return new ResponseEntity<String>(responseBody, headers, HttpStatus.CREATED);
  }

  /**
   * Retrive the technical Metadata of a file
   * 
   * @param componentId
   * @return
   * @throws IOException
   * @throws SAXException
   * @throws TikaException
   */
  @RequestMapping(path = COMPONENT_ID_PATH + COMPONENT_METADATA_PATH, method = RequestMethod.GET,
      produces = MediaType.TEXT_PLAIN_VALUE)
  public String getTechnicalMetadataByTika(@RequestHeader(
      value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token,
      @PathVariable String componentId) throws AuthenticationException, AuthorizationException,
      IngeTechnicalException, IngeApplicationException {
    return fileService.getFileMetadata(componentId, token);
  }
}
