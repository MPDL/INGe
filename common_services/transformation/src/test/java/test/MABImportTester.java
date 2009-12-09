package test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.transformation.Transformation;
import de.mpg.escidoc.services.transformation.TransformationBean;
import de.mpg.escidoc.services.transformation.Util;
import de.mpg.escidoc.services.transformation.transformations.otherFormats.mab.MABTransformation;
import de.mpg.escidoc.services.transformation.transformations.otherFormats.mab.Pair;
import de.mpg.escidoc.services.transformation.transformations.otherFormats.mab.MABImport;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

public class MABImportTester {
    
    private final Logger logger = Logger.getLogger(MABImportTester.class);
    private Util util = new Util();
    MABTransformation mapTransformer = new MABTransformation();

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		MABImport imp = new MABImport();
		
		Transformation transformation = new TransformationBean();
    	Format inputFormat = new Format("MAB", "text/plain", "utf-8");
    	Format outputFormat = new Format("eSciDoc-publication-item-list", "application/xml", "utf-8");
    	
    	//InputStream inputStream = ResourceUtil.getResourceAsStream("/home/kurt/Dokumente/metadateningest2009-08-13_utf8.txt");
    	InputStream inputStream = ResourceUtil.getResourceAsStream("/home/kurt/Dokumente/mab-metadata-mpi-eva.txt");
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	byte[] buffer = new byte[2048];
    	int read;
    	while ((read = inputStream.read(buffer)) != -1)
    	{
    		baos.write(buffer, 0, read);
    	}
    	byte[] result = transformation.transform(baos.toByteArray(), inputFormat, outputFormat, "escidoc");
    	
    	String out = imp.transformMAB2XML(new String(baos.toByteArray(),"utf-8"));
    	
    	
    	System.out.print(new String(out.getBytes(),"utf-8"));
    	System.out.print("\n\n\n\n\n\n********************");
    	System.out.print(new String(result,"UTF-8"));
    	
	}
	
	    @Test
	    public void mabListTransformation() throws Exception
	    {
	        this.logger.info("Transform MAB list to xml format");
	        
	        Format inputFormat = new Format("MAB", "text/plain", "utf-8");
	        Format outputFormat = new Format("eSciDoc-publication-item-list", "application/xml", "utf-8");
	        byte[] result = mapTransformer.transform(this.util.getResourceAsString("testFiles/mab/mab.txt").getBytes("UTF-8"), 
	                inputFormat, outputFormat, "escidoc");
	        this.logger.info("transformation successful");
	        this.logger.info(new String(result,"UTF-8"));
	    }

}
