package de.mpg.mpdl.inge.service.pubman.impl;

import java.io.OutputStream;

import de.mpg.mpdl.inge.filestorage.Range;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mpg.mpdl.inge.filestorage.FileStorageInterface;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;

public class FileVOWrapper {

  private final String fileId;

  private final FileDbVO fileVO;

  private final FileStorageInterface fileStorageInterface;

  private static final Logger logger = LogManager.getLogger(FileVOWrapper.class);


  protected FileVOWrapper(String fileId, FileDbVO fileVO, FileStorageInterface fileStorageInterface) {
    this.fileId = fileId;
    this.fileVO = fileVO;
    this.fileStorageInterface = fileStorageInterface;
  }

  public FileDbVO getFileVO() {
    return this.fileVO;
  }

  public void readFile(OutputStream outputStream) throws IngeTechnicalException {
    this.fileStorageInterface.readFile(this.fileId, outputStream);

  }

  public void readFile(OutputStream outputStream, Range range) throws IngeTechnicalException {
    this.fileStorageInterface.readFile(this.fileId, outputStream, range);

  }


}
