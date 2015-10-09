package de.mpg.escidoc.tools.util;

import java.io.File;

public class Util
{
	private Util()
	{
	}

	/**
	 * Count files in a directory (including files in all subdirectories)
	 * 
	 * @param directory
	 *            the directory to start in
	 * @return the total number of files
	 */
	public static int countFilesInDirectory(File directory)
	{
		int count = 0;

		if (directory.isFile())
			return 1;

		for (File file : directory.listFiles())
		{
			if (file.isFile())
			{
				count++;
			}
			if (file.isDirectory())
			{
				count += countFilesInDirectory(file);
			}
		}
		return count;
	}

}
