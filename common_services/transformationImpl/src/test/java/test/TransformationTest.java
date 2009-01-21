package test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.mpg.escidoc.services.transformationImpl.TransformationImplInterface;


public class TransformationTest
{
    TransformationImplInterface trans = new TransformationImplInterface();
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
        
        this.logger.info("Check Transformation");
        ClassLoader cl = this.getClass().getClassLoader();
        InputStream in = cl.getResourceAsStream("resources/testFiles/escidoc.xml");
        
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
                    "escidoc", "text/xml", "*", "endnote", "text/plain", "UTF-8", "escidoc")));
        }
        catch (Exception e)
        {
            this.logger.error("An error occurred during transformation",e);
        }
    }
}
