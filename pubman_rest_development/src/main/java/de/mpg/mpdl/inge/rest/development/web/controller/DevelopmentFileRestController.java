package de.mpg.mpdl.inge.rest.development.web.controller;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.mpg.mpdl.inge.filestorage.filesystem.FileSystemServiceBean;

/**
 * Rest controller for development issues
 * 
 * @author walter
 * 
 */
@RestController
@RequestMapping("/development")
public class DevelopmentFileRestController {

  private static final String COMPONENT_NAME_PATH = "/component/{componentName:.*}";
  private static final String COMPONENT_LOCAL_PATH =
      "/component/{componentPathYear:.*}/{componentPathMonth:.*}/{componentPathDay:.*}/{componentName:.*}";

  /**
   * generate a File via REST for development issues
   * 
   * @param componentName
   * @param request
   * @return stagedFileId
   */
  @RequestMapping(path = COMPONENT_NAME_PATH, method = RequestMethod.POST, produces = MediaType.ALL_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public String createComponent(@PathVariable String componentName, HttpServletRequest request) throws Exception {

    FileSystemServiceBean fileSystemService = new FileSystemServiceBean();
    return fileSystemService.createFile(request.getInputStream(), URLDecoder.decode(componentName, StandardCharsets.UTF_8.name()));
  }

  /**
   * read a File via REST for development issues
   * 
   * @param componentName
   * @param request
   * @return stagedFileId
   */
  @RequestMapping(path = COMPONENT_LOCAL_PATH, method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.CREATED)
  //Doesn't work if the environment from the development REST service is development enabled (loop)
  public void readComponent(@PathVariable String componentPathYear, @PathVariable String componentPathMonth,
      @PathVariable String componentPathDay, @PathVariable String componentName, HttpServletResponse response) throws Exception {
    FileSystemServiceBean fileSystemService = new FileSystemServiceBean();
    fileSystemService.readFile(componentPathYear + "/" + componentPathMonth + "/" + componentPathDay + "/"
        + URLDecoder.decode(componentName, StandardCharsets.UTF_8.name()), response.getOutputStream());
  }

  //TODO deleteComponent

}
