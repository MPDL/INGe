package test.metadata;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.StringReader;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.InputSource;

import de.mpg.escidoc.services.common.MetadataHandler;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.metadata.MetadataHandlerBean;
import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;

public class MetadataHandlerTest {

    private MetadataHandler metadataHandler;
    static final String JAXP_SCHEMA_LANGUAGE =
        "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

    static final String W3C_XML_SCHEMA =
        "http://www.w3.org/2001/XMLSchema"; 
    static final String JAXP_SCHEMA_SOURCE =
        "http://java.sun.com/xml/jaxp/properties/schemaSource";
    
    private static Logger logger = Logger.getLogger(MetadataHandlerTest.class);
    private XmlTransforming xmlTransforming = new XmlTransformingBean();
    
    @Before
    public void getMetadataHandler()
    {
        metadataHandler = new MetadataHandlerBean();
    }
    
    @Ignore
    @Test
    //TODO: Rework for new ImportService
    public void testFetchOAIRecord() throws Exception
    {
//        
//        logger.info("testFetchOAIRecord");
//        
//        String[] identifiers = new String[]{
////                "math-ph/0404037",
////                "0804.1597",
//                "0804.1593"
////                "0804.1221",
////                "0803.0264v1"
//        };
//        String source = "http://export.arxiv.org/oai2?verb=GetRecord&identifier=oai:arXiv.org:";
//        String format = "arXiv";
//        for (String identifier : identifiers) {    
//            String result = metadataHandler.fetchOAIRecord(identifier, source, format);
//            
//            logger.debug("Result: " + result);
//            
//            assertNotNull(result);
//            assertTrue(isValidPublication(result));
//            
//            PubItemVO itemVO = xmlTransforming.transformToPubItem(result);
//            
//            logger.debug("ResultVO: " + itemVO);
//        }
    }
    
    @Test
    public void testBibtex2item() throws Exception
    {
        File[] examples = ResourceUtil.getFilenamesInDirectory("metadata/bibtex/");
        for (int i = 0; i < examples.length; i++) {
            
            String result = metadataHandler.bibtex2item(ResourceUtil.getResourceAsString(examples[i].getAbsolutePath()));
            
            logger.debug("Result: " + result);
            
            assertNotNull(result);
        }
    }
    
    private boolean isValidPublication(String itemXml) throws Exception
    {
        URL schemaUrl = new URL("http://www.escidoc-project.de/schemas/soap/item/0.3/item.xsd");
        return isValidXml(itemXml, schemaUrl);
    }
    
    private boolean isValidXml(String xml, URL schema) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
 
        factory.setNamespaceAware(true);
        factory.setValidating(true);
        factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
        factory.setAttribute(JAXP_SCHEMA_SOURCE, schema.toURI().toString());
        try
        {
            factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
            return true;
        }
        catch (Exception e)
        {
            logger.error("Item XML invalid", e);
            return false;
        }

    }
}
