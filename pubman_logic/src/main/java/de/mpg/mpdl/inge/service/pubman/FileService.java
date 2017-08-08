/**
 * 
 */
package de.mpg.mpdl.inge.service.pubman;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

  public static final String TEMP_FILE_PATH = "E:\\tmp\\filetest\\";

  // public static final String TEMP_FILE_PATH = PropertyReader.getProperty("TempFilePath");

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
}
