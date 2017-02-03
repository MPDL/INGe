/**
 * 
 */
package de.mpg.mpdl.inge.services;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Factory for retrieving an implementation of the @UserInterface
 * 
 * @author walter
 * 
 */
public class UserInterfaceConnectorFactory {
  private static final String CONNECTOR_CLASS = "inge.inge_services.user_interface.connector_class";

  public UserInterface getInstance() throws InstantiationException, IllegalAccessException,
      ClassNotFoundException, IOException, URISyntaxException {
    return (UserInterface) Class.forName(CONNECTOR_CLASS).newInstance();
  }
}
