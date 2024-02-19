package de.mpg.mpdl.inge.model.xmltransforming.util;

import java.util.Comparator;

import de.mpg.mpdl.inge.model.valueobjects.FileVO;

public class FileVOCreationDateComparator implements Comparator<FileVO> {

  public int compare(FileVO file1, FileVO file2) {

    if (null != file1 && null != file1.getCreationDate() && null != file2 && null != file2.getCreationDate()) {
      return file1.getCreationDate().compareTo(file2.getCreationDate());
    }
    return 0;
  }

}
