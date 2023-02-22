package de.mpg.mpdl.inge.db.filestorage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.postgresql.PGConnection;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.mpg.mpdl.inge.filestorage.FileStorageInterface;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;


/**
 * File storage service for seaweed (handling full text files and so on)
 * 
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@Service
public class PostgresDbFileServiceBean implements FileStorageInterface {

  private static final Logger logger = Logger.getLogger(PostgresDbFileServiceBean.class);


  @Autowired
  private DataSource dataSource;


  private PGConnection getConnection() throws Exception {
    return DataSourceUtils.getConnection(dataSource).unwrap(PGConnection.class);
  }

  /**
   * creates a file in the seaweed instance
   * 
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.FileStorageInterface#createFile(java.io.InputStream,
   *      java.lang.String)
   * 
   * @return json - response returned (including "fid", "fileUrl", "fileName", ...)
   * @throws IOException
   */
  @Override
  @Transactional(rollbackFor = Throwable.class)
  public String createFile(InputStream fileInputStream, String fileName) throws IngeTechnicalException {
    try {

      LargeObjectManager lobm = getConnection().getLargeObjectAPI();

      long oid = lobm.createLO();
      LargeObject obj = lobm.open(oid);


      byte buf[] = new byte[2048];
      int s, tl = 0;
      while ((s = fileInputStream.read(buf, 0, 2048)) > 0) {
        obj.write(buf, 0, s);
        tl += s;
      }
      obj.close();
      return String.valueOf(oid);
    } catch (Exception e) {
      logger.error("Error while writing file to database", e);
      throw new IngeTechnicalException(e);

    } finally {
      try {
        fileInputStream.close();
      } catch (IOException e) {
        logger.error("Error closing stream", e);
      }

    }



  }

  /**
   * read a file from the seaweed instance to an outputstream
   * 
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.FileStorageInterface#readFile(java.lang.String,
   *      java.io.OutputStream)
   * 
   * @param fileId - Id of the file to read
   * @param out - OutputStream where result is written
   * @throws IOException
   */
  @Override
  @Transactional(rollbackFor = Throwable.class, readOnly = true)
  public void readFile(String fileId, OutputStream out) throws IngeTechnicalException {
    try {
      logger.info("Read file " + fileId);
      LargeObjectManager lobm = getConnection().getLargeObjectAPI();

      LargeObject obj = lobm.open(Long.parseLong(fileId), LargeObjectManager.READ);


      byte buf[] = new byte[2048];
      int s, tl = 0;
      while ((s = obj.read(buf, 0, 2048)) > 0) {
        out.write(buf, 0, s);
        tl += s;
      }
      obj.close();
    } catch (Exception e) {
      logger.error("Error while reading file from database", e);
      throw new IngeTechnicalException(e);

    } finally {
      try {
        out.close();
      } catch (IOException e) {
        logger.error("Error closing stream", e);
      }

    }

  }

  /**
   * delete a file with a specific id from the seaweed instance
   * 
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.services.FileStorageInterface#deleteFile(java.lang.String)
   * 
   * @param fileId - Id of the file to read
   * @throws Exception
   */
  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void deleteFile(String fileId) throws IngeTechnicalException {
    try {

      LargeObjectManager lobm = getConnection().getLargeObjectAPI();

      lobm.delete(Long.parseLong(fileId));

    } catch (Exception e) {
      logger.error("Error while deleting file from database", e);
      throw new IngeTechnicalException(e);

    }
  }
}
