package de.mpg.mpdl.inge.transformation;

import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;

public class TransformerEdge {

  private Class<? extends Transformer> transformerClass;

  private FORMAT sourceFormat;

  private FORMAT targetFormat;

  public TransformerEdge(Class<? extends Transformer> transformerClass, FORMAT sourceFormat, FORMAT targetFormat) {
    this.transformerClass = transformerClass;
    this.sourceFormat = sourceFormat;
    this.targetFormat = targetFormat;
  }

  public Class<? extends Transformer> getTransformerClass() {
    return transformerClass;
  }

  public void setTransformerClass(Class<? extends Transformer> transformerClass) {
    this.transformerClass = transformerClass;
  }

  public FORMAT getSourceFormat() {
    return sourceFormat;
  }

  public void setSourceFormat(FORMAT sourceFormat) {
    this.sourceFormat = sourceFormat;
  }

  public FORMAT getTargetFormat() {
    return targetFormat;
  }

  public void setTargetFormat(FORMAT targetFormat) {
    this.targetFormat = targetFormat;
  }

  public String toString() {
    return sourceFormat + " --> " + targetFormat + " (" + transformerClass.toString() + ")";
  }

}
