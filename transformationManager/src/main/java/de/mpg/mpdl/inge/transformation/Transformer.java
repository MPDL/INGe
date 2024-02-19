package de.mpg.mpdl.inge.transformation;

import java.util.List;
import java.util.Map;

import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerSource;

public interface Transformer {
  void transform(TransformerSource source, TransformerResult result) throws TransformationException;

  Map<String, String> getConfiguration();

  void setConfiguration(Map<String, String> config);

  void mergeConfiguration(Map<String, String> config);

  void setSourceFormat(TransformerFactory.FORMAT sourceFormat);

  TransformerFactory.FORMAT getSourceFormat();

  void setTargetFormat(TransformerFactory.FORMAT targetFormat);

  TransformerFactory.FORMAT getTargetFormat();

  List<String> getAllConfigurationValuesFor(String key) throws TransformationException;
}
