/**
 *
 */
package de.mpg.mpdl.inge.util;

import java.net.URI;
import java.net.URISyntaxException;

public class UriBuilder {

  private UriBuilder() {}

  public static URI getItemLink() throws URISyntaxException {
    return new URI( //
        PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL) + //
            PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_CONTEXT_PATH) + //
            PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_ITEM_PATTERN) //
    );
  }

  public static URI getItemObjectLink(String itemObjectId) throws URISyntaxException {
    return new URI( //
        PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL) + //
            PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_CONTEXT_PATH) + //
            PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_ITEM_PATTERN) //
                .replace("$1", itemObjectId) //
    );
  }

  public static URI getItemObjectAndVersionLink(String itemObjectId, int versionNumber) throws URISyntaxException {
    return new URI( //
        PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL) + //
            PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_CONTEXT_PATH) + //
            PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_ITEM_PATTERN) //
                .replace("$1", (itemObjectId + "_" + versionNumber)) //
    );
  }

  public static URI getItemComponentLink() throws URISyntaxException {
    return new URI( //
        PropertyReader.getProperty(PropertyReader.INGE_REST_SERVICE_URL) + //
            PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_COMPONENT_PATTERN) //
    );
  }

  public static URI getItemComponentLink(String itemObjectId, int versionNumber, String fileId) throws URISyntaxException {
    return new URI( //
        PropertyReader.getProperty(PropertyReader.INGE_REST_SERVICE_URL) + //
            PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_COMPONENT_PATTERN) //
                .replace("$1", (itemObjectId + "_" + versionNumber)) //
                .replace("$2", fileId) //
    );
  }
}
