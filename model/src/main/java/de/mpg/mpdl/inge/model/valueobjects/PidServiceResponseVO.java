package de.mpg.mpdl.inge.model.valueobjects;

@SuppressWarnings("serial")
public class PidServiceResponseVO extends ValueObject {
  protected String identifier;
  protected String url;

  public PidServiceResponseVO() {}

  public String getUrl() {
    return this.url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getIdentifier() {
    return this.identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  @Override
  public String toString() {
    return "PidServiceResponseVO [identifier=" + this.identifier + ", url=" + this.url + "]";
  }
}
