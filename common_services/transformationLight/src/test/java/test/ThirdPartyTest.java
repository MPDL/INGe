package test;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.transformation.TransformationBean;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

/**
 * This test class tests all transformations from non escidoc repositories.
 * @author kleinfe1
 *
 */
public class ThirdPartyTest {

    public static TransformationBean trans;
    private final Logger logger = Logger.getLogger(ThirdPartyTest.class);
    
    /**
     * Initializes the {@link TransformationBean}.
     */
    @BeforeClass
    public static void initTransformation()
    {
        trans = new TransformationBean(true);
    }
	
    //Transformation currently not in use
    public void bmcarticle2htmlTest() throws Exception
    {
        this.logger.info("---Transformation mnc article to html format ---");
        Format xml = new Format("bmc-fulltext-xml", "application/xml", "UTF-8");
        Format html = new Format("bmc-fulltext-html", "text/html", "UTF-8");
         
        byte[] result;
        result = this.trans.transform(ResourceUtil.getResourceAsString("testFiles/bmc_article.xml").getBytes("UTF-8"), xml, html, "escidoc");
        this.logger.debug(new String(result, "UTF-8"));
     }
    
    @Test
    public void bmc2escidocTest() throws Exception
    {
        this.logger.info("---Transformation BMC to escidoc format ---");
        Format bmc = new Format("bmc", "application/xml", "UTF-8");
        Format escidoc = new Format("escidoc-publication-item", "application/xml", "UTF-8");
        Format escidocComponent = new Format("escidoc-publication-component",  "application/xml", "UTF-8");        
        
        byte[] result;
        result = this.trans.transform(ResourceUtil.getResourceAsString("testFiles/externalSources/bmc.xml").getBytes("UTF-8"), bmc, escidoc, "escidoc");
        
        System.out.println(new String(result, "UTF-8"));  
    }
    
    @Test
    public void arxiv2escidocTest() throws Exception
    {
        this.logger.info("---Transformation arXiv to escidoc format ---");
        Format arxivItem = new Format("arxiv", "application/xml", "UTF-8");
        Format escidoc = new Format("escidoc-publication-item", "application/xml", "UTF-8");
        Format escidocComponent = new Format("escidoc-publication-component", "application/xml", "UTF-8");        
        
        byte[] result;
        result = this.trans.transform(ResourceUtil.getResourceAsString("testFiles/externalSources/arxivItem.xml").getBytes("UTF-8"), arxivItem, escidoc, "escidoc");
        
        System.out.println(new String(result, "UTF-8"));   
    }
    
    
    @Test
    public void spires2escidocTest() throws Exception
    {
        this.logger.info("---Transformation spires to escidoc format ---");
        Format spires = new Format("spires", "application/xml", "UTF-8");
        Format escidoc = new Format("escidoc-publication-item", "application/xml", "UTF-8");
        
        byte[] result;
        result = this.trans.transform(ResourceUtil.getResourceAsString("testFiles/externalSources/spires.xml").getBytes("UTF-8"), spires, escidoc, "escidoc");
        
        System.out.println(new String(result, "UTF-8"));   
    }
    
    @Test
    public void pmc2escidocTest() throws Exception
    {
        this.logger.info("---Transformation PMC to escidoc format ---");
        Format pmcItem = new Format("pmc", "application/xml", "UTF-8");
        Format escidoc = new Format("escidoc-publication-item", "application/xml", "UTF-8");
        Format escidocComponent = new Format("escidoc-publication-component", "application/xml", "UTF-8");
        
        byte[] result;
        result = this.trans.transform(ResourceUtil.getResourceAsString("testFiles/externalSources/pmc2.xml").getBytes("UTF-8"), pmcItem, escidoc, "escidoc");
        
        System.out.println(new String(result, "UTF-8")); 
        
    }
    
}
