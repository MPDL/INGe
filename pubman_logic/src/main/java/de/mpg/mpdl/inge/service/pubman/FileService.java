/**
 * 
 */
package de.mpg.mpdl.inge.service.pubman;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import de.mpg.mpdl.inge.filestorage.FileStorageInterface;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;

/**
 * FileService Interface - staging, storing and indexing files
 * 
 * @author walter
 * 
 */
public interface FileService extends FileStorageInterface {

  /**
   * @param fileInputStream
   * @param fileName
   * @return Path for the created stage-file
   * @throws IOException
   */
  public Path createStageFile(InputStream fileInputStream, String fileName)
      throws IngeTechnicalException;

  /**
   * @param stagedFilePath
   * @return String representing the
   * @throws IngeTechnicalException
   */
  public InputStream readStageFile(Path stagedFilePath) throws IngeTechnicalException;

  /**
   * @param path
   * @throws IngeTechnicalException
   */
  public void deleteStageFile(Path stagedFilePath) throws IngeTechnicalException;

  /**
   * @param fileInputStream
   */
  public void indexFile(InputStream fileInputStream);

  /**
   * @param fileId
   * @return mime-type of the file
   */
  public String getFileType(String fileId);

  /**
   * @param fileId
   * @return name of the file
   */
  public String getFileName(String fileName);
}
