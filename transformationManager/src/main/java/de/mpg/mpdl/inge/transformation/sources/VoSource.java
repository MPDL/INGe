package de.mpg.mpdl.inge.transformation.sources;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

public class VoSource extends TransformerSourceAbstractImpl<ValueObject> implements
    TransformerSource {

  public VoSource(ValueObject s) {
    super(s);
  }

}
