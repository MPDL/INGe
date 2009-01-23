package test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.mpg.escidoc.services.transformation.TransformationBean;
import de.mpg.escidoc.services.transformation.Util;
import de.mpg.escidoc.services.transformation.valueObjects.Format;


public class TransformationTest
{
    TransformationBean trans = new TransformationBean();
    Util util = new Util();
    private final Logger logger = Logger.getLogger(TransformationTest.class);
    
    @Test
    public void test()
    {
        this.logger.info("Check sources xml:");
        this.logger.info(this.trans.getSourceFormatsAsXml());
        this.logger.info("Check target xml for escidoc item:");
        this.logger.info(this.trans.getTargetFormatsAsXml("eSciDoc", "application/xml", "*"));
        this.logger.info("Check target xml for escidocToc item:");
        this.logger.info(this.trans.getTargetFormatsAsXml("eSciDocToc", "application/xml", "*"));
        this.logger.info("Check source xml for escidoc item:");
        Format[] tmp = this.trans.getSourceFormats(new Format ("eSciDoc", "application/xml", "UTF-8"));
        this.logger.info(this.util.createFormatsXml(tmp));
        
        this.logger.info("Check Transformation");
        ClassLoader cl = this.getClass().getClassLoader();
        InputStream in = cl.getResourceAsStream("test/resources/testFiles/escidocItem.xml");
        
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;

        try
        {
            while ((line = bufferedReader.readLine()) != null) 
            {
                stringBuilder.append(line + "\n");
            }
            this.logger.info(new String(this.trans.transform( stringBuilder.toString().getBytes("UTF-8"), 
                    "escidoc", "application/xml", "UTF-8", "endnote", "text/plain", "UTF-8", "escidoc")));
        }
        catch (Exception e)
        {
            this.logger.error("An error occurred during transformation",e);
        }
    }
}
