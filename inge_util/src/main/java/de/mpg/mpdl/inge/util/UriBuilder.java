/**
 * 
 */
package de.mpg.mpdl.inge.util;

import java.net.URI;
import java.net.URISyntaxException;

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
   * @throws MalformedURIException
   * @throws URISyntaxException
   */
  public static URI getItemObjectLink(String itemObjectId) throws URISyntaxException {
    return new URI(PropertyReader.getProperty("inge.pubman.instance.url") + PropertyReader.getProperty("inge.pubman.instance.context.path")
        + PropertyReader.getProperty("inge.pubman.item.pattern").replace("$1", itemObjectId));
  }

  /**
   * get URI for publication item with version
   * 
   * @param itemObjectId (format: item_12345)
   * @param versionNumber (format: 12)
   * @return
   * @throws MalformedURIException
   * @throws URISyntaxException
   */
  public static URI getItemObjectAndVersionLink(String itemObjectId, int versionNumber) throws URISyntaxException {
    return new URI(PropertyReader.getProperty("inge.pubman.instance.url") + PropertyReader.getProperty("inge.pubman.instance.context.path")
        + PropertyReader.getProperty("inge.pubman.item.pattern").replace("$1", (itemObjectId + "_" + versionNumber)));
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
    return new URI(PropertyReader.getProperty("inge.pubman.instance.url") + PropertyReader.getProperty("inge.pubman.instance.context.path")
        + PropertyReader.getProperty("inge.pubman.component.pattern").replace("$1", (itemObjectId + "_" + versionNumber)).replace("$2", fileId)
            .replace("$3", fileName));
  }
}
