package test;


import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mpg.escidoc.services.transformation.TransformationBean;
import de.mpg.escidoc.services.transformation.valueObjects.Format;
import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;


public class ZfNTest
{
    public static TransformationBean trans;
    

    /**
     * Initializes the {@link TransformationBean}.
     */
    @BeforeClass
    public static void initTransformation()
    {
        trans = new TransformationBean(true);
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
   
        XmlTransformingBean xmlTransforming = new XmlTransformingBean();
        PubItemVO itemVO = xmlTransforming.transformToPubItem(new String(result, "UTF-8"));
        Assert.assertNotNull(itemVO);
        System.out.println("PubItemVO successfully created.");    
    }

}
