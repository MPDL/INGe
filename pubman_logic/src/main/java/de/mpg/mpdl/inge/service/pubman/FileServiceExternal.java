/**
 * 
 */
package de.mpg.mpdl.inge.service.pubman;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

import de.mpg.mpdl.inge.filestorage.FileStorageInterface;
import de.mpg.mpdl.inge.model.db.valueobjects.StagedFileDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.aa.IngeSpringAuthenticationProvider;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;

/**
 * FileService Interface - staging, storing and indexing files
 * 
 * @author walter
 * 
 */
public interface FileServiceExternal {

  /**
   * create a stage file for storing later
   * 
   * @param fileInputStream
   * @param fileName
   * @return An pbject containing information about the staged file
   * @throws IOException
   */
  public StagedFileDbVO createStageFile(InputStream fileInputStream, String fileName,
      String authenticationToken) throws IngeTechnicalException, IngeApplicationException,
      AuthorizationException, AuthenticationException;



  public void readFile(String fileId, OutputStream out, String authentitationToken)
      throws IngeTechnicalException, IngeApplicationException, AuthorizationException,
      AuthenticationException;



  /**
   * retrieve the metadata for a file
   * 
   * @param fileId
   * @return
   */
  public String getFileMetadata(String fileId, String authenticationToken)
      throws IngeTechnicalException, IngeApplicationException, AuthorizationException,
      AuthenticationException;

  /**
   * get the file mime type
   * 
   * @param fileId
   * @return mime-type of the file
   */
  public String getFileType(String fileId);

  /**
   * get the file name
   * 
   * @param fileId
   * @return name of the file
   */
  public String getFileName(String fileName);
}
