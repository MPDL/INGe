package de.mpg.mpdl.inge.dataacquisition.webservice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlOptions;

import de.mpg.mpdl.inge.dataacquisition.DataHandlerService;
import de.mpg.mpdl.inge.dataacquisition.DataSourceHandlerService;
import de.mpg.mpdl.inge.dataacquisition.DataaquisitionException;
import de.mpg.mpdl.inge.dataacquisition.Util;
import de.mpg.mpdl.inge.dataacquisition.valueobjects.DataSourceVO;
import de.mpg.mpdl.inge.dataacquisition.valueobjects.FullTextVO;
import de.mpg.mpdl.inge.dataacquisition.valueobjects.MetadataVO;
import noNamespace.FormatType;
import noNamespace.FormatsDocument;
import noNamespace.FormatsType;

/**
 * This class provides the implementation of the {@link Unapi} interface.
 * 
 * @author Friederike Kleinfercher (initial creation)
 */
public class UnapiServlet extends HttpServlet implements Unapi {
  private static final long serialVersionUID = 1L;
  private final String idTypeUri = "URI";
  private final String idTypeUrl = "URL";
  private final String idTypeEscidoc = "ESCIDOC";
  private final String idTypeUnknown = "UNKNOWN";
  private final Logger logger = Logger.getLogger(UnapiServlet.class);
  private DataHandlerService dataHandler = new DataHandlerService();
  private DataSourceHandlerService sourceHandler = new DataSourceHandlerService();
  private boolean view = false; // default option is download
  private String filename = "unapi";

  /**
   * Http get method for unAPI interface.
   * 
   * @param request
   * @param response
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response) {
    this.doPost(request, response);
  }

  /**
   * Http post method for unAPI interface.
   * 
   * @param request
   * @param response
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response) {
    try {
      String identifier = null;
      String format = null;
      OutputStream outStream = response.getOutputStream();
      // Retrieve the command from the location path
      String command = request.getPathInfo();
      if (command != null && command.length() > 0) {
        command = command.substring(1);
      }
      if (request.getRequestURL().toString().contains("view")) {
        this.view = true;
      }
      // Handle Call
      if ("unapi".equals(command)) {
        identifier = request.getParameter("id");
        format = request.getParameter("format");
        if (identifier == null) {
          // Gives back a description of escidoc formats as default
          response.setStatus(200);
          response.setContentType("application/xml");
          outStream.write(this.unapi(this.idTypeEscidoc, false));
        } else {
          if (format == null) {
            // Gives back a description of all available formats for a source
            byte[] xml = this.unapi(identifier, true);
            if (xml != null) {
              response.setStatus(200);
              response.setContentType("application/xml");
              outStream.write(xml);
            } else {
              response.sendError(404, "Identifier " + identifier + " not recognized");
            }
          } else { // Fetch data
            try {
              byte[] data = this.unapi(identifier, format);
              if (data == null) {
                response.sendError(404, "Identifier " + identifier + " not recognized");
              } else {
                response.setContentType(this.dataHandler.getContentType());
                if (!this.view) {
                  response.setHeader("Content-disposition", "attachment; filename=" + this.filename
                      + this.dataHandler.getFileEnding());
                }
                response.setStatus(200);
                outStream.write(data);
              }
            } catch (DataaquisitionException e) {
              this.resetValues();
              this.logger.error("Item with identifier " + identifier + " was not found.", e);
              response.sendError(404, "Identifier " + identifier + " not recognized");
            }
          }
        }
      } else {
        // Gives back a description of all available sources
        response.setStatus(200);
        response.setContentType("application/xml");
        outStream.write(this.unapi());
      }
      outStream.flush();
      outStream.close();
      this.resetValues();
    } catch (IOException e) {
      this.resetValues();
      this.logger.error("unAPI request could not be processed due to technical problems.", e);
    } catch (DataaquisitionException e) {
      this.resetValues();
      this.logger.error("unAPI request could not be processed due to technical problems.", e);
    }
  }

  /**
   * {@inheritDoc}
   */
  public byte[] unapi() {
    Util util = new Util();
    return util.createUnapiSourcesXml();
  }

  /**
   * {@inheritDoc} if unapi interface is called with no identifier, the identifier is set to escidoc
   * as default, showing escidoc formats to fetch only when not the default identifier is set, the
   * identifier is displayed in the formats xml.
   * 
   * @throws DataaquisitionException
   */
  public byte[] unapi(String identifier, boolean show) throws DataaquisitionException {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    List<FullTextVO> fullTextV = new ArrayList<FullTextVO>();
    List<MetadataVO> metadataV = new ArrayList<MetadataVO>();
    Util util = new Util();
    String[] tmp = identifier.split(":");
    DataSourceVO source = this.sourceHandler.getSourceByIdentifier(tmp[0]);
    // No source for this identifier
    if (source == null) {
      return null;
    }
    FormatsDocument xmlFormatsDoc = FormatsDocument.Factory.newInstance();
    FormatsType xmlFormats = xmlFormatsDoc.addNewFormats();
    if (show) {
      xmlFormats.setId(identifier);
    }
    fullTextV = source.getFtFormats();
    // get fetchable metadata formats
    metadataV = source.getMdFormats();
    // get transformable formats
    metadataV.addAll(util.getTransformFormats(metadataV));
    // get transformable formats via escidoc format
    if (util.checkEscidocTransition(metadataV, identifier)) {
      String transitionFormatName = util.getInternalFormat();
      MetadataVO transitionFormat = new MetadataVO();
      transitionFormat.setName(transitionFormatName);
      transitionFormat.setEncoding(util.getDefaultEncoding(transitionFormatName));
      transitionFormat.setMdFormat(util.getDefaultMimeType(transitionFormatName));
      List<MetadataVO> transitionFormatV = new ArrayList<MetadataVO>();
      transitionFormatV.add(transitionFormat);
      // Call method with transition format escidoc
      metadataV.addAll(util.getTransformFormats(transitionFormatV));
    }
    metadataV = util.getRidOfDuplicatesInVector(metadataV);

    for (int i = 0; i < metadataV.size(); i++) {
      MetadataVO md = metadataV.get(i);
      FormatType xmlFormat = xmlFormats.addNewFormat();
      xmlFormat.setName(md.getName());
      xmlFormat.setType(md.getMdFormat());
      if (md.getMdDesc() != null) {
        xmlFormat.setDocs(md.getMdDesc());
      }
    }
    // get fetchable file formats
    for (int i = 0; i < fullTextV.size(); i++) {
      FullTextVO ft = fullTextV.get(i);
      if (!ft.getName().equals(util.getDummyFormat())) {
        FormatType xmlFormat = xmlFormats.addNewFormat();
        xmlFormat.setName(ft.getName());
        xmlFormat.setType(ft.getFtFormat());
      }
    }
    try {
      XmlOptions xOpts = new XmlOptions();
      xOpts.setSavePrettyPrint();
      xOpts.setSavePrettyPrintIndent(4);
      xmlFormatsDoc.save(baos, xOpts);
    } catch (IOException e) {
      this.logger.info("Error when creating output xml.", e);
      throw new DataaquisitionException(e);
    }
    return baos.toByteArray();
  }

  /**
   * {@inheritDoc}
   */
  public byte[] unapi(String identifier, String format) throws DataaquisitionException {
    this.filename = identifier;

    try {
      String[] tmp = identifier.split(":", 2);
      String sourceId = tmp[0];
      String fullId = tmp[1];

      String sourceName = this.sourceHandler.getSourceNameByIdentifier(sourceId);
      String idType = this.checkIdentifier(identifier, format);

      if (idType.equals(this.idTypeUri)) {
        if (sourceId != null) {
          return this.dataHandler.doFetch(sourceName, fullId, format);
        }
      }
      if (idType.equals(this.idTypeUrl)) {
        return this.dataHandler.fetchMetadatafromURL(new URL(identifier));
      }
      // if (idType.equals(this.idTypeEscidoc))
      // {
      // this.filename = trimmedId;
      // return this.dataHandler.doFetch(sourceName, trimmedId, format);
      // }
      if (idType.equals(this.idTypeUnknown) || sourceId == null) {
        this.logger.warn("The type of the identifier (" + identifier + ") was not recognised.");
        throw new DataaquisitionException("The type of the identifier (" + identifier
            + ") was not recognised.");
      }
    } catch (DataaquisitionException e) {
      throw new DataaquisitionException(identifier, e);
    } catch (MalformedURLException e) {
      throw new DataaquisitionException(identifier, e);
    }
    return null;
  }

  private String checkIdentifier(String identifier, String format) {
    // identifier = identifier.toLowerCase().trim();
    // if (identifier.startsWith("escidoc"))
    // {
    // return this.idTypeEscidoc;
    // }
    if (identifier.startsWith("http")) {
      // Fetch from url => only download possible
      this.view = false;
      return this.idTypeUrl;
    }
    return this.idTypeUri;
  }

  private void resetValues() {
    this.dataHandler.setContentType("");
    this.dataHandler.setFileEnding("");
    this.filename = ("");
    this.view = false;
  }
}
