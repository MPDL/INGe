package de.mpg.escidoc.services.transformation.transformations.commonPublicationFormats.endnote;

import java.io.FileOutputStream;
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
import de.mpg.escidoc.services.transformation.Transformation;
import de.mpg.escidoc.services.transformation.Util;
import de.mpg.escidoc.services.transformation.Transformation.TransformationModule;
import de.mpg.escidoc.services.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.escidoc.services.transformation.transformations.LocalUriResolver;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

@TransformationModule
public class EndNoteTransformation implements Transformation
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
            	
/*FileOutputStream fos = new FileOutputStream("target/endnote.xml");
fos.write(output.getBytes("UTF-8"));
fos.close();
*/            	            	
            	TransformerFactory factory = new net.sf.saxon.TransformerFactoryImpl();
            	InputStream stylesheet = ResourceUtil.getResourceAsStream("transformations/commonPublicationFormats/xslt/" +
            			"endnote" +
            			( util.isFormatEqual(srcFormat, ENDNOTE_ICE_FORMAT) ? "ice" : "" ) +
            			"xml2escidoc.xsl"
            	);
            	factory.setURIResolver(new LocalUriResolver("transformations/commonPublicationFormats/xslt"));
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
            	transformer.setParameter("root-ou", PropertyReader.getProperty("escidoc.pubman.root.organisation.id"));
            	
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


	/* (non-Javadoc)
	 * @see de.mpg.escidoc.services.transformation.Transformation#getSourceFormats()
	 */
	public Format[] getSourceFormats() throws RuntimeException {
		return new Format[]{ENDNOTE_FORMAT, ENDNOTE_ICE_FORMAT};
	}


	/* (non-Javadoc)
	 * @see de.mpg.escidoc.services.transformation.Transformation#getSourceFormats(de.mpg.escidoc.services.transformation.valueObjects.Format)
	 */
	public Format[] getSourceFormats(Format trg) throws RuntimeException {
		if (ESCIDOC_ITEM_FORMAT.equals(trg) || ESCIDOC_ITEM_LIST_FORMAT.equals(trg))
		{
			return new Format[]{ENDNOTE_FORMAT, ENDNOTE_ICE_FORMAT};
		}
		else
		{
			return new Format[]{};
		}
	}


	/* (non-Javadoc)
	 * @see de.mpg.escidoc.services.transformation.Transformation#getSourceFormatsAsXml()
	 */
	public String getSourceFormatsAsXml() throws RuntimeException {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see de.mpg.escidoc.services.transformation.Transformation#getTargetFormats(de.mpg.escidoc.services.transformation.valueObjects.Format)
	 */
	public Format[] getTargetFormats(Format src) throws RuntimeException {
		if (ENDNOTE_FORMAT.equals(src) || ENDNOTE_ICE_FORMAT.equals(src))
		{
			return new Format[]{ESCIDOC_ITEM_FORMAT, ESCIDOC_ITEM_LIST_FORMAT};
		}
		else
		{
			return new Format[]{};
		}
	}


	/* (non-Javadoc)
	 * @see de.mpg.escidoc.services.transformation.Transformation#getTargetFormatsAsXml(java.lang.String, java.lang.String, java.lang.String)
	 */
	public String getTargetFormatsAsXml(String srcFormatName, String srcType,
			String srcEncoding) throws RuntimeException {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see de.mpg.escidoc.services.transformation.Transformation#transform(byte[], de.mpg.escidoc.services.transformation.valueObjects.Format, de.mpg.escidoc.services.transformation.valueObjects.Format, java.lang.String)
	 */
	public byte[] transform(byte[] src, Format srcFormat, Format trgFormat,
			String service) throws TransformationNotSupportedException,
			RuntimeException {
		return endnoteTransform(src, srcFormat, trgFormat, service);
	}


	/* (non-Javadoc)
	 * @see de.mpg.escidoc.services.transformation.Transformation#transform(byte[], java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public byte[] transform(byte[] src, String srcFormatName, String srcType,
			String srcEncoding, String trgFormatName, String trgType,
			String trgEncoding, String service)
			throws TransformationNotSupportedException, RuntimeException {
		return transform(src, new Format(srcFormatName, srcType, srcEncoding), new Format(trgFormatName, trgType, trgEncoding), service);
	}

}
