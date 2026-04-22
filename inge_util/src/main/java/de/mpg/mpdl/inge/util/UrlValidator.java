package de.mpg.mpdl.inge.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class for URL validation to prevent Open Redirect vulnerabilities.
 */
public class UrlValidator {

  private static final Logger logger = LogManager.getLogger(UrlValidator.class);

  /**
   * Validates a target URL for redirects. A URL is considered valid if it is relative or starts
   * with an allowed prefix.
   *
   * @param url The URL to validate.
   * @return true if the URL is valid, false otherwise.
   */
  public static boolean isValidRedirectUrl(String url) {
    if (url == null || url.isEmpty()) {
      return false;
    }

    // Relative URLs starting with / (but not //) are generally safe
    if (url.startsWith("/") && !url.startsWith("//")) {
      return true;
    }

    // Check against allowed instance URLs
    String pubmanUrl = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL);
    String aaUrl = PropertyReader.getProperty(PropertyReader.INGE_AA_INSTANCE_URL);
    String coneUrl = PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL);
    String defaultTarget = PropertyReader.getProperty(PropertyReader.INGE_AA_DEFAULT_TARGET);

    if (isAllowed(url, pubmanUrl) || isAllowed(url, aaUrl) || isAllowed(url, coneUrl) || isAllowed(url, defaultTarget)) {
      return true;
    }

    return false;
  }

  /**
   * Validates a target URL for redirects and throws an exception if invalid.
   *
   * @param url The URL to validate.
   * @throws IllegalArgumentException if the URL is invalid.
   */
  public static void validateRedirectUrl(String url) {
    if (!isValidRedirectUrl(url)) {
      logger.warn("Potential Open Redirect attempt blocked for URL: " + url);
      throw new IllegalArgumentException("Invalid redirect target URL: " + url);
    }
  }

  private static boolean isAllowed(String url, String allowedPrefix) {
    return allowedPrefix != null && !allowedPrefix.isEmpty() && url.startsWith(allowedPrefix);
  }
}
