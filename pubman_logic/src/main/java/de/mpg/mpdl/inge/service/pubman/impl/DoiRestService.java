package de.mpg.mpdl.inge.service.pubman.impl;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.transformation.Transformer;
import de.mpg.mpdl.inge.transformation.TransformerCache;
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

  private static final Logger logger = Logger.getLogger(DoiRestService.class);

  public void main(String[] args) throws Exception {
    logger.info("DOI: " + getNewDoi(new PubItemVO()));
  }

  /**
   * creates a new DOI for the given item
   * 
   * @param pubItem
   * @return new DOI
   */
  // TODO change PubManException
  public static String getNewDoi(PubItemVO pubItem) throws Exception {
    if (logger.isDebugEnabled()) {
      logger.debug("Getting new DOI for [" + pubItem.getVersion().getObjectId() + "]");
    }

    // validate if a DOI can be generated for the given item
    if (!isDoiReady(pubItem)) {
      throw new IngeTechnicalException();
    }

    String doi = "";

    try {
      // Generate metadata xml for the DOI service
      String itemXml = XmlTransformingService.transformToItem(pubItem);
      Transformer transformer =
          TransformerCache.getTransformer(TransformerFactory.getInternalFormat(), TransformerFactory.FORMAT.DOI_METADATA_XML);
      StringWriter wr = new StringWriter();

      transformer.transform(new TransformerStreamSource(new ByteArrayInputStream(itemXml.getBytes())), new TransformerStreamResult(wr));

      // REST request to the DOI service for creating a new DOI
      RequestEntity xmlEntity = new StringRequestEntity(wr.toString(), "text/xml", "UTF-8");
      String queryParams =
          "?url=" + PropertyReader.getProperty("inge.pubman.instance.url") + PropertyReader.getProperty("inge.pubman.instance.context.path")
              + (PropertyReader.getProperty("inge.pubman.item.pattern")).replace("$1", pubItem.getVersion().getObjectId()) + "&suffix="
              + pubItem.getVersion().getObjectId().substring(pubItem.getVersion().getObjectId().indexOf(":") + 1);
      HttpClient client = new HttpClient();
      client.getParams().setAuthenticationPreemptive(true);
      Credentials defaultcreds = new UsernamePasswordCredentials(PropertyReader.getProperty("inge.doi.service.user"),
          PropertyReader.getProperty("inge.doi.service.password"));
      client.getState().setCredentials(AuthScope.ANY, defaultcreds);
      client.getParams().setParameter(HttpClientParams.ALLOW_CIRCULAR_REDIRECTS, true);
      PutMethod putMethod = new PutMethod(
          PropertyReader.getProperty("inge.doi.service.url") + PropertyReader.getProperty("inge.doi.service.create.url") + queryParams);
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
      logger.error("Error getting new DOI for [" + pubItem.getVersion().getObjectId() + "]", e);
      throw new Exception("Error getting new DOI for [" + pubItem.getVersion().getObjectId() + "]", e);
    }

    if (logger.isDebugEnabled()) {
      logger.debug("Successfully retrieved new DOI for [" + pubItem.getVersion().getObjectId() + "]");
    }

    return doi;
  }

  /**
   * Checks if the given item is suitable for getting a new DOI
   * 
   * @param pubItem
   * @return
   */
  public static boolean isDoiReady(PubItemVO pubItem) {
    boolean doiReady = false;
    // Item must be released to create a DOI
    if (ItemVO.State.RELEASED.equals(pubItem.getVersion().getState()) == false) {
      return false;
    }
    // Item must not contain any DOI to create a DOI
    for (IdentifierVO identifier : pubItem.getMetadata().getIdentifiers()) {
      if (IdType.DOI.equals(identifier.getType())) {
        return false;
      }
    }
    // Item must include at least one fulltext to create a DOI
    for (FileVO file : pubItem.getFiles()) {
      if (file.getVisibility() == FileVO.Visibility.PUBLIC
          && ("http://purl.org/escidoc/metadata/ves/content-categories/any-fulltext".equals(file.getContentCategory())
              || "http://purl.org/escidoc/metadata/ves/content-categories/pre-print".equals(file.getContentCategory())
              || "http://purl.org/escidoc/metadata/ves/content-categories/post-print".equals(file.getContentCategory())
              || "http://purl.org/escidoc/metadata/ves/content-categories/publisher-version".equals(file.getContentCategory()))) {
        doiReady = true;
      }
    }

    // TODO Metadaten Publisher (see Colab:
    // http://colab.mpdl.mpg.de/mediawiki/PubMan_Func_Spec_DOI_creation/assigning)
    return doiReady;
  }

}
