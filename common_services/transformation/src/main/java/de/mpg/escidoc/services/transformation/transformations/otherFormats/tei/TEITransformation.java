package de.mpg.escidoc.services.transformation.transformations.otherFormats.tei;

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
import de.mpg.escidoc.services.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

public class TEITransformation {
	
	private Logger logger = Logger.getLogger(getClass());

	private static final Format ESCIDOC_ITEM_LIST_FORMAT = new Format("eSciDoc-publication-item-list", "application/xml", "*");
	private static final Format ESCIDOC_ITEM_FORMAT = new Format("eSciDoc-publication-item", "application/xml", "*");
    
    
    /* (non-Javadoc)
     * @see de.mpg.escidoc.services.transformation.Transformation#transform(byte[], de.mpg.escidoc.services.transformation.valueObjects.Format, de.mpg.escidoc.services.transformation.valueObjects.Format, java.lang.String)
     */
    public byte[] transform(byte[] src, Format trgFormat)
    {	
        try
        {	
            StringWriter result = new StringWriter();
            String teiSource = new String(src,"UTF-8");
            	
            TransformerFactory factory = new net.sf.saxon.TransformerFactoryImpl();
            InputStream stylesheet = ResourceUtil.getResourceAsStream("transformations/otherFormats/xslt/tei2escidoc.xsl");
            Transformer transformer = factory.newTransformer(new StreamSource(stylesheet));
            	
            if (trgFormat.matches(ESCIDOC_ITEM_LIST_FORMAT))
            {
                transformer.setParameter("is-item-list", Boolean.TRUE);
            }
            else if (trgFormat.matches(ESCIDOC_ITEM_FORMAT))
            {
                transformer.setParameter("is-item-list", Boolean.FALSE);
            }
            else
            {
                this.logger.error("The requested target format (" + trgFormat.toString() + ") is not supported");
            }
            	
            transformer.setParameter("content-model", PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication"));
            transformer.setOutputProperty(OutputKeys.ENCODING, trgFormat.getEncoding());
            transformer.transform(new StreamSource(new StringReader(teiSource)), new StreamResult(result));

            return result.toString().getBytes("UTF-8");

        }
        catch (Exception e) {
            throw new RuntimeException("Error getting file content", e);
        }
    }
    
    /* (non-Javadoc)
     * @see de.mpg.escidoc.services.transformation.Transformation#transform(byte[], java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public byte[] transform(byte[] arg0, String arg1, String arg2, String arg3) throws TransformationNotSupportedException, RuntimeException
    {
        return transform(arg0, new Format(arg1, arg2, arg3));
    }
    

}
