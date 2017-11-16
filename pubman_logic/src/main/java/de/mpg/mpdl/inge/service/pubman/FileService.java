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
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
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
public interface FileService extends FileServiceExternal {



  /**
   * 
   * @param stagedFileId
   * @param fileName
   * @param authenticationToken
   * @return An id or path identifying the file
   * @throws IngeTechnicalException
   * @throws IngeApplicationException
   * @throws AuthorizationException
   * @throws AuthenticationException
   */
  public String createFileFromStagedFile(int stagedFileId, String fileName, AccountUserVO user)
      throws IngeTechnicalException, IngeApplicationException;


  /**
   * @param fileInputStream
   */
  public void indexFile(InputStream fileInputStream);

  public void deleteFile(String reference) throws IngeTechnicalException;

}
