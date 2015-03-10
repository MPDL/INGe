/**
 * 
 */
package de.mpg.escidoc.tools.reindex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author franke
 *
 */
public class Indexer
{

	String indexPath = "c:/tmp/index";
	boolean create = true;
	
	private File baseDir;
	
	Transformer transformer1 = TransformerFactory.newInstance().newTransformer(new StreamSource(new File("scripts/indexer/src/de/mpg/escidoc/tools/reindex/foxml2escidoc.xsl")));
	Transformer transformer2 = TransformerFactory.newInstance().newTransformer(new StreamSource(new File("scripts/indexer/src/de/mpg/escidoc/tools/reindex/mpdlEscidocXmlToLucene.xslt")));

	IndexWriter writer;
	
	/**
	 * Constructor with initial base directory, should be the fedora "objects" directory.
	 * @param baseDir
	 */
	public Indexer(File baseDir) throws Exception
	{
		this.baseDir = baseDir;
	}
	
	public void prepareIndex()
	{
	    try {
	    	System.out.println("Indexing to directory '" + indexPath + "'...");

	    	Directory dir = FSDirectory.open(new File(indexPath));
	    	// :Post-Release-Update-Version.LUCENE_XY:
	    	Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
	    	IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_36, analyzer);

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

	    	writer = new IndexWriter(dir, iwc);

	    	// NOTE: if you want to maximize search performance,
	    	// you can optionally call forceMerge here.  This can be
	    	// a terribly costly operation, so generally it's only
	    	// worth it when your index is relatively static (ie
	    	// you're done adding documents to it):
	    	//
	    	// writer.forceMerge(1);

	    } catch (IOException e) {
	    	System.out.println(" caught a " + e.getClass() +
	    	 "\n with message: " + e.getMessage());
	    }
	}
	
	public void finalizeIndex() throws Exception
	{
		writer.close();
	}
	
	public void indexDoc(InputStream inputStream) throws Exception
	{

	      try {

	        // make a new, empty document
	        Document doc = new Document();
	        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
	        DefaultHandler dh = new IndexDocument(doc);
	        parser.parse(inputStream, dh);

	        if (writer.getConfig().getOpenMode() == OpenMode.CREATE)
	        {
				// New index, so we just add the document (no old document can be there):
				writer.addDocument(doc);
	        }
	        else
	        {
	        	// Existing index (an old copy of this document may have been indexed) so 
	        	// we use updateDocument instead to replace the old one matching the exact 
	        	// path, if present:
	        	writer.updateDocument(new Term("PID", doc.get("PID")), doc);
	        }
	        
	      }
	      finally
	      {
	    	  inputStream.close();
	      }
	}

	/**
	 * Gather information from the FOXMLs and write them into the given file.
	 * 
	 * @param file The file where to write the data to.
	 */
	public void createDatabase(File file) throws Exception
	{
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		fileOutputStream.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes("UTF-8"));
		fileOutputStream.write("<index>\n".getBytes("UTF-8"));
		checkDir(baseDir, fileOutputStream);
		fileOutputStream.write("</index>\n".getBytes("UTF-8"));
		fileOutputStream.close();
	}
	
	private void checkDir(File dir, FileOutputStream fileOutputStream) throws Exception
	{
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
	
	private void indexItems(File dir) throws Exception
	{
		for (File file : dir.listFiles())
		{
			if (file.isDirectory())
			{
				indexItems(file);
			}
			else
			{
				indexItem(file);
			}
		}
	}
	
	private void indexItem(File file) throws Exception
	{
		File tmpFile1 = File.createTempFile("file", ".tmp");
		File tmpFile2 = File.createTempFile("file", ".tmp");
		transformer1.transform(new StreamSource(file), new StreamResult(tmpFile1));
		transformer2.transform(new StreamSource(tmpFile1), new StreamResult(tmpFile2));
		indexDoc(new FileInputStream(tmpFile2));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{

		File baseDir = new File(args[0]);
		File dbFile = new File(args[1]);
		
		Indexer indexer = new Indexer(baseDir);
		indexer.prepareIndex();
		indexer.createDatabase(dbFile);
		
		indexer.indexItems(baseDir);
		indexer.finalizeIndex();

			
	}

}
