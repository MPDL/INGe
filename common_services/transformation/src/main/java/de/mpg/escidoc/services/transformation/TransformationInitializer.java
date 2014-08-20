package de.mpg.escidoc.services.transformation;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.reflections.Reflections;

import de.mpg.escidoc.services.transformation.Transformation.TransformationModule;

public class TransformationInitializer
{
    private final Logger logger = Logger.getLogger(TransformationInitializer.class);   
    
    private Set<Class<?>> transformationClasses = new HashSet<Class<?>>();

    private boolean local = false;
    private boolean init = false;
    
    
    public void initializeTransformationModules(boolean local) throws RuntimeException
    {
        this.local = local;
        this.initializeTransformationModules();
    }
    
    /**
     * Searches for all classes which implement the transformation module.
     * @throws RuntimeException
     */
    public void initializeTransformationModules() throws RuntimeException
    {
    	 try
         {    
	        this.logger.debug("Classes which implement the transformation interface:");
	        
	       transformationClasses = new Reflections("de.mpg.escidoc.services.transformation").getTypesAnnotatedWith(TransformationModule.class);
        
        /*
        
        
        URL classPath = null;
        Set entities;
        Vector entitiesV = new Vector();

        String searchDir= "";
        
        Set<Class<?>> classes = new Reflections("de.mpg.escidoc.services.transformation").getTypesAnnotatedWith(TransformationModule.class);

                 
            //For local testing, only search in Transformation module
            if (this.local)
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
                entitiesV.addAll(entities);
            }
            //Search in jboss directory
            else
            {
                searchDir = this.setJbossSearchPath();   
                File dir = new File(searchDir);
                String[] children = dir.list();
              
                if (children != null)
                {
                    for (int i=0; i<children.length; i++) 
                    {
                        String filename = children[i];
                        if (filename.endsWith(".jar"))
                        {
                            URL url = new URL ("file:" + searchDir + "/" +filename);
                            AnnotationDB anDB = new AnnotationDB();
                            anDB.scanArchives(url);
                            anDB.setScanClassAnnotations(true);
                
                            entities = anDB.getAnnotationIndex().get(TransformationModule.class.getName());     
                            if (entities != null)
                            {
                                entitiesV.addAll(entities);
                            }
                        }                            
                    }
                }
            }
  
            for (int i = 0; i < entitiesV.size(); i++)
            {
                this.logger.debug(entitiesV.get(i));
                transformationClass = cl.loadClass(entitiesV.get(i).toString());
                this.transformationClasses.add(transformationClass);
            }   
            */    

        }
        catch (Exception e)
        {
            this.logger.error("An error occurred during the allocation of transformation classes.", e);
            throw new RuntimeException(e);
        }
    }
    
    
    /**
     * This method returns the path to the pubman_ear file
     * in the jboss directory.
     * @return path to ear file
     */
    private String setJbossSearchPath ()
    {
        String path = "";
        path = System.getProperty("jboss.server.home.dir");
        path += "/tmp/deploy/";
        
        File dir = new File(path);           
        String[] children = dir.list();
  
        if (children != null)
        {
            //Search children
            for (int i=0; i<children.length; i++) 
            {
                String filename = children[i];
                if ((filename.contains("pubman_ear")|| filename.contains("virr_ear")|| filename.contains("dataacquisition_ear")) && filename.contains("contents"))
                {
                    logger.debug("PubMan contents at: " + path + filename);
                    return path + filename;
                }
            }
        }

        logger.debug("PubMan contents at: " + path);
        
        return path;
    }
    
    public Set<Class<?>> getTransformationClasses()
    {
        return transformationClasses;
    }

    public void setTransformationClasses(Set<Class<?>> transformationClasses)
    {
        this.transformationClasses = transformationClasses;
    }
}
