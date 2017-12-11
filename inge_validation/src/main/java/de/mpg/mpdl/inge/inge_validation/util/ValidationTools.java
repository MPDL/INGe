package de.mpg.mpdl.inge.inge_validation.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

public class ValidationTools {
  private static final SimpleDateFormat SHORT = new SimpleDateFormat("yyyy");
  private static final SimpleDateFormat MEDIUM = new SimpleDateFormat("yyyy-MM");
  private static final SimpleDateFormat LONG = new SimpleDateFormat("yyyy-MM-dd");

  public static boolean isEmpty(String s) {
    return s == null || s.trim().isEmpty();
  }

  public static boolean isNotEmpty(String s) {
    return s != null && !s.trim().isEmpty();
  }

  public static boolean isEmpty(List<?> l) {
    return l == null || l.isEmpty();
  }

  public static boolean isNotEmpty(List<?> l) {
    return l != null && !l.isEmpty();
  }

  public static boolean isNotEmpty(Set<?> s) {
    return s != null && !s.isEmpty();
  }

  public static boolean isEmpty(Set<?> s) {
    return s == null || s.isEmpty();
  }

  public static boolean checkDate(String s) {

    if (ValidationTools.isNotEmpty(s)) {
      switch (s.length()) {
        case 10:
          try {
            ValidationTools.LONG.setLenient(false);
            ValidationTools.LONG.parse(s);
          } catch (final ParseException e) {
            return false;
          }
          break;

        case 7:
          try {
            ValidationTools.MEDIUM.setLenient(false);
            ValidationTools.MEDIUM.parse(s);
          } catch (final ParseException e) {
            return false;
          }
          break;

        case 4:
          try {
            ValidationTools.SHORT.setLenient(false);
            ValidationTools.SHORT.parse(s);
          } catch (final ParseException e) {
            return false;
          }
          break;

        default:
          return false;
      }
    }

    return true;
  }

}
