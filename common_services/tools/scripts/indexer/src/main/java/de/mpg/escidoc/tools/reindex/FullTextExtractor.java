/**
 * 
 */
package de.mpg.escidoc.tools.reindex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.mpg.escidoc.services.extraction.ExtractionChain;
import de.mpg.escidoc.tools.util.ExtractionStatistic;
import de.mpg.escidoc.tools.util.Util;

/**
 * @author siedersleben
 *
 */
public class FullTextExtractor
{
	private static Logger logger = Logger.getLogger(FullTextExtractor.class);
	private static String propFileName = "indexer.properties";

	private String fulltextPath = "";
	private ExtractionStatistic statistic = new ExtractionStatistic();
	private Properties properties = new Properties();
	private String[] envp = new String[2]; 
	
	
	public FullTextExtractor() throws Exception
	{		
		InputStream s = getClass().getClassLoader().getResourceAsStream(propFileName);
		
		if (s != null)
		{
			properties.load(s);
			logger.info(properties.toString());
		}
		else 
		{
			throw new FileNotFoundException("Not found " + propFileName);
		}
		
		fulltextPath = properties.getProperty("fulltexts.path");
		FileUtils.forceMkdir(new File(properties.getProperty("fulltexts.path")));
		
		envp[0] = "pdftotext.path=" + properties.getProperty("pdftotext.path");
		envp[1] = "pdfbox-app-jar.path=" + properties.getProperty("pdfbox-app-jar.path");
	}
	
	public void init(File baseDir)
	{		
		statistic.setFilesTotal(Util.countFilesInDirectory(baseDir));	
	}
	
	public String getFulltextPath()
	{
		return this.fulltextPath;
	}
	
	public ExtractionStatistic getStatistic()
	{
		return this.statistic;
	}
	
	void extractFulltexts(File dirOrFile) throws Exception
	{
		File[] files = dirOrFile.listFiles();
		
		if (files != null)
		{
			Collections.sort(Arrays.asList(files));
		}
		else 
		{
			files = new File[1];
			files[0] = dirOrFile;
		}
		
		
		for (File file : files)
		{
			if (file.isDirectory())
			{
				extractFulltexts(file);
			}
			else
			{
				try
				{
					ExtractionChain chain = new ExtractionChain();
					
					// too much properties 
					
					chain.setProperties(properties, this.logger);
					int ret = chain.doExtract(file.getAbsolutePath(), (new File(fulltextPath, file.getName())).getAbsolutePath().concat(".txt"));
					
					if (ret == 0)
					{
						statistic.incrementFilesExtractionDone();
					}
					else
					{
						statistic.incrementFilesErrorOccured();
						statistic.addToErrorList(file.getName());
					}
				} catch (Exception e)
				{
					statistic.incrementFilesErrorOccured();
					statistic.addToErrorList(file.getName());
					e.printStackTrace();
					
					continue;
				}
			}
		}
	}


	void extractFulltext(File file) throws Exception
	{   
		if (!(new File(fulltextPath, file.getName()).exists()))
		{
			logger.info("****************** Start extracting " + file.getName());
		}
		else 
		{
			logger.info("****************** Skipping extraction " + file.getName());
			return;
		}
        
		String[] cmd = getCommand(file);
		
		Process proc = Runtime.getRuntime().exec(cmd, envp);
		
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

	private String[] getCommand(File f)
	{
		String[] cmd = new String[5];
		
		cmd[0] = "java";
		cmd[1] = "-jar";
		cmd[2] = properties.getProperty("extraction-chain.path");
		cmd[3] = f.getAbsolutePath();
		cmd[4] = (new File(fulltextPath, f.getName())).getAbsolutePath().concat(".txt");
		
		logger.info("extract command <" + Arrays.toString(cmd) + ">");
		return cmd;
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
				String line = null;

				while ((line = br.readLine()) != null)
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
	public static void main(String[] args) throws Exception
	{
		File baseDir = new File(args[0]);
		
		// TODO check parameter
		

		FullTextExtractor extractor = new FullTextExtractor();
		
		extractor.init(baseDir);		
		extractor.extractFulltexts(baseDir);
		
		logger.info(extractor.getStatistic().toString());
	}

}
