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
import de.mpg.escidoc.services.transformation.transformations.otherFormats.ris.RISImport;
import de.mpg.escidoc.services.transformation.transformations.otherFormats.ris.RISTransformation;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

public class RISImportTester {

    private final Logger logger = Logger.getLogger(RISImportTester.class);
    private Util util = new Util();
    RISTransformation risTransformer = new RISTransformation();
    
	/**
	 * @param args
	 */    
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		RISImport imp = new RISImport();
		
		Transformation transformation = new TransformationBean();
    	Format inputFormat = new Format("RIS", "text/plain", "UTF-8");
    	Format outputFormat = new Format("eSciDoc-publication-item-list", "application/xml", "UTF-8");
    	
    	InputStream inputStream = ResourceUtil.getResourceAsStream("/home/kurt/Dokumente/RIS_utf8.txt");
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	byte[] buffer = new byte[2048];
    	int read;
    	while ((read = inputStream.read(buffer)) != -1)
    	{
    		baos.write(buffer, 0, read);
    	}
    	byte[] result = transformation.transform(baos.toByteArray(), inputFormat, outputFormat, "escidoc");

    	String out = imp.transformRIS2XML(new String(baos.toByteArray(),"utf-8"));

	}
	
    @Test
    public void risListTransformation() throws Exception
    {
        this.logger.info("Transform RIS list to xml format");
        Format inputFormat = new Format("RIS", "text/plain", "utf-8");
        Format outputFormat = new Format("eSciDoc-publication-item-list", "application/xml", "utf-8");
        byte[] result = risTransformer.transform(ResourceUtil.getResourceAsString("testFiles/ris/RIS.txt").getBytes("UTF-8"), inputFormat, outputFormat, "escidoc");

        XmlTransformingBean xmlTransforming = new XmlTransformingBean();
        List <PubItemVO> itemVOList = xmlTransforming.transformToPubItemList(new String(result));
        this.logger.info("PubItemVO List successfully created.");
    }

}
