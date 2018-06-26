package de.mpg.mpdl.inge.transformation.transformers;

import de.mpg.mpdl.inge.transformation.SingleTransformer;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerSource;

public class IdentityTransformer extends SingleTransformer {

  @Override
  public void transform(TransformerSource source, TransformerResult result) throws TransformationException {
    String in = getStringFromSource(source);
    writeStringToStreamResult(in, result);
  }

}
