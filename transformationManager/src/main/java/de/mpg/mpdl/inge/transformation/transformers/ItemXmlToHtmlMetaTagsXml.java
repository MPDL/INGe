package de.mpg.mpdl.inge.transformation.transformers;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;

import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.util.PropertyReader;

@TransformerModule(sourceFormat = FORMAT.ESCIDOC_ITEM_V3_XML, targetFormat = FORMAT.HTML_METATAGS_DC_XML)
@TransformerModule(sourceFormat = FORMAT.ESCIDOC_ITEM_V3_XML, targetFormat = FORMAT.HTML_METATAGS_HIGHWIRE_PRESS_CIT_XML)
public class ItemXmlToHtmlMetaTagsXml extends XslTransformer implements ChainableTransformer {


	@Override
	public Source getXsltSource() throws TransformationException{
		
			if(FORMAT.HTML_METATAGS_DC_XML.equals(getTargetFormat())){
				return getXmlSourceFromProperty("escidoc.transformation.html_metatags_dc.stylesheet.filename" ,"transformations/standardFormats/xslt/escidoc-publication-item2html-meta-tags-dc.xsl");
			}
			else if(FORMAT.HTML_METATAGS_HIGHWIRE_PRESS_CIT_XML.equals(getTargetFormat())) {
				return getXmlSourceFromProperty("escidoc.transformation.html_metatags_highwire.stylesheet.filename" ,"transformations/standardFormats/xslt/escidoc-publication-item2html-meta-tags-highwire-press-citation.xsl");
			}
			else {
				return null;
			}
			
	}

	@Override
	public Map<String, Object> getParameters() throws TransformationException {
        Map<String, Object> map = new HashMap<String, Object>();
		map.put("pubmanInstanceUrl", PropertyReader.getProperty("escidoc.pubman.instance.url"));
		map.put("pubmanComponentPattern",PropertyReader.getProperty("escidoc.pubman.component.pattern"));
		map.put("pubmanContextPath",PropertyReader.getProperty("escidoc.pubman.instance.context.path"));
		return map;
	}

	@Override
	public Map<String, String> getDefaultConfiguration() throws TransformationException {
		return null;
	}


}
