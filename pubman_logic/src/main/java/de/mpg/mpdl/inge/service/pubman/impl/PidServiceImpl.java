package de.mpg.mpdl.inge.service.pubman.impl;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.inge.model.valueobjects.PidServiceResponseVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
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

  private static final Logger logger = LogManager.getLogger(PidServiceImpl.class);

  private static final String URL = "url";

  private String createPath;

  //private WebTarget target;
  private Executor httpExecutor;

  private String serviceUrl;

  public PidServiceImpl() {
    this.init();
  }

  private void init() {
    this.createPath = PropertyReader.getProperty(PropertyReader.INGE_PID_SERVICE_CREATE_PATH);
    String user = PropertyReader.getProperty(PropertyReader.INGE_PID_SERVICE_USER);
    String passwd = PropertyReader.getProperty(PropertyReader.INGE_PID_SERVICE_PASSWORD);
    int timeout = Integer.parseInt(PropertyReader.getProperty(PropertyReader.INGE_PID_SERVICE_TIMEOUT));
    this.serviceUrl = PropertyReader.getProperty(PropertyReader.INGE_PID_SERVICE_URL);

    //ClientConfig clientConfig = new ClientConfig();

    BasicCredentialsProvider basicCredProvider = new BasicCredentialsProvider();
    basicCredProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, passwd));

    RequestConfig config =
        RequestConfig.custom().setConnectTimeout(timeout).setConnectionRequestTimeout(timeout).setSocketTimeout(timeout).build();

    HttpClient httpClient =
        HttpClientBuilder.create().setDefaultRequestConfig(config).setDefaultCredentialsProvider(basicCredProvider).build();

    this.httpExecutor = Executor.newInstance(httpClient);
  }

  @Override
  public PidServiceResponseVO createPid(URI url) throws IngeApplicationException {
    try {

      URI uri = URI.create(this.serviceUrl).resolve(this.createPath);

      HttpResponse httpResponse =
          this.httpExecutor.execute(Request.Post(uri).bodyForm(Form.form().add(URL, url.toString()).build())).returnResponse();

      if (HttpStatus.SC_CREATED == httpResponse.getStatusLine().getStatusCode()) {
        String xml = EntityUtils.toString(httpResponse.getEntity());
        PidServiceResponseVO pidServiceResponseVO = XmlTransformingService.transformToPidServiceResponse(xml);
        return pidServiceResponseVO;
      } else {
        logger.error("Error occured, when contacting DOxI. StatusCode= " + httpResponse.getStatusLine().getStatusCode());
        throw new IngeApplicationException("Error occured, when contacting DOxI: " + EntityUtils.toString(httpResponse.getEntity()));
      }
    } catch (Exception e) {
      logger.error("Error occured, when contacting DOxI.", e);
      throw new IngeApplicationException("Error occured, when contacting DOxI", e);
    }


  }

}
