package de.mpg.mpdl.inge.seaweedfs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URI;
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


/**
 * File storage service for seaweed (handling full text files and so on)
 * 
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@Service
public class SeaweedFileServiceBean {
  
  private static Logger logger = Logger.getLogger(SeaweedFileServiceBean.class);

  @Value("${seaweed_master_server_ip:http://localhost:9333}")
  private String seaweedMasterUrl;
  @Value("${seaweed_direct_submit_path:/submit}")
  private String seaweedDirectSubmitPath;
  @Autowired
  private CloseableHttpClient httpClient;

  /**
   * creates a file in the seaweed instance
   * 
   * @return json - response returned (including "fid", "fileUrl", "fileName", ...)
   * @throws IOException
   */
  public JsonNode createFile(File file) throws IOException {
    JsonNode jsonObject = null;
    HttpEntity entity =
        MultipartEntityBuilder.create()
            .addBinaryBody("upload_file", file, ContentType.DEFAULT_BINARY, "myDatei").build();

    HttpPost httpPost = new HttpPost(seaweedMasterUrl + seaweedDirectSubmitPath);
    httpPost.setEntity(entity);
    CloseableHttpResponse response = null;

    try {
      response = httpClient.execute(httpPost);
      logger.info(response.getStatusLine());
      HttpEntity responseEntity = response.getEntity();
      ObjectMapper mapper = new ObjectMapper();
      jsonObject = mapper.readTree(responseEntity.getContent());
      // ensure it is fully consumed
      EntityUtils.consume(responseEntity);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      response.close();
    }
    return jsonObject;
  }

  /**
   * read a file from the seaweed instance to an outputstream
   * 
   * @param fileId - Id of the file to read
   * @param out - OutputStream where result is written
   * @throws IOException
   */
  public void readFile(String fileId, OutputStream out) throws IOException {
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
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      response.close();
    }
  }

  /**
   * delete a file with a specific id from the seaweed instance
   * 
   * @param fileId - Id of the file to read
   * @throws Exception
   */
  public void deleteFile(String fileId) throws Exception {
    HttpDelete httpDelete =
        new HttpDelete(seaweedMasterUrl + "/" + URLEncoder.encode(fileId, "UTF-8"));
    System.out.println(httpDelete.getURI().toString());
    CloseableHttpResponse response = null;

    try {
      response = httpClient.execute(httpDelete);
      logger.info(response.getStatusLine());
      logger.info(response.getFirstHeader("Location"));
      if (response.getStatusLine().getStatusCode() == 301) {
        logger.info("Redirecting delete manually");
        httpDelete.setURI(new URI(response.getFirstHeader("Location").getValue()));
        System.out.println(httpDelete.getURI().toString());
        response.close();
        response = httpClient.execute(httpDelete);
      }
      HttpEntity responseEntity = response.getEntity();
      InputStream retrievedFileInputStream = responseEntity.getContent();
      StringWriter stringWriter = new StringWriter();
      IOUtils.copy(retrievedFileInputStream, stringWriter);
      System.out.println("Delete Response: " + stringWriter);
      // ensure it is fully consumed
      EntityUtils.consume(responseEntity);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      response.close();
    }
  }
}
