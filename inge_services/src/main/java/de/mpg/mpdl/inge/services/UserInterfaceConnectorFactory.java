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
public class UserInterfaceConnectorFactory {
  private static final String CONNECTOR_CLASS_PROPERTY =
      "inge.inge_services.user_interface.connector_class";

  public static UserInterface getInstance() throws InstantiationException, IllegalAccessException,
      ClassNotFoundException, IOException, URISyntaxException {
    return (UserInterface) Class.forName(PropertyReader.getProperty(CONNECTOR_CLASS_PROPERTY))
        .newInstance();
  }
}
