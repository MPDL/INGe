/**
 * 
 */
package de.mpg.escidoc.tools.reindex;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.mpg.escidoc.tools.util.ExtractionStatistic;

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
	private ExtractionStatistic statistic = new ExtractionStatistic();
	
	
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
	
	public ExtractionStatistic getStatistic()
	{
		return this.statistic;
	}
	
	void extractFulltexts(File dir) throws Exception
	{
		File[] files = dir.listFiles();
		Collections.sort(Arrays.asList(files));
		
		statistic.setFilesTotal(files.length);
		
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
		Process proc = Runtime.getRuntime().exec(cmd);
		
		StreamGobbler inputGobbler = new StreamGobbler(proc.getInputStream(), "Extractor in");
        StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "Extractor err");
        
        inputGobbler.start();
        errorGobbler.start();
        
        int exitCode = proc.waitFor();
        
        if (proc.exitValue() == 0)
        {
        	statistic.incrementFilesExtractionDone();
        }
        else
        {
        	statistic.incrementFilesErrorOccured();
        	statistic.addToErrorList(file.getName());
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
                    System.out.println("[" + name + "] " + line);    
                } catch (IOException ioe)
                  {
                    ioe.printStackTrace();  
                  }
        }
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
