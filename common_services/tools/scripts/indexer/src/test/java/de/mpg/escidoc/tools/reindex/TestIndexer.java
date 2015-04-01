package de.mpg.escidoc.tools.reindex;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class TestIndexer
{

	private static Indexer indexer;
	private static FullTextExtractor extractor;
	private static String referenceIndexPath;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		indexer = new Indexer(new File("../test-classes/20"), "escidoc_all");
		indexer.prepareIndex();
		extractor = new FullTextExtractor();
		
		extractor.init(new File("../test-classes/19/escidoc_2110752+content+content.0"));
		extractor.extractFulltexts(new File("../test-classes/19/escidoc_2110752+content+content.0"));
		
		//referenceIndexPath = "C:/tmp/jboss/server/default/data/index/lucene/escidoc_all";
		referenceIndexPath = indexer.getIndexPath();
		
	}

	

	@Test
	// escidoc_2110118 item without component
	// escidoc_2110119 with component escidoc_2110752
	public void test() throws Exception
	{
		indexer.indexItemsStart(new File("../test-classes/20"));
		indexer.finalizeIndex();
		
		assertTrue("Expected 2 Found " + indexer.getItemCount(), indexer.getItemCount() == 1);
		
		verify();
	}

	private void verify() throws CorruptIndexException, IOException
	{
		Document document1 = null;
		Document document2 = null;
		
		IndexReader indexReader1 = IndexReader.open(FSDirectory.open(new File(indexer.getIndexPath())), true);
		IndexReader indexReader2 = IndexReader.open(FSDirectory.open(new File(referenceIndexPath)), true);
		IndexSearcher indexSearcher2 = new IndexSearcher(indexReader2);
		
		for (int i = 0; i < indexReader1.maxDoc(); i++)
		{			
			document1 = indexReader1.document(i);			
			document2 = getReferenceDocument("escidoc.objid", document1.get("escidoc.objid"), indexSearcher2);
			
			if (document2 == null)
			{
				assertTrue("No reference document found for <" + document1.get("escidoc.objid") + ">", false);
			}
			
			List<Fieldable> fields1 = document1.getFields();			
			List<Fieldable> fields2 = document2.getFields();
			
			assertTrue(fields1.size() == fields2.size());
			
			// todo
						
			for (Fieldable f : fields1)
			{
				f.name();
				byte[] b = f.getBinaryValue();
				IndexOptions o = f.getIndexOptions();
			}
		}
		
	}



	private Document getReferenceDocument(String field, String value, IndexSearcher searcher) throws IOException
	{
		Document doc = null;
		
		Query query = new TermQuery(new Term(field, value));		
		TopDocs topDocs = searcher.search(query, 1);
		
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		
		for (int j = 0; j < scoreDocs.length; ++j)
		{
			int docId = scoreDocs[j].doc;
			
			doc = searcher.getIndexReader().document(docId);
		}
		
		return doc;
	}

}
