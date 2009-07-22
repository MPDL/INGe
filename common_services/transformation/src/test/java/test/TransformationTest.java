package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.citationmanager.utils.ResourceUtil;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.transformation.Transformation;
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

    
    public void test() throws Exception
    {
        try{

            this.logger.debug("Check sources xml:");
            this.logger.debug(this.trans.getSourceFormatsAsXml());
            this.logger.debug("-----OK");
            
            this.logger.debug("Check target xml for escidoc item:");
            this.logger.debug(this.trans.getTargetFormatsAsXml("eSciDoc-publication-item", "application/xml", "*"));
            this.logger.debug("-----OK");
            
            this.logger.info("Check target xml for mods item:");
            this.logger.info(this.trans.getTargetFormatsAsXml("mods", "application/xml", "UTF-8"));
            this.logger.info("-----OK");
            
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
    
    public void tei2escidoc() throws Exception
    {    	
    	Format teiFormat = new Format("peer_tei", "application/xml", "UTF-8");
    	Format escidocFormat = new Format("eSciDoc-publication-item", "application/xml", "UTF-8");
    	Format escidocComponentFormat = new Format("eSciDoc-publication-component", "application/xml", "UTF-8");
    	
    	byte[] result = this.trans.transform(ResourceUtil.getResourceAsString("testFiles/tei/Wiley1.tei").getBytes(), teiFormat, escidocFormat, "escidoc");   	
    	this.logger.info(new String(result));
    	
        result = this.trans.transform(ResourceUtil.getResourceAsString("testFiles/tei/Springer-351-S2.tei").getBytes(), teiFormat, escidocComponentFormat, "escidoc");    
        this.logger.info(new String(result));
    	
    	this.logger.info("Get all target formats for peer_tei: ");
    	this.logger.info(this.trans.getTargetFormatsAsXml("peer_tei", "application/xml", "UTF-8"));   	
    }
    
     public void bmcArticleTest() throws Exception
     {
         Format xml = new Format("bmc-fulltext-xml", "application/xml", "UTF-8");
         Format html = new Format("bmc-fulltext-html", "text/html", "UTF-8");
         
         
         byte[] result;
         result = this.trans.transform(ResourceUtil.getResourceAsString("testFiles/bmc_article.xml").getBytes(), xml, html, "escidoc");
         this.logger.info(new String(result));     
     }
     
  
     
     public void bmcTest() throws Exception
     {
         Format bmc = new Format("bmc", "application/xml", "UTF-8");
         Format escidocComponent = new Format("escidoc-publication-component",  "application/xml", "UTF-8");        
         
         byte[] result;
         result = this.trans.transform(ResourceUtil.getResourceAsString("testFiles/bmc.xml").getBytes(), bmc, escidocComponent, "escidoc");
         this.logger.info(new String(result));     
     }
     
     public void arxivTest() throws Exception
     {
         Format arxivItem = new Format("arxiv", "application/xml", "UTF-8");
         Format escidoc = new Format("escidoc-publication-item", "application/xml", "UTF-8");
         Format escidocComponent = new Format("escidoc-publication-component", "application/xml", "UTF-8");        
         Format bibtex = new Format("bibtex", "text/plain", "*");
         
         byte[] result;
         result = this.trans.transform(ResourceUtil.getResourceAsString("testFiles/arxivItem.xml").getBytes(), arxivItem, escidoc, "escidoc");
         this.logger.info(new String(result));     
         
         result = this.trans.transform(result, escidoc, bibtex, "escidoc");
         this.logger.info(new String(result)); 
         
//         result = this.trans.transform(ResourceUtil.getResourceAsString("testFiles/arxivItem.xml").getBytes(), arxivItem, escidocComponent, "escidoc");
//         this.logger.info(new String(result));   
     }
     
     
     public void pmcTest() throws Exception
     {
         Format pmcItem = new Format("pmc", "application/xml", "UTF-8");
         Format escidoc = new Format("escidoc-publication-item", "application/xml", "UTF-8");
         Format escidocComponent = new Format("escidoc-publication-component", "application/xml", "UTF-8");
                      
         XmlTransformingBean xmlTransforming = new XmlTransformingBean();
         
         byte[] result;
         result = this.trans.transform(ResourceUtil.getResourceAsString("testFiles/pmc2.xml").getBytes("UTF-8"), pmcItem, escidoc, "escidoc");
         this.logger.info(new String(result, "UTF-8"));   
         
         PubItemVO itemVO = xmlTransforming.transformToPubItem(new String(result));
         System.out.println("itemVO successfully created. ");
         
         result = this.trans.transform(ResourceUtil.getResourceAsString("testFiles/pmc2.xml").getBytes(), pmcItem, escidocComponent, "escidoc");
         this.logger.info(new String(result));   
         
         FileVO componentVO = xmlTransforming.transformToFileVO(new String(result));
         System.out.println("FileVO successfully created. ");
         
     }
	
    @Test
     public void mods2oaidcTest () throws Exception
     {
         Format mods = new Format("mods", "application/xml", "UTF-8");
         Format oai = new Format("oai_dc", "application/xml", "UTF-8");
         
         byte[] result;
         result = this.trans.transform(ResourceUtil.getResourceAsString("testFiles/mods.xml").getBytes("UTF-8"), mods, oai, "escidoc");
         this.logger.info("OAI_DC:");
         this.logger.info(new String(result, "UTF-8"));           
     }
     
    @Test
     public void mods2marcTest () throws Exception
     {
         Format mods = new Format("mods", "application/xml", "UTF-8");
         Format marc = new Format("marc21", "text/plain", "*");
         
         byte[] result;
         result = this.trans.transform(ResourceUtil.getResourceAsString("testFiles/mods.xml").getBytes("UTF-8"), mods, marc, "escidoc");
         this.logger.info("MARC21:");
         this.logger.info(new String(result, "UTF-8"));           
     }
}
