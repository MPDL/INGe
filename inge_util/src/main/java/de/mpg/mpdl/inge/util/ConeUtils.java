package de.mpg.mpdl.inge.util;

public class ConeUtils {

  private ConeUtils() {}

  // used for writing relative person link into elastic search index (/persons/resources/...)
  public static String makeConePersonsLinkRelative(String conePersonsLink) {
    return conePersonsLink.substring(conePersonsLink.indexOf(ConeUtils.getConePersonsIdIdentifier()));
  }

  // used for converting CONE id to elastic search id
  public static String convertConeId2EsId(String id) {
    return "/" + id;
  }

  // used for search in CONE table MATCHES (persons/resources/...)
  public static String makeConePersonsLinkRelative(String[] path) {
    return path[1] + "/" + path[2] + "/" + path[3];
  }

  // used for making complete person link from elastic search index (http://... /persons/resources/...)
  public static String makeConePersonsLinkFull(String conePersonsIdIdentifier) {
    return ConeUtils.getConeServiceUrl() + conePersonsIdIdentifier;
  }

  // used for making complete person link from elastic search index (http://... /persons/resources/...)
  public static String getFullConePersonsLink() {
    return ConeUtils.getConeServiceUrl() + getConePersonsIdIdentifier();
  }

  // identifier used in elastic search index (/persons/resources/...)
  public static String getConePersonsIdIdentifier() {
    return PropertyReader.getProperty(PropertyReader.INGE_CONE_PERSON_ID_IDENTIFIER);
  }

  // CONE service url (.../)
  public static String getConeServiceUrl() {
    String coneServiceUrl = PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL);
    return coneServiceUrl.substring(0, coneServiceUrl.length() - 1);
  }
}
