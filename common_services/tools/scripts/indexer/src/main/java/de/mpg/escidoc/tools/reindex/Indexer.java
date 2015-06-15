/**
 * 
 */
package de.mpg.escidoc.tools.reindex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.trans.DynamicError;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.escidoc.sb.common.lucene.analyzer.EscidocAnalyzer;
import de.mpg.escidoc.tools.util.IndexingReport;
import de.mpg.escidoc.tools.util.Util;

/**
 * @author franke
 *
 */
public class Indexer
{

	private static Logger logger = Logger.getLogger(Indexer.class);
	
	protected String indexPath = "";
	static final String defaultDate = "0000-01-01 00:00:00";
	
	private static final String propFileName = "indexer.properties";
	private static Properties properties = new Properties();
	
	boolean create = true;
	
	private File baseDir;
	private File dbFile;
	private String indexStylesheet;
	private String indexName;
	private String indexAttributesName;
	private String fulltextDir;
	private long mDateMillis;
	private String resumeFilename = "current-dir.txt";
	private String resumeDir = null;
	private int procCount;
	int busyProcesses = 0;
	private String currentDir = null;
	
	private String mimetypes;
	
	Stack<Transformer> transformerStackFoxml2Escidoc = new Stack<Transformer>();
	Stack<Transformer> transformerStackEscidoc2Index = new Stack<Transformer>();
	
	private TransformerFactory saxonFactory = new net.sf.saxon.TransformerFactoryImpl();
	
	private Transformer transformerStylesheet = saxonFactory.newTransformer(new StreamSource(new File("./target/classes/prepareStylesheet.xsl")));

	IndexWriter indexWriter;
	
	private IndexingReport indexingReport = new IndexingReport();
	

	
	/**
	 * Constructor with initial base directory, should be the fedora "objects" directory.
	 * 
	 * @param baseDir
	 * @param indexName 
	 * @throws Exception
	 */
	public Indexer(File baseDir, String indexName) throws Exception
	{
		this.baseDir = baseDir;
		
		logger.info("Found " + Util.countFilesInDirectory(baseDir) + " files to index in " + baseDir.getAbsolutePath());
		
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
		
		this.dbFile = new File(properties.getProperty("index.db.file"));
		
		this.indexPath = properties.getProperty("index.result.directory");
		if (!(new File(indexPath)).exists())
		{
			FileUtils.forceMkdir(new File(indexPath));
		}
		
		this.indexStylesheet = properties.getProperty("index.stylesheet");
		this.indexName = properties.getProperty("index.name.built");;
		this.indexAttributesName = properties.getProperty("index.stylesheet.attributes");
	
		String mDate = properties.getProperty("index.modification.date", "0");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");					
		String combinedDate = mDate + defaultDate.substring(mDate.length());
		mDateMillis = dateFormat.parse(combinedDate).getTime();
		this.mDateMillis = mDateMillis;
		
		this.mimetypes = readMimetypes();
		this.procCount = Integer.parseInt(properties.getProperty("index.number.processors"));;
		
		// Create temp file for modified index stylesheet
		File tmpFile = File.createTempFile("stylesheet", ".tmp");
		
		logger.info("transforming index stylesheet to " + tmpFile);
		
		long s3 = System.currentTimeMillis();
		
		//Transformer transformerStylesheet = saxonFactory.newTransformer(new StreamSource(getClass().getClassLoader().getResourceAsStream("prepareStylesheet.xsl")));

		transformerStylesheet.setParameter("attributes-file", indexAttributesName.replace("\\", "/"));
		transformerStylesheet.transform(new StreamSource(getClass().getClassLoader().getResourceAsStream(indexStylesheet)), new StreamResult(tmpFile));
		
		long e3 = System.currentTimeMillis();
		logger.info("transforming index stylesheet used <" + (e3-s3) + "> ms");
		
		for (int i = 0; i < procCount; i++)
		{		
			Transformer transformerFoxml2escidoc = saxonFactory.newTransformer(new StreamSource(getClass().getClassLoader().getResourceAsStream("foxml2escidoc.xsl")));
			transformerFoxml2escidoc.setParameter("index-db", dbFile.getAbsolutePath().replace("\\", "/"));
			transformerFoxml2escidoc.setParameter("version", "escidoc_all".equals(this.indexName) ? "latest-release" : "latest-version");
			transformerFoxml2escidoc.setParameter("number", i);
			transformerStackFoxml2Escidoc.push(transformerFoxml2escidoc);
			
			Transformer transformerEscidoc2Index = saxonFactory.newTransformer(new StreamSource(tmpFile));
			//Xalan transformation not possible due to needed XSLT2 functions
			//transformer2 = xalanFactory.newTransformer(new StreamSource(tmpFile));
			transformerEscidoc2Index.setParameter("index-db", dbFile.getAbsolutePath().replace("\\", "/"));
			transformerEscidoc2Index.setParameter("SUPPORTED_MIMETYPES", mimetypes);
			transformerEscidoc2Index.setParameter("fulltext-directory", fulltextDir.replace("\\", "/"));
			transformerStackEscidoc2Index.push(transformerEscidoc2Index);
		}

		File resumeFile = new File(resumeFilename);
		char[] buffer = new char[2048];
		if (resumeFile.exists())
		{
			FileReader reader = new FileReader(resumeFile);
			int len = reader.read(buffer);
			reader.close();
			if (len > 0)
			{
			resumeDir = new String(buffer, 0, len);
			logger.info("Resuming at directory " + resumeDir);
			}
		}		
	}

	/**
	 * 
	 * If everything went fine, delete the file with the current directory.
	 * 
	 */
	private void removeResumeFile() {
		File resumeFile = new File(resumeFilename);
		resumeFile.delete();
	}
	
	public String getIndexPath()
	{
		return this.indexPath;
	}
	
	public IndexingReport getIndexingReport()
	{
		return this.indexingReport;
	}

	/**
	 * Read the allowed mimetypes for fulltexts from a file.
	 * 
	 * @return A String holding the mimetype list that is given to the transformation as a parameter.
	 * @throws Exception Any exception.
	 */
	private String readMimetypes() throws Exception
	{
		String line;
		StringWriter result = new StringWriter();		
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("mimetypes.txt")));
		
		while ((line = reader.readLine()) != null)
		{
			result.write(line);
			result.write("\n");
		}
		return result.toString();
	}

	/**
	 * Open lucene index for writing.
	 */
	public void prepareIndex()
	{
	    try {
	    	logger.info("Indexing to directory '" + indexPath + "'...");

	    	Directory dir = FSDirectory.open(new File(indexPath));
	    	// :Post-Release-Update-Version.LUCENE_XY:
	    	final Analyzer analyzer = new EscidocAnalyzer();
	    	IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_34, analyzer);

	    	if (create) {
	    	  // Create a new index in the directory, removing any
	    	  // previously indexed documents:
	    	  iwc.setOpenMode(OpenMode.CREATE);
	    	} else {
	    	  // Add new documents to an existing index:
	    	  iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
	    	}

	    	// Optional: for better indexing performance, if you
	    	// are indexing many documents, increase the RAM
	    	// buffer.  But if you do this, increase the max heap
	    	// size to the JVM (eg add -Xmx512m or -Xmx1g):
	    	//
	    	iwc.setRAMBufferSizeMB(256.0);

	    	indexWriter = new IndexWriter(dir, iwc);

	    	// NOTE: if you want to maximize search performance,
	    	// you can optionally call forceMerge here.  This can be
	    	// a terribly costly operation, so generally it's only
	    	// worth it when your index is relatively static (ie
	    	// you're done adding documents to it):
	    	//
	    	// writer.forceMerge(1);

	    } catch (IOException e) {
	    	logger.info(" caught a " + e.getClass() +
	    	 "\n with message: " + e.getMessage());
	    }
	}
	
	public void finalizeIndex() throws Exception
	{
		indexWriter.close();
		logger.info(this.getIndexingReport().toString());
	}

	/**
	 * Gather information from the FOXMLs and write them into the given file.
	 * 
	 * @param file The file where to write the data to.
	 */
	public void createDatabase() throws Exception
	{
		FileOutputStream fileOutputStream = new FileOutputStream(dbFile);
		fileOutputStream.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes("UTF-8"));
		fileOutputStream.write("<index>\n".getBytes("UTF-8"));
		checkDir(baseDir, fileOutputStream);
		fileOutputStream.write("</index>\n".getBytes("UTF-8"));
		fileOutputStream.close();
	}
	
	/**
	 * read the contents of a directory and write it to index db.
	 * 
	 * @param dir Current directory or file.
	 * @param fileOutputStream The stream pointing to the index db.
	 * @throws Exception Any exception.
	 */
	private void checkDir(File dir, FileOutputStream fileOutputStream) throws Exception
	{
		if (dir.isFile())
		{
			fileOutputStream.write(("<object name=\"" + dir.getName().replace("_", ":") + "\" path=\"" + dir.getAbsolutePath().replace("\\", "/") + "\"/>\n").getBytes("UTF-8"));
			return;
		}
		
		for (File file : dir.listFiles())
		{
			if (file.isDirectory())
			{
				checkDir(file, fileOutputStream);
			}
			else
			{
				fileOutputStream.write(("<object name=\"" + file.getName().replace("_", ":") + "\" path=\"" + file.getAbsolutePath().replace("\\", "/") + "\"/>\n").getBytes("UTF-8"));
			}
		}
	}

	/**
	 * Initialize indexing process and wait until all processes are ready.
	 * 
	 * @param baseDir The base directory.
	 * @throws Exception Any exception.
	 */
	public void indexItemsStart(File baseDir) throws Exception
	{
		try
		{
			
			indexItems(baseDir);
			
			while (busyProcesses > 0)
			{
				Thread.sleep(10);
			}

		}
		catch (Exception e)
		{
			logger.warn("Indexing interrupted at <" + currentDir + ">");
			FileWriter writer = new FileWriter(new File(resumeFilename));
			writer.write(currentDir);
		}
		
	}
	
	/**
	 * Index a directory.
	 * 
	 * @param dir The directory.
	 * @throws Exception Any exception.
	 */
	private void indexItems(File dir) throws Exception
	{
		currentDir = dir.getAbsolutePath();
		if (resumeDir == null || resumeDir.startsWith(currentDir) || currentDir.compareTo(resumeDir) > 0)
		{
			if (dir.isFile())
			{
				new IndexThread(dir).start();
				busyProcesses++;
				return;
			}
			
			Arrays.sort(dir.listFiles());
			for (File file : dir.listFiles())
			{
				if (file.isDirectory())
				{
					logger.info("Indexing directory " + file);
					indexItems(file);
				}
				else if (file.lastModified() >= mDateMillis)
				{
					while (busyProcesses >= procCount)
					{
						System.out.print(".");
						Thread.sleep(10);
					}
					new IndexThread(file).start();
					busyProcesses++;
				}
				else if (file.lastModified() < mDateMillis)
				{
					indexingReport.incrementFilesSkippedBecauseOfTime();
				}
			}
		}
		else
		{
			logger.info("Omitting directory " + currentDir);
		}
	}
	
	/**
	 * Subclass to enable parallelization.
	 * 
	 * @author franke
	 *
	 */
	class IndexThread extends Thread
	{
		File file;
		Transformer transformer1;
		Transformer transformer2;
		
		/**
		 * Constructor with the FOXML file.
		 * 
		 * @param file The FOXML file
		 */
		public IndexThread(File file)
		{
			this.file = file;
			transformer1 = transformerStackFoxml2Escidoc.pop();
			transformer2 = transformerStackEscidoc2Index.pop();
		}
		
		public void run()
		{
			
			try
			{
				logger.info("------------------------------------------------------------------------------------------");
				logger.info("Start indexItem <" + file.getName() + ">");
				long start = System.currentTimeMillis();
				indexItem(file);
				long end = System.currentTimeMillis();
				
				logger.info("Total time used for <" + file.getName() + "> <" + (end - start) + "> ms");	
			}
			catch (Exception e)
			{
				cleanup();
				indexingReport.addToErrorList(file.getName());
				indexingReport.incrementFilesErrorOccured();
				throw new RuntimeException(e);
			}
			cleanup();
			logger.info("------------------------------------------------------------------------------------------");
		}
		
		void cleanup()
		{
			transformerStackFoxml2Escidoc.push(transformer1);
			transformerStackEscidoc2Index.push(transformer2);
			busyProcesses--;
			logger.info(transformer1.getParameter("number") + " done.");
		}
		
		/**
		 * Do all the necessary transformations and build the index document.
		 * 
		 * @param file The FOXML file.
		 * @throws Exception Any exception.
		 */
		private void indexItem(File file) throws Exception
		{
			logger.info("Indexing file " + file);
			
			//kaputt or modified
			// TODO
			/*if ("escidoc_2110486".equals(file.getName()) || "escidoc_2110490".equals(file.getName()))
					return;*/
			
			StringWriter writer1 = new StringWriter();
			StringWriter writer2 = new StringWriter();
			
			try
			{
				long s1 = System.currentTimeMillis();
				transformer1.transform(new StreamSource(file), new StreamResult(writer1));
				long e1 = System.currentTimeMillis();
				logger.info("FOXML2eSciDoc transformation used <" + (e1 - s1) + "> ms");
				
				if (logger.isDebugEnabled())
				{
					File tmpFile1 = File.createTempFile(file.getName() + "_foxml2escidoc_", ".tmp");
					FileUtils.writeStringToFile(tmpFile1, writer1.toString());
				}
			}
			catch (DynamicError de)
			{
				if ("noitem".equals(de.getErrorCodeLocalPart()))
				{
					logger.info("No item in < " + file + ">");
					indexingReport.incrementFilesSkippedBecauseOfStatusOrType();
					return;
				} 
				else if ("wrongStatus".equals(de.getErrorCodeLocalPart()))
				{
					logger.info("Item in wrong public status < " + file + ">");
					indexingReport.incrementFilesSkippedBecauseOfStatusOrType();
					return;
				}
			}		
						
			long s2 = System.currentTimeMillis();
			transformer2.transform(new StreamSource(new StringReader(writer1.toString())), new StreamResult(writer2));
			long e2 = System.currentTimeMillis();
			logger.info("eSciDoc2IndexDoc transformation used <" + (e2 - s2) + "> ms");
			
			if (logger.isDebugEnabled())
			{
				File tmpFile2 = File.createTempFile(file.getName() + "_idx_", ".tmp");			
				FileUtils.writeStringToFile(tmpFile2, writer2.toString());
			}
			
			try
			{
				indexDoc(new StringReader(writer2.toString()));
			} 
			catch (IOException e)
			{
				indexWriter.close();
				e.printStackTrace();
			}
			indexingReport.incrementFilesIndexingDone();
		}
		
		/**
		 * Write the contents of the index document and the fulltext to the lucene index.
		 * 
		 * @param inputStream The index document.
		 * @throws ParserConfigurationException 
		 * @throws Exception Any exception.
		 */
		public void indexDoc(StringReader inputStream) throws IOException, SAXException, ParserConfigurationException
		{

		      try {

		        // make a new, empty document
		        Document doc = new Document();
		        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		        DefaultHandler dh = new IndexDocument(doc, fulltextDir);
		        parser.parse(new InputSource(inputStream), dh);

		        if (indexWriter.getConfig().getOpenMode() == OpenMode.CREATE)
		        {
					// New index, so we just add the document (no old document can be there):
					indexWriter.addDocument(doc);
		        }
		        else
		        {
		        	// Existing index (an old copy of this document may have been indexed) so 
		        	// we use updateDocument instead to replace the old one matching the exact 
		        	// path, if present:
		        	indexWriter.updateDocument(new Term("PID", doc.get("PID")), doc);
		        	
		        	logger.info(doc.toString());
		        }
		        
		      }
		      finally
		      {
		    	  inputStream.close();
		      }
		}
	}

	/**
	 * Main method.
	 * 
	 * @param args Command line parameters.
	 */
	public static void main(String[] args) throws Exception
	{
		long start = new Date().getTime();
		
/*		if (null == args || args.length != 10)
		{
			logger.info("Usage: java Indexer [parameters]");
			logger.info("Parameters:");
			logger.info("1 - Base directory");
			logger.info("2 - Index result directory");
			logger.info("3 - File for temporary foxml data");
			logger.info("4 - Generate temporary foxml data (true/false)");
			logger.info("5 - Index stylesheet");
			logger.info("6 - Index stylesheet attributes");
			logger.info("7 - Index name");
			logger.info("8 - Modification date");
			logger.info("9 - Fulltext directory");
			logger.info("10 - Number of processors that should be used");
			System.exit(0);
		}*/
		
		File baseDir = new File(args[0]);
		
		String indexName = args[1];
		
		/*indexPath = args[1];
		File dbFile = new File(args[2]);
		File indexStylesheet = new File(args[4]);
		String indexAttributesName = args[5];
		String indexName = args[6];
		
		String mDate = args[7];
		String fulltextDir = args[8];
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String combinedDate = mDate + defaultDate.substring(mDate.length());
		long mDateMillis = dateFormat.parse(combinedDate).getTime();
		int procCount = Integer.parseInt(args[9]);*/
		
		Indexer indexer = new Indexer(baseDir, indexName);
		
		indexer.prepareIndex();
		
/*		if ("true".equals(args[2]))
		{*/
			indexer.createDatabase();
	//	}
		
		indexer.indexItemsStart(baseDir);
		indexer.finalizeIndex();
		indexer.removeResumeFile();

		long end = new Date().getTime();
		logger.info("Time: " + (end - start));
		logger.info(indexer.getIndexingReport().toString());
	}

	public void getIndexDirectory()
	{
		// TODO Auto-generated method stub
		
	}

}
