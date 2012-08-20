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

package de.mpg.escidoc.services.extraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class ExtractionChain
{
    public static boolean verbose = false;
    
    public ExtractionChain(String infileName, String outfileName)
    {
        File infile = new File(infileName);
        File outfile = new File(outfileName);
        
        System.err.println("Extracting PDF content ----------------------------------------");
        System.err.println("Infile: " + infileName);
        System.err.println("Outfile: " + outfileName);
        Date stepStart = new Date();
        Date current;
        System.err.println(stepStart + " -- started");
        
        // xPDF
        try
        {
            System.err.println("Extracting with xPDF");
            String command ="/usr/bin/pdftotext -cfg /etc/xpdfrc "
                + infileName + " " + outfileName;
            Process proc = Runtime.getRuntime().exec(command);
            InputStream errorStream = proc.getErrorStream();
            InputStream inputStream = proc.getInputStream();
            int exitCode = proc.waitFor();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                System.err.println("[WARN]  " + line);
            }
            if (exitCode != 0)
            {
                System.err.println("[ERROR] xPDF returned " + exitCode);
                bufferedReader = new BufferedReader(new InputStreamReader(errorStream, "UTF-8"));
                while ((line = bufferedReader.readLine()) != null)
                {
                    System.err.println("[ERROR] " + line);
                }
            }
            else
            {
                if (verbose)
                {
                    bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(outfile), "UTF-8"));
                    while ((line = bufferedReader.readLine()) != null)
                    {
                        System.err.println(line);
                    }
                }
                current = new Date();
                System.err.println(current + " -- finished successfully");
                System.err.println("Extraction took " + (current.getTime() - stepStart.getTime()));
                System.err.println();
                return;
            }
        }
        catch (Exception e)
        {
            System.err.println("[ERROR] Error extracting PDF with xPDF:");
            System.err.println("[ERROR] " + e.getMessage());
            for (StackTraceElement ste : e.getStackTrace())
            {
                System.err.println("[ERROR] at " + ste.getClassName() + "." + ste.getMethodName() + " (" + ste.getLineNumber() + ")");
            }
        }
        
        current = new Date();
        System.err.println(current + " -- finished unsuccessfully");
        System.err.println("Extraction attempt took " + (current.getTime() - stepStart.getTime()));
        
        // PDFBox
        try
        {
            System.err.println("Extracting with PDFBox");
            stepStart = new Date();
            String command = "/usr/bin/java -jar /usr/share/jboss/server/default/conf/pdfbox-app-1.4.0.jar ExtractText "
                + infileName + " " + outfileName;
            Process proc = Runtime.getRuntime().exec(command);
            InputStream errorStream = proc.getErrorStream();
            InputStream inputStream = proc.getInputStream();
            int exitCode = proc.waitFor();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                System.err.println("[WARN]  " + line);
            }
            if (exitCode != 0)
            {
                System.err.println("[ERROR] PDFBox returned " + exitCode);
                bufferedReader = new BufferedReader(new InputStreamReader(errorStream, "UTF-8"));
                while ((line = bufferedReader.readLine()) != null)
                {
                    System.err.println("[ERROR] " + line);
                }
            }
            else
            {
                if (verbose)
                {
                    bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(outfile), "UTF-8"));
                    while ((line = bufferedReader.readLine()) != null)
                    {
                        System.err.println(line);
                    }
                }
                current = new Date();
                System.err.println(current + " -- finished successfully");
                System.err.println("Extraction took " + (current.getTime() - stepStart.getTime()));
                System.err.println();
                return;
            }
        }
        catch (Exception e)
        {
            System.err.println("[ERROR] Error extracting PDF with PDFBox:");
            System.err.println("[ERROR] " + e.getMessage());
            for (StackTraceElement ste : e.getStackTrace())
            {
                System.err.println("[ERROR] at " + ste.getClassName() + "." + ste.getMethodName() + " (" + ste.getLineNumber() + ")");
            }
        }
        
        current = new Date();
        System.err.println(current + " -- finished unsuccessfully");
        System.err.println("Extraction attempt took " + (current.getTime() - stepStart.getTime()));

        
        // iText
        try
        {
            System.err.println("Extracting with iText");
            stepStart = new Date();
            
            PdfReader reader = new PdfReader(infileName);
            int numberOfPages = reader.getNumberOfPages();
            
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(outfile), "UTF-8");
            for (int i = 0; i < numberOfPages; i++)
            {
                outputStreamWriter.write(PdfTextExtractor.getTextFromPage(reader, i+1));
            }
            
            if (verbose)
            {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(outfile), "UTF-8"));
                String line;
                while ((line = bufferedReader.readLine()) != null)
                {
                    System.err.println(line);
                }
            }
            
            current = new Date();
            System.err.println(current + " -- finished successfully");
            System.err.println("Extraction took " + (current.getTime() - stepStart.getTime()));
            System.err.println();
            return;

        }
        catch (Exception e)
        {
            System.err.println("[ERROR] Error extracting PDF with iText:");
            System.err.println("[ERROR] " + e.getMessage());
            for (StackTraceElement ste : e.getStackTrace())
            {
                System.err.println("[ERROR] at " + ste.getClassName() + "." + ste.getMethodName() + " (" + ste.getLineNumber() + ")");
            }
        }
        
        current = new Date();
        System.err.println(current + " -- finished unsuccessfully");
        System.err.println("Extraction attempt took " + (current.getTime() - stepStart.getTime()));
        
        
        
        System.err.println("... giving up");
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        if (args.length < 2)
        {
            System.err.println("usage: ExctractionChain [-v[erbose]] <infile> <outfile>");
        }
        else
        {
            int offset = 0;
            if ("-v".equals(args[0]) || "-verbose".equals(args[0]))
            {
                offset = 1;
                verbose = true;
            }
            ExtractionChain exctractionChain = new ExtractionChain(args[0 + offset], args[1 + offset]);
        }
    }
}
