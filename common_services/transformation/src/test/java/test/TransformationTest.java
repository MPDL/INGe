package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.transformation.TransformationBean;
import de.mpg.escidoc.services.transformation.Util;
import de.mpg.escidoc.services.transformation.valueObjects.Format;


public class TransformationTest
{
    TransformationBean trans = new TransformationBean();
    Util util = new Util();
    private final Logger logger = Logger.getLogger(TransformationTest.class);
    
    @Test
    public void test() throws Exception
    {
        
        try{

            this.logger.debug("Check sources xml:");
            this.trans.getSourceFormatsAsXml();
            this.logger.debug("-----OK");
            
            this.logger.debug("Check target xml for escidoc item:");
            this.trans.getTargetFormatsAsXml("eSciDoc", "application/xml", "*");
            this.logger.debug("-----OK");
            
            this.logger.debug("Check target xml for escidocToc item:");
            this.trans.getTargetFormatsAsXml("eSciDocToc", "application/xml", "*");
            this.logger.debug("-----OK");
            
            this.logger.debug("Check source xml for escidoc item:");
            Format[] tmp = this.trans.getSourceFormats(new Format ("eSciDoc", "application/xml", "UTF-8"));
            this.util.createFormatsXml(tmp);
            this.logger.debug("-----OK");
            
            this.logger.debug("Check Transformation");
            ClassLoader cl = this.getClass().getClassLoader();
            InputStream in = cl.getResourceAsStream("test/resources/testFiles/escidocItem.xml");
            
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;

            while ((line = bufferedReader.readLine()) != null) 
            {
                stringBuilder.append(line + "\n");
            }
            new String(this.trans.transform( stringBuilder.toString().getBytes("UTF-8"), 
                    "escidoc", "application/xml", "UTF-8", "endnote", "text/plain", "UTF-8", "escidoc"));
            this.logger.debug("-----OK");
        }
        catch (Exception e)
        {
            this.logger.error("An error occurred during transformation",e);
            throw new Exception(e);
        }
        
        this.logger.debug("Transformation Tests succeeded");
    }
    
    @Test
    @Ignore
    public void test2() throws Exception
    {
        ItemHandler ih = ServiceLocator.getItemHandler();
        String itemXml = ih.retrieve("escidoc:139621");
        
        Format escidocFormat = new Format("escidoc", "application/xml", "UTF-8");
        Format metsFormat = new Format("virr-mets", "application/xml", "UTF-8");
        
        byte[] result = trans.transform(itemXml.getBytes(), escidocFormat, metsFormat, "escidoc");
        
        
        File f = new File("src/main/resources/dfg_mets.xml");
        OutputStream fileStream = new FileOutputStream(f);
        fileStream.write(result);
        fileStream.flush();
        fileStream.close();
        
        logger.info(new String(result));
        
        
        
    }
}
