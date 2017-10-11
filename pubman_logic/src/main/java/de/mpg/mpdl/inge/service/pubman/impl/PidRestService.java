package de.mpg.mpdl.inge.pubman;

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

import de.mpg.mpdl.inge.model.valueobjects.PidServiceResponseVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.util.PropertyReader;

public class PidRestService {

  private static final Logger logger = Logger.getLogger(PidRestService.class);

  private static final String URL = "url";

  private WebTarget target;

  public PidRestService() {
    this.init();
  }

  private void init() {
    String user = PropertyReader.getProperty("escidoc.pid.service.user");
    String passwd = PropertyReader.getProperty("escidoc.pid.service.password");
    int timeout = Integer.parseInt(PropertyReader.getProperty("escidoc.pid.service.timeout"));
    String serviceUrl = PropertyReader.getProperty("escidoc.pid.service.url");

    ClientConfig clientConfig = new ClientConfig();

    HttpAuthenticationFeature feature =
        HttpAuthenticationFeature.basicBuilder().credentials(user, passwd).build();

    clientConfig.register(feature);

    Client client = ClientBuilder.newClient(clientConfig);

    client.property(ClientProperties.CONNECT_TIMEOUT, timeout);
    client.property(ClientProperties.READ_TIMEOUT, timeout);

    this.target = client.target(serviceUrl);
  }

  public PidServiceResponseVO createPid(URI url) throws IngeApplicationException,
      TechnicalException {
    String createPath = PropertyReader.getProperty("escidoc.pid.service.create.path");

    Form form = new Form();
    form.param(URL, url.toString());

    Response response =
        target.path(createPath).request(MediaType.TEXT_PLAIN_TYPE).post(Entity.form(form));

    if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
      String xml = response.readEntity(String.class);
      PidServiceResponseVO pidServiceResponseVO =
          XmlTransformingService.transformToPidServiceResponse(xml);
      return pidServiceResponseVO;
    }

    logger.error("Error occured, when contacting DOxI. StatusCode=" + response.getStatus());
    throw new IngeApplicationException("Error occured, when contacting DOxI: "
        + response.readEntity(String.class));
  }

  public static void main(String[] args) throws Exception {
    PidRestService pidRestService = new PidRestService();
    String url = "www.test.de/" + Math.random();
    logger.info("PID: " + pidRestService.createPid(new URI(url)));
  }

}
