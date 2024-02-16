package de.mpg.mpdl.inge.util;

import java.util.regex.Pattern;

import org.apache.commons.net.util.SubnetUtils;

public class NetworkUtils {


  private static final Pattern urlMatcherPattern = Pattern.compile(

      "^(https?|ftp)://(-\\.)?([^\\s/?\\.#-]+\\.?)+(/[^\\s]*)?$",
      //"^(?:(?:https?|ftp):\\/\\/)(?:\\S+(?::\\S*)?@)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,}))\\.?)(?::\\d{2,5})?(?:[/?#]\\S*)?$",
      Pattern.CASE_INSENSITIVE);



  /**
   * method checks if an address fits in a given ip range (pattern)
   *
   * @param pattern
   * @param address
   * @return
   */
  public static boolean checkIPMatching(String pattern, String address) {

    if (pattern.equals("*.*.*.*") || pattern.equals("*"))
      return true;
    // pattern like 123.123.123.123/31
    if (pattern.contains("/")) {
      SubnetUtils ipRangeUtil = new SubnetUtils(pattern);
      ipRangeUtil.setInclusiveHostCount(true);
      if (ipRangeUtil.getInfo().isInRange(address)) {
        return true;
      } else {
        return false;
      }
    }

    // pattern like 123.123.123.123-124
    String[] mask = pattern.split("\\.");
    String[] ip_address = address.split("\\.");
    for (int i = 0; i < mask.length; i++) {
      if (mask[i].equals("*") || mask[i].equals(ip_address[i])) {
      } else if (mask[i].contains("-")) {
        int min = Integer.parseInt(mask[i].split("-")[0]);
        int max = Integer.parseInt(mask[i].split("-")[1]);
        int ip = Integer.parseInt(ip_address[i]);
        if (ip < min || ip > max)
          return false;
      } else
        return false;
    }
    return true;
  }


  public static Pattern getUrlMatchPattern() {
    return urlMatcherPattern;
  }


  public static void main(String[] args) {

    System.out.println(
        getUrlMatchPattern().matcher("https://qa.inge.mpdl.mpg.de/rest/items/item_3004881_1/component/file_3004882/content").matches());
  }
}
