package de.mpg.mpdl.inge.util;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.log4j.Logger;

/**
 * Utility class to validate target URLs for redirects to prevent Open Redirect vulnerabilities.
 */
public class ProxyValidator {

  private static final Logger logger = Logger.getLogger(ProxyValidator.class);

  /**
   * Checks if the given target URL is valid for redirection.
   * A URL is considered valid if it is relative or belongs to an allowed domain.
   *
   * @param target The target URL to validate.
   * @return true if the target is valid, false otherwise.
   */
  public static boolean isValidTarget(String target) {
    if (target == null || target.isEmpty()) {
      return false;
    }

    // Allow relative URLs (starting with / but not //)
    if (target.startsWith("/") && !target.startsWith("//")) {
      return true;
    }

    try {
      URI uri = new URI(target);
      String host = uri.getHost();

      if (host == null) {
        // If no host, and not starting with /, it might be invalid or relative
        return !target.contains(":");
      }

      String pubmanInstanceUrl = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL);
      String aaInstanceUrl = PropertyReader.getProperty(PropertyReader.INGE_AA_INSTANCE_URL);

      if (isSameDomain(host, pubmanInstanceUrl) || isSameDomain(host, aaInstanceUrl)) {
        return true;
      }

      logger.warn("Invalid redirect target blocked: " + target);
    } catch (URISyntaxException e) {
      logger.warn("Malformed redirect target: " + target);
    }

    return false;
  }

  private static boolean isSameDomain(String host, String instanceUrl) {
    if (instanceUrl == null || instanceUrl.isEmpty()) {
      return false;
    }
    try {
      URI instanceUri = new URI(instanceUrl);
      String instanceHost = instanceUri.getHost();
      return host.equalsIgnoreCase(instanceHost);
    } catch (URISyntaxException e) {
      return false;
    }
  }
}
