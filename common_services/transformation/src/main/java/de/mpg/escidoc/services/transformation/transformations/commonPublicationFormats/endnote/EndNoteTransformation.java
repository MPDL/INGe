package de.mpg.escidoc.services.transformation.transformations.commonPublicationFormats.endnote;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.transformation.Util;
import de.mpg.escidoc.services.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.escidoc.services.transformation.transformations.commonPublicationFormats.LocalUriResolver;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

public class EndNoteTransformation 
{
	
	private Logger logger = Logger.getLogger(getClass());
	
    private static final Format ENDNOTE_FORMAT = new Format("EndNote", "text/plain", "UTF-8");
    private static final Format ENDNOTE_ICE_FORMAT = new Format("EndNote-ICE", "text/plain", "UTF-8");

	private static final Format ESCIDOC_ITEM_LIST_FORMAT = new Format("eSciDoc-publication-item-list", "application/xml", "*");
	private static final Format ESCIDOC_ITEM_FORMAT = new Format("eSciDoc-publication-item", "application/xml", "*");

    
    /* (non-Javadoc)
     * @see de.mpg.escidoc.services.transformation.Transformation#transform(byte[], de.mpg.escidoc.services.transformation.valueObjects.Format, de.mpg.escidoc.services.transformation.valueObjects.Format, java.lang.String)
     */
    public byte[] endnoteTransform(byte[] src, Format srcFormat, Format trgFormat, String service)
            throws TransformationNotSupportedException, RuntimeException
    {	
        String output="";
        try
        {	
            
            StringWriter result = new StringWriter();
            Util util = new Util();
            
            
            if(util.isFormatEqual(srcFormat, ENDNOTE_FORMAT) || util.isFormatEqual(srcFormat, ENDNOTE_ICE_FORMAT))
            {
            	
            	String endnoteSource = new String(src,"UTF-8");
            	EndNoteImport endnote = new EndNoteImport();
            	output = endnote.transformEndNote2XML(endnoteSource);
            	TransformerFactory factory = new net.sf.saxon.TransformerFactoryImpl();
            	InputStream stylesheet = ResourceUtil.getResourceAsStream("transformations/commonPublicationFormats/xslt/endnotexml2escidoc.xsl");
            	factory.setURIResolver(new LocalUriResolver());
            	Transformer transformer = factory.newTransformer(new StreamSource(stylesheet));
            	//Transformer transformer = factory.newTransformer(stylesheet);
            	
            	if (util.isFormatEqual(trgFormat, ESCIDOC_ITEM_LIST_FORMAT))
            	{
            		transformer.setParameter("is-item-list", Boolean.TRUE);
            	}
            	else if (util.isFormatEqual(trgFormat, ESCIDOC_ITEM_FORMAT))
            	{
            		transformer.setParameter("is-item-list", Boolean.FALSE);
            	}
            	else
            	{
            		throw new TransformationNotSupportedException("The requested target format (" + trgFormat.toString() + ") is not supported");
            	}
            	
            	transformer.setParameter("content-model", PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication"));
            	transformer.setParameter("source-name", srcFormat.getName().toLowerCase());
            	
            	transformer.setOutputProperty(OutputKeys.ENCODING, trgFormat.getEncoding());
            	transformer.transform(new StreamSource(new StringReader(output)), new StreamResult(result));
            	
            	// throw new TransformationNotSupportedException("Sorry, WoS is not yet implemented");
            	
            }
            else
            {
                throw new TransformationNotSupportedException("Sorry, this is the Endnote transformation");
            }

            return result.toString().getBytes("UTF-8");
           //return output.getBytes();
           // return ResourceUtil.getResourceAsString(src).getBytes("UTF-8");
        }
        catch (Exception e) {
            throw new RuntimeException("Error getting file content", e);
        }
    }

}
