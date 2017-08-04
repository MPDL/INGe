package de.mpg.mpdl.inge.service.pubman.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;

import de.mpg.mpdl.inge.filestorage.filesystem.FileSystemServiceBean;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.pubman.FileService;

public class FileServiceFSImpl implements FileService {

  @Autowired
  private FileSystemServiceBean fssb;

  @Override
  public String createFile(InputStream fileInputStream, String fileName)
      throws IngeTechnicalException {
    fssb.createFile(fileInputStream, fileName);
    return null;
  }

  @Override
  public void readFile(String filePath, OutputStream out) throws IngeTechnicalException {
    fssb.readFile(filePath, out);
  }

  @Override
  public void deleteFile(String filePath) throws IngeTechnicalException {
    fssb.deleteFile(filePath);
  }

  @Override
  public Path createStageFile(InputStream fileInputStream, String fileName)
      throws IngeTechnicalException {
    int fileHashValue = fileName.hashCode();
    File tmpFile = new File(TEMP_FILE_PATH + fileHashValue);
    // Get the file reference

    try {
      Files.copy(fileInputStream, tmpFile.toPath());
    } catch (IOException e) {
      e.printStackTrace();
      throw new IngeTechnicalException("Could not write temp file", e);
    }
    return tmpFile.toPath();
  }

  @Override
  public void readStageFile(Path stagedFilePath, OutputStream fileOutputStream)
      throws IngeTechnicalException {
    try {
      Files.copy(stagedFilePath, fileOutputStream);
    } catch (IOException e) {
      e.printStackTrace();
      throw new IngeTechnicalException("Could not read temp file", e);
    }
  }

  @Override
  public void deleteStageFile(Path path) throws IngeTechnicalException {
    try {
      Files.deleteIfExists(path);
    } catch (IOException e) {
      e.printStackTrace();
      throw new IngeTechnicalException("Could not delete temp file", e);
    }
  }

  @Override
  public void indexFile(InputStream fileInputStream) {
    // TODO Auto-generated method stub

  }

  protected static void handleIOException(IOException exception) throws IngeTechnicalException {

    try {
      throw exception;
    } catch (IOException ex) {
      StringBuilder message =
          new StringBuilder("An error occured while reading or writing the file");
      // Get message from
      if (ex.getCause() != null && ex.getCause().getCause() != null) {
        message.append(" ").append(ex.getCause().getCause().getMessage());
      }
      throw new IngeTechnicalException(message.toString(), ex);
    }

  }



}
