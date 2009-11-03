package de.mpg.escidoc.pubman.installer.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;

/**
 * Utility class to deal with resources such as files and directories. Either on the file system or in jar files.
 *
 * @version $Revision: 1951 $ $LastChangedDate: 2009-05-07 10:27:06 +0200 (Do, 07 Mai 2009) $
 */
public class ResourceUtil 
{

	private static Logger logger = Logger.getLogger(ResourceUtil.class);

    /**
     * Hidden constructor.
     */
    protected ResourceUtil()
    {

    }

    /**
     * Gets a resource as InputStream.
     *
     * @param fileName The path and name of the file relative from the working directory.
     * @return The resource as InputStream.
     * @throws FileNotFoundException Thrown if the resource cannot be located.
     */
    public static File getResourceAsFile(final String fileName) throws FileNotFoundException
    {
        URL url = ResourceUtil.class.getClassLoader().getResource(fileName);
        
        File file = null;
        if (url != null)
        {
            logger.debug("Resource found: " + url.getFile());
            file = new File(url.getFile());
        }

        if (file == null)
        {

            logger.debug("Resource not found, getting file.");

            file = new File(fileName);
            if (!file.exists())
            {
                throw new FileNotFoundException("File '" + fileName + "' not found.");
            }
        }
        return file;
    }

    /**
     * Gets a resource as InputStream.
     *
     * @param fileName The path and name of the file relative from the working directory.
     * @return The resource as InputStream.
     * @throws FileNotFoundException Thrown if the resource cannot be located.
     */
    public static InputStream getResourceAsStream(final String fileName) throws FileNotFoundException
    {
        // TODO: When the calling class is defined in a war file, this method
        // is unable to find a class loader for it. The commented-out code below was an attempt
        // to manage this behaviour but wasn't successful. Maybe it gives some ideas for the next
        // to try it.
        
        InputStream fileIn;
        fileIn = ResourceUtil.class.getClassLoader().getResourceAsStream(fileName);

        if (fileIn == null)
        {
            fileIn = new FileInputStream(fileName);
        }
        return fileIn;
    }

    /**
     * Gets a resource as String.
     *
     * @param fileName The path and name of the file relative from the working directory.
     * @return The resource as String.
     * @throws IOException Thrown if the resource cannot be located.
     */
    public static String getResourceAsString(final String fileName) throws IOException
    {
        InputStream fileIn = getResourceAsStream(fileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(fileIn, "UTF-8"));
        String line = null;
        String result = "";
        while ((line = br.readLine()) != null)
        {
            result += line + "\n";
        }
        return result;
    }

    /**
     * Gets an array of files containing the files in a given directory.
     *
     * @param dir The name of the directory.
     * @return Array of files.
     * @throws IOException Thrown if file is not found.
     */
    public static File[] getFilenamesInDirectory(String dir) throws IOException
    {
        File dirFile = getResourceAsFile(dir);

        logger.debug("dirFile: " + dirFile + " : " + dirFile.isDirectory() + " : " + dirFile.exists());

        if (dirFile == null)
        {
            return null;
        }
        else if (!dirFile.isDirectory())
        {
            throw new IOException("The given path is not a directory.");
        }
        else
        {
            ArrayList<File> fileArray = new ArrayList<File>();
            String[] fileNames = dirFile.list();
            
            for (int i = 0; i < fileNames.length; i++)
            {
                File file = new File(dirFile.getAbsolutePath() + "/" + fileNames[i]);
                
                if (file.isFile())
                {
                    fileArray.add(file);
                }
                else
                {
                    fileArray.addAll(Arrays.asList(getFilenamesInDirectory(file.getAbsolutePath())));
                }
            }
            return fileArray.toArray(new File[]{});
        }
    }
}
