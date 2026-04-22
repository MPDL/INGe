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

    // Prevent CRLF injection and other control characters
    if (containsControlCharacters(target)) {
      throw new IllegalArgumentException("Redirect target contains invalid characters");
    }

    // Relative URLs are generally allowed
    // Check for both / and \ as some browsers interpret \ as /
    // Also check for encoded versions
    String normalizedTarget = target.replace('\\', '/');
    if (normalizedTarget.startsWith("/") && !normalizedTarget.startsWith("//")) {
      // Still need to check if it's an encoded backslash trick like /%5c
      if (normalizedTarget.contains("%5c") || normalizedTarget.contains("%5C")) {
        throw new IllegalArgumentException("Redirect target contains encoded backslash");
      }
      return;
    }

    try {
      URL url = new URL(target);
      String host = url.getHost().toLowerCase();

      // Prevent User-Info (e.g. http://localhost@evil.com)
      if (url.getUserInfo() != null) {
        throw new IllegalArgumentException("Redirect target must not contain user info");
      }

      if (!ALLOWED_HOSTS.contains(host) && !"localhost".equals(host) && !"127.0.0.1".equals(host)) {
        throw new IllegalArgumentException("Redirect to external host not allowed: " + host);
      }
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("Invalid redirect target URL: " + target);
    }
  }

  private static boolean containsControlCharacters(String s) {
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c < 32 || c == 127) {
        return true;
      }
    }
    return false;
  }
}
