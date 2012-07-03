package de.mpg.escidoc.services.transformation.transformations.commonPublicationFormats.endnote;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.transformation.Configurable;
import de.mpg.escidoc.services.transformation.Transformation;
import de.mpg.escidoc.services.transformation.Transformation.TransformationModule;
import de.mpg.escidoc.services.transformation.Util;
import de.mpg.escidoc.services.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.escidoc.services.transformation.transformations.LocalUriResolver;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

@TransformationModule
public class EndNoteTransformation implements Transformation, Configurable
{
	
	private Logger logger = Logger.getLogger(getClass());
	
    private static final Format ENDNOTE_FORMAT = new Format("EndNote", "text/plain", "UTF-8");

	private static final Format ESCIDOC_ITEM_LIST_FORMAT = new Format("eSciDoc-publication-item-list", "application/xml", "*");
	private static final Format ESCIDOC_ITEM_FORMAT = new Format("eSciDoc-publication-item", "application/xml", "*");

    private Map<String, List<String>> properties = null;
    private Map<String, String> configuration = null;

    private void init() throws IOException, FileNotFoundException, URISyntaxException
    {
        configuration = new LinkedHashMap<String, String>();
        properties = new HashMap<String, List<String>>();
        Properties props = new Properties();
        props.load(ResourceUtil.getResourceAsStream(PropertyReader.getProperty("escidoc.transformation.endnote.configuration.filename")));
        for (Object key : props.keySet())
        {
            if (!"configuration".equals(key.toString()))
            {
                String[] values = props.getProperty(key.toString()).split(",");
                properties.put(key.toString(), Arrays.asList(values));
            }
            else
            {
                String[] confValues = props.getProperty("configuration").split(",");
                for (String field : confValues)
                {
                    String[] fieldArr = field.split("=", 2);
                    configuration.put(fieldArr[0], fieldArr[1] == null ? "" : fieldArr[1]);
                }
            }
        }
    }

    public List<String> getConfigurationValues(Format srcFormat, Format trgFormat, String key) throws Exception
    {
        if (properties == null)
        {
            init();
        }

        return properties.get(key);
    }

    public Map<String, String> getConfiguration(Format srcFormat, Format trgFormat) throws Exception
    {
        if (configuration == null)
        {
            init();
        }

        return configuration;
    }

    /* (non-Javadoc)
     * @see de.mpg.escidoc.services.transformation.Transformation#transform(byte[], de.mpg.escidoc.services.transformation.valueObjects.Format, de.mpg.escidoc.services.transformation.valueObjects.Format, java.lang.String)
     */
    public byte[] endnoteTransform(byte[] src, Format srcFormat, Format trgFormat, String service, Map<String, String> configuration)
            throws TransformationNotSupportedException, RuntimeException
    {	
        String output="";
        try
        {	
            
            StringWriter result = new StringWriter();
            
            if(Util.isFormatEqual(srcFormat, ENDNOTE_FORMAT))
            {
            	
            	String endnoteSource = new String(src,"UTF-8");
            	EndNoteImport endnote = new EndNoteImport();
            	output = endnote.transformEndNote2XML(endnoteSource);
            	
            	String flavor = (configuration == null ? null : configuration.get("Flavor"));
            	            	
            	TransformerFactory factory = new net.sf.saxon.TransformerFactoryImpl();
            	
            	String fileName;
            	
            	if (flavor != null && ("ICE".equals(flavor) || "BGC".equals(flavor)))
            	{
            		fileName = PropertyReader.getProperty("escidoc.transformation.endnote.ice.stylesheet.filename");
            	}
            	else
            	{
            		fileName = PropertyReader.getProperty("escidoc.transformation.endnote.stylesheet.filename");
            	}
            	
            	InputStream stylesheet = ResourceUtil.getResourceAsStream(fileName);
            	
            	factory.setURIResolver(new LocalUriResolver("transformations/commonPublicationFormats/xslt"));
            	Transformer transformer = factory.newTransformer(new StreamSource(stylesheet));
            	//Transformer transformer = factory.newTransformer(stylesheet);
            	
            	if (Util.isFormatEqual(trgFormat, ESCIDOC_ITEM_LIST_FORMAT))
            	{
            		transformer.setParameter("is-item-list", Boolean.TRUE);
            	}
            	else if (Util.isFormatEqual(trgFormat, ESCIDOC_ITEM_FORMAT))
            	{
            		transformer.setParameter("is-item-list", Boolean.FALSE);
            	}
            	else
            	{
            		throw new TransformationNotSupportedException("The requested target format (" + trgFormat.toString() + ") is not supported");
            	}

                if (configuration != null)
                {
                    for (String key : configuration.keySet())
                    {
                        System.out.println("ADD PARAM " + key + " WITH VALUE " + configuration.get(key));
                        transformer.setParameter(key, configuration.get(key));
                    }
                }
                
            	transformer.setParameter("content-model", PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication"));
            	transformer.setParameter("source-name", srcFormat.getName().toLowerCase());
            	transformer.setParameter("root-ou", PropertyReader.getProperty("escidoc.pubman.root.organisation.id"));
            	transformer.setParameter("external-ou", PropertyReader.getProperty("escidoc.pubman.external.organisation.id"));
            	
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
		return new Format[]{ENDNOTE_FORMAT};
	}


	/* (non-Javadoc)
	 * @see de.mpg.escidoc.services.transformation.Transformation#getSourceFormats(de.mpg.escidoc.services.transformation.valueObjects.Format)
	 */
	public Format[] getSourceFormats(Format trg) throws RuntimeException {
		if (trg != null && (trg.matches(ESCIDOC_ITEM_FORMAT) || trg.matches(ESCIDOC_ITEM_LIST_FORMAT)))
		{
			return new Format[]{ENDNOTE_FORMAT};
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
		if (ENDNOTE_FORMAT.equals(src))
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

	/**
     * {@inheritDoc}
     */
    public byte[] transform(byte[] src, Format srcFormat, Format trgFormat, String service, Map<String, String> configuration)
        throws TransformationNotSupportedException
    {
        return endnoteTransform(src, srcFormat, trgFormat, service, configuration);
    }

	/* (non-Javadoc)
	 * @see de.mpg.escidoc.services.transformation.Transformation#transform(byte[], de.mpg.escidoc.services.transformation.valueObjects.Format, de.mpg.escidoc.services.transformation.valueObjects.Format, java.lang.String)
	 */
	public byte[] transform(byte[] src, Format srcFormat, Format trgFormat,
			String service) throws TransformationNotSupportedException,
			RuntimeException {
		return endnoteTransform(src, srcFormat, trgFormat, service, null);
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
