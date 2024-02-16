/**
 *
 */
package de.mpg.mpdl.inge.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Util class for setting URIs
 *
 * @author walter
 *
 */
public class UriBuilder {

  /**
   * get URI for publication item
   *
   * @param itemObjectId
   * @return
   * @throws URISyntaxException
   */
  public static URI getItemObjectLink(String itemObjectId) throws URISyntaxException {
    return new URI(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL)
        + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_CONTEXT_PATH)
        + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_ITEM_PATTERN).replace("$1", itemObjectId));
  }

  /**
   * get URI for publication item with version
   *
   * @param itemObjectId (format: item_12345)
   * @param versionNumber (format: 12)
   * @return
   * @throws URISyntaxException
   */
  public static URI getItemObjectAndVersionLink(String itemObjectId, int versionNumber) throws URISyntaxException {
    return new URI(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL)
        + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_CONTEXT_PATH)
        + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_ITEM_PATTERN).replace("$1", (itemObjectId + "_" + versionNumber)));
  }

  /**
   * get URI for component contained in a publication item
   *
   * @param itemObjectId (format: item_12345)
   * @param versionNumber (format: 12)
   * @return
   * @throws URISyntaxException
   */
  public static URI getItemComponentLink(String itemObjectId, int versionNumber, String fileId, String fileName) throws URISyntaxException {
    return new URI(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL)
        + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_CONTEXT_PATH)
        + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_COMPONENT_PATTERN).replace("$1", (itemObjectId + "_" + versionNumber))
            .replace("$2", fileId).replace("$3", URLEncoder.encode(fileName, StandardCharsets.UTF_8)));
  }
}
