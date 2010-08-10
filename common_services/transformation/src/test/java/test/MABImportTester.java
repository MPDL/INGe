package test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.transformation.Transformation;
import de.mpg.escidoc.services.transformation.TransformationBean;
import de.mpg.escidoc.services.transformation.Util;
import de.mpg.escidoc.services.transformation.transformations.otherFormats.mab.MABImport;
import de.mpg.escidoc.services.transformation.transformations.otherFormats.mab.MABTransformation;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

public class MABImportTester {
    
    private static final Logger logger = Logger.getLogger(MABImportTester.class);
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
    	
    	
    	logger.debug(new String(out.getBytes(),"utf-8"));
    	logger.debug("\n\n\n\n\n\n********************");
    	logger.debug(new String(result,"UTF-8"));
    	
	}
	
	    @Test
	    public void mabListTransformation() throws Exception
	    {
	        this.logger.info("Transform MAB list to xml format");
	        
	        Format inputFormat = new Format("MAB", "text/plain", "utf-8");
	        Format outputFormat = new Format("eSciDoc-publication-item-list", "application/xml", "utf-8");
	        byte[] result = mapTransformer.transform(ResourceUtil.getResourceAsString("testFiles/mab/mab.txt").getBytes("UTF-8"), 
	                inputFormat, outputFormat, "escidoc");

	        XmlTransformingBean xmlTransforming = new XmlTransformingBean();
	        List <PubItemVO> itemVOList = xmlTransforming.transformToPubItemList(new String(result));
	        this.logger.info("PubItemVO List successfully created.");
	    }

}
