package de.mpg.mpdl.inge.model.xmltransforming.util;

import java.io.Serializable;
import java.util.Comparator;

import de.mpg.mpdl.inge.model.valueobjects.FileVO;

public class FileVOCreationDateComparator implements Comparator<FileVO>, Serializable {

  public int compare(FileVO file1, FileVO file2) {

    if (file1 != null && file1.getCreationDate() != null && file2 != null && file2.getCreationDate() != null) {
      return file1.getCreationDate().compareTo(file2.getCreationDate());
    }
    return 0;
  }

}
