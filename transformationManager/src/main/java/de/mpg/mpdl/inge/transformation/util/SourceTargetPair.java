package de.mpg.mpdl.inge.transformation.util;

import de.mpg.mpdl.inge.transformation.TransformerFactory;


public class SourceTargetPair {
  private TransformerFactory.FORMAT source;
  private TransformerFactory.FORMAT target;

  public SourceTargetPair(TransformerFactory.FORMAT s, TransformerFactory.FORMAT t) {
    this.source = s;
    this.target = t;
  }

  @Override
  public boolean equals(Object other) {
    if (null == other) {
      return false;
    } else if (!(other instanceof SourceTargetPair)) {
      return false;
    } else {
      return (null == this.source ? null == ((SourceTargetPair) other).source : this.source.equals(((SourceTargetPair) other).source))
          && (null == this.target ? null == ((SourceTargetPair) other).target : this.target.equals(((SourceTargetPair) other).target));

    }
  }

  @Override
  public int hashCode() {
    return this.source.hashCode() * this.target.hashCode();
  }

  public TransformerFactory.FORMAT getTarget() {
    return this.target;
  }

  public void setTarget(TransformerFactory.FORMAT target) {
    this.target = target;
  }

  public TransformerFactory.FORMAT getSource() {
    return this.source;
  }

  public void setSource(TransformerFactory.FORMAT source) {
    this.source = source;
  }
}
