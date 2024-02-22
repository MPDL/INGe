package de.mpg.mpdl.inge.transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerSource;

public class ChainTransformer extends SingleTransformer implements Transformer {

  private static final Logger logger = LogManager.getLogger(ChainTransformer.class);

  private List<ChainableTransformer> transformerChain;

  @Override
  public void transform(TransformerSource source, TransformerResult result) throws TransformationException {
    TransformerSource currentSource = null;
    TransformerResult currentResult = null;

    for (int i = 0; i < this.transformerChain.size(); i++) {
      ChainableTransformer transformer = this.transformerChain.get(i);

      // First round
      if (0 == i) {
        currentSource = source;
      } else {
        currentSource = currentResult.createSourceFromInBetweenResult();
      }

      // Last round
      if (i == this.transformerChain.size() - 1) {
        currentResult = result;
      } else {
        currentResult = transformer.createNewInBetweenResult();
      }

      transformer.transform(currentSource, currentResult);
    }
  }

  public List<ChainableTransformer> getTransformerChain() {
    return this.transformerChain;
  }

  public void setTransformerChain(List<ChainableTransformer> transformerChain) {
    this.transformerChain = transformerChain;
  }

  @Override
  public Map<String, String> getConfiguration() {
    Map<String, String> c = new HashMap<>();

    for (ChainableTransformer t : this.transformerChain) {
      Map<String, String> tConfig = t.getConfiguration();
      if (null != tConfig) {
        c.putAll(tConfig);
      }
    }

    logger.debug("Chaintransformer");
    if (null != c && null != c.entrySet()) {
      for (Map.Entry<String, String> entry : c.entrySet()) {
        logger.debug("Transformation parameter from configuration " + entry.getKey() + " -- " + entry.getValue());
      }
    }

    return c;
  }

  @Override
  public void setConfiguration(Map<String, String> configuration) {
    for (ChainableTransformer t : this.transformerChain) {
      t.setConfiguration(configuration);
    }
  }

  @Override
  public void mergeConfiguration(Map<String, String> givenConfiguration) {
    for (ChainableTransformer t : this.transformerChain) {
      t.mergeConfiguration(givenConfiguration);
    }
  }

  @Override
  public List<String> getAllConfigurationValuesFor(String key) throws TransformationException {
    List<String> v = new ArrayList<>();
    for (ChainableTransformer t : this.transformerChain) {
      if (null != t.getAllConfigurationValuesFor(key))
        v.addAll(t.getAllConfigurationValuesFor(key));
    }

    return v;
  }

  public String toString() {
    String chain = "";
    if (null != this.transformerChain) {
      chain = this.transformerChain.stream().map(Object::toString).collect(Collectors.joining(" -- "));
    }

    return super.toString() + " via " + chain;
  }

}
