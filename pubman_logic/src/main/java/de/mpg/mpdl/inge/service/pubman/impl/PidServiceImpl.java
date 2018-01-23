package de.mpg.mpdl.inge.service.pubman.impl;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
    this.createPath = PropertyReader.getProperty("inge.pid.service.create.path");
    String user = PropertyReader.getProperty("inge.pid.service.user");
    String passwd = PropertyReader.getProperty("inge.pid.service.password");
    int timeout = Integer.parseInt(PropertyReader.getProperty("inge.pid.service.timeout"));
    String serviceUrl = PropertyReader.getProperty("inge.pid.service.url");

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
