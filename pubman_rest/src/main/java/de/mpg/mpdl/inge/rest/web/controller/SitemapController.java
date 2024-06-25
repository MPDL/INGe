package de.mpg.mpdl.inge.rest.web.controller;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.rest.web.exceptions.NotFoundException;
import de.mpg.mpdl.inge.util.PropertyReader;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sitemap")
@Tag(name = "Sitemap")
public class SitemapController {

  private static final String SITEMAP_PATH = System.getProperty(PropertyReader.JBOSS_HOME_DIR) + "/standalone/data/sitemap/";

  private static final String SITEMAP_FILE_PATH = "/{sitemapFile:.+}";
  private static final String Sitemap_VAR = "sitemapFile";

  public SitemapController() {}

  @RequestMapping(value=SITEMAP_FILE_PATH, method = RequestMethod.GET)
  public void getSitemap( //
      @PathVariable(Sitemap_VAR) String sitemapFile, //
      HttpServletResponse response) throws NotFoundException, IngeTechnicalException {

    if (null != sitemapFile && sitemapFile.matches("^/sitemap\\d*\\.xml$")) {
      File mySitemapFile = new File(SITEMAP_PATH + sitemapFile);

      if (!mySitemapFile.exists()) {
        throw new NotFoundException();
      }

      response.setContentType("text/xml");
      response.setContentLength((int) mySitemapFile.length());

      writeOutput(response, mySitemapFile);
    }
  }

  private void writeOutput(HttpServletResponse response, File file) throws IngeTechnicalException {
    try {
      OutputStream out = response.getOutputStream();
      BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
      byte[] buffer = new byte[8 * 1024];

      int count;

      while (-1 != (count = in.read(buffer))) {
        out.write(buffer, 0, count);
      }

      in.close();
      out.flush();
      out.close();
    } catch (IOException e) {
      throw new IngeTechnicalException(e);
    }
  }
}
