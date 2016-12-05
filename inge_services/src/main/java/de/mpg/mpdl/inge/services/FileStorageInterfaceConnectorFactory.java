/**
 * 
 */
package de.mpg.mpdl.inge.services;

import java.io.IOException;
import java.net.URISyntaxException;

import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * Factory for retrieving an implementation of the @FileStorageInterface
 * 
 * @author walter
 * 
 */
public class FileStorageInterfaceConnectorFactory {
  private static final String CONNECTOR_CLASS =
      "inge.inge_services.file_storage_interface.connector_class";

  public static FileStorageInterface getInstance() throws InstantiationException,
      IllegalAccessException, ClassNotFoundException, IOException, URISyntaxException {
    return (FileStorageInterface) Class.forName(CONNECTOR_CLASS).newInstance();
  }
}
