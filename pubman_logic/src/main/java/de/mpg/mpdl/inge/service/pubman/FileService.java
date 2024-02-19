/**
 *
 */
package de.mpg.mpdl.inge.service.pubman;

import java.io.InputStream;

import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.aa.Principal;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;

/**
 * FileService Interface - staging, storing and indexing files
 *
 * @author walter
 *
 */
public interface FileService extends FileServiceExternal {



  void createFileFromStagedFile(FileDbVO fileVO, Principal user) throws IngeTechnicalException, IngeApplicationException;

  void createFileFromStagedFile(FileDbVO fileVO, Principal user, String forcedFileName)
      throws IngeTechnicalException, IngeApplicationException;


  /**
   * @param fileInputStream
   */
  void indexFile(InputStream fileInputStream);

  void deleteFile(String reference) throws IngeTechnicalException;

  void deleteOldStagingFiles();

}
