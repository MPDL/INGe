package de.mpg.mpdl.inge.service.pubman.impl;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.filestorage.FileStorageInterface;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.util.PropertyReader;
import net.arnx.wmf2svg.util.Base64;

public class FileVOWrapper {

  private String fileId;

  private FileDbVO fileVO;

  private FileStorageInterface fileStorageInterface;

  private static final Logger logger = Logger.getLogger(FileVOWrapper.class);


  protected FileVOWrapper(String fileId, FileDbVO fileVO, FileStorageInterface fileStorageInterface) {
    this.fileId = fileId;
    this.fileVO = fileVO;
    this.fileStorageInterface = fileStorageInterface;
  }

  public FileDbVO getFileVO() {
    return fileVO;
  }

  public void readFile(OutputStream outputStream) throws IngeTechnicalException {
    if (!"true".equals(PropertyReader.getProperty("inge.rest.development.enabled"))) {
      fileStorageInterface.readFile(fileId, outputStream);
    } else {
      try {
        Response response = Request.Get(PropertyReader.getProperty("inge.rest.development.file_url") + fileId)
            .addHeader("Authorization", "Basic " + Base64.encode((PropertyReader.getProperty("inge.rest.development.admin.username") + ":"
                + PropertyReader.getProperty("inge.rest.development.admin.password")).getBytes()))
            .execute();
        IOUtils.copy(response.returnContent().asStream(), outputStream);
      } catch (Exception e) {
        logger.error("Error reading file over ", e);
        e.printStackTrace();
      }
    }
  }


}
