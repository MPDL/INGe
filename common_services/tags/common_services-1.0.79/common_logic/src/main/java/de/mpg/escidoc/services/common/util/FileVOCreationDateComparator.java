package de.mpg.escidoc.services.common.util;

import java.util.Comparator;

import de.mpg.escidoc.services.common.valueobjects.FileVO;

public class FileVOCreationDateComparator implements Comparator<FileVO> {

	public int compare(FileVO file1, FileVO file2) {
		
		if(file1 != null && file1.getCreationDate()!=null && file2!=null && file2.getCreationDate()!=null)
		{
			return file1.getCreationDate().compareTo(file2.getCreationDate());
		}
		return 0;
	}

}
