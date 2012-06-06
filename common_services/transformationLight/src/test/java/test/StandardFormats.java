package test;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.transformation.TransformationBean;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

/**
 * This test class tests all transformations defined as "standard" formats.
 * @author kleinfe1
 *
 */
public class StandardFormats {

    public static TransformationBean trans;
    private final Logger logger = Logger.getLogger(StandardFormats.class);
    
    /**
     * Initializes the {@link TransformationBean}.
     */
    @BeforeClass
    public static void initTransformation()
    {
        trans = new TransformationBean(true);
    }  
    
    /* 
     * test TEI2 to eSciDoc item transformation 
     * */
    @Test
    @Ignore
    public void tei2escidoc() throws Exception
    {
        this.logger.info("---Transformation TEI to escidoc format ---");
        Format teiFormat = new Format("peer_tei", "application/xml", "UTF-8");
        Format escidocFormat = new Format("eSciDoc-publication-item", "application/xml", "UTF-8");
        Format escidocComponentFormat = new Format("eSciDoc-publication-component", "application/xml", "UTF-8");

        byte[] result = this.trans.transform(ResourceUtil.getResourceAsString("testFiles/tei/tei1.tei")
                .getBytes("UTF-8"), teiFormat, escidocFormat, "escidoc");

//        result = this.trans.transform(this.util.getResourceAsString("testFiles/tei/Springer-351-S2.tei")
//                .getBytes("UTF-8"), teiFormat, escidocComponentFormat, "escidoc");    
        System.out.println(new String(result, "UTF-8")); 
    }
    
    @Test
    public void mods2oaidcTest () throws Exception
    {
        this.logger.info("---Transformation MODS to oai_dc format ---");
        
        Format mods = new Format("mods", "application/xml", "UTF-8");
        Format oai = new Format("oai_dc", "application/xml", "UTF-8");
        
        byte[] result;
        result = this.trans.transform(ResourceUtil.getResourceAsString("testFiles/mods/mods2.xml").getBytes("UTF-8"), mods, oai, "escidoc");
        logger.debug("Result: "+ new String (result, "UTF-8"));
        
//        String referenceItem = this.normalizeString(this.util.getResourceAsString("testFiles/testResults/modsAsOaidc.xml"));
//        String actualItem = this.normalizeString(new String(result, "UTF-8"));        
//        Assert.assertTrue(referenceItem.equals(actualItem));
//        this.logger.info("Transformation to oai_dc successful.");      
    }
    
    @Test
    public void mods2marcTest () throws Exception
    {
        this.logger.info("---Transformation MODS to MARC format ---");
        
        Format mods = new Format("mods", "application/xml", "UTF-8");
        Format marc = new Format("marc21", "application/xml", "UTF-8");
        
        byte[] result;
        result = this.trans.transform(ResourceUtil.getResourceAsString("testFiles/mods/mods.xml").getBytes("UTF-8"), mods, marc, "escidoc");
        this.logger.debug(new String(result, "UTF-8"));
    }
    
    @Test
    
    //TODO: check, currently not needed
    public void mods2escidocTest() throws Exception
    {
        this.logger.info("---Transformation MODS to escidoc format ---");
        
        Format mods = new Format("mods", "application/xml", "UTF-8");
        Format escidoc = new Format("escidoc-publication-item-list", "application/xml", "UTF-8");
        
        byte[] result;
        result = this.trans.transform(ResourceUtil.getResourceAsString("testFiles/mods/mods.xml").getBytes("UTF-8"), mods, escidoc, "escidoc");

        System.out.println(new String(result, "UTF-8")); 
    }
    
    
   @Test
    public void escidoc2oaidcTest () throws Exception
    {
        this.logger.info("---Transformation escidoc to oai_dc format ---");
        
        Format escidoc = new Format("escidoc-publication-item", "application/xml", "UTF-8");
        Format oai = new Format("oai_dc", "application/xml", "UTF-8");
       
        byte[] result;
        result = this.trans.transform(ResourceUtil.getResourceAsString("testFiles/escidoc/escidocItem_newFormat.xml").getBytes("UTF-8"), escidoc, oai, "escidoc");
        logger.debug("Result: "+ new String (result, "UTF-8"));
        
//        String referenceItem = this.normalizeString(this.util.getResourceAsString("testFiles/testResults/escidocAsOaidc.xml"));
//        String actualItem = this.normalizeString(new String(result, "UTF-8"));        
//        Assert.assertTrue(referenceItem.equals(actualItem));
//        this.logger.info("Transformation to oai_dc successful.");
    }
   
   /* 
    * test ZfN TEI to eSciDoc item transformation 
    * Will not work as junit test due to xslt path property
    * */
   @Test
   public void zfn2escidoc() throws Exception
   {
       System.out.println("---Transformation ZfN to escidoc format ---");
       Format teiFormat = new Format("zfn_tei", "application/xml", "UTF-8");
       Format escidocFormat = new Format("eSciDoc-publication-item", "application/xml", "UTF-8");

       byte[] result = this.trans.transform(ResourceUtil.getResourceAsString("testFiles/zfn/ZNC-1988-43c-0979_b.header.tei.xml")
     .getBytes("UTF-8"), teiFormat, escidocFormat, "escidoc");

       System.out.println(new String(result, "UTF-8"));    
   }
	
}
