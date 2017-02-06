/**
 * 
 */
package de.mpg.mpdl.inge.services;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Factory for retrieving an implementation of the @FileStorageInterface
 * 
 * @author walter
 * 
 */
public class FileStorageInterfaceConnectorFactory {
  private static final String CONNECTOR_CLASS_PROPERTY =
      "inge.inge_services.file_storage_interface.connector_class";

  public static FileStorageInterface getInstance() throws InstantiationException,
      IllegalAccessException, ClassNotFoundException, IOException, URISyntaxException {
    return (FileStorageInterface) Class.forName(
        PropertyReader.getProperty(CONNECTOR_CLASS_PROPERTY)).newInstance();
  }
}
