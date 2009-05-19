package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.citationmanager.utils.ResourceUtil;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.transformation.TransformationBean;
import de.mpg.escidoc.services.transformation.Util;
import de.mpg.escidoc.services.transformation.valueObjects.Format;


public class TransformationTest
{
    private TransformationBean trans;
    Util util = new Util();
    private final Logger logger = Logger.getLogger(TransformationTest.class);
    
    /**
     * Initializes the {@link TransformationBean}.
     */
    @Before
    public void initTransformation()
    {
        trans = new TransformationBean();
    }

    
    @Test
    @Ignore
    public void test() throws Exception
    {
        try{

            this.logger.debug("Check sources xml:");
            this.logger.debug(this.trans.getSourceFormatsAsXml());
            this.logger.debug("-----OK");
            
            this.logger.debug("Check target xml for escidoc item:");
            this.logger.debug(this.trans.getTargetFormatsAsXml("eSciDoc-publication-item", "application/xml", "*"));
            this.logger.debug("-----OK");
            
            this.logger.debug("Check target xml for escidocToc item:");
            this.trans.getTargetFormatsAsXml("eSciDocToc", "application/xml", "*");
            this.logger.debug("-----OK");
            
            this.logger.debug("Check source xml for escidoc item:");
            Format[] tmp = this.trans.getSourceFormats(new Format ("eSciDoc-publication-item", "application/xml", "UTF-8"));
            this.logger.debug(this.util.createFormatsXml(tmp));            this.logger.debug("-----OK");
            
            this.logger.debug("Check Transformation");
            ClassLoader cl = this.getClass().getClassLoader();
            InputStream in = cl.getResourceAsStream("testFiles/escidocItem.xml");
            
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;

            while ((line = bufferedReader.readLine()) != null) 
            {
                stringBuilder.append(line + "\n");
            }
            new String(this.trans.transform( stringBuilder.toString().getBytes("UTF-8"), 
                    "eSciDoc-publication-item-list", "application/xml", "UTF-8", "endnote", "text/plain", "UTF-8", "escidoc"));
            this.logger.debug("-----OK");
        }
        catch (Exception e)
        {
            this.logger.error("An error occurred during transformation",e);
            throw new Exception(e);
        }
        
        this.logger.info("Transformation Tests succeeded");
    }
    
    //Identifier not valid! (dev-coreservice)
    public void test2() throws Exception
    {
        ItemHandler ih = ServiceLocator.getItemHandler();
        String itemXml = ih.retrieve("escidoc:139621");
        
        Format escidocFormat = new Format("escidoc-virr-item", "application/xml", "UTF-8");
        Format metsFormat = new Format("virr-mets", "application/xml", "UTF-8");
        
        byte[] result = trans.transform(itemXml.getBytes(), escidocFormat, metsFormat, "escidoc");
        
        
        File f = new File("dfg_mets.xml");
        OutputStream fileStream = new FileOutputStream(f);
        fileStream.write(result);
        fileStream.flush();
        fileStream.close();
        
        logger.info(new String(result));
    }

    /* 
     * test TEI2 to eSciDoc item transformation 
     * */
    @Test
    public void tei2escidoc() throws Exception
    {    	
    	Format teiFormat = new Format("peer_tei", "application/xml", "UTF-8");
    	Format escidocFormat = new Format("eSciDoc-publication-item", "application/xml", "UTF-8");
    	
    	byte[] result = this.trans.transform(ResourceUtil.getResourceAsString("testFiles/tei/Elsevier1.tei").getBytes(), teiFormat, escidocFormat, "escidoc");   	
    	this.logger.info(new String(result));
    	
    	this.logger.info("Get all target formats for peer_tei: ");
    	this.logger.info(this.trans.getTargetFormatsAsXml("peer_tei", "application/xml", "UTF-8"));   	
    }
	
}
