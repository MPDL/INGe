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

package de.mpg.escidoc.services.transformation.transformations.feedFormats;

import java.util.Vector;

import org.apache.log4j.Logger;

import de.mpg.escidoc.metadataprofile.schema.x01.transformation.TransformationType;
import de.mpg.escidoc.metadataprofile.schema.x01.transformation.TransformationsDocument;
import de.mpg.escidoc.metadataprofile.schema.x01.transformation.TransformationsType;
import de.mpg.escidoc.services.transformation.Transformation;
import de.mpg.escidoc.services.transformation.Util;
import de.mpg.escidoc.services.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

/**
 * Implementation of the transformation interface for feeds.
 * @author Friederike Kleinfercher (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class FeedTransformationInterface implements Transformation
{

    private final Logger logger = Logger.getLogger(FeedTransformationInterface.class);
    
    private final String EXPLAIN_FILE_PATH ="resources/transformations/feeds/";
    private final String EXPLAIN_FILE_NAME="explain-transformations.xml";

    private Util util;
    
    /**
     * Public constructor.
     */
    public FeedTransformationInterface()
    {
        this.util = new Util();
    }
    
    /**
     * {@inheritDoc}
     */
    public Format[] getSourceFormats() throws RuntimeException
    {
        Vector<Format> sourceFormats = new Vector<Format>();
        TransformationsDocument transDoc = null;
        TransformationsType transType = null;
          
        ClassLoader cl = this.getClass().getClassLoader();
        java.io.InputStream in = cl.getResourceAsStream(this.EXPLAIN_FILE_PATH + this.EXPLAIN_FILE_NAME);
        try
        {
            transDoc = TransformationsDocument.Factory.parse(in);
        }
        catch (Exception e)
        {
            this.logger.error("An error occurred while reading transformations.xml for feeds.", e);
            throw new RuntimeException();
        }
        transType = transDoc.getTransformations();
        TransformationType[] transformations = transType.getTransformationArray();
        for (int i = 0; i < transformations.length; i++)
        {
            TransformationType transformation = transformations [i];
            String name = this.util.simpleLiteralTostring(transformation.getSource().getName());
            String type = this.util.simpleLiteralTostring(transformation.getSource().getType());
            String encoding = this.util.simpleLiteralTostring(transformation.getSource().getEncoding());
            Format sourceFormat = new Format(name, type, encoding);
              
            sourceFormats.add(sourceFormat);       
        }       
        sourceFormats = this.util.getRidOfDuplicatesInVector(sourceFormats);
        Format[] dummy = new Format[sourceFormats.size()];
        return sourceFormats.toArray(dummy);       
    }

     /**
      * {@inheritDoc}
      */
    public String getSourceFormatsAsXml() throws RuntimeException
    {
        Format[] formats = this.getSourceFormats();
        return this.util.createFormatsXml(formats);
    }

     /**
      * {@inheritDoc}
      */
    public String getTargetFormatsAsXml(String srcFormatName, String srcType, String srcEncoding) 
        throws RuntimeException
    {
        Format[] formats = this.getTargetFormats(new Format(srcFormatName, srcType, srcEncoding));
        return this.util.createFormatsXml(formats);
    }

     /**
      * {@inheritDoc}
      */
    public Format[] getTargetFormats(Format src) throws RuntimeException
    {
        Vector<Format> targetFormats = new Vector<Format>();
        TransformationsDocument transDoc = null;
        TransformationsType transType = null;
       
        ClassLoader cl = this.getClass().getClassLoader();
        java.io.InputStream in = cl.getResourceAsStream(this.EXPLAIN_FILE_PATH + this.EXPLAIN_FILE_NAME);
        try
        {
            transDoc = TransformationsDocument.Factory.parse(in);
        }
        catch (Exception e)
        {
            this.logger.error("An error occurred while reading transformations.xml for feeds.", e);
            throw new RuntimeException();
        }
         
        transType = transDoc.getTransformations();
        TransformationType[] transformations = transType.getTransformationArray();
        for (TransformationType transformation : transformations)
        {
            Format source = new Format(this.util.simpleLiteralTostring(transformation.getSource().getName()),
                  this.util.simpleLiteralTostring(transformation.getSource().getType()),
                  this.util.simpleLiteralTostring(transformation.getSource().getEncoding()));
            //Only get Target if source is given source
            if (this.util.isFormatEqual(source, src))
            {
                String name = this.util.simpleLiteralTostring(transformation.getTarget().getName());
                String type = this.util.simpleLiteralTostring(transformation.getTarget().getType());
                String encoding = this.util.simpleLiteralTostring(transformation.getTarget().getEncoding());
                Format sourceFormat = new Format(name, type, encoding);
               
                targetFormats.add(sourceFormat);   
            }
        }    
        targetFormats = this.util.getRidOfDuplicatesInVector(targetFormats);
        Format[] dummy = new Format[targetFormats.size()];
        return targetFormats.toArray(dummy);
    }

     /**
      * {@inheritDoc}
      */
    public byte[] transform(byte[] src, String srcFormatName, String srcType, String srcEncoding, String trgFormatName,
             String trgType, String trgEncoding, String service) throws TransformationNotSupportedException
    {
        Format source = new Format(srcFormatName, srcType, srcEncoding);
        Format target = new Format(trgFormatName, trgType, trgEncoding);
        return this.transform(src, source, target, service);
    }

     /**
      * {@inheritDoc}
      */
    public byte[] transform(byte[] src, Format srcFormat, Format trgFormat, String service)
        throws TransformationNotSupportedException, RuntimeException
    {
        byte[] result = null;
        boolean supported = false;
        
        //TODO
        
        if (!supported)
        {
            this.logger.warn("Transformation not supported: /n " + srcFormat.getName() + ", " + srcFormat.getType() 
                    + ", " + srcFormat.getEncoding() + "/n" + trgFormat.getName() + ", " + trgFormat.getType() 
                    + ", " + trgFormat.getEncoding());
            throw new TransformationNotSupportedException();
        }
        
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    public Format[] getSourceFormats(Format trg) throws RuntimeException
    {
        Vector<Format> sourceFormats = new Vector<Format>();
        TransformationsDocument transDoc = null;
        TransformationsType transType = null;
      
        ClassLoader cl = this.getClass().getClassLoader();
        java.io.InputStream in = cl.getResourceAsStream(this.EXPLAIN_FILE_PATH + this.EXPLAIN_FILE_NAME);
        try
        {
            transDoc = TransformationsDocument.Factory.parse(in);
        }
        catch (Exception e)
        {
            this.logger.error("An error occurred while reading transformations.xml for common publication formats.", e);
            throw new RuntimeException();
        }
        
        transType = transDoc.getTransformations();
        TransformationType[] transformations = transType.getTransformationArray();
        for (TransformationType transformation : transformations)
        {
            Format target = new Format(this.util.simpleLiteralTostring(transformation.getTarget().getName()),
                  this.util.simpleLiteralTostring(transformation.getTarget().getType()),
                  this.util.simpleLiteralTostring(transformation.getTarget().getEncoding()));
            //Only get Target if source is given source
            if (this.util.isFormatEqual(target, trg))
            {
                String name = this.util.simpleLiteralTostring(transformation.getSource().getName());
                String type = this.util.simpleLiteralTostring(transformation.getSource().getType());
                String encoding = this.util.simpleLiteralTostring(transformation.getSource().getEncoding());
                Format format = new Format(name, type, encoding);
              
                sourceFormats.add(format);   
            }
        }    
        sourceFormats = this.util.getRidOfDuplicatesInVector(sourceFormats);
        Format[] dummy = new Format[sourceFormats.size()];
        return sourceFormats.toArray(dummy);
    }
    
}
