package de.mpg.mpdl.inge.inge_validation.util;

import java.util.List;
import java.util.Set;

public class ValidationTools {
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
}
