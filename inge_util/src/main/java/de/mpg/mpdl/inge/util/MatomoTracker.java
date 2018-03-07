package de.mpg.mpdl.inge.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

/**
 * 
 * @author walter
 *
 */
public class MatomoTracker {

  private final static Logger logger = Logger.getLogger(MatomoTracker.class);
  private final static String HOST_URL = PropertyReader.getProperty("inge.matomo.analytics.base.uri") + "/piwik.php";

  public final static String AUTH_TOKEN = "token_auth";
  public final static String REC = "rec";
  public final static String SITE_ID = "idsite";
  public final static String SITE_URL = "url";
  public final static String USER_IP = "cip";

  public MatomoTracker() {

  }

  public static void trackUrl(HashMap<String, String> parameterMap) {
    HttpClient client = new HttpClient();
    GetMethod getMethod = new GetMethod(HOST_URL);
    getMethod.setQueryString(getUrlEncodedQueryString(parameterMap));
    try {
      try {
        int i = client.executeMethod(getMethod);
        System.out.println(i);
      } catch (IOException e) {
        logger.error("Could not execute statistics call", e);
      }
    } finally {
      getMethod.releaseConnection();
    }
  }

  private static String getUrlEncodedQueryString(HashMap<String, String> parameterMap) {
    StringBuilder sb = new StringBuilder();
    for (Entry<String, String> parameter : parameterMap.entrySet()) {
      if (sb.length() > 0) {
        sb.append("&");
      }
      try {
        StringBuilder sb2 = new StringBuilder();
        sb2.append(parameter.getKey());
        sb2.append("=");
        sb2.append(URLEncoder.encode(parameter.getValue().toString(), StandardCharsets.UTF_8.toString()));
        sb.append(sb2);
      } catch (UnsupportedEncodingException e) {
        System.err.println(e.getMessage());
      }
    }


    return sb.toString();
  }
}
