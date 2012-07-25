package test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mpg.escidoc.services.common.util.PropertyReader;
import de.mpg.escidoc.services.transformationLight.Transformation;
import de.mpg.escidoc.services.transformationLight.TransformationBean;
import de.mpg.escidoc.services.transformationLight.valueObjects.Format;

/**
 * This test class test common transformation functionalities like the explain methods.
 * @author kleinfe1
 *
 */
public class CommonTest {
	
    public static TransformationBean trans;
    private final Logger logger = Logger.getLogger(CommonTest.class);   
    public static final Format SOURCE_FORMAT = new Format("test-src", "text/plain", "UTF-8");
    public static final Format TARGET_FORMAT = new Format("test-trg", "text/plain", "UTF-8");

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        Transformation transformation = new TransformationBean(true);
        byte[] result = transformation.transform(args[0].getBytes("UTF-8"), SOURCE_FORMAT, TARGET_FORMAT, "escidoc");
        System.out.println(new String(result, "UTF-8"));
    }
    
    
    /**
     * This method returns the path to project file for transformation search
     * due to the property: "transformation.initializer.projectPath".
     * @return path where to search for the transformation modules
     * @throws URISyntaxException 
     * @throws IOException 
     */
    @Test
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
	                	System.out.println("Search for transformations in path: " + path + filename);
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
	                if (filename.contains(project))
	                {	
	                	System.out.println("Search for transformations in path: " + path + filename);
	                	if (!path.endsWith("\\"))
	                	{
	                		return path +"\\"+ filename;
	                	}
	                	else
	                	{
	                		return path + filename;
	                	}   
	                }
            }
        }

        System.out.println("Search for transformations in path: " + path);
        
        return path;
    }
    
    /**
     * Initializes the {@link TransformationBean}.
     */
    @BeforeClass
    public static void initTransformation()
    {
        trans = new TransformationBean(true);
    }  

    @Test
    public void explainTest() throws Exception
    {
        try
        {
            this.logger.info("ALL SOURCE FORMATS FOR ALL TRANSFORMATIONS");
            Format[] formats = this.trans.getSourceFormats();
            for (int i = 0; i< formats.length; i++)
            {
                this.logger.info(formats[i].getName() + " (" + formats[i].getType() + ")");
            }
            
            this.logger.info("-----OK");
            
            this.logger.info("ALL TARGET FORMATS FOR escidoc-publication-item:");
            formats = this.trans.getTargetFormats(new Format("eSciDoc-publication-item", "application/xml", "*"));
            for (int i = 0; i< formats.length; i++)
            {
                this.logger.info(formats[i].getName() + " (" + formats[i].getType() + ")");
            }
            this.logger.info("-----OK");
            
            this.logger.info("ALL SOURCE FORMATS FOR escidoc-publication-item:");
            formats = this.trans.getSourceFormats(
                    new Format ("eSciDoc-publication-item", "application/xml", "UTF-8"));
            for (int i = 0; i< formats.length; i++)
            {
                this.logger.info(formats[i].getName() + " (" + formats[i].getType() + ")");
            }        
            this.logger.info("-----OK");
        }
        catch (Exception e)
        {
            this.logger.error("An error occurred during transformation", e);
            throw new Exception(e);
        }
        
        this.logger.info("--- Explain tests succeeded ---");
    }
    
}
