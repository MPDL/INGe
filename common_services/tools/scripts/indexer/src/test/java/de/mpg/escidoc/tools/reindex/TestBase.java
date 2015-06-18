package de.mpg.escidoc.tools.reindex;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class TestBase
{
	protected static Indexer indexer;
	protected static FullTextExtractor extractor;
	protected static String referenceIndexPath;
	
	protected static Logger logger = Logger.getLogger(TestBase.class);
	
	protected static String[] fieldNamesToSkip = {
		"xml_representation", 
		"xml_metadata"
		};
	
	protected static String[] objidsToSkip = {
		"escidoc:2111614",
		"escidoc:2111636",
		"escidoc:2111643",
		"escidoc:2111653",
		"escidoc:2111721",
		"escidoc:2111712",
		"escidoc:2116439"	
	};
	
	private static Pattern datePattern = Pattern.compile(
            "[0-9]{4}-[0-9]{2}-[0-9]{2}[Tt][0-9\\:\\.]*[zZ]");
    private static Matcher dateMatcher = datePattern.matcher("");
	
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
			
			if (Arrays.asList(objidsToSkip).contains(document1.get("escidoc.objid")))
			{
				logger.warn("Skipping verify for <" + document1.get("escidoc.objid") + ">");
				continue;
			}
			if (document2 == null)
			{
				indexer.getIndexingReport().addToErrorList("No reference document found for <"
						+ document1.get("escidoc.objid") + ">");	
				continue;
			}
			
			logger.info("Verify comparing index documents with <" + document1.get("escidoc.objid") + ">");
			
			List<Fieldable> fields1 = document1.getFields();	
			List<Fieldable> fields2 = document2.getFields();
	
			if (fields1.size() != fields2.size())
			{
				indexer.getIndexingReport().addToErrorList("Different amount of fields " 
						+ fields1.size() + " - " + fields2.size() + " for <" +  document1.get("escidoc.objid") + ">\n");
			}
			
			Map<String, Set<Fieldable>> m1 = getMap(fields1);
			Map<String, Set<Fieldable>> m2 = getMap(fields2);
			
			logger.info("comparing 1 - 2");
			compareFields(m1, m2);
			
			logger.info("comparing 2 - 1");
			compareFields(m2, m1);
		}
	}
	
	public Map<String, Set<Fieldable>> getFieldsOfDocument() throws CorruptIndexException, IOException
	{
		Document document1 = null;
		Map<String, Set<Fieldable>> m1 = null;
		
		IndexReader indexReader1 = IndexReader.open(FSDirectory.open(new File(indexer.getIndexPath())), true);
		
		for (int i = 0; i < indexReader1.maxDoc(); i++)
		{			
			document1 = indexReader1.document(i);			
			
			List<Fieldable> fields1 = document1.getFields();	
		
			m1 = getMap(fields1);
			
		}
		return m1;
	}

	private void compareFields(Map<String, Set<Fieldable>> m1, Map<String, Set<Fieldable>> m2)
	{
		for (String name : m1.keySet())
		{
			if (Arrays.asList(fieldNamesToSkip).contains(name))
				continue;
			
			Set<Fieldable> sf1 = m1.get(name);
			Set<Fieldable> sf2 = m2.get(name);
			
			if (("stored_fulltext".equals(name) || "stored_filename".equals(name)))					
			{
				int i = 1;
				i++;
			}
			
			if (!((sf1 != null && sf2 != null) || (sf1 == null && sf2 == null)))
			{
				indexer.getIndexingReport().addToErrorList("Nothing found for <" + name + ">" + " in <" +  m1.get("escidoc.objid") + ">\n");
				continue;
			}
			
			if (sf1.size() != sf2.size())
			{
				indexer.getIndexingReport().addToErrorList("Different field sizes sf1 - sf2 <" + sf1.size() + "><" + sf2.size() + ">\n");
			}
			
			for (Fieldable f1 : sf1)
			{
				Fieldable f2 = findFieldFor(f1, sf2);
				
				if (f2 == null)
				{
					indexer.getIndexingReport().addToErrorList("No corresponding field found for <" + name + ">" + " in <" +  m1.get("escidoc.objid") + ">\n");
					continue;
				}
			
				IndexOptions o1 = f1.getIndexOptions();
				IndexOptions o2 = f1.getIndexOptions();
				
				if (!o1.equals(o2))
				{
					indexer.getIndexingReport().addToErrorList("Different index options for <" + name + ">" + " in <" +  m1.get("escidoc.objid") + ">\n");
				};
				
				// if we compare time stamps keep in mind that the last position may be withdrawn by escidoc in case of an ending "0"
				/*if (dateMatcher.reset(f1.stringValue()).matches() && dateMatcher.reset(f2.stringValue()).matches())
				{
					int i1 = f1.stringValue().lastIndexOf('z');
					int i2 = f2.stringValue().lastIndexOf('z');
					
					int imin = Math.min(i1, i2);
					
					if (!(f1.stringValue().substring(0, imin).equals(f2.stringValue().substring(0, imin))))
					{
						indexer.getIndexingReport().addToErrorList("Difference timestamp in field(" + name + ") value " + (f1.stringValue()) + " XXXXXXXXXXXXXXXXX " + (f2.stringValue()) + "\n");
					}
							
				}
				else */if (!shorten(f1.stringValue()).equals(shorten(f2.stringValue())))
				{
					indexer.getIndexingReport().addToErrorList("Difference in field(" + name + ") value " + (f1.stringValue()) + " XXXXXXXXXXXXXXXXX " + (f2.stringValue())  + "\n");
				}
				
				if (f1.isIndexed() != f2.isIndexed())
				{
					indexer.getIndexingReport().addToErrorList("Difference in field(" + name + ") isIndexed " + f1.isIndexed() + " - " + f2.isIndexed());
				}
				if (f1.isLazy() != f2.isLazy())
				{
					indexer.getIndexingReport().addToErrorList("Difference in field(" + name + ") isLazy" + f1.isLazy() + " - " + f2.isLazy());
				}
				if (f1.isStored() != f2.isStored())
				{
					indexer.getIndexingReport().addToErrorList("Difference in field(" + name + ") isStored" + f1.isStored() + " - " + f2.isStored());
				}
				if (f1.isTermVectorStored() != f2.isTermVectorStored())
				{
					indexer.getIndexingReport().addToErrorList("Difference in field(" + name + ") isTermVectorStored" + f1.isTermVectorStored() + " - " + f2.isTermVectorStored());
				}
				if (f1.isTokenized() != f2.isTokenized())
				{
					indexer.getIndexingReport().addToErrorList("Difference in field(" + name + ") isTokenized" + f1.isTokenized() + " - " + f2.isTokenized());
				}
				
				logger.debug("comparing field <" + name + "> ok <" + (f1.stringValue()) + " XXXXXXXXX " + (f2.stringValue()) + ">");
			
			}
		}
	}

	private Fieldable findFieldFor(Fieldable f1, Set<Fieldable> sf2)
	{
		if (f1 == null || sf2 == null)
			return null;
		
		for (Fieldable f2 : sf2)
		{
			int c;
			
			if ("stored_fulltext".equals(f1.name()) || "stored_filename".equals(f1.name()))					
			{
				int i = 1;
				i++;
			}
			
			if (shorten(f1.stringValue()).equals(shorten(f2.stringValue())))
				return f2;
			/*else 
			{
				// escidoc removes some ending "0" at timestamps, e.g. 2015-11-11T09:09:99.990Z -> 2015-11-11T09:09:99.99Z 
				
				if (dateMatcher.reset(f1.stringValue()).matches() && dateMatcher.reset(f2.stringValue()).matches()) 
				{
		            int i1 = f1.stringValue().lastIndexOf("z");
		            int i2 = f2.stringValue().lastIndexOf("z");
		            
		            try
					{
						if (i1 > 0 && i2 > 0) 
						{
							if( f1.stringValue().substring(0, i1-1).equals(f2.stringValue().substring(0, i1-1)))
								return f2;
						}
					} catch (Exception e)
					{
						logger.warn(f1.stringValue() + "CCCCCCCCC" + f2.stringValue());
					}
		        }
			}*/
		}
		
		logger.info("Nothing found for <" +  f1.name() + "><" + f1.stringValue() + "> in <" + sf2.iterator().next().stringValue() + ">");

		return null;
	}

	private Map<String, Set<Fieldable>> getMap(List<Fieldable> fields)
	{
		Map<String, Set<Fieldable>> map = new HashMap<String, Set<Fieldable>>();
		
		if (fields == null)
			return map;
		
		for (Fieldable f : fields)
		{
			String name = f.name();
			
			// we put all values together in the same HashSet for the fields "stored_filename1",  "stored_filename1" ...
			// because the ordering of the components may differ
			if (name.contains("stored_filename"))
			{
				name = "stored_filename";
			}
			if (name.contains("stored_fulltext"))
			{
				name = "stored_fulltext";
			}
			Set<Fieldable> hset = map.get(name);
			
			if ("stored_fulltext".equals(name) || "stored_filename".equals(name))
			{
				int j = 0;
				j++;
			}
			
			if (hset == null)
			{
				hset = new HashSet<Fieldable>();	
			}
			
			if (!hset.contains(f))
			{
				hset.add(f);
			}
			
			map.put(name, hset);
		}

		return map;
	}
	
	// the "0" is omitted because if we compare time stamps the last position may be withdrawn by escidoc in case of an ending "0"
	private String shorten(String stringValue)
	{
		String s = stringValue.replaceAll("[^A-Za-z1-9]", "");
		
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