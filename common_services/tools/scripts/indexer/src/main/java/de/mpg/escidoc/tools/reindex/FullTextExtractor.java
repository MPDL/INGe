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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.mpg.escidoc.services.extraction.ExtractionChain;
import de.mpg.escidoc.services.extraction.ExtractionChain.ExtractionResult;
import de.mpg.escidoc.tools.util.ExtractionStatistic;
import de.mpg.escidoc.tools.util.Util;

/**
 * @author siedersleben
 *
 */
public class FullTextExtractor
{
	private static final String propFileName = "indexer.properties";
	private static final String defaultDate = "0000-01-01 00:00:00";
	
	private static Logger logger = Logger.getLogger(FullTextExtractor.class);

	private String fulltextDir = "";
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
		
		fulltextDir = properties.getProperty("index.fulltexts.path");
		
		if (!new File(fulltextDir).exists())
		{
			FileUtils.forceMkdir(new File(properties.getProperty("index.fulltexts.path")));
		}
		
		envp[0] = "extract.pdftotext.path=" + properties.getProperty("extract.pdftotext.path");
		envp[1] = "extract.pdfbox-app-jar.path=" + properties.getProperty("extract.pdfbox-app-jar.path");
	}
	
	public void init(File baseDir)
	{				
			statistic.setFilesTotal(Util.countFilesInDirectory(baseDir));
	}
	
	public String getFulltextPath()
	{
		return this.fulltextDir;
	}
	
	public ExtractionStatistic getStatistic()
	{
		return this.statistic;
	}
	
	public void extractFulltexts(File dirOrFile) throws Exception
	{
		this.extractFulltexts(dirOrFile, 0);
	}
	
	public void extractFulltexts(File dirOrFile, long mDateMillis) throws Exception
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
				extractFulltexts(file, mDateMillis);
			}
			else
			{
				if (file.lastModified() < mDateMillis)
				{
					logger.info("Skipping " + file.getName() + " last modification date <" + new Date(file.lastModified()) + ">");
					statistic.incrementFilesSkipped();
					continue;
				}
				try
				{
					ExtractionChain chain = new ExtractionChain();
					
					// too much properties 
					
					chain.setProperties(properties, this.logger);
					ExtractionResult ret = chain.doExtract(file.getCanonicalPath(), (new File(fulltextDir, file.getName())).getAbsolutePath().concat(".txt"));
					
					if (ret == ExtractionResult.OK)
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
		if (!(new File(fulltextDir, file.getName()).exists()))
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
		cmd[2] = properties.getProperty("extract.extraction-chain.path");
		cmd[3] = f.getAbsolutePath();
		cmd[4] = (new File(fulltextDir, f.getName())).getAbsolutePath().concat(".txt");
		
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
		
		long mDateMillis = 0;
		
		if (args.length > 1)
		{
			String mDate = args[1];
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");					
			String combinedDate = mDate + defaultDate.substring(mDate.length());

			mDateMillis = dateFormat.parse(combinedDate).getTime();
		}
		
		// TODO check parameter
		

		FullTextExtractor extractor = new FullTextExtractor();
		
		extractor.init(baseDir);		
		extractor.extractFulltexts(baseDir, mDateMillis);
		
		logger.info(extractor.getStatistic().toString());
	}

}
