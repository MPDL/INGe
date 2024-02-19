/**
 *
 */
package de.mpg.mpdl.inge.service.pubman;

import java.io.IOException;
import java.io.InputStream;

import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.db.valueobjects.StagedFileDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.aa.AuthorizationService.AccessType;
import de.mpg.mpdl.inge.service.aa.Principal;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.impl.FileVOWrapper;

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
  StagedFileDbVO createStageFile(InputStream fileInputStream, String fileName, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException;



  FileVOWrapper readFile(String itemId, String fileId, String authentitationToken)
      throws IngeTechnicalException, IngeApplicationException, AuthorizationException, AuthenticationException;



  /**
   * retrieve the metadata for a file
   *
   * @param fileId
   * @return
   */
  String getFileMetadata(String itemId, String fileId, String authenticationToken)
      throws IngeTechnicalException, IngeApplicationException, AuthorizationException, AuthenticationException;

  boolean checkAccess(AccessType at, Principal principal, ItemVersionVO item, FileDbVO file)
      throws IngeApplicationException, IngeTechnicalException;


}
