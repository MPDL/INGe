package test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.transformation.TransformationBean;
import de.mpg.escidoc.services.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

public class OtherFormatsTest {

    public static TransformationBean trans;
    private final Logger logger = Logger.getLogger(OtherFormatsTest.class);
    
    /**
     * Initializes the {@link TransformationBean}.
     */
    @BeforeClass
    public static void initTransformation()
    {
        trans = new TransformationBean(true);
    }
	
    @Test
    
    public void dlc2zvddmets() throws Exception
    {
        this.logger.info("Transform dlc volume to zvdd conform mets format");
        Format inputFormat = new Format("dlc", "application/xml", "UTF-8");
        Format outputFormat = new Format("zvddmets", "application/xml", "UTF-8");
        byte[] result = trans.transform(ResourceUtil.getResourceAsString("testFiles/dlc/dlcItem.xml").getBytes("UTF-8"), inputFormat, outputFormat, "escidoc");
        
        System.out.println(new String(result, "UTF-8")); 
    }
    
    @Test
    public void edoc2escidoc() throws Exception
    {
        this.logger.info("---Transformation eDoc to eSciDoc format ---");
        Format edoc = new Format("edoc", "application/xml", "UTF-8");
        Format escidoc = new Format("escidoc-publication-item-list", "application/xml", "UTF-8");    
        
        Map<String, String> configuration = new HashMap<String, String>();
        configuration.put("import-name", "OTHER");
        configuration.put("CoNE", "false");
        
        String result;
        byte[] resultBytes = this.trans.transform(ResourceUtil.getResourceAsBytes("testFiles/edoc/singleEdocItem.xml"), edoc, escidoc, "escidoc", configuration);
        result = new String(resultBytes, "UTF-8");
        
        String compare = ResourceUtil.getResourceAsString("testFiles/edoc/result.xml");
        
        logger.info(resultBytes.length);

        //TODO: check dependency
//        XmlComparator xmlComparator = new XmlComparator(result, compare);
//        
//        if (!xmlComparator.equal())
//        {
//            StringWriter stringWriter = new StringWriter();
//            stringWriter.write("The result is not the expected. There is a difference at:\n");
//            for (String error : xmlComparator.getErrors())
//           {
//                stringWriter.write("- ");
//                stringWriter.write(error);
//                stringWriter.write("\n");
//           }
//            stringWriter.write("Result XML: ");
//            stringWriter.write(result);
//            stringWriter.write("\n");
//            stringWriter.write("Expected XML: ");
//            stringWriter.write(compare);
//            stringWriter.write("\n");
//            
//            fail(stringWriter.toString());
//        }

    }
    
    @Test
    public void edoc2escidoc2() throws Exception
    {
        this.logger.info("---Transformation eDoc to eSciDoc format ---");
        Format edoc = new Format("edoc", "application/xml", "UTF-8");
        Format escidoc = new Format("escidoc-publication-item-list", "application/xml", "UTF-8");    
        
        Map<String, String> conf = new HashMap<String, String>();       
        conf.put("CoNE", "false");
        
        byte[] resultBytes = this.trans.transform(ResourceUtil.getResourceAsBytes("testFiles/edoc/test.xml"), edoc, escidoc, "escidoc", conf);
        String result = new String(resultBytes, "UTF-8");
        
        String compare = ResourceUtil.getResourceAsString("testFiles/edoc/result.xml");
        
        logger.info(resultBytes.length);
        
        //TODO: check
//        XmlComparator xmlComparator = new XmlComparator(result, compare);
//        
//        if (!xmlComparator.equal())
//        {
//            StringWriter stringWriter = new StringWriter();
//            stringWriter.write("The result is not the expected. There is a difference at:\n");
//            for (String error : xmlComparator.getErrors())
//           {
//                stringWriter.write("- ");
//                stringWriter.write(error);
//                stringWriter.write("\n");
//           }
//            stringWriter.write("Result XML: ");
//            stringWriter.write(result);
//            stringWriter.write("\n");
//            stringWriter.write("Expected XML: ");
//            stringWriter.write(compare);
//            stringWriter.write("\n");
//            stringWriter.close();
//            
//            fail(stringWriter.toString());
//            
//        }

    }  
    
    @Test
    @Ignore
    // temporarily moved to the structuredexportmanager 
    public void escidoc2edocTest() throws Exception
    {
   	 byte[] src = ResourceUtil.getResourceAsBytes("testFiles/escidoc/escidoc_xml_full.xml");
   	 Format escidoc = new Format("escidoc", "application/xml", "UTF-8");
   	 
   	 for (String format: new String[]{"edoc_export", "edoc_import"} )
   	 {
	    	 this.logger.info("---Transformation eSciDoc to " + format + " format ---");
	    	     
	    	 Format edoc = new Format(format, "application/xml", "UTF-8");
	    	 
	    	 byte[] resultBytes = trans.transform(src, escidoc, edoc, "escidoc");
	    	 Assert.assertNotNull(resultBytes);
	    	 
	//    	 String file = ResourceUtil.getResourceAsFile(".").getAbsolutePath() + "/edoc_test.xml";
	    	 /*String file = "target/"+ format + ".xml";
	    	 this.logger.info("output file: " + file);
	    	 
	    	 FileOutputStream fos = new FileOutputStream(file);
	    	 fos.write(resultBytes);
	    	 fos.close();*/
   	 }
   	 
    }
    
    
    @Test 
    
    public void eSciDocVer1toeSciDocVer2() throws TransformationNotSupportedException, RuntimeException, UnsupportedEncodingException, IOException
    {
   	 	Format escidocv1 = new Format("escidoc-publication-v1", "application/xml", "UTF-8");
        Format escidocv2 = new Format("escidoc-publication-v2", "application/xml", "UTF-8");
        
        byte[] result;
        
        logger.info("escidoc-publication-item-v1 to escidoc-publication-item-v2");
        result = trans.transform(ResourceUtil.getResourceAsString("testFiles/escidoc/escidoc-item-ver1.xml").getBytes("UTF-8"), escidocv1, escidocv2, "escidoc");
        this.logger.info("OK");
        System.out.println(new String(result, "UTF-8"));
        
        Map<String, String> conf = new HashMap<String, String>();       
        conf.put("List", "true");
        logger.info("escidoc-publication-lists-v1 to escidoc-publication-list-v2, file with multiply items");
        result = trans.transform(ResourceUtil.getResourceAsString("testFiles/escidoc/escidoc-item-list-ver1.xml").getBytes("UTF-8"), escidocv1, escidocv2, "escidoc",conf);
        this.logger.info("OK");
        System.out.println(new String(result, "UTF-8"));
    }
    
    @Test 
    
    public void eSciDocVer2toeSciDocVer1() throws TransformationNotSupportedException, RuntimeException, UnsupportedEncodingException, IOException
    {
   	 	Format escidocv1 = new Format("escidoc-publication-v1", "application/xml", "UTF-8");
        Format escidocv2 = new Format("escidoc-publication-v2", "application/xml", "UTF-8");
        
        byte[] result;
        
        logger.info("escidoc-publication-item-v1 to escidoc-publication-item-v2");
        result = trans.transform(ResourceUtil.getResourceAsString("testFiles/escidoc/escidoc-item-ver2.xml").getBytes("UTF-8"), escidocv2, escidocv1, "escidoc");
        this.logger.info("OK");
        System.out.println(new String(result, "UTF-8"));
        
        Map<String, String> conf = new HashMap<String, String>();       
        conf.put("List", "true");
        logger.info("escidoc-publication-lists-v1 to escidoc-publication-list-v2, file with multiply items");
        result = trans.transform(ResourceUtil.getResourceAsString("testFiles/escidoc/escidoc-item-list-ver2.xml").getBytes("UTF-8"), escidocv2, escidocv1, "escidoc",conf);
        this.logger.info("OK");
        System.out.println(new String(result, "UTF-8"));
    }
    
    private String normalizeString(String str)
    {
        return str.replace(" ", "").replace("\n", "");
    }
    
    @Test
    @Ignore
    //Not working due to relation to author decoder of de.mpg.escidoc.services.common.util
    public void mabListTransformation() throws Exception
    {
        this.logger.info("Transform MAB list to xml format");
        
        Format inputFormat = new Format("MAB", "text/plain", "utf-8");
        Format outputFormat = new Format("eSciDoc-publication-item-list", "application/xml", "utf-8");
        byte[] result = trans.transform(ResourceUtil.getResourceAsString("testFiles/mab/mab.txt").getBytes("UTF-8"), 
                inputFormat, outputFormat, "escidoc");

        System.out.println(new String(result, "UTF-8")); 
    }
    
    @Test
    @Ignore
  //Not working due to relation to author decoder of de.mpg.escidoc.services.common.util
    public void risListTransformation() throws Exception
    {
        this.logger.info("Transform RIS list to xml format");
        Format inputFormat = new Format("RIS", "text/plain", "utf-8");
        Format outputFormat = new Format("eSciDoc-publication-item-list", "application/xml", "utf-8");
        byte[] result = trans.transform(ResourceUtil.getResourceAsString("testFiles/ris/RIS.txt").getBytes("UTF-8"), inputFormat, outputFormat, "escidoc");

        System.out.println(new String(result, "UTF-8")); 
    }
    
    @Test
    @Ignore
  //Not working due to relation to author decoder of de.mpg.escidoc.services.common.util
    public void wosList1Transformation() throws Exception
    {
        this.logger.info("Transform WoS list 1 to xml format");
        Format inputFormat = new Format("WoS", "text/plain", "utf-8");
        Format outputFormat = new Format("eSciDoc-publication-item-list", "application/xml", "utf-8");
        byte[] result = trans.transform(ResourceUtil.getResourceAsString("testFiles/wos/WoS.txt").getBytes("UTF-8"), inputFormat, outputFormat, "escidoc");
        
        System.out.println(new String(result, "UTF-8")); 
    }

    @Test
    @Ignore
  //Not working due to relation to author decoder of de.mpg.escidoc.services.common.util
    public void wosList2Transformation() throws Exception
    {
        this.logger.info("Transform WoS list 2 to xml format");
        Format inputFormat = new Format("WoS", "text/plain", "utf-8");
        Format outputFormat = new Format("eSciDoc-publication-item-list", "application/xml", "utf-8");
        byte[] result = trans.transform(ResourceUtil.getResourceAsString("testFiles/wos/WoS_2012.txt").getBytes("UTF-8"), inputFormat, outputFormat, "escidoc");
        
        System.out.println(new String(result, "UTF-8")); 
    }
}
