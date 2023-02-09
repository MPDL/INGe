package de.mpg.mpdl.inge.service.pubman.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.util.MapperFactory;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.service.pubman.ItemTransformingService;
import de.mpg.mpdl.inge.transformation.Transformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerStreamResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerStreamSource;
import de.mpg.mpdl.inge.transformation.sources.TransformerVoSource;
import de.mpg.mpdl.inge.transformation.transformers.CitationTransformer;

@SuppressWarnings("serial")
@Service
@Primary
public class ItemTransformingServiceImpl implements ItemTransformingService, Serializable {

  private static final Logger logger = Logger.getLogger(ItemTransformingServiceImpl.class);

  //  private static final String TRANSFORMATION_ITEM_LIST_2_SNIPPET = "itemList2snippet.xsl";

  //  // Mapping the format names of a ExportVO object to the enums used in transformationManager
  //  private static Map<String, TransformerFactory.FORMAT> map;
  //  static {
  //    map = new HashMap<String, TransformerFactory.FORMAT>();
  //    map.put(TransformerFactory.MARC_XML, TransformerFactory.FORMAT.MARC_XML);
  //    map.put(TransformerFactory.ENDNOTE, TransformerFactory.FORMAT.ENDNOTE_STRING);
  //    map.put(TransformerFactory.BIBTEX, TransformerFactory.FORMAT.BIBTEX_STRING);
  //    map.put(TransformerFactory.ESCIDOC_ITEM_XML, TransformerFactory.FORMAT.ESCIDOC_ITEM_V3_XML);
  //    map.put(TransformerFactory.EDOC_XML, TransformerFactory.FORMAT.EDOC_XML);
  //  }

  private byte[] getOutputForExport(ExportFormatVO exportFormat, List<ItemVersionVO> itemList,
      SearchRetrieveResponseVO<ItemVersionVO> searchResult) throws IngeTechnicalException {
    try {
      if (searchResult == null) {
        searchResult = new SearchRetrieveResponseVO<>();
        searchResult.setNumberOfRecords(itemList.size());
        List<SearchRetrieveRecordVO<ItemVersionVO>> recordList = new ArrayList<>();
        for (ItemVersionVO item : itemList) {
          SearchRetrieveRecordVO<ItemVersionVO> srr = new SearchRetrieveRecordVO<>();
          srr.setData(new ItemVersionVO(item));
          srr.setPersistenceId(item.getObjectIdAndVersion());
          recordList.add(srr);
        }
        searchResult.setRecords(recordList);
      }

      if (exportFormat.getFormat().equals(TransformerFactory.FORMAT.JSON.getName())) {
        return MapperFactory.getObjectMapper().writeValueAsBytes(searchResult);
      } else {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Transformer trans = TransformerFactory.newTransformer(TransformerFactory.FORMAT.SEARCH_RESULT_VO,
            TransformerFactory.getFormat(exportFormat.getFormat()));
        trans.getConfiguration().put(CitationTransformer.CONFIGURATION_CITATION, exportFormat.getCitationName());
        trans.getConfiguration().put(CitationTransformer.CONFIGURATION_CSL_ID, exportFormat.getId());
        trans.transform(new TransformerVoSource(searchResult), new TransformerStreamResult(bos));

        return bos.toByteArray();
      }
    } catch (Exception e) {
      logger.warn("Exception occured when transforming from <" + TransformerFactory.FORMAT.ESCIDOC_ITEMLIST_V3_XML + "> to <"
          + exportFormat.getFormat());
      //              + map.get(exportFormat.getName()));
      throw new IngeTechnicalException(e);
    }
  }

  @Override
  public byte[] getOutputForExport(ExportFormatVO exportFormat, SearchRetrieveResponseVO<ItemVersionVO> srr) throws IngeTechnicalException {
    return getOutputForExport(exportFormat, null, srr);
  }

  @Override
  public byte[] getOutputForExport(ExportFormatVO exportFormat, List<ItemVersionVO> pubItemVOList) throws IngeTechnicalException {
    return getOutputForExport(exportFormat, pubItemVOList, null);
  }

  @Override
  public TransformerFactory.FORMAT[] getAllSourceFormatsFor(TransformerFactory.FORMAT target) {
    return TransformerFactory.getAllSourceFormatsFor(target);
  }

  @Override
  public TransformerFactory.FORMAT[] getAllTargetFormatsFor(TransformerFactory.FORMAT source) {
    return TransformerFactory.getAllTargetFormatsFor(source);
  }

  @Override
  public String transformFromTo(TransformerFactory.FORMAT source, TransformerFactory.FORMAT target, String itemXml,
      Map<String, String> configuration) throws TransformationException {
    StringWriter wr = new StringWriter();

    final Transformer t = TransformerFactory.newTransformer(source, target);

    if (configuration != null) {
      t.mergeConfiguration(configuration);
    }

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

      final Transformer t = TransformerFactory.newTransformer(TransformerFactory.getInternalFormat(), target);
      //      logger.info(itemXml);
      t.transform(new TransformerStreamSource(new ByteArrayInputStream(itemXml.getBytes("UTF-8"))), new TransformerStreamResult(wr));
    } catch (Exception e) {
      throw new TransformationException(e);
    }

    return wr.toString();
  }

  @Override
  public boolean isTransformationExisting(TransformerFactory.FORMAT sourceFormat, TransformerFactory.FORMAT targetFormat) {
    return TransformerFactory.isTransformationExisting(sourceFormat, targetFormat);
  }
}
