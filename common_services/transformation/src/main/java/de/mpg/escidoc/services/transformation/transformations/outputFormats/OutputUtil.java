package de.mpg.escidoc.services.transformation.transformations.outputFormats;

import java.io.IOException;
import java.net.URLDecoder;

import de.mpg.escidoc.services.transformation.Util;
import de.mpg.escidoc.services.transformation.Util.Styles;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

/**
 * 
 * This class contains helper methods for the transformation
 * into output formats.
 * @author kleinfe1 (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class OutputUtil
{
    /**
     * Returns true if val is not null && not empty String 
     * @param val 
     * @return first not null && not empty String
     */
    public static boolean checkVal(String val)
    {
        return ( val != null && !val.trim().equals("") );
    }

    /**
     * Returns true if val is not null && Length >0 
     * @param val 
     * @return first not null && Length >0
     */
    public static boolean checkLen(String val)
    {
        return ( val != null && val.length()>0 );
    }
    
    
    /**
     * Returns path to the Citation Styles directory for jasper report
     * TODO: This should be in common utils, as I copied this from citation manager
     * @return path
     * @throws IOException 
     */
    public static String getPathToCitationStyles() throws IOException 
    {
        String CITATIONSTYLES_DIRECTORY = "transformations/outputFormats";
         return getPathToResources() + CITATIONSTYLES_DIRECTORY;
    }
    
    public static String getPathToResources() throws IOException
    {
        String CLASS_DIRECTORY = "target/classes/";
        String RESOURCES_DIRECTORY_LOCAL = "src/main/resources/";
        return
            getPathToClasses().replace(CLASS_DIRECTORY, RESOURCES_DIRECTORY_LOCAL);
    }
    
    public static String getPathToClasses() throws IOException
    {
        String classString = Util.class.getName().replace(".", "/") + ".class";
        String result = Util.class.getClassLoader().getResource(classString).getFile().replace(classString, "");
        return 
            result.indexOf(".jar!") == -1 ?
                //Decode necessary for windows paths
                URLDecoder.decode(result, "cp1253") : "";
    }

}
