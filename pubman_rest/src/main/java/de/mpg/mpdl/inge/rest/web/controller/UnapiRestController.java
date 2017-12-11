package de.mpg.mpdl.inge.rest.web.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.xmlbeans.XmlOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.mpg.mpdl.inge.dataaquisition.unapiFormats.FormatType;
import de.mpg.mpdl.inge.dataaquisition.unapiFormats.FormatsDocument;
import de.mpg.mpdl.inge.dataaquisition.unapiFormats.FormatsType;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ItemTransformingService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;

@RestController
@RequestMapping("/unapi")
public class UnapiRestController {

  private PubItemService pis;
  private ItemTransformingService its;

  @Autowired
  public UnapiRestController(PubItemService pis, ItemTransformingService its) {
    this.pis = pis;
    this.its = its;
  }

  @RequestMapping(value = "", method = RequestMethod.GET)
  public ResponseEntity<String> unapi( //
      @RequestParam(value = "id", required = false) String identifier, //
      @RequestParam(value = "show", required = false) Boolean show, //
      @RequestParam(value = "format", required = false) String formatName)
      //
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    String srResponse = null;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    // public byte[] unapi()
    if (identifier == null && show == null && formatName == null) {
      FormatsDocument xmlFormatsDoc = FormatsDocument.Factory.newInstance();
      FormatsType xmlFormats = xmlFormatsDoc.addNewFormats();
      FormatType xmlFormat = xmlFormats.addNewFormat();
      xmlFormat.setName(TransformerFactory.getInternalFormat().getName());
      xmlFormat.setType(TransformerFactory.getInternalFormat().getType());

      try {
        XmlOptions xOpts = new XmlOptions();
        xOpts.setSavePrettyPrint();
        xOpts.setSavePrettyPrintIndent(4);
        xmlFormatsDoc.save(baos, xOpts);
      } catch (IOException e) {
        throw new IngeTechnicalException(e);
      }

      srResponse = new String(baos.toByteArray());

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_XML);

      return new ResponseEntity<String>(srResponse, headers, HttpStatus.OK);

      // public byte[] unapi(String identifier, boolean show)
    } else if (identifier != null && show != null && formatName == null) {
      FormatsDocument xmlFormatsDoc = FormatsDocument.Factory.newInstance();
      FormatsType xmlFormats = xmlFormatsDoc.addNewFormats();

      if (show) {
        xmlFormats.setId(identifier);
      }

      TransformerFactory.FORMAT[] targetFormats = this.its.getAllTargetFormatsFor(TransformerFactory.getInternalFormat());

      for (TransformerFactory.FORMAT targetFormat : targetFormats) {
        FormatType xmlFormat = xmlFormats.addNewFormat();
        xmlFormat.setName(targetFormat.getName());
        xmlFormat.setType(targetFormat.getType());
      }

      try {
        XmlOptions xOpts = new XmlOptions();
        xOpts.setSavePrettyPrint();
        xOpts.setSavePrettyPrintIndent(4);
        xmlFormatsDoc.save(baos, xOpts);
      } catch (IOException e) {
        throw new IngeTechnicalException(e);
      }

      srResponse = new String(baos.toByteArray());

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_XML);

      return new ResponseEntity<String>(srResponse, headers, HttpStatus.OK);

      // public byte[] unapi(String identifier, String format)
    } else if (identifier != null && show == null && formatName != null) {
      PubItemVO pubItemVO = this.pis.get(identifier, null);

      TransformerFactory.FORMAT targetFormat = TransformerFactory.getFormat(formatName);

      try {
        srResponse = this.its.transformPubItemTo(targetFormat, pubItemVO);
      } catch (TransformationException e) {
        throw new IngeApplicationException(e);
      }

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.parseMediaType(targetFormat.getType()));

      return new ResponseEntity<String>(srResponse, HttpStatus.OK);
    }

    return new ResponseEntity<String>(srResponse, HttpStatus.BAD_REQUEST);
  }

}
