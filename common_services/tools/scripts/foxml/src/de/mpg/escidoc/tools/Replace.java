package de.mpg.escidoc.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.crypto.dsig.Transform;
import javax.xml.stream.util.StreamReaderDelegate;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/*
 *
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
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class Replace
{
    public Replace(File rootDir) throws Exception
    {
        File[] files = rootDir.listFiles();
        
        Arrays.sort(files, new Comparator<File>() {
        	
        	@Override
        	public int compare(File f1, File f2) {
        		if (f1 == null && f2 == null)
        		{
        			return 0;
        		}
        		else if (f1 == null)
        		{
        			return -1;
        		}
        		else
        		{
        			return f1.getName().compareTo(f2.getName());
        		}
        	}
		});
        
        for (File file : files)
        {
            if (file.isDirectory())
            {
                new Replace(file);
            }
            else
            {
                replacePattern(file);
            }
        }
    }

    private void replacePattern(File file) throws Exception
    {
    	Date startDate = new Date();

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        String line;
        boolean found = false;
        boolean component = false;
        boolean xsi = false;
        
        while ((line = reader.readLine()) != null)
        {
            if (line.matches("\\s*<(\\w+:)?file [^>]+>"))
            {
            	found = true;
            	if (line.contains("xmlns:xsi"))
            	{
            		xsi = true;
            		System.out.println(file.getAbsolutePath() + ": File data and XSI namespace found");
            	}
            	else
            	{
            		//System.out.println(file.getAbsolutePath() + "Publication data found");
            	}
            }
            if (line.contains("http://escidoc.de/core/01/resources/Component"))
            {
            	component = true;
            }
        }
        reader.close();
        
        if (!found && component)
        {
        	System.out.println(file.getAbsolutePath() + ": No file data found");
        }
        
        if (found && component && !xsi)
        {
        	 BufferedReader reader2 = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
             String line2;
             File tempFile = File.createTempFile("xxx", "yyy", file.getParentFile());
             
             FileWriter writer = new FileWriter(tempFile);
             
             while ((line2 = reader2.readLine()) != null)
             {
            	 if (line2.matches("\\s*<(\\w+:)?file [^>]+>"))
                 {
            		 writer.write(line2.replaceFirst("file ", "file xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "));
            		 writer.write("\n");
                 }
            	 else
            	 {
            		 writer.write(line2);
            		 writer.write("\n");
            	 }
             }
             writer.close();
             File oldFile = new File(file.getAbsolutePath());
             File bakFile = new File(file.getAbsolutePath() + ".bak");
             file.renameTo(bakFile);
             tempFile.renameTo(oldFile);
             bakFile.delete();
             
             Date endDate = new Date();
             
             System.out.println(file.getAbsolutePath() + " transformed in " + (endDate.getTime() - startDate.getTime()) + "ms.");
        }
        Date endDate = new Date();
        
        //System.out.println(file + " transformed in " + (endDate.getTime() - startDate.getTime()) + "ms.");
    }
}
