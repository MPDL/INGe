package de.mpg.mpdl.inge.transformation.results;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.sources.TransformerSource;
import de.mpg.mpdl.inge.transformation.sources.TransformerVoSource;

public class VoResult extends TransformerResultAbstractImpl<ValueObject> implements TransformerResult {

  public VoResult(ValueObject r) {
    super(r);

  }

  public VoResult() {
    super(null);

  }

  @Override
  public TransformerSource createSourceFromInBetweenResult() {
    return new TransformerVoSource(this.getResult());
  }

}
