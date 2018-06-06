package de.mpg.mpdl.inge.util;

public class ConeUtils {

  public static String makeConePersonsLinkRelative(String conePersonsLink) {
    return conePersonsLink.substring(conePersonsLink.indexOf(ConeUtils.getConePersonsIdIdentifier()));
  }

  public static String makeConePersonsLinkFull(String conePersonsIdIdentifier) {
    return ConeUtils.getConeServiceUrl() + conePersonsIdIdentifier;
  }

  public static String getFullConePersonsLink() {
    return ConeUtils.getConeServiceUrl() + getConePersonsIdIdentifier();
  }

  public static String getConePersonsIdIdentifier() {
    return PropertyReader.getProperty("inge.cone.person.id.identifier");
  }

  public static String getConeServiceUrl() {
    String coneServiceUrl = PropertyReader.getProperty("inge.cone.service.url");
    return coneServiceUrl.substring(0, coneServiceUrl.length() - 1);
  }
}
