package test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.transformation.Transformation;
import de.mpg.escidoc.services.transformation.TransformationBean;
import de.mpg.escidoc.services.transformation.transformations.otherFormats.ris.Pair;
import de.mpg.escidoc.services.transformation.transformations.otherFormats.ris.RISImport;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

public class RISImportTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		RISImport imp = new RISImport();
		
    	Transformation transformation = new TransformationBean();
    	Format inputFormat = new Format("RIS", "text/plain", "UTF-8");
    	Format outputFormat = new Format("eSciDoc", "application/xml", "UTF-8");
    	
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
	
	

}
