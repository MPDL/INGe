package de.mpg.mpdl.inge.rest.web.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.XmlOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.mpg.mpdl.inge.dataacquisition.Util;
import de.mpg.mpdl.inge.dataacquisition.valueobjects.MetadataVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ItemTransformingService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import noNamespace.FormatType;
import noNamespace.FormatsDocument;
import noNamespace.FormatsType;

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

  @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/xml")
  public ResponseEntity<String> unapi( //
      @RequestParam(value = "id", required = false) String identifier, //
      @RequestParam(value = "show", required = false) Boolean show, //
      @RequestParam(value = "format", required = false) String format)
      //
      throws AuthenticationException, AuthorizationException, IngeTechnicalException,
      IngeApplicationException {

    String srResponse = null;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    // public byte[] unapi()
    if (identifier == null && show == null && format == null) {
      FormatsDocument xmlFormatsDoc = FormatsDocument.Factory.newInstance();
      FormatsType xmlFormats = xmlFormatsDoc.addNewFormats();
      FormatType xmlFormat = xmlFormats.addNewFormat();
      String esciDocFormatName = Util.getInternalFormat();
      xmlFormat.setName(esciDocFormatName);
      xmlFormat.setType(Util.getDefaultMimeType(esciDocFormatName));

      try {
        XmlOptions xOpts = new XmlOptions();
        xOpts.setSavePrettyPrint();
        xOpts.setSavePrettyPrintIndent(4);
        xmlFormatsDoc.save(baos, xOpts);
      } catch (IOException e) {
        throw new IngeTechnicalException(e);
      }

      srResponse = new String(baos.toByteArray());

      return new ResponseEntity<String>(srResponse, HttpStatus.OK);

      // public byte[] unapi(String identifier, boolean show)
    } else if (identifier != null && show != null && format == null) {
      List<MetadataVO> metadataV = new ArrayList<MetadataVO>();

      FormatsDocument xmlFormatsDoc = FormatsDocument.Factory.newInstance();
      FormatsType xmlFormats = xmlFormatsDoc.addNewFormats();

      if (show) {
        xmlFormats.setId(identifier);
      }

      // get fetchable metadata formats
      MetadataVO sourceFormat = new MetadataVO();
      String sourceFormatName = Util.getInternalFormat();
      sourceFormat.setName(sourceFormatName);
      sourceFormat.setEncoding(Util.getDefaultEncoding(sourceFormatName));
      sourceFormat.setMdFormat(Util.getDefaultMimeType(sourceFormatName));

      metadataV.add(sourceFormat);
      metadataV.addAll(Util.getTransformFormats(metadataV));

      for (int i = 0; i < metadataV.size(); i++) {
        MetadataVO md = metadataV.get(i);
        FormatType xmlFormat = xmlFormats.addNewFormat();
        xmlFormat.setName(md.getName());
        xmlFormat.setType(md.getMdFormat());
        if (md.getMdDesc() != null) {
          xmlFormat.setDocs(md.getMdDesc());
        }
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

      return new ResponseEntity<String>(srResponse, HttpStatus.OK);

      // public byte[] unapi(String identifier, String format)
    } else if (identifier != null && show == null && format != null) {
      PubItemVO pubItemVO = this.pis.get(identifier, null);

      try {
        srResponse = this.its.transformPubItemTo(FORMAT.valueOf(format), pubItemVO);
      } catch (TransformationException e) {
        throw new IngeApplicationException(e);
      }

      return new ResponseEntity<String>(srResponse, HttpStatus.OK);
    }

    return new ResponseEntity<String>(srResponse, HttpStatus.BAD_REQUEST);
  }

}
