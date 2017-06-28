package de.mpg.mpdl.inge.transformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerSource;

public class ChainTransformer extends SingleTransformer implements Transformer {

  private static Logger logger = Logger.getLogger(ChainTransformer.class);

  private List<ChainableTransformer> transformerChain;

  @Override
  public void transform(TransformerSource source, TransformerResult result)
      throws TransformationException {


    logger.debug("Found "
        + getTransformerChain().size()
        + " transformations in transformation chain: "
        + Arrays.toString(getTransformerChain().toArray(
            new ChainableTransformer[getTransformerChain().size()])) + ">");


    TransformerSource currentSource = null;
    TransformerResult currentResult = null;

    for (int i = 0; i < getTransformerChain().size(); i++) {


      ChainableTransformer transformer = getTransformerChain().get(i);
      transformer.setConfiguration(getConfiguration());

      // First round
      if (i == 0) {
        currentSource = source;
      } else {
        currentSource = currentResult.createSourceFromInBetweenResult();
      }


      // Last round
      if (i == getTransformerChain().size() - 1) {
        currentResult = result;
      } else {
        currentResult = transformer.createNewInBetweenResult();
      }


      logger.debug("Delegating to transformer " + transformer.getSourceFormat() + " --> "
          + transformer.getTargetFormat() + " (" + transformer.toString() + ")");
      transformer.transform(currentSource, currentResult);

    }

  }



  public List<ChainableTransformer> getTransformerChain() {
    return transformerChain;
  }

  public void setTransformerChain(List<ChainableTransformer> transformerChain) {
    this.transformerChain = transformerChain;
  }

  @Override
  public Map<String, String> getConfiguration() {

    Map<String, String> c = new HashMap<String, String>();

    for (ChainableTransformer t : transformerChain) {
      if (t.getConfiguration() != null) {
        c.putAll(t.getConfiguration());
      }
    }

    return c;
  }

  @Override
  public List<String> getAllConfigurationValuesFor(String key) throws TransformationException {
    List<String> v = new ArrayList<String>();

    for (ChainableTransformer t : transformerChain) {
      if (t.getAllConfigurationValuesFor(key) != null)
        v.addAll(t.getAllConfigurationValuesFor(key));
    }
    return v;
  }

}
