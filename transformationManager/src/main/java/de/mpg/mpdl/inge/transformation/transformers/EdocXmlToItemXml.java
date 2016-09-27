package de.mpg.mpdl.inge.transformation.transformers;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;

import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.SingleTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.util.PropertyReader;

@TransformerModule(sourceFormat = FORMAT.EDOC_XML, targetFormat = FORMAT.ESCIDOC_ITEM_V3_XML)
@TransformerModule(sourceFormat = FORMAT.EDOC_XML, targetFormat = FORMAT.ESCIDOC_ITEMLIST_V3_XML)
public class EdocXmlToItemXml extends XslTransformer implements ChainableTransformer {


	@Override
	public Source getXsltSource() throws TransformationException{
		return getXmlSourceFromProperty("escidoc.transformation.edoc.stylesheet.filename", "transformations/otherFormats/xslt/edoc-to-escidoc.xslt");
	}

	@Override
	public Map<String, Object> getParameters() throws TransformationException {
		Map<String, Object> map = new HashMap<String, Object>(); 
		
		  if(FORMAT.ESCIDOC_ITEM_V3_XML.equals(getTargetFormat())){
	        	map.put("is-item-list", Boolean.FALSE);
	      }
	      else if(FORMAT.ESCIDOC_ITEMLIST_V3_XML.equals(getTargetFormat())){
	        	map.put("is-item-list", Boolean.TRUE);
	       }
		map.put("content-model", PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication"));
		map.put("source-name", "endnote");
		map.put("root-ou", PropertyReader.getProperty("escidoc.pubman.root.organisation.id"));
		map.put("external-ou", PropertyReader.getProperty("escidoc.pubman.external.organisation.id"));
		map.put("frameworkUrl", PropertyReader.getProperty("escidoc.framework_access.framework.url"));
        
        return map;

	}
	

	@Override
	public Map<String, String> getDefaultConfiguration() throws TransformationException{
		return SingleTransformer.getDefaultConfigurationFromProperty("escidoc.transformation.edoc.configuration.filename", "transformations/otherFormats/conf/edoc.properties");
	}

}