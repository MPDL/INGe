package de.mpg.escidoc.tools.reindex;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
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
import org.junit.Test;

public class TestBase
{
	protected static Indexer indexer;
	protected static String referenceIndexPath;
	
	protected static Logger logger = Logger.getLogger(TestBase.class);
	
	protected static String[] fieldNamesToSkip = {
		"xml_representation", 
		"xml_metadata", 
		"stored_filename1",
		"escidoc.publication.creator.compound.organization-path-identifiers",
		"escidoc.publication.creator.any.organization-path-identifiers",
		"escidoc.any-organization-pids"};
	
	public TestBase()
	{
		super();
	}

	public void verify() throws CorruptIndexException, IOException
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
			
			logger.info("Verify comparing index documents with <" + document1.get("escidoc.objid") + ">");
			
			List<Fieldable> fields1 = document1.getFields();	
			List<Fieldable> fields2 = document2.getFields();
	
			assertTrue("Different amount of fields " + fields1.size() + " - " + fields2.size(), fields1.size() == fields2.size());
			
			Map<String, Set<Fieldable>> m1 = getMap(fields1);
			Map<String, Set<Fieldable>> m2 = getMap(fields2);
			
			compareFields(m1, m2);
			
			logger.info("comparing 2 - 1");
			compareFields(m2, m1);
		}
		logger.info("Verify succeeded ");
	}

	private void compareFields(Map<String, Set<Fieldable>> m1, Map<String, Set<Fieldable>> m2)
	{
		for (String name : m1.keySet())
		{
			if (Arrays.asList(fieldNamesToSkip).contains(name))
				continue;
			
			Set<Fieldable> sf1 = m1.get(name);
			Set<Fieldable> sf2 = m2.get(name);
			
			if ("stored_filename1".equals(name))					
			{
				int i = 1;
				i++;
			}
			
			assertTrue("Nothing found for <" + name + ">", (sf1 != null && sf2 != null) || (sf1 == null && sf2 == null));
			assertTrue(sf1.size() == sf2.size());
			
			for (Fieldable f1 : sf1)
			{
				Fieldable f2 = findFieldFor(f1, sf2);
				
				assertTrue("No corresponding field found for <" + name + ">", f2 != null);
			
				IndexOptions o1 = f1.getIndexOptions();
				IndexOptions o2 = f1.getIndexOptions();
				
				assertTrue(o1.equals(o2));
				
				assertTrue("Difference in field(" + name + ") value " + (f1.stringValue()) + " XXXXXXXXXXXXXXXXX " + (f2.stringValue()), 
						shorten(f1.stringValue()).equals(shorten(f2.stringValue())));
				assertTrue("Difference in field(" + name + ") isIndexed " + f1.isIndexed() + " - " + f2.isIndexed(),
						f1.isIndexed() == f2.isIndexed());
				assertTrue("Difference in field(" + name + ") isLazy " + f1.isLazy() + " - " + f2.isLazy(), 
						f1.isLazy() == f2.isLazy());
				assertTrue("Difference in field(" + name + ") isStored " + f1.isStored() + " - " + f2.isStored(),
						f1.isStored() == f2.isStored());
				assertTrue("Difference in field(" + name + ") isTermVectorStored " + f1.isTermVectorStored() + " - " + f2.isTermVectorStored(),
						f1.isTermVectorStored() == f2.isTermVectorStored());
				assertTrue("Difference in field(" + name + ") isTokenized " + f1.isTokenized() + " - " + f2.isTokenized(),
						f1.isTokenized() == f2.isTokenized());
				
				logger.info("Field <" + name + "> ok " + (f1.stringValue()) + " XXXXXXXXX " + (f2.stringValue()));
			
			}
		}
	}

	private Fieldable findFieldFor(Fieldable f1, Set<Fieldable> sf2)
	{
		if (f1 == null || sf2 == null)
			return null;
		
		for (Fieldable f2 : sf2)
		{
			if (shorten(f1.stringValue()).equals(shorten(f2.stringValue())))
				return f2;
		}
		
		return null;
	}

	private Map<String, Set<Fieldable>> getMap(List<Fieldable> fields)
	{
		Map<String, Set<Fieldable>> map = new HashMap<String, Set<Fieldable>>();
		
		if (fields == null)
			return map;
		
		for (Fieldable f : fields)
		{
			Set<Fieldable> hset = map.get(f.name());
			
			if (hset == null)
			{
				hset = new HashSet<Fieldable>();
				hset.add(f);
			}
			map.put(f.name(), hset);
		}
		return map;
	}
	
	private String shorten(String stringValue)
	{
		String s = stringValue.replaceAll("[^A-Za-z0-9]", "");
		
		return s;
	}
	
	private Document getReferenceDocument(String field, String value, IndexSearcher searcher)
			throws IOException
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