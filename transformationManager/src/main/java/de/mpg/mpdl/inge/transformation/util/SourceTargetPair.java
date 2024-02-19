package de.mpg.mpdl.inge.transformation.util;

import de.mpg.mpdl.inge.transformation.TransformerFactory;


public class SourceTargetPair {
  private TransformerFactory.FORMAT source;
  private TransformerFactory.FORMAT target;

  public SourceTargetPair(TransformerFactory.FORMAT s, TransformerFactory.FORMAT t) {
    this.setSource(s);
    this.setTarget(t);
  }

  @Override
  public boolean equals(Object other) {
    if (null == other) {
      return false;
    } else if (!(other instanceof SourceTargetPair)) {
      return false;
    } else {
      return (null == this.getSource() ? null == ((SourceTargetPair) other).getSource()
          : this.getSource().equals(((SourceTargetPair) other).getSource()))
          && (null == this.getTarget() ? null == ((SourceTargetPair) other).getTarget()
              : this.getTarget().equals(((SourceTargetPair) other).getTarget()));

    }
  }

  @Override
  public int hashCode() {
    return this.getSource().hashCode() * this.getTarget().hashCode();
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
