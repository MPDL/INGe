package de.mpg.mpdl.inge.transformation.transformers;

import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.util.EntityTransformer;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.wrappers.ItemVOListWrapper;
import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.SingleTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerResult;
import de.mpg.mpdl.inge.transformation.results.TransformerStreamResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerSource;
import de.mpg.mpdl.inge.transformation.sources.TransformerVoSource;

@TransformerModule(sourceFormat = FORMAT.SEARCH_RESULT_VO, targetFormat = FORMAT.ESCIDOC_ITEMLIST_V3_XML)
public class SearchRetrieveResponseVoToItemXml extends SingleTransformer implements ChainableTransformer {

  public static final String CONFIGURATION_CITATION = "citation";
  public static final String CONFIGURATION_CSL_ID = "csl_id";

  @Override
  public void transform(TransformerSource source, TransformerResult result) throws TransformationException {
    try {
      SearchRetrieveResponseVO<ItemVersionVO> searchResult =
          (SearchRetrieveResponseVO<ItemVersionVO>) ((TransformerVoSource) source).getSource();
      List<ItemVersionVO> itemList = searchResult.getRecords().stream().map(SearchRetrieveRecordVO::getData).collect(Collectors.toList());
      List<PubItemVO> transformedList = EntityTransformer.transformToOld(itemList);

      ItemVOListWrapper listWrapper = new ItemVOListWrapper();
      listWrapper.setItemVOList(transformedList);

      if (searchResult != null) {
        listWrapper.setNumberOfRecords(String.valueOf(searchResult.getNumberOfRecords()));
      }

      String escidocItemList = XmlTransformingService.transformToItemList(listWrapper);
      writeStringToStreamResult(escidocItemList, result);
    } catch (Exception e) {
      throw new TransformationException("Error while citation transformation", e);
    }
  }

  @Override
  public TransformerResult createNewInBetweenResult() {
    TransformerStreamResult tr = new TransformerStreamResult(new ByteArrayOutputStream());

    return tr;
  }

  public void xmlSourceToXmlResult(Source s, Result r) throws TransformerException {
    TransformerFactory xslTransformerFactory = new net.sf.saxon.TransformerFactoryImpl();
    Transformer t = xslTransformerFactory.newTransformer();
    t.setOutputProperty(OutputKeys.INDENT, "yes");
    t.setOutputProperty(OutputKeys.METHOD, "xml");
    t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    t.transform(s, r);
  }

}
