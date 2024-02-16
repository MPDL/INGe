package de.mpg.mpdl.inge.rest.web.controller;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.apache.xmlbeans.XmlOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.mpg.mpdl.inge.dataacquisition.unapiFormats.FormatType;
import de.mpg.mpdl.inge.dataacquisition.unapiFormats.FormatsDocument;
import de.mpg.mpdl.inge.dataacquisition.unapiFormats.FormatsType;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.util.EntityTransformer;
import de.mpg.mpdl.inge.rest.web.spring.AuthCookieToHeaderFilter;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ItemTransformingService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/unapi")
@Hidden
public class UnapiRestController {

  private final PubItemService pis;
  private final ItemTransformingService its;

  @Autowired
  public UnapiRestController(PubItemService pis, ItemTransformingService its) {
    this.pis = pis;
    this.its = its;
  }

  @RequestMapping(value = "", method = RequestMethod.GET)
  public void unapi( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token,
      @RequestParam(value = "id", required = false) String identifier, //
      @RequestParam(value = "show", required = false) Boolean show, //
      @RequestParam(value = "format", required = false) String formatName, //
      HttpServletResponse response)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    String result = null;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    // public byte[] unapi()
    if (identifier == null) {
      getFormatDescription(baos);

      response.setContentType(MediaType.APPLICATION_XML.getType());

      writeOutput(response, baos);

      // public byte[] unapi(String identifier, boolean show)
    } else if (formatName == null) {
      getTargetFormats(identifier, show, baos);

      response.setContentType(MediaType.APPLICATION_XML.getType());

      writeOutput(response, baos);

      // public byte[] unapi(String identifier, String format)
    } else {
      ItemVersionVO pubItemVO = this.pis.get(identifier, token);

      TransformerFactory.FORMAT targetFormat = TransformerFactory.getFormat(formatName);

      try {
        result = this.its.transformPubItemTo(targetFormat, EntityTransformer.transformToOld(pubItemVO));
      } catch (TransformationException e) {
        throw new IngeApplicationException(e);
      }

      response.setContentType(targetFormat.getFileFormat().getMimeType());

      writeOutput(response, result);
    }
  }

  @RequestMapping(value = "/download", method = RequestMethod.GET)
  public void unapiDownload( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token,
      @RequestParam(value = "id", required = false) String identifier, //
      @RequestParam(value = "show", required = false) Boolean show, //
      @RequestParam(value = "format", required = false) String formatName, //
      HttpServletResponse response)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    String result = null;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    // public byte[] unapi()
    if (identifier == null) {
      getFormatDescription(baos);

      response.setContentType(MediaType.APPLICATION_XML.getType());

      writeOutput(response, baos);

      // public byte[] unapi(String identifier, boolean show)
    } else if (formatName == null) {
      getTargetFormats(identifier, show, baos);

      response.setContentType(MediaType.APPLICATION_XML.getType());

      writeOutput(response, baos);

      // public byte[] unapi(String identifier, String format)
    } else {
      ItemVersionVO pubItemVO = this.pis.get(identifier, token);

      TransformerFactory.FORMAT targetFormat = TransformerFactory.getFormat(formatName);

      try {
        result = this.its.transformPubItemTo(targetFormat, EntityTransformer.transformToOld(pubItemVO));
      } catch (TransformationException e) {
        throw new IngeApplicationException(e);
      }

      response.setContentType(targetFormat.getFileFormat().getMimeType());
      response.setHeader("Content-disposition",
          "attachment; filename=" + targetFormat.getName() + "." + targetFormat.getFileFormat().getExtension());

      writeOutput(response, result);
    }
  }

  private void getTargetFormats(String identifier, Boolean show, ByteArrayOutputStream baos) throws IngeTechnicalException {
    FormatsDocument xmlFormatsDoc = FormatsDocument.Factory.newInstance();
    FormatsType xmlFormats = xmlFormatsDoc.addNewFormats();

    if (show != null && show) {
      xmlFormats.setId(identifier);
    }

    TransformerFactory.FORMAT[] targetFormats = this.its.getAllTargetFormatsFor(TransformerFactory.getInternalFormat());

    for (TransformerFactory.FORMAT targetFormat : targetFormats) {
      FormatType xmlFormat = xmlFormats.addNewFormat();
      xmlFormat.setName(targetFormat.getName());
      xmlFormat.setType(targetFormat.getFileFormat().getMimeType());
    }

    try {
      XmlOptions xOpts = new XmlOptions();
      xOpts.setSavePrettyPrint();
      xOpts.setSavePrettyPrintIndent(4);
      xmlFormatsDoc.save(baos, xOpts);
    } catch (IOException e) {
      throw new IngeTechnicalException(e);
    }
  }

  private void getFormatDescription(ByteArrayOutputStream baos) throws IngeTechnicalException {
    FormatsDocument xmlFormatsDoc = FormatsDocument.Factory.newInstance();
    FormatsType xmlFormats = xmlFormatsDoc.addNewFormats();
    FormatType xmlFormat = xmlFormats.addNewFormat();
    xmlFormat.setName(TransformerFactory.getInternalFormat().getName());
    xmlFormat.setType(TransformerFactory.getInternalFormat().getFileFormat().getMimeType());

    try {
      XmlOptions xOpts = new XmlOptions();
      xOpts.setSavePrettyPrint();
      xOpts.setSavePrettyPrintIndent(4);
      xmlFormatsDoc.save(baos, xOpts);
    } catch (IOException e) {
      throw new IngeTechnicalException(e);
    }
  }

  private void writeOutput(HttpServletResponse response, ByteArrayOutputStream baos) throws IngeTechnicalException {
    try {
      OutputStream output = response.getOutputStream();
      output.write(baos.toByteArray());
    } catch (IOException e) {
      throw new IngeTechnicalException(e);
    }
  }

  private void writeOutput(HttpServletResponse response, String result) throws IngeTechnicalException {
    try {
      OutputStream output = response.getOutputStream();
      output.write(result.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new IngeTechnicalException(e);
    }
  }

}
