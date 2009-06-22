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

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.Vector;

import javax.ejb.Init;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.RemoteBinding;
import org.scannotation.AnnotationDB;
import org.scannotation.ClasspathUrlFinder;

import de.mpg.escidoc.services.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

/**
 * Implementation of the Transformation Service.
 *
 * @author Friederike Kleinfercher (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */

public class TransformationBean implements Transformation
{
    
    private final Logger logger = Logger.getLogger(TransformationBean.class);    
    private Util util;
    private Vector<Class> transformationClasses = new Vector<Class>();
   
    
    /**
     * Public constructor.
     */
    public TransformationBean()
    {
        this.util = new Util();
        this.initializeTransformationModules();
    }
    
    /**
     * Initializes the Transformation service
     */
    @Init
    public void initialize()
    {
        this.util = new Util();
        this.initializeTransformationModules();
    }
    
    /**
     * {@inheritDoc}
     */
    public Format[] getSourceFormats() throws RuntimeException
    {
        Format[] allSourceFormats = null;
        String thisMethodName = "getSourceFormats";
        allSourceFormats = this.callMethodOnTransformationModules(thisMethodName, null);
        
        return allSourceFormats;
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
        Format[] allTargetFormats = null;
        String thisMethodName = "getTargetFormats";
        
        //Normalize mimetype to avoid that e.g. application/xml and text/xml need two different transformations
        src.setType(this.util.normalizeMimeType(src.getType()));
        allTargetFormats = this.callMethodOnTransformationModules(thisMethodName, src);
        
        return allTargetFormats;
    }
    
    /**
     * {@inheritDoc}
     */
    public Format[] getSourceFormats(Format trg) throws RuntimeException
    {
        //Normalize mimetype to avoid that e.g. application/xml and text/xml need two different transformations
        trg.setType(this.util.normalizeMimeType(trg.getType()));
        
        Format[] allSourceFormats = null;
        String thisMethodName = "getSourceFormats";
        allSourceFormats = this.callMethodOnTransformationModules(thisMethodName, trg);
        
        return allSourceFormats;
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
        Class transformationClass = this.getTransformationClassForTransformation(srcFormat, trgFormat);
        byte[] result = null;
        String methodName ="transform";
        
        if (transformationClass == null)
        {
            this.logger.warn("Transformation not supported: \n" + srcFormat.getName() + ", " + srcFormat.getType() 
                    + ", " + srcFormat.getEncoding() + "\n" + trgFormat.getName() + ", " + trgFormat.getType() 
                    + ", " + trgFormat.getEncoding());
            throw new TransformationNotSupportedException();
        }
        else 
        {
            try{
                //Instanciate the class
                ClassLoader cl = this.getClass().getClassLoader();
                transformationClass = cl.loadClass(transformationClass.getName());
    
                //Set methods parameters
                Class[] parameterTypes = new Class[]{ byte[].class, Format.class, Format.class, String.class };
                
                //Call the method
                Method method = transformationClass.getMethod(methodName, parameterTypes);

                //Execute the method
                result = (byte[])method.invoke(transformationClass.newInstance(), src, srcFormat, trgFormat, service);
            }
            catch(Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        
        return result;
    }
    
    private void initializeTransformationModules() throws RuntimeException
    {
        this.logger.debug("Classes which implement the transformation interface:");
        URL classPath = null;
        Set entities;
        Vector entetiesV = new Vector();
        ClassLoader cl = this.getClass().getClassLoader();
        Class transformationClass;
        ClasspathUrlFinder classPathFinder = new ClasspathUrlFinder();
        
        try
        {
            //Location of classes to search
            classPath = classPathFinder.findClassBase(this.getClass());
            //Small hack due to problems with blanks in URLs
            String classPathStr = classPath.toExternalForm();
            classPath = new URL (java.net.URLDecoder.decode(classPathStr));          
            
            AnnotationDB anDB = new AnnotationDB();
            anDB.scanArchives(classPath);
            anDB.setScanClassAnnotations(true);

            entities = anDB.getAnnotationIndex().get(TransformationModule.class.getName());           
            entetiesV.addAll(entities);
                
            for (int i = 0; i< entetiesV.size(); i++)
            {
                this.logger.debug(entetiesV.get(i));
                transformationClass = cl.loadClass(entetiesV.get(i).toString());
                this.transformationClasses.add(transformationClass);
            }       

        }
        catch (MalformedURLException e)
        {
            this.logger.error("Invalid classpath: " + classPath.toString(), e);
            throw new RuntimeException(e);
        }
        catch (Exception e)
        {
            this.logger.error("An error occurred during the allocation of transformation classes.", e);
            throw new RuntimeException(e);
        }
    }
    
    private Format[] callMethodOnTransformationModules (String methodName, Format param) throws RuntimeException
    {
        Vector<Format[]> allFormats = new Vector<Format[]>();
        Format[] formats = null;
        
        for (int i=0; i<this.transformationClasses.size(); i++)
        {
            try
            {
                //Instanciate the class
                Class transformationClass = (Class) transformationClasses.get(i);
                ClassLoader cl = this.getClass().getClassLoader();
                transformationClass = cl.loadClass(transformationClass.getName());
  
                if (param == null)
                {
                    //Call the method
                    Method method = transformationClass.getMethod(methodName, null);

                    //Execute the method
                    formats = (Format[])method.invoke(transformationClass.newInstance(), null);
                }
                else
                {
                    //Set methods parameters
                    Class[] parameterTypes = new Class[]{ Format.class }; 
                    
                    //Call the method
                    Method method = transformationClass.getMethod(methodName, parameterTypes);
                    
                    //Execute the method
                    formats = (Format[])method.invoke(transformationClass.newInstance(), param);
                }
                allFormats.add(formats);
            } 
            catch(Exception e)
            {
                this.logger.error("An error occurred during the allocation of transformation classes.", e);
                throw new RuntimeException(e);
            }
        }
        
        return this.util.mergeFormats(allFormats);    
    }
    
    private Class getTransformationClassForTransformation (Format source, Format target) throws RuntimeException
    {
        Class transformationClass = null;
        Format[] targets;
        String methodName = "getTargetFormats";
        
        for (int i=0; i<this.transformationClasses.size(); i++)
        {
            try
            {
                //Instanciate the class
                transformationClass = (Class) transformationClasses.get(i);
                ClassLoader cl = this.getClass().getClassLoader();
                transformationClass = cl.loadClass(transformationClass.getName());
    
                //Set methods parameters
                Class[] parameterTypes = new Class[]{ Format.class };
                
                //Call the method
                Method method = transformationClass.getMethod(methodName, parameterTypes);
    
                //Execute the method
                targets = (Format[])method.invoke(transformationClass.newInstance(), source);
                if (this.util.containsFormat(targets, target))
                {
                    return transformationClass;
                }
            }
            catch (Exception e)
            {
                this.logger.error("An error occurred during the allocation of transformation classes.", e);
                throw new RuntimeException(e);
            }
        }
        
        return null;
    }

}
