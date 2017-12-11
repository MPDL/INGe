package de.mpg.mpdl.inge.transformation;

import de.mpg.mpdl.inge.transformation.results.TransformerResult;

public interface ChainableTransformer extends Transformer {

  public TransformerResult createNewInBetweenResult();
}
