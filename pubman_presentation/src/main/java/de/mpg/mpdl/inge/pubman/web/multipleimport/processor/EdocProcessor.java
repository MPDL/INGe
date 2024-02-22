/*
 *
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.pubman.web.multipleimport.processor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.Future;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.StagedFileDbVO;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.FileService;
import de.mpg.mpdl.inge.util.IdentityHandler;

/**
 * Format processor for eDoc XML files.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class EdocProcessor extends FormatProcessor {

  private static final Logger logger = LogManager.getLogger(EdocProcessor.class);


  @Autowired
  private FileService fileService;

  private boolean init = false;
  private final List<String> items = new ArrayList<>();
  private int counter = -1;
  private int length = -1;
  private byte[] originalData = null;

  @Override
  public boolean hasNext() {
    if (!this.init) {
      this.initialize();
    }
    return (null != this.originalData && this.counter < this.length);
  }

  @Override
  public String next() throws NoSuchElementException {
    if (!this.init) {
      this.initialize();
    }
    if (null != this.originalData && this.counter < this.length) {
      String s = this.items.get(this.counter);
      this.counter++;
      return s;
    } else {
      throw new NoSuchElementException("No more entries left");
    }

  }

  /**
   * Not implemented.
   */
  @Override
  @Deprecated
  public void remove() {
    throw new RuntimeException("Method not implemented");
  }

  private void initialize() {
    this.init = true;

    try {

      SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
      EdocHandler edocHandler = new EdocHandler();
      parser.parse(this.getSourceFile(), edocHandler);

      this.originalData = edocHandler.getResult().getBytes(this.getEncoding());

      this.length = this.items.size();

      this.counter = 0;

    } catch (Exception e) {
      throw new RuntimeException("Error reading input stream", e);
    }

  }

  @Override
  public int getLength() {
    return this.length;
  }

  @Override
  public String getDataAsBase64() {
    if (null == this.originalData) {
      return null;
    }

    return Base64.getEncoder().encodeToString(this.originalData);
  }

  /**
   * Fetches a file from a given URL and adds the staged file-path to fileDbVO
   *
   * @param url
   * @param file
   * @param authenticationToken
   * @return
   * @throws Exception
   */
  public void getFileforImport(String url, FileDbVO file, String authenticationToken) throws Exception {
    try (CloseableHttpAsyncClient httpClient = HttpAsyncClients.createDefault()) {
      httpClient.start();
      HttpGet getRequest = new HttpGet(url);
      Future<HttpResponse> future = httpClient.execute(getRequest, null);
      HttpResponse response = future.get();
      if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
        InputStream is = response.getEntity().getContent();
        // try to retrive name - if this is not possible set dummy-name
        if (null != file.getMetadata() && null != file.getMetadata().getTitle()) {
          StagedFileDbVO stagedFile = this.fileService.createStageFile(is, file.getMetadata().getTitle(), authenticationToken);
          is.close();
          logger.info("StagedFilePath: " + stagedFile.getPath());
          file.setContent(stagedFile.getPath());
        } else {
          StagedFileDbVO stagedFile = this.fileService.createStageFile(is, "defaultFileName", authenticationToken);
          logger.info("ElseStagedFilePath: " + stagedFile.getPath());
          is.close();
          file.setContent(stagedFile.getPath());
        }
      } else {
        logger.error("Could not donwload file for eDoc import");
        throw new IngeApplicationException("Server did not respond with 200 - OK --> could not download file from [" + url + "]");
      }
    }
  }

  /**
   * SAX parser to extract the items out of the XML.
   *
   * @author franke (initial creation)
   * @author $Author$ (last modification)
   * @version $Revision$ $LastChangedDate$
   *
   */
  public class EdocHandler extends IdentityHandler {

    private StringBuilder builder;
    private boolean inItem = false;

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
      if ("edoc".equals(this.getStack().toString())) {
        this.builder = new StringBuilder();
        this.inItem = true;
      }
      super.startElement(uri, localName, name, attributes);

      if (this.inItem) {
        this.builder.append("<");
        this.builder.append(name);
        for (int i = 0; i < attributes.getLength(); i++) {

          this.builder.append(" ");
          this.builder.append(attributes.getQName(i));
          this.builder.append("=\"");
          this.builder.append(this.escape(attributes.getValue(i)));
          this.builder.append("\"");
        }
        this.builder.append(">");
      }

    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
      super.endElement(uri, localName, name);

      if (this.inItem) {
        this.builder.append("</");
        this.builder.append(name);
        this.builder.append(">");
      }

      if ("edoc".equals(this.getStack().toString())) {
        EdocProcessor.this.items.add(this.builder.toString());
        this.builder = null;
        this.inItem = false;
      }
    }

    @Override
    public void content(String uri, String localName, String name, String content) throws SAXException {
      super.content(uri, localName, name, content);
      if (this.inItem) {
        this.builder.append(this.escape(content));
      }
    }

    /**
     * Returns an XML-escaped String that can be used for writing an XML.
     *
     * @param input A string
     * @return The XML-escaped string
     */
    public String escape(String input) {
      if (null != input) {
        input = input.replace("&", "&amp;");
        input = input.replace("<", "&lt;");
        input = input.replace("\"", "&quot;");
      }

      return input;
    }
  }
}
