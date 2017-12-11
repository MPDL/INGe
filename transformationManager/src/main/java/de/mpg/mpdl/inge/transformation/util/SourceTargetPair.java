package de.mpg.mpdl.inge.transformation.util;

import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;


public class SourceTargetPair {
  private FORMAT source;
  private FORMAT target;

  public SourceTargetPair(FORMAT s, FORMAT t) {
    this.setSource(s);
    this.setTarget(t);
  }

  @Override
  public boolean equals(Object other) {
    if (other == null) {
      return false;
    } else if (!(other instanceof SourceTargetPair)) {
      return false;
    } else {
      return (this.getSource() == null ? ((SourceTargetPair) other).getSource() == null
          : this.getSource().equals(((SourceTargetPair) other).getSource()))
          && (this.getTarget() == null ? ((SourceTargetPair) other).getTarget() == null
              : this.getTarget().equals(((SourceTargetPair) other).getTarget()));

    }
  }

  @Override
  public int hashCode() {
    return this.getSource().hashCode() * this.getTarget().hashCode();
  }

  public FORMAT getTarget() {
    return target;
  }

  public void setTarget(FORMAT target) {
    this.target = target;
  }

  public FORMAT getSource() {
    return source;
  }

  public void setSource(FORMAT source) {
    this.source = source;
  }
}
