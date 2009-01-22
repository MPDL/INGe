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

package de.mpg.escidoc.services.transformation;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.RemoteBinding;
import org.junit.Test;

import de.mpg.escidoc.services.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.escidoc.services.transformation.transformations.citationFormats.CitationTransformationInterface;
import de.mpg.escidoc.services.transformation.transformations.commonPublicationFormats.CommonTransformationInterface;
import de.mpg.escidoc.services.transformation.transformations.feedFormats.FeedTransformationInterface;
import de.mpg.escidoc.services.transformation.transformations.microFormats.MicroTransformationInterface;
import de.mpg.escidoc.services.transformation.transformations.otherFormats.OtherFormatsTransformationInterface;
import de.mpg.escidoc.services.transformation.transformations.thirdPartyFormats.ThirdPartyTransformationInterface;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

/**
 * Implementation of the Transformation Service.
 *
 * @author Friederike Kleinfercher (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */

@Stateless
@Remote
@RemoteBinding(jndiBinding = Transformation.SERVICE_NAME)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)

public class TransformationBean implements Transformation
{
    
    private final Logger logger = Logger.getLogger(TransformationBean.class);
    
    private final String thirdPartyModule = "THIRD_PARTY_MODULE";
    private final String microModule = "MICRO_MODULE";
    private final String feedsModule = "FEEDS_MODULE";
    private final String commonModule = "COMMON_MODULE";
    private final String citationModule = "CITATION_MODULE";
    private final String otherModule = "OTHER_MODULE";
    
    private Util util;
    
    //All transformation modules:
    private MicroTransformationInterface microTrans;
    private ThirdPartyTransformationInterface thirdPartyTrans;
    private CitationTransformationInterface citeTrans;
    private CommonTransformationInterface commonTrans;
    private FeedTransformationInterface feedTrans;
    private OtherFormatsTransformationInterface otherTrans;

   
    
    /**
     * Public constructor.
     */
    public TransformationBean()
    {
        this.util = new Util();
        
        this.microTrans = new MicroTransformationInterface();
        this.thirdPartyTrans = new ThirdPartyTransformationInterface();
        this.citeTrans = new CitationTransformationInterface();
        this.commonTrans = new CommonTransformationInterface();
        this.feedTrans = new FeedTransformationInterface();
        this.otherTrans = new OtherFormatsTransformationInterface();
    }
    
    /**
     * {@inheritDoc}
     */
    public Format[] getSourceFormats() throws RuntimeException
    {
        Vector<Format[]> allFormats = new Vector<Format[]>();
        
        Format[] microFormats = this.microTrans.getSourceFormats();       
        Format[] thirdPartyFormats = this.thirdPartyTrans.getSourceFormats();
        Format[] citeFormats = this.citeTrans.getSourceFormats();
        Format[] commonFormats = this.commonTrans.getSourceFormats();
        Format[] feedFormats = this.feedTrans.getSourceFormats();
        Format[] otherFormats = this.otherTrans.getSourceFormats();
        
        allFormats.add(microFormats);
        allFormats.add(thirdPartyFormats);
        allFormats.add(citeFormats);
        allFormats.add(commonFormats);
        allFormats.add(feedFormats);
        allFormats.add(otherFormats);
        
        return this.util.mergeFormats(allFormats);
    }

    /**
     * {@inheritDoc}
     */
    public String getSourceFormatsAsXml() throws RuntimeException
    {
        Format[] allFormats = this.getSourceFormats();       
        return this.util.createFormatsXml(allFormats);
    }

    /**
     * {@inheritDoc}
     */
    public String getTargetFormatsAsXml(String srcFormatName, String srcType, String srcEncoding)
        throws RuntimeException
    {
        Format[] allFormats = this.getTargetFormats(new Format(srcFormatName, srcType, srcEncoding));
        return this.util.createFormatsXml(allFormats);
    }

    /**
     * {@inheritDoc}
     */
    public Format[] getTargetFormats(Format src) throws RuntimeException
    {
        //Normalize mimetype to avoid that e.g. application/xml and text/xml need two different transformations
        src.setType(this.util.normalizeMimeType(src.getType()));
        
        Vector<Format[]> allFormats = new Vector<Format[]>();

        Format[] microFormats = this.microTrans.getTargetFormats(src);
        Format[] thirdPartyFormats = this.thirdPartyTrans.getTargetFormats(src);
        Format[] citeFormats = this.citeTrans.getTargetFormats(src);
        Format[] commonFormat = this.commonTrans.getTargetFormats(src);
        Format[] feedFormat = this.feedTrans.getTargetFormats(src);
        Format[] otherFormat = this.otherTrans.getTargetFormats(src);
        
        allFormats.add(microFormats);
        allFormats.add(thirdPartyFormats);
        allFormats.add(citeFormats);
        allFormats.add(commonFormat);
        allFormats.add(feedFormat);
        allFormats.add(otherFormat);
        
        return this.util.mergeFormats(allFormats);
    }
    
    /**
     * {@inheritDoc}
     */
    public Format[] getSourceFormats(Format trg) throws RuntimeException
    {
        //Normalize mimetype to avoid that e.g. application/xml and text/xml need two different transformations
        trg.setType(this.util.normalizeMimeType(trg.getType()));
        
        Vector<Format[]> allFormats = new Vector<Format[]>();

        Format[] microFormats = this.microTrans.getSourceFormats(trg);
        Format[] thirdPartyFormats = this.thirdPartyTrans.getSourceFormats(trg);
        Format[] citeFormats = this.citeTrans.getSourceFormats(trg);
        Format[] commonFormat = this.commonTrans.getSourceFormats(trg);
        Format[] feedFormat = this.feedTrans.getSourceFormats(trg);
        Format[] otherFormat = this.otherTrans.getSourceFormats(trg);
        
        allFormats.add(microFormats);
        allFormats.add(thirdPartyFormats);
        allFormats.add(citeFormats);
        allFormats.add(commonFormat);
        allFormats.add(feedFormat);
        allFormats.add(otherFormat);
        
        return this.util.mergeFormats(allFormats);
    }

    /**
     * {@inheritDoc}
     * @throws TransformationNotSupportedException 
     */
    public byte[] transform(byte[] src, String srcFormatName, String srcType, String srcEncoding, String trgFormatName,
        String trgType, String trgEncoding, String service) 
        throws TransformationNotSupportedException, RuntimeException
    {
        Format source = new Format(srcFormatName, srcType, srcEncoding);
        Format target = new Format(trgFormatName, trgType, trgEncoding);       
        return this.transform(src, source, target, service);
    }

    /**
     * {@inheritDoc}
     * @throws TransformationNotSupportedException 
     */
    public byte[] transform(byte[] src, Format srcFormat, Format trgFormat, String service) 
        throws TransformationNotSupportedException, RuntimeException
    {
        //Normalize mimetype to avoid that e.g. application/xml and text/xml need two different transformations
        srcFormat.setType(this.util.normalizeMimeType(srcFormat.getType()));
        
        if (service.toLowerCase().equals("escidoc"))
        {
            return this.escidocTransformService(src, srcFormat, trgFormat, service);
        }
        return null;
    }
    
    private byte[] escidocTransformService(byte[] src, Format srcFormat, Format trgFormat, String service) 
        throws TransformationNotSupportedException, RuntimeException
    {
        String module = this.findModule(srcFormat, trgFormat);
        if (module == null)
        {
            this.logger.warn("Transformation not supported: \n" + srcFormat.getName() + ", " + srcFormat.getType() 
                        + ", " + srcFormat.getEncoding() + "\n" + trgFormat.getName() + ", " + trgFormat.getType() 
                        + ", " + trgFormat.getEncoding());
            throw new TransformationNotSupportedException();
        }
        if (module.equals(this.citationModule))
        {          
            return this.citeTrans.transform(src, srcFormat, trgFormat, service);        
        }
        if (module.equals(this.commonModule))
        {
            return this.commonTrans.transform(src, srcFormat, trgFormat, service);
        }
        if (module.equals(this.feedsModule))
        {
            return this.feedTrans.transform(src, srcFormat, trgFormat, service);
        }
        if (module.equals(this.microModule))
        {
            return this.microTrans.transform(src, srcFormat, trgFormat, service);
        }
        if (module.equals(this.thirdPartyModule))
        {
            return this.thirdPartyTrans.transform(src, srcFormat, trgFormat, service);
        }
        if (module.equals(this.otherModule))
        {
            return this.otherTrans.transform(src, srcFormat, trgFormat, service);
        }
        return null;
    }
    
    private String findModule(Format source, Format target)
    {
        Format[] targets;
        
        targets = this.microTrans.getTargetFormats(source);
        if (this.util.containsFormat(targets, target))
        {
            return this.microModule;
        }
        targets = this.thirdPartyTrans.getTargetFormats(source);
        if (this.util.containsFormat(targets, target))
        {
            return this.thirdPartyModule;
        }
        targets = this.citeTrans.getTargetFormats(source);
        if (this.util.containsFormat(targets, target))
        {
            return this.citationModule;
        }
        targets = this.commonTrans.getTargetFormats(source);
        if (this.util.containsFormat(targets, target))
        {
            return this.commonModule;
        }
        targets = this.feedTrans.getTargetFormats(source);
        if (this.util.containsFormat(targets, target))
        {
            return this.feedsModule;
        }
        targets = this.otherTrans.getTargetFormats(source);
        if (this.util.containsFormat(targets, target))
        {
            return this.otherModule;
        }
        return null;
    }
    
    @Test
    public void getTransformationModule()
    {
        //Annotation transformationModule;
        //transformationModule = this.getClass().getAnnotation(TransformationModule.class);
        System.out.println("start...");
        
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        
//        try
//        {
            //Enumeration<URL> urls = cl.getResources("java.class.path");
            URL url = cl.getResource("java.class.path");
            System.out.println(url);
            
//            System.out.println("has element: " + urls.hasMoreElements());
//            while (urls.hasMoreElements())
//            {
//                URL url = urls.nextElement();
//                System.out.println(url);
//            }
            
            Annotation[] transformationModules = Transformation.class.getAnnotations();
            for (int i =0; i< transformationModules.length; i++)
            {
                Annotation a = transformationModules[i];
                System.out.println(a);
            }
//        }
//        catch (IOException e)
//        {
//        }
        
    }

}
