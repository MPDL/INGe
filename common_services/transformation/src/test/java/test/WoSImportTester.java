package test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

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
import de.mpg.escidoc.services.transformation.transformations.otherFormats.wos.WoSImport;
import de.mpg.escidoc.services.transformation.transformations.otherFormats.wos.WoSTransformation;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

public class WoSImportTester {

	private static final Logger logger = Logger.getLogger(WoSImportTester.class);
    WoSTransformation wosTransformer = new WoSTransformation();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		WoSImport imp = new WoSImport();
		
    	Transformation transformation = new TransformationBean();
    	Format inputFormat = new Format("WoS", "text/plain", "UTF-8");
    	Format outputFormat = new Format("eSciDoc-publication-item-list", "application/xml", "UTF-8");
    	
    	InputStream inputStream = ResourceUtil.getResourceAsStream("/home/kurt/Dokumente/wok-isi-test.txt");
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	byte[] buffer = new byte[2048];
    	int read;
    	while ((read = inputStream.read(buffer)) != -1)
    	{
    		baos.write(buffer, 0, read);
    	}
    	//String str = imp.readFile();
    	String out = imp.transformWoS2XML(new String(baos.toByteArray(),"UTF-8"));
    	//String out = imp.transformWoS2XML(str);
    	byte[] result = transformation.transform(baos.toByteArray(), inputFormat, outputFormat, "escidoc");

    	logger.debug(new String(result,"UTF-8"));
	}
	

    @Test
    public void wosList1Transformation() throws Exception
    {
        this.logger.info("Transform WoS list 1 to xml format");
        Format inputFormat = new Format("WoS", "text/plain", "utf-8");
        Format outputFormat = new Format("eSciDoc-publication-item-list", "application/xml", "utf-8");
        byte[] result = wosTransformer.transform(ResourceUtil.getResourceAsString("testFiles/wos/WoS.txt").getBytes("UTF-8"), inputFormat, outputFormat, "escidoc");
        
        XmlTransformingBean xmlTransforming = new XmlTransformingBean();
        List <PubItemVO> itemVOList = xmlTransforming.transformToPubItemList(new String(result));
        
        assertEquals(1, itemVOList.size());
        
        this.logger.info("PubItemVO List successfully created.");
    }

    @Test
    public void wosList2Transformation() throws Exception
    {
        this.logger.info("Transform WoS list 2 to xml format");
        Format inputFormat = new Format("WoS", "text/plain", "utf-8");
        Format outputFormat = new Format("eSciDoc-publication-item-list", "application/xml", "utf-8");
        byte[] result = wosTransformer.transform(ResourceUtil.getResourceAsString("testFiles/wos/WoS_2012.txt").getBytes("UTF-8"), inputFormat, outputFormat, "escidoc");
        
        XmlTransformingBean xmlTransforming = new XmlTransformingBean();
        List <PubItemVO> itemVOList = xmlTransforming.transformToPubItemList(new String(result));
        
        assertEquals(5, itemVOList.size());
        
        this.logger.info("PubItemVO List successfully created.");
    }


}
