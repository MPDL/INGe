package de.mpg.mpdl.inge.inge_validation.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;

public class ValidationTools {
  private static final SimpleDateFormat SHORT = new SimpleDateFormat("yyyy");
  private static final SimpleDateFormat MEDIUM = new SimpleDateFormat("yyyy-MM");
  private static final SimpleDateFormat LONG = new SimpleDateFormat("yyyy-MM-dd");

  public static final String ORCID_HTTPS = "https://orcid.org/";
  public static final String ORCID_REGEX = "^\\d{4}-\\d{4}-\\d{4}-(\\d{3}X|\\d{4})$";

  private ValidationTools() {}

  public synchronized static boolean isEmpty(String s) {
    return null == s || s.trim().isEmpty();
  }

  public synchronized static boolean isNotEmpty(String s) {
    return null != s && !s.trim().isEmpty();
  }

  public synchronized static boolean isEmpty(List<?> l) {
    return null == l || l.isEmpty();
  }

  public synchronized static boolean isNotEmpty(List<?> l) {
    return null != l && !l.isEmpty();
  }

  public synchronized static boolean isNotEmpty(Set<?> s) {
    return null != s && !s.isEmpty();
  }

  public synchronized static boolean isEmpty(Set<?> s) {
    return null == s || s.isEmpty();
  }

  public synchronized static boolean checkDate(String s) {
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

  public synchronized static boolean checkUtf8(ValidatorContext context, String text, String errorMessage) {
    boolean ok = true;

    for (int i = 0; i < text.length(); i++) {
      char chr = text.charAt(i);
      if (0x20 > chr && 0x9 != chr && 0xA != chr && 0xD != chr
          || 0xD7FF < chr && (0xE000 > chr || 0xFFFE == chr || 0xFFFF == chr || 0x10FFFF < chr)) {
        context.addError(
            ValidationError.create(errorMessage).setField(" " + chr + " (0x" + Integer.toHexString(chr) + ", pos " + (i + 1) + ")"));
        ok = false;
      }
    }

    return ok;
  }
}
