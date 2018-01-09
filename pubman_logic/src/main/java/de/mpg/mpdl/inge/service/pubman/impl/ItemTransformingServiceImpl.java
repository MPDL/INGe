package de.mpg.mpdl.inge.service.pubman.impl;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.inge.citationmanager.CitationStyleExecuterService;
import de.mpg.mpdl.inge.citationmanager.CitationStyleManagerException;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.service.pubman.ItemTransformingService;
import de.mpg.mpdl.inge.service.util.EntityTransformer;
import de.mpg.mpdl.inge.transformation.Transformer;
import de.mpg.mpdl.inge.transformation.TransformerCache;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerStreamResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerStreamSource;

@Service
@Primary
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
    map.put("EDOC_IMPORT", TransformerFactory.FORMAT.EDOC_XML);
  }

  @Override
  public byte[] getOutputForExport(ExportFormatVO exportFormat, String itemList) throws IngeTechnicalException {

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

        if ("ESCIDOC_XML".equalsIgnoreCase(exportFormat.getName())) {
          return itemList.getBytes();
        }

        Transformer trans = null;
        StringWriter wr = new StringWriter();

        try {
          trans = TransformerCache.getTransformer(TransformerFactory.FORMAT.ESCIDOC_ITEMLIST_V3_XML, map.get(exportFormat.getName()));

          trans.transform(new TransformerStreamSource(new ByteArrayInputStream(itemList.getBytes("UTF-8"))),
              new TransformerStreamResult(wr));

          exportData = wr.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException | TransformationException e) {
          logger.warn("Exception occured when transforming from <" + TransformerFactory.FORMAT.ESCIDOC_ITEMLIST_V3_XML + "> to <"
              + map.get(exportFormat.getName()));
          throw new IngeTechnicalException(e);
        }
        break;

      default:
        throw new IngeTechnicalException("format Type <" + exportFormat.getFormatType() + "> is not supported");
    }

    return exportData;
  }

  @Override
  public byte[] getOutputForExport(ExportFormatVO exportFormat, List<ItemVersionVO> pubItemVOList) throws TechnicalException {

    
    
    
    String itemList = XmlTransformingService.transformToItemList(EntityTransformer.transformToOld(pubItemVOList));

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
  public String transformFromTo(TransformerFactory.FORMAT source, TransformerFactory.FORMAT target, String itemXml)
      throws TransformationException {
    StringWriter wr = new StringWriter();

    final Transformer t = TransformerCache.getTransformer(source, target);

    try {
      t.transform(new TransformerStreamSource(new ByteArrayInputStream(itemXml.getBytes("UTF-8"))), new TransformerStreamResult(wr));
    } catch (UnsupportedEncodingException e) {
      throw new TransformationException(e);
    }

    return wr.toString();
  }

  public String transformPubItemTo(TransformerFactory.FORMAT target, PubItemVO item) throws TransformationException {
    StringWriter wr = new StringWriter();
    try {
      String itemXml = XmlTransformingService.transformToItem(item);

      final Transformer t = TransformerCache.getTransformer(TransformerFactory.getInternalFormat(), target);


      t.transform(new TransformerStreamSource(new ByteArrayInputStream(itemXml.getBytes("UTF-8"))), new TransformerStreamResult(wr));
    } catch (Exception e) {
      throw new TransformationException(e);
    }

    return wr.toString();
  }

  @Override
  public boolean isTransformationExisting(TransformerFactory.FORMAT sourceFormat, TransformerFactory.FORMAT targetFormat) {
    return TransformerCache.isTransformationExisting(sourceFormat, targetFormat);
  }
}
