
/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.transformation.transformations.otherFormats.escidoc;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.helpers.DefaultHandler;

import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.transformation.Transformation;
import de.mpg.escidoc.services.transformation.Transformation.TransformationModule;
import de.mpg.escidoc.services.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

/**
 * Transformation from the eSciDoc metadata profile v1 to v2   
 * @author vmakarenko (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$ 
 *
 */
@TransformationModule
public class eSciDocVer1ToeSciDocVer2 extends DefaultHandler implements Transformation
{

    
    private static final Format ESCIDOC_ITEM_LIST_V1_FORMAT = new Format("escidoc-publication-item-list-v1", "application/xml", "*");
    private static final Format ESCIDOC_ITEM_V1_FORMAT = new Format("escidoc-publication-item-v1", "application/xml", "*");
    private static final Format ESCIDOC_ITEM_LIST_V2_FORMAT = new Format("escidoc-publication-item-list-v2", "application/xml", "*");
    private static final Format ESCIDOC_ITEM_V2_FORMAT = new Format("escidoc-publication-item-v2", "application/xml", "*");
    
    private static final String XSLT_PATH = "transformations/otherFormats/xslt/escidoc-publication-v1_2_escidoc-publication-v2.xsl";
    
    /**
     * {@inheritDoc}
     */
    public Format[] getSourceFormats()
    {
        return new Format[]{ESCIDOC_ITEM_LIST_V1_FORMAT, ESCIDOC_ITEM_V1_FORMAT};
    }

    /**
     * {@inheritDoc}
     */
    public Format[] getSourceFormats(Format trg)
    {
        if (trg != null && (trg.matches(ESCIDOC_ITEM_LIST_V2_FORMAT) || trg.matches(ESCIDOC_ITEM_V2_FORMAT)))
        {
            return getSourceFormats();
        }
        else
        {
            return new Format[]{};
        }
    }

    
    /**
     * {@inheritDoc}
     */
    @Deprecated
    public String getSourceFormatsAsXml()
    {
        throw new RuntimeException("Not implemented");
    }

    /**
     * {@inheritDoc}
     */
    public Format[] getTargetFormats(Format src) throws RuntimeException
    {
        if (src != null && (src.matches(ESCIDOC_ITEM_LIST_V1_FORMAT) || src.matches(ESCIDOC_ITEM_V1_FORMAT)))
        {
            return new Format[]{ESCIDOC_ITEM_LIST_V2_FORMAT,  ESCIDOC_ITEM_V2_FORMAT};
        }
        else
        {
            return new Format[]{};
        }
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    public String getTargetFormatsAsXml(String srcFormatName, String srcType, String srcEncoding)
    {
        throw new RuntimeException("Not implemented");
    }

    /**
     * {@inheritDoc}
     */
    public byte[] transform(byte[] src, String srcFormatName, String srcType, String srcEncoding, String trgFormatName,
            String trgType, String trgEncoding, String service) throws TransformationNotSupportedException,
            RuntimeException
    {
        Format srcFormat = new Format(srcFormatName, srcType, srcEncoding);
        Format trgFormat = new Format(trgFormatName, trgType, trgEncoding);
        return transform(src, srcFormat, trgFormat, service);
    }

    /**
     * {@inheritDoc}
     */
    public byte[] transform(byte[] src, Format srcFormat, Format trgFormat, String service)
        throws TransformationNotSupportedException
    {
        StringWriter result = new StringWriter();
        
        try
        {
            System.out.print("Started xslt transformation...");
            TransformerFactory factory = new net.sf.saxon.TransformerFactoryImpl();
            InputStream stylesheet = ResourceUtil.getResourceAsStream(XSLT_PATH);
            
            Transformer transformer = factory.newTransformer(new StreamSource(stylesheet));
            
            if (trgFormat.matches(ESCIDOC_ITEM_LIST_V2_FORMAT))
            {
                transformer.setParameter("is-item-list", Boolean.TRUE);
            }
            else if (trgFormat.matches(ESCIDOC_ITEM_V2_FORMAT))
            {
                transformer.setParameter("is-item-list", Boolean.FALSE);
            }
            else
            {
                throw new TransformationNotSupportedException("The requested target format (" + trgFormat.toString() + ") is not supported");
            }
            
            //Find path to the xslt directory.
            //The path is needed to resolve the ves-mapping.xml in xslt  
//            String path = ResourceUtil.getResourceAsFile(XSLT_PATH).getAbsolutePath();
//            path = path.replaceFirst("\\/[\\w_.-]+$", "");
            transformer.setURIResolver(new myURIResolver());
//            transformer.setParameter("path", path);
            
            transformer.setOutputProperty(OutputKeys.ENCODING, trgFormat.getEncoding());
            transformer.transform(new StreamSource(new ByteArrayInputStream(src)), new StreamResult(result));
            System.out.println("Finished!");
            
            return result.toString().getBytes(trgFormat.getEncoding());
            
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error parsing edoc xml", e);
        }
        
    }

    /**
     * URIResolver for the documnet() in the XSLT 
     *
     */
    class myURIResolver implements URIResolver 
    {
    	public String absPath;
    	
    	public myURIResolver() throws FileNotFoundException 
    	{
    		absPath = ResourceUtil.getResourceAsFile(XSLT_PATH).getParent();
		}
    	
		public Source resolve(String href, String base) throws TransformerException {
//			System.out.println("resolving stylesheet ref " + href);
//			System.out.println("abs path:" + absPath);
			Source src = null;
			try 
			{
				src = new StreamSource(ResourceUtil.getResourceAsStream(absPath + "/" + href));
			} 
			catch (FileNotFoundException e) 
			{
				// TODO Auto-generated catch block
				throw new TransformerException("cannot find path to the document:" + href, e);
			} 
			return src;
		}
	}

    
    
}
