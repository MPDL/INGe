package de.mpg.mpdl.inge.filestorage.seaweedfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.mpdl.inge.filestorage.FileStorageInterface;


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

  private static Logger logger = Logger.getLogger(SeaweedFileServiceBean.class);

  @Value("${seaweed_master_server_ip}")
  private String seaweedMasterUrl;
  @Value("${seaweed_direct_submit_path}")
  private String seaweedDirectSubmitPath;
  @Autowired
  private CloseableHttpClient httpClient;

  /**
   * creates a file in the seaweed instance
   * 
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.FileStorageInterface#createFile(java.io.InputStream,
   *      java.lang.String)
   * 
   * @return json - response returned (including "fid", "fileUrl", "fileName", ...)
   * @throws IOException
   */
  @Override
  public String createFile(InputStream fileInputStream, String fileName) throws IOException {
    String fileId;
    HttpEntity entity =
        MultipartEntityBuilder.create()
            .addBinaryBody("upload_file", fileInputStream, ContentType.DEFAULT_BINARY, fileName)
            .build();

    HttpPost httpPost = new HttpPost(seaweedMasterUrl + seaweedDirectSubmitPath);
    httpPost.setEntity(entity);
    CloseableHttpResponse response = null;

    try {
      System.out.println("Trying to create new File [" + fileName + "]");
      response = httpClient.execute(httpPost);
      logger.info(response.getStatusLine());
      HttpEntity responseEntity = response.getEntity();
      ObjectMapper mapper = new ObjectMapper();
      JsonNode jsonObject = mapper.readTree(responseEntity.getContent());
      System.out.println("Created Object [" + jsonObject + "]");
      // ensure it is fully consumed
      EntityUtils.consume(responseEntity);
      fileId = jsonObject.findValuesAsText("fid").get(0);
    } catch (IOException e) {
      logger.error("An error occoured, when trying to create file [" + fileName + "]", e);
      throw e;
    } finally {
      response.close();
    }
    return fileId;
  }

  /**
   * read a file from the seaweed instance to an outputstream
   * 
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
  public void readFile(String fileId, OutputStream out) throws IOException {
    System.out.println("Trying to read Id [" + fileId + "]");
    HttpGet httpGet = new HttpGet(seaweedMasterUrl + "/" + fileId);
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
      e.printStackTrace();
    } finally {
      response.close();
    }
  }

  /**
   * delete a file with a specific id from the seaweed instance
   * 
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.FileStorageInterface#deleteFile(java.lang.String)
   * 
   * @param fileId - Id of the file to read
   * @throws Exception
   */
  @Override
  public void deleteFile(String fileId) throws Exception {
    System.out.println("Trying to delete Id [" + fileId + "]");
    HttpDelete httpDelete =
        new HttpDelete(seaweedMasterUrl + "/" + URLEncoder.encode(fileId, "UTF-8"));
    logger.info("Delete request: " + httpDelete.getURI().toString());
    CloseableHttpResponse response = null;

    try {
      response = httpClient.execute(httpDelete);
      logger.info(response.getStatusLine());
      logger.info(response.getFirstHeader("Location"));
      if (response.getStatusLine().getStatusCode() == 301) {
        logger.info("Redirecting delete manually");
        httpDelete.setURI(new URI(response.getFirstHeader("Location").getValue()));
        response.close();
        System.out.println("[" + httpDelete.toString() + "]");
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
      throw e;
    } catch (URISyntaxException e) {
      logger.error("An error with the generated URI occoured, "
          + "when trying to delete the file [" + fileId + "]", e);
      throw e;
    } finally {
      response.close();
    }
  }
}
