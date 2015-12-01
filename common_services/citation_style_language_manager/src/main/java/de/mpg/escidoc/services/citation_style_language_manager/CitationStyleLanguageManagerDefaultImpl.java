package de.mpg.escidoc.services.citation_style_language_manager;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.TransformerFactoryImpl;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import de.undercouch.citeproc.CSL;
import de.undercouch.citeproc.ItemDataProvider;
import de.undercouch.citeproc.output.Bibliography;
import de.mpg.escidoc.services.common.valueobjects.ExportFormatVO;

/**
 * CitationStyleLanguageManagerDefaultImpl is used to generate a citation for an escidoc item or a list of escidoc items
 * 
 * @author walter
 */
@Stateless
@Remote
public class CitationStyleLanguageManagerDefaultImpl implements CitationStyleLanguageManagerInterface
{
    private final static Logger logger = Logger.getLogger(CitationStyleLanguageManagerDefaultImpl.class);
    private final static String TRANSFORMATION_ITEM_LIST_2_SNIPPET = "itemList2snippet.xsl";
    private final static String CITATION_PROCESSOR_OUTPUT_FORMAT = "html";

    private String citationStyle = null;
    
    /**
     * default constructor 
     */
    public CitationStyleLanguageManagerDefaultImpl ()
    {
    }
    
    /**
     * constructor for setting an citation style explicit
     * (not needed if you have a running CoNE instance and use the autosuggest)
     * 
     * @param citationStyleXml
     */
    public CitationStyleLanguageManagerDefaultImpl (String citationStyleXml)
    {
        this.citationStyle = citationStyleXml;
    }
    
    /*
     * (non-Javadoc)
     * @see org.citation_style_language_manager.CitationStyleLanguageManagerInterface #getOutput(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public byte[] getOutput(ExportFormatVO exportFormat, String itemList) throws Exception
    {
        List<String> citationList = new ArrayList<String>();
        StringWriter snippet = new StringWriter();
        byte[] result = null;
        try
        {
            ItemDataProvider itemDataProvider = new MetadataProvider(itemList);
            if(this.citationStyle == null) 
            {    
                this.citationStyle = CitationStyleLanguageUtils.loadStyleFromConeJsonUrl(exportFormat.getId());
            }
            String defaultLocale = CitationStyleLanguageUtils.parseDefaultLocaleFromStyle(citationStyle);
            CSL citeproc = null;
            if (defaultLocale != null)
            {
                citeproc = new CSL(itemDataProvider, citationStyle, defaultLocale);
            }
            else
            {
                citeproc = new CSL(itemDataProvider, citationStyle);
            }
            citeproc.registerCitationItems(itemDataProvider.getIds());
            citeproc.setOutputFormat(CITATION_PROCESSOR_OUTPUT_FORMAT);
            Bibliography bibl = citeproc.makeBibliography();
            
            List<String> biblIds = Arrays.asList(bibl.getEntryIds());
            
            // remove surrounding <div>-tags
            for (String id : itemDataProvider.getIds())
            {
            	String citation = "";
            	
            	int citationPosition = biblIds.indexOf(id);
            	if(citationPosition!=-1)
            	{
	            	citation = bibl.getEntries()[citationPosition];
	            	
	            	
	                if (citation.contains("<div class=\"csl-right-inline\">"))
	                {
	                    citation = citation.substring(citation.indexOf("<div class=\"csl-right-inline\">") + 30);
	                    citation = citation.substring(0, citation.indexOf("</div>"));
	                }
	                else if (citation.contains("<div class=\"csl-entry\">"))
	                {
	                    citation = citation.substring(citation.indexOf("<div class=\"csl-entry\">") + 23);
	                    citation = citation.substring(0, citation.indexOf("</div>"));
	                }
	                else
	                {
	                    citation = citation.trim();
	                }
	                if (logger.isDebugEnabled())
	                {
	                    logger.debug("Citation: " + citation);
	                }
            	}
                citationList.add(citation);
            }
            // create snippet format
            TransformerFactory factory = new TransformerFactoryImpl();
            Transformer transformer = factory.newTransformer(new StreamSource(
                    this.getClass().getClassLoader().getResourceAsStream(TRANSFORMATION_ITEM_LIST_2_SNIPPET)));
            transformer.setParameter("citations", citationList);
            transformer.transform(new StreamSource(new StringReader(itemList)), new StreamResult(snippet));
            if (logger.isDebugEnabled())
            {
                logger.debug("eSciDoc-Snippet including Ciation: " + snippet);
            }
            result = snippet.toString().getBytes("UTF-8");
        }
        catch (IOException e)
        {
            logger.error("Error creating CSL processor", e);
            throw new Exception(e);
        }
        catch (TransformerConfigurationException e)
        {
            logger.error("Error preparing transformation itemList to snippet", e);
            throw new Exception(e);
        }
        catch (TransformerException e)
        {
            logger.error("Error transforming itemList to snippet", e);
            throw new Exception(e);
        }
        catch (Exception e)
        {
            logger.error("Error getting output", e);
            throw new Exception(e);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see org.citation_style_language_manager.CitationStyleLanguageManagerInterface #isCitationStyle(java.lang.String)
     */
    @Override
    public boolean isCitationStyle(String cs)
    {
        return false;
    }
}
