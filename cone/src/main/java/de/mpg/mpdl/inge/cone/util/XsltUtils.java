package de.mpg.mpdl.inge.cone.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class XsltUtils {

  private XsltUtils() {

  }

  public static boolean validateDate(String date) {
    SimpleDateFormat yearDf = new SimpleDateFormat("yyyy");
    yearDf.setLenient(false);
    SimpleDateFormat yearMonthDf = new SimpleDateFormat("yyyy-MM");
    yearMonthDf.setLenient(false);
    SimpleDateFormat yearMonthDayDf = new SimpleDateFormat("yyyy-MM-dd");
    yearMonthDayDf.setLenient(false);

    try {
      if (null != date && 4 == date.trim().length()) {
        yearDf.parse(date);
        return true;
      } else if (7 == date.trim().length()) {
        yearMonthDf.parse(date);
        return true;
      } else if (10 == date.trim().length()) {
        yearMonthDayDf.parse(date);
        return true;
      } else {
        return false;
      }

    } catch (ParseException e) {
      return false;
    }

  }

}
