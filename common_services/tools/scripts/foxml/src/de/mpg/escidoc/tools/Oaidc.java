package de.mpg.escidoc.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
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
public class Oaidc
{
    public Oaidc(File rootDir, String stylesheet) throws Exception
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
                new Oaidc(file, stylesheet);
            }
            else
            {
                transform(file, stylesheet);
            }
        }
    }

    private void transform(File file, String stylesheet) throws Exception
    {
    	Date startDate = new Date();
        Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(new File(stylesheet)));
        File tempFile = File.createTempFile("file", ".tmp", file.getParentFile());
        transformer.setParameter("ID", file.getName().replace("_", ":"));
        
        Date date = new Date();
        
        transformer.setParameter("year", date.getYear());
        transformer.setParameter("month", ("0" + (date.getMonth() + 1)).substring(("0" + (date.getMonth() + 1)).length() - 2));
        transformer.setParameter("day", ("0" + date.getDate()).substring(("0" + date.getDate()).length() - 2));
        transformer.setParameter("hour", ("0" + date.getHours()).substring(("0" + date.getHours()).length() - 2));
        transformer.setParameter("minute", ("0" + date.getMinutes()).substring(("0" + date.getMinutes()).length() - 2));
        transformer.setParameter("second", ("0" + date.getSeconds()).substring(("0" + date.getSeconds()).length() - 2));
        transformer.setParameter("millis", ("00" + date.getTime() % 1000).substring(("00" + date.getTime() % 1000).length() - 3));
        
        transformer.transform(new StreamSource(file), new StreamResult(tempFile));
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(tempFile), "UTF-8"));
        String line;
        StringWriter writer = new StringWriter();
        while ((line = reader.readLine()) != null)
        {
            writer.write(line);
            writer.write("\n");
        }
        file.delete();
        FileOutputStream outputStream = new FileOutputStream(file);
        String result = writer.toString();
        int pos = result.indexOf("XXXXX-SIZE-TOKEN-YYYYY");
        int start = result.indexOf("<oai_dc:dc", pos);
        int end = result.indexOf("</oai_dc:dc>", start);

        int len = end - start + 15; // Very magic number!

        String newResult = result.replace("XXXXX-SIZE-TOKEN-YYYYY", len + "");

        outputStream.write(newResult.getBytes("UTF-8"));
        outputStream.close();
        tempFile.deleteOnExit();
        
        Date endDate = new Date();
        
        System.out.println(file + " transformed in " + (endDate.getTime() - startDate.getTime()) + "ms.");
    }
}
