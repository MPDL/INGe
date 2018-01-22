package de.mpg.mpdl.inge.rest.development.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
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

  private static final String COMPONENT_NAME_PATH = "/component/{componentName:.*\\..*}";

  /**
   * generate a File via REST for development issues
   * 
   * @param componentName
   * @param request
   * @return stagedFileId
   */
  @RequestMapping(path = COMPONENT_NAME_PATH, method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.CREATED)
  public String createComponent(@PathVariable String componentName, HttpServletRequest request) throws Exception {

    FileSystemServiceBean fileSystemService = new FileSystemServiceBean();
    return fileSystemService.createFile(request.getInputStream(), componentName);
  }

}
