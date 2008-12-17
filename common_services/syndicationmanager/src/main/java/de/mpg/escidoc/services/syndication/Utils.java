package de.mpg.escidoc.services.syndication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

 
/*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/


/**
 * Utils class. 
 *
 * @author vmakarenko (initial creation)
 * @author $Author: vmakarenko $ (last modification)
 * @version $Revision: 1145 $ $LastChangedDate: 2008-10-29 09:42:36 +0000 (Wed, 29 Oct 2008) $
 *
 */ 
public class Utils {
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
	 * Returns <code>true</code> if list is not empty
	 * @param l
	 * @return
	 */
	public static <T> boolean checkList(List<T> l)
	{
		return ( l != null && !l.isEmpty() );
	}    
    
    
    /**
     * Throws ExportManagerException true if cond is true 
     * @param cond
     * @param message
     * @throws ExportManagerException
     */
    public static void checkCondition(final boolean cond, final String message) throws SyndicationManagerException
    {
    	if ( cond )
    		throw new SyndicationManagerException(message);
    }
	
	public static void checkName(final String name) throws SyndicationManagerException
	{
		Utils.checkCondition(!checkVal(name), "Empty name");
	}
	
	public static void checkName(final String name, final String message) throws SyndicationManagerException
	{
		Utils.checkCondition(!checkVal(name), message);
	}
	


    /**
     * Find <code>name</code> in <code>a</code> String[]
     * @return <code>true</code> if <code>name</code> has been found 
     */
    public static boolean findInList(final String[] a, final String name){
    	for (String s: a) {
			if (s.equals(name)) 
				return true;
		}
    	return false;
    }      
    

    
    public static String replaceAllTotal(String what, String expr, String replacement)
    {
	    return 
	    	Pattern
	    		.compile(expr, Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
	    		.matcher(what)
	    		.replaceAll(replacement);
    }
    
    
    public static String quoteReplacement(String str)
    {
    	return 
    		Matcher.quoteReplacement(str)
    		.replace("{","\\{" )
    		.replace("}","\\}" );
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
        InputStream fileIn = null;
        
        File file = new File(fileName);
        if (file.exists())
        {
        	fileIn = new FileInputStream(fileName);
        }
        else
	    {
	    	fileIn = Feeds.class.getClassLoader().getResourceAsStream(fileName);
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
     * Join elements of any collection with delimiter
     * @param <T>
     * @param objs 
     * @param delimiter
     * @return a joined string
     */
    public static <T> String join(final Collection<T> objs, final String delimiter) 
    {
    	if ( objs == null || objs.isEmpty()) return "";
    	Iterator<T> iter = objs.iterator();
    	StringBuffer buffer = new StringBuffer(iter.next().toString());
    	while (iter.hasNext())
    		buffer.append(delimiter).append(iter.next().toString());
    	return buffer.toString();
    }
    
    
}
