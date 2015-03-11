/**
 * 
 */
package de.mpg.escidoc.tools.reindex;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * @author franke
 *
 */
public class FullTextExtractor
{
	private static Logger logger = Logger.getLogger(FullTextExtractor.class);
	private static String extractCmd = "java -jar C:/Users/sieders.MUCAM/.m2/repository/de/mpg/escidoc/tools/extraction_chain/1.0-SNAPSHOT/extraction_chain-1.0-SNAPSHOT-jar-with-dependencies.jar ";
//	private static String extractCmd = "java de.mpg.escidoc.services.extraction.ExtractionChain ";

	private String fulltextPath = "c:/fulltexts";	
	private File baseDir;
	
	
	/**
	 * Constructor with initial base directory, should be the fedora "objects" directory.
	 * @param baseDir
	 */
	public FullTextExtractor(File baseDir) throws Exception
	{
		this.baseDir = baseDir;
		
		FileUtils.forceMkdir(new File(fulltextPath));
	}
	
	public String getFulltextPath()
	{
		return this.fulltextPath;
	}
	
	void extractFulltexts(File dir) throws Exception
	{
		File[] files = dir.listFiles();
		Collections.sort(Arrays.asList(files));
		
		for (File file : files)
		{
			if (file.isDirectory())
			{
				extractFulltexts(file);
			}
			else
			{
				extractFulltext(file);
			}
		}
	}


	void extractFulltext(File file) throws Exception
	{
		BufferedReader stdIn = null;
        BufferedReader errIn = null;
        
        logger.info("****************** Start extracting " + file.getName());
        
		String cmd = getCommand(extractCmd, file);
		Process p = Runtime.getRuntime().exec(cmd);
		
		//wait until process is finished
        stdIn = new BufferedReader(
                new InputStreamReader(p.getInputStream(), "UTF-8"));
        errIn = new BufferedReader(
                new InputStreamReader(p.getErrorStream(), "UTF-8"));
        StringBuffer outBuf = new StringBuffer("");
        long time = System.currentTimeMillis();
        while (true) {
            try {
                p.exitValue();
        		logger.info("exit value " + p.exitValue());
                break;
            } catch (Exception e) {
                Thread.sleep(200);
                int c;
                if (stdIn.ready()) {
                   while ((stdIn.read()) > -1) {
                   }
                }
                if (errIn.ready()) {
                   while ((c = errIn.read()) > -1) {
                       outBuf.append((char)c);
                   }
                }
                if (System.currentTimeMillis() - time > 60000) {
                    throw new IOException(
                            "couldnt extract text from pdf, timeout reached");
                }
            }
        }
	}

	private String getCommand(String cmd, File f)
	{
		StringBuffer b = new StringBuffer();
		b.append(cmd);
		b.append(" ");
		b.append(f.getAbsolutePath());
		b.append(" ");
		b.append((new File(fulltextPath, f.getName())).getAbsolutePath());
		b.append(".txt");
		
		logger.info("extract command <" + b.toString() + ">");
		return b.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{

		File baseDir = new File(args[0]);
		
		FullTextExtractor extractor = new FullTextExtractor(baseDir);
		
			
	}

}
