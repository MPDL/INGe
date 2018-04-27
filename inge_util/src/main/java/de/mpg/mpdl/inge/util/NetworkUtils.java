package de.mpg.mpdl.inge.util;

public class NetworkUtils {


  public static boolean checkIPMatching(String pattern, String address) {
    if (pattern.equals("*.*.*.*") || pattern.equals("*"))
      return true;

    String[] mask = pattern.split("\\.");
    String[] ip_address = address.split("\\.");
    for (int i = 0; i < mask.length; i++) {
      if (mask[i].equals("*") || mask[i].equals(ip_address[i]))
        continue;
      else if (mask[i].contains("-")) {
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
  
}
