package de.mpg.escidoc.services.transformationLight;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.scannotation.AnnotationDB;
import org.scannotation.ClasspathUrlFinder;

import de.mpg.escidoc.services.common.util.PropertyReader;
import de.mpg.escidoc.services.transformationLight.Transformation.TransformationModule;

public class TransformationInitializer
{
    private final Logger logger = Logger.getLogger(TransformationInitializer.class);   
    
    private Vector<Class> transformationClasses = new Vector<Class>();

    private boolean local = false;

    
    
    public void initializeTransformationModules(boolean local) throws RuntimeException, IOException, URISyntaxException
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
        this.logger.debug("Classes which implement the transformation interface:");
        URL classPath = null;
        Set entities;
        Vector entitiesV = new Vector();
        ClassLoader cl = this.getClass().getClassLoader();
        Class transformationClass;
        ClasspathUrlFinder classPathFinder = new ClasspathUrlFinder();
        String searchDir= "";

        try
        {              
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
            //Search in server directory
            else
            {
                searchDir = this.setModulesSearchPath();   
                File dir = new File(searchDir);
                String[] children = dir.list();
              
                if (children != null)
                {
                    for (int i=0; i<children.length; i++) 
                    {
                        String filename = children[i];
                        if (filename.contains("transformationLight") && filename.endsWith(".jar"))
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

        }
        catch (Exception e)
        {
            this.logger.error("An error occurred during the allocation of transformation classes.", e);
            throw new RuntimeException(e);
        }
    }
    
    
    /**
     * This method returns the path to project file for transformation search
     * due to the property: "transformation.initializer.projectPath".
     * @return path where to search for the transformation modules
     * @throws URISyntaxException 
     * @throws IOException 
     */
    private String setModulesSearchPath ()
    {
        String path = null;
        String project = null;
        
		try {
			path = PropertyReader.getProperty("transformation.initializer.path");
			project = PropertyReader.getProperty("transformation.initializer.project");
		} catch (Exception e) {
			//get jboss dir as default
			path = System.getProperty("jboss.server.home.dir")+"\\tmp\\deploy\\";
			if (path == null)
			{
				path = System.getProperty("tomcat.install.dir")+"\\webapps\\";
			}
		}
        
        File dir = new File(path);           
        String[] children = dir.list();
  
        if (children != null && project != null)
        {
            //Look for module search directory
            for (int i=0; i<children.length; i++) 
            {
                String filename = children[i];
            	//if jboss check the contents folder
            	if (path.contains("jboss"))
            	{
	                if (filename.contains(project)&&filename.contains("contents"))
	                {	
	                	this.logger.debug("Search for transformations in path: " + path + filename);
	                	if (!path.endsWith("/"))
	                	{
	                		return path +"\\"+ filename;
	                	}
	                	else
	                	{
	                		return path + filename;
	                	}                    
	                }
            	}
            	else
            	{
            		//For tomcat search directly in path, no subfolder (ear) necessary
            		this.logger.debug("Search for transformations in path: " + path);
            		return path; 
            	}
            }
        }

        this.logger.debug("Search for transformations in path: " + path);
        
        return path;
    }
    
    public Vector<Class> getTransformationClasses()
    {
        return transformationClasses;
    }

    public void setTransformationClasses(Vector<Class> transformationClasses)
    {
        this.transformationClasses = transformationClasses;
    }
}
