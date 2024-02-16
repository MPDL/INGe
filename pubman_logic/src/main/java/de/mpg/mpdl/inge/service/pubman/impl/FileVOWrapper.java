package de.mpg.mpdl.inge.service.pubman.impl;

import java.io.OutputStream;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.filestorage.FileStorageInterface;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;

public class FileVOWrapper {

  private final String fileId;

  private final FileDbVO fileVO;

  private final FileStorageInterface fileStorageInterface;

  private static final Logger logger = Logger.getLogger(FileVOWrapper.class);


  protected FileVOWrapper(String fileId, FileDbVO fileVO, FileStorageInterface fileStorageInterface) {
    this.fileId = fileId;
    this.fileVO = fileVO;
    this.fileStorageInterface = fileStorageInterface;
  }

  public FileDbVO getFileVO() {
    return fileVO;
  }

  public void readFile(OutputStream outputStream) throws IngeTechnicalException {
    fileStorageInterface.readFile(fileId, outputStream);

  }


}
