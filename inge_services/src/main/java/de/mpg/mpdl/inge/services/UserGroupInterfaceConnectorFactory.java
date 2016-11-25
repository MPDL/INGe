/**
 * 
 */
package de.mpg.mpdl.inge.services;

import java.io.IOException;
import java.net.URISyntaxException;

import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * Factory for retrieving an implementation of the @UserGroupInterface
 * 
 * @author walter
 * 
 */
public class UserGroupInterfaceConnectorFactory {
  private static final String CONNECTOR_CLASS =
      "inge.inge_services.usergroup_interface.connector_class";

  public ItemInterface getInstance() throws InstantiationException, IllegalAccessException,
      ClassNotFoundException, IOException, URISyntaxException {
    return (ItemInterface) Class.forName(CONNECTOR_CLASS).newInstance();
  }
}
