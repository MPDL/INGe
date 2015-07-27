/**
 * 
 */
package de.mpg.escidoc.tools.reindex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.mpg.escidoc.services.extraction.ExtractionChain;
import de.mpg.escidoc.services.extraction.ExtractionChain.ExtractionResult;
import de.mpg.escidoc.tools.util.ExtractionReport;
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
	private static ExtractionReport extractionReport = new ExtractionReport();
	private Properties properties = new Properties();
	private String[] envp = new String[2]; 
	
	// number of processor available (means number of parallel threads)
	private int procCount;

	// Thread pool for text extraction
	ExecutorService executor = null;
	
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
		logger.info("Found " + Util.countFilesInDirectory(baseDir) + " for extraction" );
		extractionReport.setFilesTotal(Util.countFilesInDirectory(baseDir));
		
		this.procCount = Integer.parseInt(properties.getProperty("index.number.processors"));
		
		executor = Executors.newFixedThreadPool(procCount);
	}
	
	public void finalizeExtraction() throws Exception
	{
		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.DAYS);
		
		logger.info(extractionReport.toString());
	}
	
	public String getFulltextPath()
	{
		return this.fulltextDir;
	}
	
	public ExtractionReport getStatistic()
	{
		return extractionReport;
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
					extractionReport.incrementFilesSkipped();
					continue;
				}
				try
				{
					Runnable task = new ExtractorThread(file);
					executor.execute(task);
					continue;
					
				} catch (Exception e)
				{
					extractionReport.incrementFilesErrorOccured();
					extractionReport.addToErrorList(file.getName());
					e.printStackTrace();
					
					continue;
				}
			}
		}
	}


	/**
	 * Subclass to enable parallelization.
	 *
	 */
	class ExtractorThread implements Runnable
	{
		File file;
		
		/**
		 * Constructor with the FOXML file.
		 * 
		 * @param file The FOXML file
		 */
		public ExtractorThread(File file)
		{
			this.file = file;
		}

		
		public void run()
		{
			
			try
			{
				logger.info("------------------------------------------------------------------------------------------");
				logger.info("Start extraction <" + file.getName() + ">");
				long start = System.currentTimeMillis();
				ExtractionChain chain = new ExtractionChain();
				
				chain.setProperties(properties, logger);
				ExtractionResult ret = chain.doExtract(file.getCanonicalPath(), (new File(fulltextDir, file.getName())).getAbsolutePath().concat(".txt"));
				
				if (ret == ExtractionResult.OK)
				{
					extractionReport.incrementFilesExtractionDone();
				}
				else
				{
					extractionReport.incrementFilesErrorOccured();
					extractionReport.addToErrorList(file.getName());
				}
				long end = System.currentTimeMillis();
				
				logger.info("Total time used for extraction <" + file.getName() + "> <" + (end - start) + "> ms");	
			}
			catch (Exception e)
			{
				cleanup();
				extractionReport.addToErrorList(file.getName());
				extractionReport.incrementFilesErrorOccured();
				throw new RuntimeException(e);
			}
			cleanup();
			logger.info("------------------------------------------------------------------------------------------");
		}
		
		void cleanup()
		{			
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
		extractor.finalizeExtraction();
	}

}
