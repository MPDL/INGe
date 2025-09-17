/**
 *
 */
package de.mpg.mpdl.inge.service.pubman;

import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.aa.Principal;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;

/**
 * FileService Interface - staging, storing and indexing files
 *
 * @author walter
 *
 */
public interface FileService extends FileServiceExternal {
  //  void createFileFromStagedFile(FileDbVO fileVO, Principal user) throws IngeTechnicalException, IngeApplicationException;

  void createFileFromStagedFile(FileDbVO fileVO, Principal user, String forcedFileName)
      throws IngeTechnicalException, IngeApplicationException;

  //  void indexFile(InputStream fileInputStream);

  void deleteFile(String reference) throws IngeTechnicalException;

  void deleteOldStagingFiles();

  void regenerateThumbnails(String token) throws IngeTechnicalException, AuthenticationException;

  void generateThumbnail(FileDbVO fileDbVO) throws IngeTechnicalException, AuthenticationException;
}
