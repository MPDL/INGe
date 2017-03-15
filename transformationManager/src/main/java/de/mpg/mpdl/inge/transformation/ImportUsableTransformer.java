package de.mpg.mpdl.inge.transformation;

import java.util.List;

import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;

public interface ImportUsableTransformer extends ChainableTransformer {

  public List<String> getConfigurationValuesFor(String key) throws TransformationException;

}
