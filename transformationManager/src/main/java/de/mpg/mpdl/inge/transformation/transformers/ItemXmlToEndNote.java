package de.mpg.mpdl.inge.transformation.transformers;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;

import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.SingleTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.util.LocalUriResolver;
import de.mpg.mpdl.inge.util.PropertyReader;

@TransformerModule(sourceFormat = FORMAT.ESCIDOC_ITEMLIST_V3_XML, targetFormat = FORMAT.ENDNOTE_STRING)
@TransformerModule(sourceFormat = FORMAT.ESCIDOC_ITEM_V3_XML, targetFormat = FORMAT.ENDNOTE_STRING)
public class ItemXmlToEndNote extends XslTransformer implements ChainableTransformer {


	@Override
	public Source getXsltSource() throws TransformationException{	
		return getXmlSourceFromProperty("escidoc.transformation.escidoc2endnote.stylesheet.filename", "transformations/commonPublicationFormats/xslt/eSciDoc_to_EndNote.xsl");
	}

	@Override
	public Map<String, Object> getParameters() throws TransformationException {
		return null;

	}
	
	
	@Override
	public URIResolver getURIResolver(){
		return new LocalUriResolver("transformations/commonPublicationFormats/xslt");
	}

	@Override
	public Map<String, String> getDefaultConfiguration() throws TransformationException {
		return null;
	}


}
