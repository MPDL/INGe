package test;


import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.transformation.TransformationBean;
import de.mpg.escidoc.services.transformation.Util;
import de.mpg.escidoc.services.transformation.valueObjects.Format;


public class TransformationTest
{
    private TransformationBean trans;
    private Util util = new Util();
    private final Logger logger = Logger.getLogger(TransformationTest.class);
    
    /**
     * Initializes the {@link TransformationBean}.
     */
    @Before
    public void initTransformation()
    {
        this.trans = new TransformationBean(true);
    }

    @Test
    public void explainTest() throws Exception
    {
        try
        {
            this.logger.info("Get all source formats for all transformations:");
            this.logger.info(this.trans.getSourceFormatsAsXml());
            this.logger.info("-----OK");
            
            this.logger.info("Get all target formats for escidoc-publication-item format:");
            this.logger.info(this.trans.getTargetFormatsAsXml("eSciDoc-publication-item", "application/xml", "*"));
            this.logger.info("-----OK");
            
            this.logger.info("Get al source formats for escidoc-publication-item format:");
            Format[] tmp = this.trans.getSourceFormats(
                    new Format ("eSciDoc-publication-item", "application/xml", "UTF-8"));
            this.logger.info(this.util.createFormatsXml(tmp));            
            this.logger.info("-----OK");
        }
        catch (Exception e)
        {
            this.logger.error("An error occurred during transformation", e);
            throw new Exception(e);
        }
        
        this.logger.info("--- Explain tests succeeded ---");
    }

    /* 
     * test TEI2 to eSciDoc item transformation 
     * */
    
    public void tei2escidoc() throws Exception
    {
        this.logger.info("---Transformation TEI to escidoc format ---");
        Format teiFormat = new Format("peer_tei", "application/xml", "UTF-8");
        Format escidocFormat = new Format("eSciDoc-publication-item", "application/xml", "UTF-8");
        Format escidocComponentFormat = new Format("eSciDoc-publication-component", "application/xml", "UTF-8");

        byte[] result = this.trans.transform(this.util.getResourceAsString("testFiles/tei/tei1.tei")
                .getBytes("UTF-8"), teiFormat, escidocFormat, "escidoc");
        this.logger.info(new String(result, "UTF-8"));

        result = this.trans.transform(this.util.getResourceAsString("testFiles/tei/Springer-351-S2.tei")
                .getBytes("UTF-8"), teiFormat, escidocComponentFormat, "escidoc");    
        this.logger.info(new String(result, "UTF-8"));
   
        this.logger.info("Get all target formats for peer_tei: ");
        this.logger.info(this.trans.getTargetFormatsAsXml("peer_tei", "application/xml", "UTF-8"));   	
    }
    
    public void bmcArticleTest() throws Exception
    {
        Format xml = new Format("bmc-fulltext-xml", "application/xml", "UTF-8");
        Format html = new Format("bmc-fulltext-html", "text/html", "UTF-8");
         
        byte[] result;
        result = this.trans.transform(this.util.getResourceAsString("testFiles/bmc_article.xml").getBytes(), xml, html, "escidoc");
        this.logger.info(new String(result));
     }
     
  
    
     public void bmcTest() throws Exception
     {
         this.logger.info("---Transformation BMC to escidoc format ---");
         Format bmc = new Format("bmc", "application/xml", "UTF-8");
         Format escidocComponent = new Format("escidoc-publication-component",  "application/xml", "UTF-8");        
         
         byte[] result;
         result = this.trans.transform(this.util.getResourceAsString("testFiles/externalSources/bmc.xml").getBytes(), bmc, escidocComponent, "escidoc");
         this.logger.info(new String(result));
     }
     
     
     public void arxivTest() throws Exception
     {
         this.logger.info("---Transformation arXiv to escidoc format ---");
         Format arxivItem = new Format("arxiv", "application/xml", "UTF-8");
         Format escidoc = new Format("escidoc-publication-item", "application/xml", "UTF-8");
         Format escidocComponent = new Format("escidoc-publication-component", "application/xml", "UTF-8");        
         Format bibtex = new Format("bibtex", "text/plain", "*");
         
         byte[] result;
         result = this.trans.transform(this.util.getResourceAsString("testFiles/externalSources/arxivItem.xml").getBytes(), arxivItem, escidoc, "escidoc");
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
         result = this.trans.transform(this.util.getResourceAsString("testFiles/pmc2.xml").getBytes("UTF-8"), pmcItem, escidoc, "escidoc");
         this.logger.info(new String(result, "UTF-8"));   
         
         PubItemVO itemVO = xmlTransforming.transformToPubItem(new String(result));
         System.out.println("itemVO successfully created. ");
         
         result = this.trans.transform(this.util.getResourceAsString("testFiles/pmc2.xml").getBytes(), pmcItem, escidocComponent, "escidoc");
         this.logger.info(new String(result));   
         
         FileVO componentVO = xmlTransforming.transformToFileVO(new String(result));
         System.out.println("FileVO successfully created. ");
         
     }
	
    
     public void mods2oaidcTest () throws Exception
     {
         Format mods = new Format("mods", "application/xml", "UTF-8");
         Format oai = new Format("oai_dc", "application/xml", "UTF-8");
         
         byte[] result;
         result = this.trans.transform(this.util.getResourceAsString("testFiles/mods2.xml").getBytes("UTF-8"), mods, oai, "escidoc");
         this.logger.info(new String(result, "UTF-8"));           
     }
     
    
     public void mods2marcTest () throws Exception
     {
         Format mods = new Format("mods", "application/xml", "UTF-8");
         Format marc = new Format("marc21", "application/xml", "UTF-8");
         
         byte[] result;
         result = this.trans.transform(this.util.getResourceAsString("testFiles/mods.xml").getBytes("UTF-8"), mods, marc, "escidoc");
         this.logger.info(new String(result, "UTF-8"));
     }
    
     public void escidoc2oaidcTest () throws Exception
     {
         Format escidoc = new Format("escidoc-publication-item", "application/xml", "UTF-8");
         Format oai = new Format("oai_dc", "application/xml", "UTF-8");
        
         byte[] result;
         result = this.trans.transform(this.util.getResourceAsString("testFiles/escidocItem.xml").getBytes("UTF-8"), escidoc, oai, "escidoc");
         this.logger.info("escidoc -> oai_dc");
         this.logger.info(new String(result, "UTF-8"));
     }
     
     
     public void snippetToOutputFormatTest() throws Exception
     {
         this.logger.info("snippet -> outputFormat");
         
         Format input1 = new Format("snippet", "application/xml", "UTF-8");
         Format input2 = new Format("snippet_APA", "application/xml", "UTF-8");
         Format input3 = new Format("snippet_AJP", "application/xml", "UTF-8");
         Format output1 = new Format("pdf", "application/pdf", "*");
         Format output2 = new Format("html", "text/html", "*");
         Format output3 = new Format("rtf", "application/rtf", "*");
         Format output4 = new Format("odt", "application/vnd.oasis.opendocument.text", "*");
         
         byte[] result;
         this.logger.info("APA");
         result = this.trans.transform(this.util.getResourceAsString("testFiles/apa_snippet.xml").getBytes("UTF-8"), input2, output1, "escidoc");
         this.logger.info("APA - pdf: OK");
         result = this.trans.transform(this.util.getResourceAsString("testFiles/apa_snippet.xml").getBytes("UTF-8"), input2, output2, "escidoc");
         this.logger.info(new String (result,"UTF-8"));
         this.logger.info("APA - html: OK");
         result = this.trans.transform(this.util.getResourceAsString("testFiles/apa_snippet.xml").getBytes("UTF-8"), input2, output3, "escidoc");
         this.logger.info("APA - rtf: OK");
         result = this.trans.transform(this.util.getResourceAsString("testFiles/apa_snippet.xml").getBytes("UTF-8"), input2, output4, "escidoc");
         this.logger.info("APA - odt: OK");
         
         this.logger.info("AJP");
         result = this.trans.transform(this.util.getResourceAsString("testFiles/ajp_snippet.xml").getBytes("UTF-8"), input3, output1, "escidoc");
         this.logger.info("AJP - pdf: OK");
         result = this.trans.transform(this.util.getResourceAsString("testFiles/ajp_snippet.xml").getBytes("UTF-8"), input3, output2, "escidoc");
         this.logger.info(new String (result,"UTF-8"));
         this.logger.info("AJP - html: OK");
         result = this.trans.transform(this.util.getResourceAsString("testFiles/ajp_snippet.xml").getBytes("UTF-8"), input3, output3, "escidoc");
         this.logger.info("AJP - rtf: OK");
         result = this.trans.transform(this.util.getResourceAsString("testFiles/ajp_snippet.xml").getBytes("UTF-8"), input3, output4, "escidoc");
         this.logger.info("AJP - odt: OK");
     }
}
