package de.mpg.mpdl.inge.rest.web.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.pubman.FileService;

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
  private static final String COMPONENT_METADATA_PATH = "/metadata";

  @Autowired
  private FileService fileService;

  /**
   * Retrieve a file with a given ID
   * 
   * @param componentId
   * @param response
   */
  @RequestMapping(path = COMPONENT_ID_PATH, method = RequestMethod.GET)
  public void getComponentContent(@PathVariable String componentId, HttpServletResponse response) {
    try {
      OutputStream output = response.getOutputStream();
      response.setContentType(fileService.getFileType(componentId));
      response.setHeader("Content-disposition",
          "attachment; filename=" + fileService.getFileName(componentId));
      fileService.readFile(componentId, output);
      output.flush();
      output.close();
    } catch (IngeTechnicalException | IOException e) {
      logger.error("could not read file [" + componentId + "]");
    }
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
  private String getTechnicalMetadataByTika(@PathVariable String componentId) {

    final StringBuffer b = new StringBuffer(2048);
    final Metadata metadata = new Metadata();
    final AutoDetectParser parser = new AutoDetectParser();
    final BodyContentHandler handler = new BodyContentHandler();
    ParseContext context = new ParseContext();

    ByteArrayOutputStream fileOutput = new ByteArrayOutputStream();
    try {
      fileService.readFile(componentId, fileOutput);
      final TikaInputStream input =
          TikaInputStream.get(new ByteArrayInputStream(fileOutput.toByteArray()));
      parser.parse(input, handler, metadata, context);
      fileOutput.close();
      input.close();
    } catch (IngeTechnicalException | IOException | SAXException | TikaException e) {
      logger.error("could not read file [" + componentId + "] for Metadata extraction");
    }

    for (final String name : metadata.names()) {
      b.append(name).append(": ").append(metadata.get(name))
          .append(System.getProperty("line.separator"));
    }
    return b.toString();
  }
}
