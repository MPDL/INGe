package de.mpg.mpdl.inge.transformation.transformers;

import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.util.EntityTransformer;
import de.mpg.mpdl.inge.model.util.MapperFactory;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.wrappers.ItemVOListWrapper;
import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.SingleTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerResult;
import de.mpg.mpdl.inge.transformation.results.TransformerStreamResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerSource;
import de.mpg.mpdl.inge.transformation.sources.TransformerVoSource;

import javax.xml.transform.*;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

@TransformerModule(sourceFormat = de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT.SEARCH_RESULT_VO,
    targetFormat = TransformerFactory.FORMAT.JSON)
public class SearchRetrieveResponseVoToItemJson extends SingleTransformer implements ChainableTransformer {

  @Override
  public void transform(TransformerSource source, TransformerResult result) throws TransformationException {
    try {
      SearchRetrieveResponseVO<ItemVersionVO> searchResult =
          (SearchRetrieveResponseVO<ItemVersionVO>) ((TransformerVoSource) source).getSource();

      MapperFactory.getObjectMapper().writeValue(getOutputStream(result), searchResult);
    } catch (Exception e) {
      throw new TransformationException("Error while citation transformation", e);
    }
  }

  @Override
  public TransformerResult createNewInBetweenResult() {
    TransformerStreamResult tr = new TransformerStreamResult(new ByteArrayOutputStream());
    return tr;
  }

  public void xmlSourceToXmlResult(Source s, Result r) throws TransformerException {}

}
