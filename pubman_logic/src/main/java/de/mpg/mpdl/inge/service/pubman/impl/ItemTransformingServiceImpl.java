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
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.service.pubman.ItemTransformingService;
import de.mpg.mpdl.inge.transformation.Transformer;
import de.mpg.mpdl.inge.transformation.TransformerCache;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerStreamResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerStreamSource;

public class ItemTransformingServiceImpl implements ItemTransformingService {

  private static Logger logger = Logger.getLogger(ItemTransformingServiceImpl.class);

  // Mapping the format names of a ExportVO object to the enums used in transformationManager
  private static Map<String, TransformerFactory.FORMAT> map;
  static {
    map = new HashMap<String, TransformerFactory.FORMAT>();
    map.put("MARCXML", TransformerFactory.FORMAT.MARC_XML);
    map.put("ENDNOTE", TransformerFactory.FORMAT.ENDNOTE_STRING);
    map.put("BIBTEX", TransformerFactory.FORMAT.BIBTEX_STRING);
    map.put("ESCIDOC_XML", TransformerFactory.FORMAT.ESCIDOC_ITEM_V3_XML);
    map.put("EDOC_EXPORT", TransformerFactory.FORMAT.EDOC_XML);
    map.put("EDOC_IMPORT", TransformerFactory.FORMAT.EDOC_XML);
  }

  @Override
  public byte[] getOutputForExport(ExportFormatVO exportFormat, String itemList)
      throws IngeTechnicalException {

    byte[] exportData = null;

    switch (exportFormat.getFormatType()) {

      case LAYOUT:

        try {
          exportData = CitationStyleExecuterService.getOutput(itemList, exportFormat);
        } catch (CitationStyleManagerException e) {
          throw new IngeTechnicalException(e);
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
              TransformerCache.getTransformer(TransformerFactory.FORMAT.ESCIDOC_ITEMLIST_V3_XML,
                  map.get(exportFormat));

          trans.transform(
              new TransformerStreamSource(new ByteArrayInputStream(itemList.getBytes("UTF-8"))),
              new TransformerStreamResult(wr));

          exportData = wr.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException | TransformationException e) {
          logger.warn("Exception occured when transforming from <"
              + TransformerFactory.FORMAT.ESCIDOC_ITEMLIST_V3_XML + "> to <"
              + map.get(exportFormat));
          throw new IngeTechnicalException(e);
        }
        break;

      default:
        throw new IngeTechnicalException("format Type <" + exportFormat.getFormatType()
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
  public TransformerFactory.FORMAT[] getAllSourceFormatsFor(TransformerFactory.FORMAT target) {
    return TransformerCache.getAllSourceFormatsFor(target);
  }

  @Override
  public TransformerFactory.FORMAT[] getAllTargetFormatsFor(TransformerFactory.FORMAT source) {
    return TransformerCache.getAllTargetFormatsFor(source);
  }

  @Override
  public String transformFromTo(TransformerFactory.FORMAT source, TransformerFactory.FORMAT target,
      String itemXml) throws TransformationException {
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
