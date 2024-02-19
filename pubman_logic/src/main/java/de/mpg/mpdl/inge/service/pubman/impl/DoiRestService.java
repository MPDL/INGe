package de.mpg.mpdl.inge.service.pubman.impl;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO.Visibility;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO.State;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.util.EntityTransformer;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.transformation.Transformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.results.TransformerStreamResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerStreamSource;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * Class handling REST request to the MPDL DOxI
 *
 * @author walter
 *
 */
public class DoiRestService {

  private static final Logger logger = LogManager.getLogger(DoiRestService.class);


  /**
   * creates a new DOI for the given item
   *
   * @param pubItem
   * @return new DOI
   */
  // TODO change PubManException
  public static String getNewDoi(ItemVersionVO pubItem) throws Exception {
    if (logger.isDebugEnabled()) {
      logger.debug("Getting new DOI for [" + pubItem.getObjectId() + "]");
    }

    // validate if a DOI can be generated for the given item
    if (!isDoiReady(pubItem)) {
      throw new IngeTechnicalException();
    }

    String doi = "";

    try {
      // Generate metadata xml for the DOI service
      String itemXml = XmlTransformingService.transformToItem(EntityTransformer.transformToOld(pubItem));
      Transformer transformer =
          TransformerFactory.newTransformer(TransformerFactory.getInternalFormat(), TransformerFactory.FORMAT.DOI_METADATA_XML);
      StringWriter wr = new StringWriter();
      //      itemXml = StringEscapeUtils.escapeXml(itemXml);
      transformer.transform(new TransformerStreamSource(new ByteArrayInputStream(itemXml.getBytes(StandardCharsets.UTF_8))),
          new TransformerStreamResult(wr));

      // REST request to the DOI service for creating a new DOI
      RequestEntity xmlEntity = new StringRequestEntity(wr.toString(), "text/xml", "UTF-8");
      String queryParams = "?url=" + URLEncoder.encode(
          PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL)
              + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_CONTEXT_PATH)
              + (PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_ITEM_PATTERN)).replace("$1", pubItem.getObjectId()),
          StandardCharsets.UTF_8) + "&suffix=" + pubItem.getObjectId().substring(pubItem.getObjectId().indexOf("_") + 1);
      HttpClient client = new HttpClient();
      client.getParams().setAuthenticationPreemptive(true);
      Credentials defaultcreds = new UsernamePasswordCredentials(PropertyReader.getProperty(PropertyReader.INGE_DOI_SERVICE_USER),
          PropertyReader.getProperty(PropertyReader.INGE_DOI_SERVICE_PASSWORD));
      client.getState().setCredentials(AuthScope.ANY, defaultcreds);
      client.getParams().setParameter(HttpClientParams.ALLOW_CIRCULAR_REDIRECTS, true);
      PutMethod putMethod = new PutMethod(PropertyReader.getProperty(PropertyReader.INGE_DOI_SERVICE_URL)
          + PropertyReader.getProperty(PropertyReader.INGE_DOI_SERVICE_CREATE_URL) + queryParams);
      putMethod.setRequestEntity(xmlEntity);
      int statusCode = client.executeMethod(putMethod);

      // throw Exception if the DOI service request fails
      if (statusCode != 201) {
        String responseBody = putMethod.getResponseBodyAsString();
        logger.error("Error occured, when contacting DOxI. StatusCode=" + statusCode);
        logger.error(putMethod.getResponseBodyAsString());
        throw new Exception("Error occured, when contacting DOxI. StatusCode=" + statusCode + "\nServer responded with: " + responseBody);
      }

      doi = putMethod.getResponseBodyAsString();

    } catch (Exception e) {
      logger.error("Error getting new DOI for [" + pubItem.getObjectId() + "]", e);
      throw new Exception("Error getting new DOI for [" + pubItem.getObjectId() + "]", e);
    }

    if (logger.isDebugEnabled()) {
      logger.debug("Successfully retrieved new DOI for [" + pubItem.getObjectId() + "]");
    }

    return doi;
  }

  /**
   * Checks if the given item is suitable for getting a new DOI
   *
   * @param pubItem
   * @return
   */
  public static boolean isDoiReady(ItemVersionVO pubItem) {
    boolean doiReady = false;
    // useDOI must be true
    if (!PropertyReader.getProperty(PropertyReader.INGE_DOI_SERVICE_USE).equalsIgnoreCase("true")) {
      return false;
    }

    // Item must be released to create a DOI
    if (State.RELEASED.equals(pubItem.getVersionState()) == false) {
      return false;
    }

    // Item must not contain any DOI to create a DOI
    for (IdentifierVO identifier : pubItem.getMetadata().getIdentifiers()) {
      if (IdType.DOI.equals(identifier.getType())) {
        return false;
      }
    }

    // Item must include at least one fulltext to create a DOI
    for (FileDbVO file : pubItem.getFiles()) {
      if (file.getVisibility() == Visibility.PUBLIC && ("any-fulltext".equals(file.getMetadata().getContentCategory())
          || "pre-print".equals(file.getMetadata().getContentCategory()) || "post-print".equals(file.getMetadata().getContentCategory())
          || "publisher-version".equals(file.getMetadata().getContentCategory()) || "code".equals(file.getMetadata().getContentCategory())
          || "research-data".equals(file.getMetadata().getContentCategory())
          || "multimedia".equals(file.getMetadata().getContentCategory()))) {
        doiReady = true;
        break;
      }
    }

    // TODO Metadaten Publisher (see Colab:
    // http://colab.mpdl.mpg.de/mediawiki/PubMan_Func_Spec_DOI_creation/assigning)
    return doiReady;
  }

}
