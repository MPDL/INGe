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
  private static final String CONNECTOR_CLASS = "inge.inge_services.user_interface.connector_class";

  public ItemInterface getInstance() throws InstantiationException, IllegalAccessException,
      ClassNotFoundException, IOException, URISyntaxException {
    return (ItemInterface) Class.forName(CONNECTOR_CLASS).newInstance();
  }
}