package de.mpg.escidoc.services.transformation.transformations.otherFormats.endnote;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.transformation.Transformation;
import de.mpg.escidoc.services.transformation.Transformation.TransformationModule;
import de.mpg.escidoc.services.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

@TransformationModule
public class EndNoteTransformation implements Transformation{
	
	private Logger logger = Logger.getLogger(getClass());
	
	private static final Format ENDNOTE_FORMAT = new Format("EndNote", "text/plain", "UTF-8");
	private static final Format ESCIDOC_FORMAT = new Format("eSciDoc", "application/xml", "*");

	 /**
     * Get all possible source formats. 
     * @return Format[]: list of possible source formats as value object
     * @throws RuntimeException
     */
    public Format[] getSourceFormats() throws RuntimeException{
    	return new Format[]{ENDNOTE_FORMAT};
    }
    
    /**
     * Get all possible source formats for a target format. 
     * @param Format : the target format
     * @return Format[]: list of possible source formats as value object
     * @throws RuntimeException
     */
    public Format[] getSourceFormats(Format targetFormat) throws RuntimeException{
    	if (ESCIDOC_FORMAT.getName().equals(targetFormat.getName()) && ESCIDOC_FORMAT.getType().equals(targetFormat.getType()))
        {
            return new Format[]{ENDNOTE_FORMAT};
        }
        else
        {
            return new Format[]{};
        }
    }
    
    /**
     * Get all possible source formats. 
     * @return String: list of possible source formats as xml
     * @throws RuntimeException
     */
    public String getSourceFormatsAsXml() throws RuntimeException{
    
    	return "";
    }
    
    /**
     * Get all possible target formats for a source format.
     * @param src  A source value object 
     * @return Format[]: list of possible target formats as value object
     * @throws RuntimeException
     */
    public Format[] getTargetFormats(Format sourceFormat) throws RuntimeException{
    	if (ENDNOTE_FORMAT.equals(sourceFormat))
        {
            return new Format[]{ESCIDOC_FORMAT};
        }
        else
        {
            return new Format[]{};
        }
    }
    
    /**
     * Get all possible target formats for a source format. 
     * @param srcFormatName  The name of the source format
     * @param srcType  The type of the source
     * @param srcEncoding  The sources encoding
     * @return String: list of possible target formats as xml
     * @throws RuntimeException
     */
    public String getTargetFormatsAsXml(String srcFormatName, String srcType, String srcEncoding)
            throws RuntimeException{
    	// TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see de.mpg.escidoc.services.transformation.Transformation#transform(byte[], de.mpg.escidoc.services.transformation.valueObjects.Format, de.mpg.escidoc.services.transformation.valueObjects.Format, java.lang.String)
     */
    public byte[] transform(byte[] arg0, Format arg1, Format arg2, String arg3)
            throws TransformationNotSupportedException, RuntimeException
    {	 String output="";
        try
        {	File stylesheet = ResourceUtil.getResourceAsFile("transformations/otherFormats/xslt/endnotexml2escidoc.xsl");
            String input = new String(arg0, "UTF-8"); //endnote item in xml
            StringWriter result = new StringWriter();
            if(arg1.getName().equalsIgnoreCase("EndNote")){
            	String endnoteSource = new String(arg0,"UTF-8");
            	EndNoteImport endnote = new EndNoteImport();
            	output = endnote.transformEndNote2XML(endnoteSource);
            	logger.info("intermediate xml:" + output);
            	TransformerFactory factory = TransformerFactory.newInstance();
        		Transformer transformer = factory.newTransformer(new StreamSource(new FileInputStream(stylesheet)));
        		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        		transformer.transform(new StreamSource(new StringReader(output)), new StreamResult(result));
            }
            else
            {
            	
            }
            return result.toString().getBytes("UTF-8");
            
        }
        catch (Exception e) 
        {
            throw new RuntimeException("Error getting file content", e);
        }
    }
    
    /* (non-Javadoc)
     * @see de.mpg.escidoc.services.transformation.Transformation#transform(byte[], java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public byte[] transform(byte[] arg0, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6,
            String arg7) throws TransformationNotSupportedException, RuntimeException
    {
        return transform(arg0, new Format(arg1, arg2, arg3), new Format(arg4, arg5, arg6), arg7);
    }
    

}
