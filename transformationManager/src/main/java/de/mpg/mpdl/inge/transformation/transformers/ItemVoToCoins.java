package de.mpg.mpdl.inge.transformation.transformers;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.SingleTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerResult;
import de.mpg.mpdl.inge.transformation.results.TransformerStreamResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerSource;
import de.mpg.mpdl.inge.transformation.sources.TransformerVoSource;
import de.mpg.mpdl.inge.transformation.transformers.helpers.coins.CoinsTransformation;

@TransformerModule(sourceFormat = FORMAT.ESCIDOC_ITEM_VO, targetFormat = FORMAT.COINS_STRING)
public class ItemVoToCoins extends SingleTransformer implements ChainableTransformer {

  @Override
  public void transform(TransformerSource source, TransformerResult result) throws TransformationException {
    TransformerVoSource s = (TransformerVoSource) source;

    CoinsTransformation coinsTransformation = new CoinsTransformation();
    String stringResult = coinsTransformation.getCOinS((PubItemVO) s.getSource());

    writeStringToStreamResult(stringResult, result);
  }

  @Override
  public TransformerResult createNewInBetweenResult() {
    return null;
  }

  @Override
  public void xmlSourceToXmlResult(Source s, Result r) throws TransformerException {
    TransformerFactory xslTransformerFactory = new net.sf.saxon.TransformerFactoryImpl();
    Transformer t = xslTransformerFactory.newTransformer();
    t.setOutputProperty(OutputKeys.INDENT, "yes");
    t.setOutputProperty(OutputKeys.METHOD, "xml");
    t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    t.transform(s, r);
  }

}
