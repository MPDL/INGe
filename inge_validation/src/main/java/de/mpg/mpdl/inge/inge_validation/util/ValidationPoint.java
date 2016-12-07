package de.mpg.mpdl.inge.inge_validation.util;

public enum ValidationPoint {

  DEFAULT("default"), //
  ACCEPT_ITEM("accept_item"), //
  SUBMIT_ITEM("submit_item");

  private String name;


  ValidationPoint(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

}
