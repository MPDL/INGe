package de.mpg.escidoc.tools.reindex;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
			
			Map<String, Fieldable> m1 = getMap(fields1);
			Map<String, Fieldable> m2 = getMap(fields2);
			
			compareFields(m1, m2);
			compareFields(m2, m1);
		}
		
	}

	private void compareFields(Map<String, Fieldable> m1,
			Map<String, Fieldable> m2)
	{
		for (String name : m1.keySet())
		{
			Fieldable f1 = m1.get(name);
			Fieldable f2 = m2.get(name);
			
			assertTrue(f1 != null && f2 != null);
			
			IndexOptions o1 = f1.getIndexOptions();
			IndexOptions o2 = f1.getIndexOptions();
			
			assertTrue(o1.equals(o2));
			
			assertTrue(f1.stringValue().equals(f2.stringValue()));
			assertTrue(f1.isIndexed() == f2.isIndexed());
			assertTrue(f1.isLazy() == f2.isLazy());
			assertTrue(f1.isStored() == f2.isStored());
			assertTrue(f1.isTermVectorStored() == f2.isTermVectorStored());
			assertTrue(f1.isTokenized() == f2.isTokenized());
		}
	}

	private Map<String, Fieldable> getMap(List<Fieldable> fields)
	{
		Map<String, Fieldable> map = new HashMap<String, Fieldable>();
		
		if (fields == null)
			return map;
		
		for (Fieldable f : fields)
		{
			map.put(f.name(), f);
		}
		return map;
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
