package de.mpg.mpdl.inge.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for URL validation to prevent Open Redirect vulnerabilities.
 */
public class UrlValidator {

  /**
   * Validates if the given target URL is safe for redirection. Throws an IllegalArgumentException
   * if the target is invalid.
   *
   * @param target The URL to validate.
   * @throws IllegalArgumentException if the target is considered unsafe or invalid.
   */
  public static void validate(String target) {
    if (target == null || target.isEmpty()) {
      throw new IllegalArgumentException("Target URL is null or empty");
    }

    List<String> allowedBaseUrls = getAllowedBaseUrls();

    try {
      URL targetUrl = new URL(target);
      String targetHost = targetUrl.getHost();

      // Always allow localhost for development
      if (targetHost.equalsIgnoreCase("localhost") || targetHost.equals("127.0.0.1")) {
        return;
      }

      for (String allowedBase : allowedBaseUrls) {
        try {
          URL allowedUrl = new URL(allowedBase);

          if (targetUrl.getProtocol().equalsIgnoreCase(allowedUrl.getProtocol()) && targetHost.equalsIgnoreCase(allowedUrl.getHost())) {

            int targetPort = targetUrl.getPort() == -1 ? targetUrl.getDefaultPort() : targetUrl.getPort();
            int allowedPort = allowedUrl.getPort() == -1 ? allowedUrl.getDefaultPort() : allowedUrl.getPort();

            if (targetPort == allowedPort) {
              return; // Match found
            }
          }
        } catch (MalformedURLException e) {
          // Ignore invalid base URLs in configuration
        }
      }

      throw new IllegalArgumentException("Invalid target URL for redirection: " + target);
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("Invalid target URL format: " + target, e);
    }
  }

  private static List<String> getAllowedBaseUrls() {
    List<String> allowed = new ArrayList<String>();

    // Add default target
    String defaultTarget = PropertyReader.getProperty(PropertyReader.INGE_AA_DEFAULT_TARGET);
    if (defaultTarget != null && !defaultTarget.isEmpty()) {
      allowed.add(defaultTarget);
    }

    // Add instance URL
    String instanceUrl = PropertyReader.getProperty(PropertyReader.INGE_AA_INSTANCE_URL);
    if (instanceUrl != null && !instanceUrl.isEmpty()) {
      allowed.add(instanceUrl);
    }

    // Add pubman instance URL
    String pubmanUrl = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL);
    if (pubmanUrl != null && !pubmanUrl.isEmpty()) {
      allowed.add(pubmanUrl);
    }

    if (allowed.isEmpty()) {
      throw new IllegalArgumentException("No allowed targets configured (inge.aa.default.target is missing)");
    }

    return allowed;
  }

  /**
   * Validates if the given target URL is safe for redirection. It compares the target URL against
   * the default target configured in properties.
   *
   * @param target The URL to validate.
   * @return true if the target is considered safe, false otherwise.
   * @deprecated Use {@link #validate(String)} instead to get detailed error information via
   *             Exception.
   */
  @Deprecated
  public static boolean isValidTarget(String target) {
    try {
      validate(target);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
