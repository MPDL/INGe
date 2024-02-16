package de.mpg.mpdl.inge.transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerSource;

public class ChainTransformer extends SingleTransformer implements Transformer {

  private static final Logger logger = Logger.getLogger(ChainTransformer.class);

  private List<ChainableTransformer> transformerChain;

  @Override
  public void transform(TransformerSource source, TransformerResult result) throws TransformationException {
    TransformerSource currentSource = null;
    TransformerResult currentResult = null;

    for (int i = 0; i < getTransformerChain().size(); i++) {
      ChainableTransformer transformer = getTransformerChain().get(i);

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
      if (tConfig != null) {
        c.putAll(tConfig);
      }
    }

    logger.debug("Chaintransformer");
    if (c != null && c.entrySet() != null) {
      for (Entry<String, String> entry : c.entrySet()) {
        logger.debug("Transformation parameter from configuration " + entry.getKey() + " -- " + entry.getValue());
      }
    }

    return c;
  }

  @Override
  public void setConfiguration(Map<String, String> config) {
    for (ChainableTransformer t : this.transformerChain) {
      t.setConfiguration(config);
    }
  }

  @Override
  public void mergeConfiguration(Map<String, String> config) {
    for (ChainableTransformer t : this.transformerChain) {
      t.mergeConfiguration(config);
    }
  }

  @Override
  public List<String> getAllConfigurationValuesFor(String key) throws TransformationException {
    List<String> v = new ArrayList<>();
    for (ChainableTransformer t : this.transformerChain) {
      if (t.getAllConfigurationValuesFor(key) != null)
        v.addAll(t.getAllConfigurationValuesFor(key));
    }

    return v;
  }

  public String toString() {
    String chain = "";
    if (this.transformerChain != null) {
      chain = transformerChain.stream().map(Object::toString).collect(Collectors.joining(" -- "));
    }

    return super.toString() + " via " + chain;
  }

}
