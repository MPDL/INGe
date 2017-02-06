/**
 * 
 */
package de.mpg.mpdl.inge.services;

import java.io.IOException;
import java.net.URISyntaxException;

import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * Factory for retrieving an implementation of the @ItemInterface
 * 
 * @author walter
 * 
 */
public class ItemInterfaceConnectorFactory {
  private static final String CONNECTOR_CLASS_PROPERTY =
      "inge.inge_services.item_interface.connector_class";

  public static ItemInterface getInstance() throws InstantiationException, IllegalAccessException,
      ClassNotFoundException, IOException, URISyntaxException {
    return (ItemInterface) Class.forName(PropertyReader.getProperty(CONNECTOR_CLASS_PROPERTY))
        .newInstance();
  }
}
