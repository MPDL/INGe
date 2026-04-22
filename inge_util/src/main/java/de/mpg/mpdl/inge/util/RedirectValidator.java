package de.mpg.mpdl.inge.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Validator for redirect URLs to prevent Open Redirect vulnerabilities.
 */
public class RedirectValidator {

  private static final Set<String> ALLOWED_HOSTS = new HashSet<>();

  static {
    addAllowedHost(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL));
    addAllowedHost(PropertyReader.getProperty(PropertyReader.INGE_AA_INSTANCE_URL));
    addAllowedHost(PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL));
    addAllowedHost(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_PRESENTATION_URL));
  }

  private static void addAllowedHost(String urlString) {
    if (urlString != null && !urlString.isEmpty()) {
      try {
        URL url = new URL(urlString);
        ALLOWED_HOSTS.add(url.getHost().toLowerCase());
      } catch (MalformedURLException e) {
        // Ignore invalid URLs in configuration
      }
    }
  }

  /**
   * Validates if the given target URL is allowed for redirection.
   *
   * @param target The target URL to validate.
   * @throws IllegalArgumentException if the target is invalid or not allowed.
   */
  public static void validate(String target) {
    if (target == null || target.isEmpty()) {
      throw new IllegalArgumentException("Redirect target must not be null or empty");
    }

    // Relative URLs are generally allowed
    if (target.startsWith("/") && !target.startsWith("//")) {
      return;
    }

    try {
      URL url = new URL(target);
      String host = url.getHost().toLowerCase();
      if (!ALLOWED_HOSTS.contains(host)) {
        throw new IllegalArgumentException("Redirect to external host not allowed: " + host);
      }
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("Invalid redirect target URL: " + target);
    }
  }
}
