package de.mpg.mpdl.inge.transformation.results;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.transformation.Transformer;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.sources.TransformerSource;

public class TransformerWrapper {
  private Transformer transformer;
  private TransformerSource source;

  public TransformerWrapper(Transformer transformer, TransformerSource source) {
    this.transformer = transformer;
    this.source = source;
  }

  public void executeTransformation(TransformerResult transResult) throws IngeTechnicalException {
    try {
      this.transformer.transform(this.source, transResult);
    } catch (TransformationException e) {
      throw new IngeTechnicalException(e);
    }
  }
}
