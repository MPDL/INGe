package de.mpg.mpdl.inge.service.pubman.impl;

import java.net.URI;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.inge.model.valueobjects.PidServiceResponseVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.PidService;
import de.mpg.mpdl.inge.util.PropertyReader;


/**
 * PidService implementation
 * 
 * @author przibylla
 * 
 */
@Service
@Primary
public class PidServiceImpl implements PidService {

  private static final Logger logger = Logger.getLogger(PidServiceImpl.class);

  private static final String URL = "url";

  private String createPath;

  private WebTarget target;

  public PidServiceImpl() {
    this.init();
  }

  private void init() {
    this.createPath = PropertyReader.getProperty(PropertyReader.INGE_PID_SERVICE_CREATE_PATH);
    String user = PropertyReader.getProperty(PropertyReader.INGE_PID_SERVICE_USER);
    String passwd = PropertyReader.getProperty(PropertyReader.INGE_PID_SERVICE_PASSWORD);
    int timeout = Integer.parseInt(PropertyReader.getProperty(PropertyReader.INGE_PID_SERVICE_TIMEOUT));
    String serviceUrl = PropertyReader.getProperty(PropertyReader.INGE_PID_SERVICE_URL);

    ClientConfig clientConfig = new ClientConfig();

    HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder().credentials(user, passwd).build();

    clientConfig.register(feature);

    Client client = ClientBuilder.newClient(clientConfig);

    client.property(ClientProperties.CONNECT_TIMEOUT, timeout);
    client.property(ClientProperties.READ_TIMEOUT, timeout);

    this.target = client.target(serviceUrl);
  }

  @Override
  public PidServiceResponseVO createPid(URI url) throws IngeApplicationException, TechnicalException {
    Response response;
    try {
      Form form = new Form();
      form.param(URL, url.toString());

      response = this.target.path(this.createPath).request(MediaType.TEXT_PLAIN_TYPE).post(Entity.form(form));

      if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
        String xml = response.readEntity(String.class);
        PidServiceResponseVO pidServiceResponseVO = XmlTransformingService.transformToPidServiceResponse(xml);
        return pidServiceResponseVO;
      } else {
        logger.error("Error occured, when contacting DOxI. StatusCode= " + response.getStatus());
        throw new IngeApplicationException("Error occured, when contacting DOxI: " + response.readEntity(String.class));
      }
    } catch (Exception e) {
      logger.error("Error occured, when contacting DOxI.", e);
      throw new IngeApplicationException("Error occured, when contacting DOxI", e);
    }


  }

}
