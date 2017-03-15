package de.mpg.mpdl.inge.cone.web;

import java.io.UnsupportedEncodingException;

import de.mpg.mpdl.inge.cone.ConeException;

/**
 * Helper class for URL handling.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class UrlHelper {
  /**
   * Hide constructor of util class.
   */
  private UrlHelper() {}

  /**
   * Transforms broken ISO-8859-1 strings into correct UTF-8 strings.
   * 
   * @param brokenValue
   * @return hopefully fixed string.
   * @throws ConeException
   */
  public static String fixURLEncoding(String input) throws ConeException {
    if (input != null) {
      try {
        String utf8 = new String(input.getBytes("ISO-8859-1"), "UTF-8");
        if (utf8.equals(input) || utf8.contains("�") || utf8.length() == input.length()) {
          return input;
        } else {
          return utf8;
        }
      } catch (UnsupportedEncodingException e) {
        throw new ConeException(e);
      }
    } else {
      return null;
    }
  }
}
