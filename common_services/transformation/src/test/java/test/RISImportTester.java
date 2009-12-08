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
import de.mpg.escidoc.services.transformation.transformations.otherFormats.mab.MABImport;
import de.mpg.escidoc.services.transformation.transformations.otherFormats.ris.Pair;
import de.mpg.escidoc.services.transformation.transformations.otherFormats.ris.RISImport;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

public class RISImportTester {

    private final Logger logger = Logger.getLogger(RISImportTester.class);
    private Util util = new Util();
    RISImport imp = new RISImport();
    
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		RISImport imp = new RISImport();
		
    	Transformation transformation = new TransformationBean();
    	Format inputFormat = new Format("RIS", "text/plain", "UTF-8");
    	Format outputFormat = new Format("eSciDoc-publication-item-list", "application/xml", "UTF-8");
    	
    	InputStream inputStream = ResourceUtil.getResourceAsStream("/home/kurt/Dokumente/ris-testdatensaetze.txt");
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	byte[] buffer = new byte[2048];
    	int read;
    	while ((read = inputStream.read(buffer)) != -1)
    	{
    		baos.write(buffer, 0, read);
    	}
    	byte[] result = transformation.transform(baos.toByteArray(), inputFormat, outputFormat, "escidoc");
    	
    	
    	
    	
    	System.out.print(new String(result,"UTF-8"));
	}
	
    @Test
    public void risListTransformation() throws Exception
    {
        this.logger.info("Transform RIS list to xml format");
        
        String result = imp.transformRIS2XML(this.util.getResourceAsString("testFiles/ris/RIS.txt"));
        this.logger.info("transformation successful");
        this.logger.info(result);
    }

}
