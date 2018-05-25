package de.mpg.mpdl.inge.util;

public class ConeUtils {

  public static String makeConePersonsLinkRelative(String conePersonsLink) {
    return conePersonsLink.substring(conePersonsLink.indexOf(PropertyReader.getProperty("inge.cone.person.id.identifier")) - 1);
  }

  public static String getFullConePersonsLink() {
    return PropertyReader.getProperty("inge.cone.service.url") + PropertyReader.getProperty("inge.cone.person.id.identifier");
  }

  public static String getConePersonsIdIdentifier() {
    return "/" + PropertyReader.getProperty("inge.cone.person.id.identifier");
  }
}
