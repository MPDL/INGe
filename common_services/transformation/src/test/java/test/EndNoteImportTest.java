package test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.transformation.Transformation;
import de.mpg.escidoc.services.transformation.TransformationBean;
import de.mpg.escidoc.services.transformation.Util;
import de.mpg.escidoc.services.transformation.transformations.otherFormats.endnote.EndNoteImport;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

public class EndNoteImportTest 
{
	
	private final Logger logger = Logger.getLogger(EndNoteImportTest.class);
	
	Transformation transformation = new TransformationBean();
	EndNoteImport imp = new EndNoteImport();
	private Util util = new Util();

	
	@Test
	@Ignore
	public void CheckEndNoteImport() throws Exception
	{
		// TODO Auto-generated method stub
		
    	Format inputFormat = new Format("EndNote", "text/plain", "UTF-8");
    	Format outputFormat = new Format("eSciDoc-publication-item-list", "application/xml", "UTF-8");
    	
    	InputStream inputStream = ResourceUtil.getResourceAsStream("./src/test/resources/testFiles/endnote/publikationsliste_2008_endnote.txt");
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	byte[] buffer = new byte[2048];
    	int read;
    	while ((read = inputStream.read(buffer)) != -1)
    	{
    		baos.write(buffer, 0, read);
    	}
    	byte[] result = transformation.transform(baos.toByteArray(), inputFormat, outputFormat, "escidoc");
    	
    	logger.info(new String(result,"UTF-8"));
	}
	
	@Test
	public void checkEndnoteListTransformation() throws Exception
	{
	    this.logger.info("Transform EndNote list to escidoc format");
	    
       	String result = imp.transformEndNote2XML(this.util.getResourceAsString("testFiles/endnote/publikationsliste_2008_endnote.txt"));

	}

}
