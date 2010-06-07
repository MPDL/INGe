package test;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.transformation.Transformation;
import de.mpg.escidoc.services.transformation.TransformationBean;
import de.mpg.escidoc.services.transformation.Util;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

public class EndNoteImportTest 
{
	
	private final Logger logger = Logger.getLogger(EndNoteImportTest.class);
	
	Transformation transformation;
	private Util util = new Util();
	
    private Format inputFormatICE = new Format("EndNote-ICE", "text/plain", "UTF-8");
    private Format inputFormat = new Format("EndNote", "text/plain", "UTF-8");
    private Format outputFormat = new Format("escidoc-publication-item-list", "application/xml", "UTF-8");

    /**
     * Initializes the {@link TransformationBean}.
     */
    @Before
    public void initTransformation()
    {
        this.transformation = new TransformationBean(true);
    } 
	
	@Test
//	@Ignore
	public void CheckEndNoteImport() throws Exception
	{  	
    	InputStream inputStream = ResourceUtil.getResourceAsStream("testFiles/endnote/publikationsliste_2008_endnote.txt");
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	byte[] buffer = new byte[2048];
    	int read;
    	while ((read = inputStream.read(buffer)) != -1)
    	{
    		baos.write(buffer, 0, read);
    	}
    	byte[] result = transformation.transform(baos.toByteArray(), inputFormat, outputFormat, "escidoc");
    	
    	logger.debug(new String(result,"UTF-8"));
	}
	
	@Test
	@Ignore
	public void checkEndnoteICEListTransformation() throws Exception
	{
	    this.logger.info("Transform EndNote ICE list to escidoc list");
	    
       	byte[] result = this.transformation.transform(this.util.getResourceAsString("testFiles/endnote/publikationsliste_2008_endnote2.txt").getBytes(), 
       	        this.inputFormatICE, this.outputFormat, "escidoc");
       	XmlTransformingBean xmlTransforming = new XmlTransformingBean();
       	List<PubItemVO> itemList = (List<PubItemVO>)xmlTransforming.transformToPubItemList(new String(result, "UTF-8"));
       	Assert.assertNotNull(itemList);
	}
	
	@Test
	public void checkEndnoteListTransformation() throws Exception
	{
		this.logger.info("Transform EndNote list to escidoc list. New implemetation");
		
		byte[] result = this.transformation.transform(this.util.getResourceAsString("testFiles/endnote/EndNote_Import_revised_implementation.txt").getBytes(), 
				this.inputFormat, this.outputFormat, "escidoc");
		
//		FileOutputStream fos = new FileOutputStream("target/endnote_transformed.xml");
//    	fos.write(result);
//    	fos.close();
		
		XmlTransformingBean xmlTransforming = new XmlTransformingBean();
		List<PubItemVO> itemList = (List<PubItemVO>)xmlTransforming.transformToPubItemList(new String(result, "UTF-8"));
		Assert.assertNotNull(itemList);
		
//		logger.info(new String(result,"UTF-8"));
	}

}
