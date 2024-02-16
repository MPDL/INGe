package de.mpg.mpdl.inge.filestorage.seaweedfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.mpdl.inge.filestorage.FileStorageInterface;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.util.PropertyReader;


/**
 * File storage service for seaweed (handling full text files and so on)
 *
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
@Service
public class SeaweedFileServiceBean implements FileStorageInterface {

  private static final Logger logger = Logger.getLogger(SeaweedFileServiceBean.class);

  private static final String SEAWEED_MASTER_URL = PropertyReader.getProperty(PropertyReader.INGE_FILESTORAGE_SEAWEED_MASTER_SERVER_IP);

  private static final String SEAWEED_DIRECT_SUBMIT_PATH =
      PropertyReader.getProperty(PropertyReader.INGE_FILESTORAGE_SEAWEED_DIRECT_SUBMIT_PATH);
  @Autowired
  private CloseableHttpClient httpClient;

  /**
   * creates a file in the seaweed instance
   * <p>
   * (non-Javadoc)
   *
   * @see de.mpg.mpdl.inge.services.FileStorageInterface#createFile(java.io.InputStream,
   *      java.lang.String)
   *
   * @return json - response returned (including "fid", "fileUrl", "fileName", ...)
   * @throws IOException
   */
  @Override
  public String createFile(InputStream fileInputStream, String fileName) throws IngeTechnicalException {
    String fileId;
    HttpEntity entity =
        MultipartEntityBuilder.create().addBinaryBody("upload_file", fileInputStream, ContentType.DEFAULT_BINARY, fileName).build();

    HttpPost httpPost = new HttpPost(SEAWEED_MASTER_URL + SEAWEED_DIRECT_SUBMIT_PATH);
    httpPost.setEntity(entity);
    CloseableHttpResponse response = null;

    try {
      System.out.println("Trying to create new File [" + fileName + "] on host " + httpPost.getURI());
      response = httpClient.execute(httpPost);
      logger.info(response.getStatusLine());
      HttpEntity responseEntity = response.getEntity();
      String resp = EntityUtils.toString(responseEntity);
      logger.info("Received from SeaweedFS: " + resp);
      ObjectMapper mapper = new ObjectMapper();
      JsonNode jsonObject = mapper.readTree(resp);
      System.out.println("Created Object [" + jsonObject + "]");
      // ensure it is fully consumed
      EntityUtils.consume(responseEntity);
      fileId = jsonObject.findValuesAsText("fid").get(0);
    } catch (Exception e) {
      logger.error("An error occoured, when trying to create file [" + fileName + "]", e);
      throw new IngeTechnicalException("An error occoured, when trying to create file [" + fileName + "]", e);
    } finally {
      try {
        response.close();
      } catch (Exception e) {
        logger.error("An error occoured, when trying to close response for [" + fileName + "]", e);
        throw new IngeTechnicalException("An error occoured, when trying to close response for [" + fileName + "]", e);
      }
    }
    return fileId;
  }

  /**
   * read a file from the seaweed instance to an outputstream
   * <p>
   * (non-Javadoc)
   *
   * @see de.mpg.mpdl.inge.services.FileStorageInterface#readFile(java.lang.String,
   *      java.io.OutputStream)
   *
   * @param fileId - Id of the file to read
   * @param out - OutputStream where result is written
   * @throws IOException
   */
  @Override
  public void readFile(String fileId, OutputStream out) throws IngeTechnicalException {
    System.out.println("Trying to read Id [" + fileId + "]");
    HttpGet httpGet = new HttpGet(SEAWEED_MASTER_URL + "/" + fileId);
    CloseableHttpResponse response = null;
    InputStream retrievedFileInputStream = null;

    try {
      response = this.httpClient.execute(httpGet);
      logger.info(response.getStatusLine());
      logger.info(response.getFirstHeader("Location"));
      HttpEntity responseEntity = response.getEntity();
      retrievedFileInputStream = responseEntity.getContent();
      IOUtils.copy(retrievedFileInputStream, out);
      // ensure it is fully consumed
      EntityUtils.consume(responseEntity);
    } catch (IOException e) {
      logger.error("An error occoured, when trying to retrieve file [" + fileId + "]", e);
      throw new IngeTechnicalException("An error occoured, when trying to retrieve file[" + fileId + "]", e);
    } finally {
      try {
        response.close();
      } catch (IOException e) {
        logger.error("An error occoured, when trying to close response for [" + fileId + "]", e);
        throw new IngeTechnicalException("An error occoured, when trying to close response for [" + fileId + "]", e);
      }
    }
  }

  /**
   * delete a file with a specific id from the seaweed instance
   * <p>
   * (non-Javadoc)
   *
   * @see de.mpg.mpdl.inge.services.FileStorageInterface#deleteFile(java.lang.String)
   *
   * @param fileId - Id of the file to read
   * @throws Exception
   */
  @Override
  public void deleteFile(String fileId) throws IngeTechnicalException {
    System.out.println("Trying to delete Id [" + fileId + "]");
    CloseableHttpResponse response = null;
    try {
      HttpDelete httpDelete = new HttpDelete(SEAWEED_MASTER_URL + "/" + URLEncoder.encode(fileId, StandardCharsets.UTF_8));
      logger.info("Delete request: " + httpDelete.getURI().toString());

      response = httpClient.execute(httpDelete);
      logger.info(response.getStatusLine());
      logger.info(response.getFirstHeader("Location"));
      if (response.getStatusLine().getStatusCode() == 301) {
        logger.info("Redirecting delete manually");
        httpDelete.setURI(new URI(response.getFirstHeader("Location").getValue()));
        response.close();
        System.out.println("[" + httpDelete + "]");
        response = httpClient.execute(httpDelete);
      }
      HttpEntity responseEntity = response.getEntity();
      InputStream retrievedFileInputStream = responseEntity.getContent();
      StringWriter stringWriter = new StringWriter();
      IOUtils.copy(retrievedFileInputStream, stringWriter);
      logger.info("Delete Response: " + stringWriter);
      // ensure it is fully consumed
      EntityUtils.consume(responseEntity);
    } catch (IOException e) {
      logger.error("An error occoured, when trying to delete the file [" + fileId + "]", e);
      throw new IngeTechnicalException("An error occoured, when trying to delete the file [" + fileId + "]", e);
    } catch (URISyntaxException e) {
      logger.error("An error with the generated URI occoured, " + "when trying to delete the file [" + fileId + "]", e);
      throw new IngeTechnicalException("An error with the generated URI occoured, " + "when trying to delete the file [" + fileId + "]", e);
    } finally {
      try {
        response.close();
      } catch (IOException e) {
        logger.error("An error occoured, when trying to close repsons for file [" + fileId + "]", e);
        throw new IngeTechnicalException("An error occoured, when trying to close repsons for file [" + fileId + "]", e);
      }
    }
  }
}
