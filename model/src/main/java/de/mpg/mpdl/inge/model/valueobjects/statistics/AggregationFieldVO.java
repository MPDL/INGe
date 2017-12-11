package de.mpg.mpdl.inge.model.valueobjects.statistics;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

public class AggregationFieldVO extends ValueObject {


  private String feed;

  private String name;

  private String xPath;

  public String getFeed() {
    return feed;
  }

  public void setFeed(String feed) {
    this.feed = feed;
  }

  public String getxPath() {
    return xPath;
  }

  public void setxPath(String xPath) {
    this.xPath = xPath;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


}
