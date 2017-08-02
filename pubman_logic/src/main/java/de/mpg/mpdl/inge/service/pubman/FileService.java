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
  
  public static final String TEMP_FILE_PATH = "E:\\temp\\filetest\\";
//  public static final String TEMP_FILE_PATH = PropertyReader.getProperty("TempFilePath");
  
  /**
   * @param fileInputStream
   * @param fileName
   * @return
   * @throws IOException
   */
  public Path stageFile(InputStream fileInputStream, String fileName) throws IngeTechnicalException;
  
  /**
   * @param fileInputStream
   */
  public void indexFile(InputStream fileInputStream);
}
