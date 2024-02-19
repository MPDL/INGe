package de.mpg.mpdl.inge.transformation.results;


public abstract class TransformerResultAbstractImpl<R> implements TransformerResult {


  private R result;


  public TransformerResultAbstractImpl(R r) {
    this.result = r;
  }

  public R getResult() {
    return this.result;
  }

  public void setResult(R result) {
    this.result = result;
  }


}
