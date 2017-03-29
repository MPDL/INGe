/**
 * 
 */
package de.mpg.mpdl.inge.services;

import java.io.IOException;
import java.net.URISyntaxException;

import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * Factory for retrieving an implementation of the @UserInterface
 * 
 * @author walter
 * 
 */
public class SearchInterfaceConnectorFactory {
  private static final String CONNECTOR_CLASS_PROPERTY =
      "inge.inge_services.search_interface.connector_class";

  public static SearchInterface getInstance() throws InstantiationException,
      IllegalAccessException, ClassNotFoundException, IOException, URISyntaxException {
    return (SearchInterface) Class.forName(PropertyReader.getProperty(CONNECTOR_CLASS_PROPERTY))
        .newInstance();
  }
}
