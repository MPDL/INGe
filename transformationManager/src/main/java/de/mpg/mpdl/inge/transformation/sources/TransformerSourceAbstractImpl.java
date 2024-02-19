package de.mpg.mpdl.inge.transformation.sources;


public abstract class TransformerSourceAbstractImpl<S> implements TransformerSource {


  private S source;


  public TransformerSourceAbstractImpl(S s) {
    this.source = s;
  }

  public S getSource() {
    return this.source;
  }

  public void setSource(S source) {
    this.source = source;
  }


}
