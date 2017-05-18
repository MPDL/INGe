package de.mpg.mpdl.inge.service.pubman.impl;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.citationmanager.CitationStyleExecuterService;
import de.mpg.mpdl.inge.citationmanager.CitationStyleManagerException;
import de.mpg.mpdl.inge.model.exception.IngeEsServiceException;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.service.pubman.ItemTransformingService;
import de.mpg.mpdl.inge.transformation.Transformer;
import de.mpg.mpdl.inge.transformation.TransformerCache;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerStreamResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerStreamSource;

public class ItemTransformingServiceImpl implements ItemTransformingService {

  private static Logger logger = Logger.getLogger(ItemTransformingServiceImpl.class);

  // Mapping the format names of a ExportVO object to the enums used in transformationManager
  private static Map<String, FORMAT> map;
  static {
    map = new HashMap<String, FORMAT>();
    map.put("MARCXML", FORMAT.MARC_XML);
    map.put("ENDNOTE", FORMAT.ENDNOTE_STRING);
    map.put("BIBTEX", FORMAT.BIBTEX_STRING);
    map.put("ESCIDOC_XML", FORMAT.ESCIDOC_ITEM_V3_XML);
    map.put("EDOC_EXPORT", FORMAT.EDOC_XML);
    map.put("EDOC_IMPORT", FORMAT.EDOC_XML);
  }

  @Override
  public byte[] getOutputForExport(ExportFormatVO exportFormat, String itemList)
      throws IngeEsServiceException {

    byte[] exportData = null;

    switch (exportFormat.getFormatType()) {

      case LAYOUT:

        try {
          exportData = CitationStyleExecuterService.getOutput(itemList, exportFormat);
        } catch (CitationStyleManagerException e) {
          throw new IngeEsServiceException(e);
        }
        break;

      case STRUCTURED:

        if ("ESCIDOC_XML_V13".equalsIgnoreCase(exportFormat.getFormatType().toString())) {
          return itemList.getBytes();
        }

        Transformer trans = null;
        StringWriter wr = new StringWriter();

        try {
          trans =
              TransformerCache
                  .getTransformer(FORMAT.ESCIDOC_ITEMLIST_V3_XML, map.get(exportFormat));

          trans.transform(
              new TransformerStreamSource(new ByteArrayInputStream(itemList.getBytes("UTF-8"))),
              new TransformerStreamResult(wr));

          exportData = wr.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException | TransformationException e) {
          logger.warn("Exception occured when transforming from <" + FORMAT.ESCIDOC_ITEMLIST_V3_XML
              + "> to <" + map.get(exportFormat));
          throw new IngeEsServiceException(e);
        }
        break;

      default:
        throw new IngeEsServiceException("format Type <" + exportFormat.getFormatType()
            + "> is not supported");
    }

    return exportData;
  }

  @Override
  public byte[] getOutputForExport(ExportFormatVO exportFormat, List<PubItemVO> pubItemVOList)
      throws TechnicalException {

    String itemList = XmlTransformingService.transformToItemList(pubItemVOList);

    byte[] exportData = null;
    try {
      exportData = getOutputForExport(exportFormat, itemList);
    } catch (Exception e) {
      throw new TechnicalException(e);
    }

    return exportData;
  }

  @Override
  public FORMAT[] getAllSourceFormatsFor(FORMAT target) {
    return TransformerCache.getAllSourceFormatsFor(target);
  }

  @Override
  public FORMAT[] getAllTargetFormatsFor(FORMAT source) {
    return TransformerCache.getAllTargetFormatsFor(source);
  }

  @Override
  public String transformFromTo(FORMAT source, FORMAT target, String itemXml)
      throws TransformationException {
    StringWriter wr = new StringWriter();

    final Transformer t = TransformerCache.getTransformer(source, target);

    try {
      t.transform(new TransformerStreamSource(new ByteArrayInputStream(itemXml.getBytes("UTF-8"))),
          new TransformerStreamResult(wr));
    } catch (UnsupportedEncodingException e) {
      throw new TransformationException(e);
    }

    return wr.toString();
  }
}
