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
* or http://www.escidoc.org/license.
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;

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
    
    private static Logger logger = Logger.getLogger(ExtractionChain.class);
    
    private String pdftotext = System.getenv("pdftotext.path");
    private String pdfboxAppJar = System.getenv("pdfbox-app-jar.path");
    
    public ExtractionChain()
    { 
         this.pdftotext = System.getenv("pdftotext.path");
         this.pdfboxAppJar = System.getenv("pdfbox-app-jar.path");

        return;
    }
    
    // if called by Runtime.exec(cmd, envp) the following properties are got by the environment. 
    // Otherwise the properties are expected to be set by the calling instance explicitly using this method.
    
    public void setProperties(Properties p, Logger logger)
    { 	
    	if (pdftotext == null) 
    	{
    		pdftotext = p.getProperty("pdftotext.path");
    	}
    	if (pdfboxAppJar == null) 
    	{
    		pdfboxAppJar= p.getProperty("pdfbox-app-jar.path");
    	}
    	
    	this.logger = logger;
    }
    
    public int doExtract(String infileName, String outfileName)
    {
    	File outfile = new File(outfileName);
    	
    	Date stepStart = new Date();
        Date current;
    	
        logger.info("Extracting PDF content ----------------------------------------");
        logger.info("Infile: " + infileName);
        logger.info("Outfile: " + outfileName);
        
        logger.info(stepStart + " -- started");  
        
        // xPDF
        
        try
        {
        	logger.info("Extracting with xPDF");
            
            StringBuffer command = new StringBuffer(2048);
            command.append(System.getProperty("os.name").contains("Windows") ? pdftotext + " -enc UTF-8 " : "/usr/bin/pdftotext -enc UTF-8 ");
            command.append(infileName);
            command.append(" ");
            command.append(outfileName);
                
            Process proc = Runtime.getRuntime().exec(command.toString());
            
            StreamGobbler inputGobbler = new StreamGobbler(proc.getInputStream(), "xPDF");
            StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "xPDF");
            
            inputGobbler.start();
            errorGobbler.start();
            
            int exitCode = proc.waitFor();
            
                    
            if (proc.exitValue() == 0)
            {
            	
                if (verbose)
                {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(outfile), "UTF-8"));
                    String line;
                    while ((line = bufferedReader.readLine()) != null)
                    {
                    	logger.info(line);
                    }
                    bufferedReader.close();
                }
                current = new Date();
                logger.info(current + " -- finished successfully");
                logger.info("Extraction took " + (current.getTime() - stepStart.getTime()));
                
                return 0;
            }
        }
        catch (Exception e)
        {
            logger.warn("Error extracting PDF with xPDF:");
            logger.warn(e.getStackTrace());
        }
        
        current = new Date();
        logger.info(current + " -- finished unsuccessfully");
        logger.info("Extraction attempt took " + (current.getTime() - stepStart.getTime()));
        
        // PDFBox
        try
        {
            logger.info("Extracting with PDFBox");
            stepStart = new Date();
            
            StringBuffer command = new StringBuffer(1024);
            command.append(System.getProperty("os.name").contains("Windows") ? 
                    "java -Dfile.encoding=UTF-8 -jar "  + pdfboxAppJar +  " ExtractText "                    
                    :
                    "/usr/bin/java -Dfile.encoding=UTF-8 -jar " + pdfboxAppJar + " ExtractText "); 
            command.append(infileName);
            command.append(" ");
            command.append(outfileName);
           
            Process proc = Runtime.getRuntime().exec(command.toString());
            StreamGobbler inputGobbler = new StreamGobbler(proc.getInputStream(), "PDFBox");
            StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "PDFBox");
            
            inputGobbler.start();
            errorGobbler.start();
         
            int exitCode = proc.waitFor();
            
            if (exitCode == 0)
            {
            	
                if (verbose)
                {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(outfile), "UTF-8"));
                    String line;
                    while ((line = bufferedReader.readLine()) != null)
                    {
                        logger.info(line);
                    }
                    bufferedReader.close();
                }
                current = new Date();
                logger.info(current + " -- finished successfully");
                logger.info("Extraction took " + (current.getTime() - stepStart.getTime()));

                return 0;
            }
        }
        catch (Exception e)
        {
            logger.warn("Error extracting PDF with PDFBox:");
            logger.warn(e.getStackTrace());
        }
        
        current = new Date();
        logger.info(current + " -- finished unsuccessfully");
        logger.info("Extraction attempt took " + (current.getTime() - stepStart.getTime()));

        
        // iText
        try
        {
            logger.info("Extracting with iText");
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
                    logger.info(line);
                }
                bufferedReader.close();
            }
            
            current = new Date();
            logger.info(current + " -- finished successfully");
            logger.info("Extraction took " + (current.getTime() - stepStart.getTime()));

            return 0;

        }
        catch (Exception e)
        {
            logger.warn("Error extracting PDF with iText:", e);
        }
        
        current = new Date();
        logger.info(current + " -- finished unsuccessfully");
        logger.info("Extraction attempt took " + (current.getTime() - stepStart.getTime()));
        
        logger.info("... giving up");
        
        return 1;
    }
    
    
    class StreamGobbler extends Thread
    {
        InputStream is;
        String name;
        
        StreamGobbler(InputStream is, String name)
        {
            this.is = is;
            this.name = name;
        }
        
        public void run()
        {
            try
            {
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                String line=null;
                while ( (line = br.readLine()) != null)
                    logger.info("[" + name + "] " + line);    
                } catch (IOException ioe)
                  {
                    ioe.printStackTrace();  
                  }
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
    	int ret = 0;
    	
        if (args.length < 2)
        {
            System.out.println("usage: ExctractionChain [-v[erbose]] <infile> <outfile>");
        }
        else
        {
            int offset = 0;
            
            if ("-v".equals(args[0]) || "-verbose".equals(args[0]))
            {
                offset = 1;
                verbose = true;
            }
            ExtractionChain exctractionChain = new ExtractionChain();            
            ret = exctractionChain.doExtract(args[0 + offset], args[1 + offset]);
            
            System.out.println("ret in main " + ret);
        }
        
        System.exit(ret);
    }
}
