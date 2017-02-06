/**
 * 
 */
package de.mpg.mpdl.inge.services;

import java.io.IOException;
import java.net.URISyntaxException;

import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * Factory for retrieving an implementation of the @OrganizationInterface
 * 
 * @author walter
 * 
 */
public class OrganizationInterfaceConnectorFactory {
  private static final String CONNECTOR_CLASS_PROPERTY =
      "inge.inge_services.organization_interface.connector_class";

  public OrganizationInterface getInstance() throws InstantiationException, IllegalAccessException,
      ClassNotFoundException, IOException, URISyntaxException {
    return (OrganizationInterface) Class.forName(
        PropertyReader.getProperty(CONNECTOR_CLASS_PROPERTY)).newInstance();
  }
}
