package de.mpg.mpdl.inge.transformation;

public class TransformerEdge {

  private Class<? extends Transformer> transformerClass;

  private TransformerFactory.FORMAT sourceFormat;

  private TransformerFactory.FORMAT targetFormat;

  public TransformerEdge(Class<? extends Transformer> transformerClass, TransformerFactory.FORMAT sourceFormat,
      TransformerFactory.FORMAT targetFormat) {
    this.transformerClass = transformerClass;
    this.sourceFormat = sourceFormat;
    this.targetFormat = targetFormat;
  }

  public Class<? extends Transformer> getTransformerClass() {
    return this.transformerClass;
  }

  public void setTransformerClass(Class<? extends Transformer> transformerClass) {
    this.transformerClass = transformerClass;
  }

  public TransformerFactory.FORMAT getSourceFormat() {
    return this.sourceFormat;
  }

  public void setSourceFormat(TransformerFactory.FORMAT sourceFormat) {
    this.sourceFormat = sourceFormat;
  }

  public TransformerFactory.FORMAT getTargetFormat() {
    return this.targetFormat;
  }

  public void setTargetFormat(TransformerFactory.FORMAT targetFormat) {
    this.targetFormat = targetFormat;
  }

  public String toString() {
    return this.sourceFormat + " --> " + this.targetFormat + " (" + this.transformerClass.toString() + ")";
  }

}
