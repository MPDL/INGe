package de.mpg.mpdl.inge.transformation.transformers;

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.apache.xml.resolver.tools.CatalogResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.XMLReader;

import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.SingleTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerSource;
import de.mpg.mpdl.inge.util.PropertyReader;

@TransformerModule(sourceFormat = FORMAT.BMC_XML, targetFormat = FORMAT.ESCIDOC_ITEM_V3_XML)
@TransformerModule(sourceFormat = FORMAT.BMC_XML, targetFormat = FORMAT.ESCIDOC_ITEMLIST_V3_XML)
public class BmcXmlToItemXml extends XslTransformer implements ChainableTransformer {

	@Override
	public void transform(TransformerSource source, TransformerResult result) throws TransformationException {
		
		
		  //For the source, a sax source is required which resolves the doctype system id's in the source xml.
		SAXSource saxSource = null;
	      try {
			  SAXParserFactory saxparserfactory = SAXParserFactory.newInstance();
			  saxparserfactory.setNamespaceAware(true);
			  saxparserfactory.setValidating(false);
			  SAXParser parser = null;
			  parser = saxparserfactory.newSAXParser();
			
			  XMLReader xmlreader = null;
			 
			  xmlreader = parser.getXMLReader(); 
			  
			  EntityResolver res = new CatalogResolver();
			  xmlreader.setEntityResolver(res);
			  saxSource =  new SAXSource(xmlreader, SAXSource.sourceToInputSource((Source)source));
		} catch (Exception e) {
			throw new TransformationException("Could not transform BMC xml source  to SAX source");
		} 
		
	      super.transform(saxSource, (Result)result);
	}
	
	
	@Override
	public Source getXsltSource() throws TransformationException{
		return getXmlSourceFromProperty("escidoc.transformation.bmc2escidoc.stylesheet.filename", "transformations/commonPublicationFormats/xslt/bmc_to_pubman.xsl");
	}

	@Override
	public Map<String, Object> getParameters() throws TransformationException {
		Map<String, Object> map = new HashMap<String, Object>(); 
		
		String ns_prefix_xsd_soap_common_srel = (PropertyReader.getProperty("xsd.soap.common.srel") != null) 
				? "{" + PropertyReader.getProperty("xsd.soap.common.srel") + "}"
		 		: "{http://escidoc.de/core/01/structural-relations/}";
	        
	        
        map.put(ns_prefix_xsd_soap_common_srel + "context-URI", PropertyReader.getProperty("escidoc.framework_access.context.id.test")); 
        map.put(ns_prefix_xsd_soap_common_srel + "content-model-URI",PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication")); 

        if(FORMAT.ESCIDOC_ITEM_V3_XML.equals(getTargetFormat()))
        {
        	map.put("{http://www.editura.de/ns/2012/misc}target-format", "eSciDoc-publication-item");
        }
        else if(FORMAT.ESCIDOC_ITEMLIST_V3_XML.equals(getTargetFormat()))
        {
        	map.put("{http://www.editura.de/ns/2012/misc}target-format", "eSciDoc-publication-item-list");
        }
        
        return map;

	}
	

	@Override
	public Map<String, String> getDefaultConfiguration() throws TransformationException{
		return SingleTransformer.getDefaultConfigurationFromProperty("escidoc.transformation.bmc2escidoc.configuration.filename", "transformations/commonPublicationFormats/conf/bmc2escidoc.properties");
	}

}