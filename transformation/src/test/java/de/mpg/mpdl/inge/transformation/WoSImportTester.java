package de.mpg.mpdl.inge.transformation;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.transformation.transformations.otherFormats.wos.WoSImport;
import de.mpg.mpdl.inge.transformation.transformations.otherFormats.wos.WoSTransformation;
import de.mpg.mpdl.inge.transformation.valueObjects.Format;
import de.mpg.mpdl.inge.util.ResourceUtil;

public class WoSImportTester {
  private static final Logger logger = Logger.getLogger(WoSImportTester.class);

  WoSTransformation wosTransformer = new WoSTransformation();

  /**
   * @param args
   */
  public static void main(String[] args) throws Exception {
    // TODO Auto-generated method stub
    WoSImport imp = new WoSImport();

    Transformation transformation = new TransformationBean();
    Format inputFormat = new Format("WoS", "text/plain", "UTF-8");
    Format outputFormat = new Format("eSciDoc-publication-item-list", "application/xml", "UTF-8");

    InputStream inputStream =
        ResourceUtil.getResourceAsStream("/home/kurt/Dokumente/wok-isi-test.txt",
            WoSImportTester.class.getClassLoader());
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] buffer = new byte[2048];
    int read;
    while ((read = inputStream.read(buffer)) != -1) {
      baos.write(buffer, 0, read);
    }
    // String str = imp.readFile();
    imp.transformWoS2XML(new String(baos.toByteArray(), "UTF-8"));
    // String out = imp.transformWoS2XML(str);
    byte[] result =
        transformation.transform(baos.toByteArray(), inputFormat, outputFormat, "escidoc");

    logger.debug(new String(result, "UTF-8"));
  }


  @Test
  public void wosList1Transformation() throws Exception {
    logger.info("Transform WoS list 1 to xml format");
    Format inputFormat = new Format("WoS", "text/plain", "utf-8");
    Format outputFormat = new Format("eSciDoc-publication-item-list", "application/xml", "utf-8");
    byte[] result =
        wosTransformer.transform(
            ResourceUtil.getResourceAsString("testFiles/wos/WoS.txt",
                WoSImportTester.class.getClassLoader()).getBytes("UTF-8"), inputFormat,
            outputFormat, "escidoc");

    List<PubItemVO> itemVOList = XmlTransformingService.transformToPubItemList(new String(result));

    assertEquals(1, itemVOList.size());

    logger.info("PubItemVO List successfully created.");
  }

  @Test
  public void wosList2Transformation() throws Exception {
    logger.info("Transform WoS list 2 to xml format");
    Format inputFormat = new Format("WoS", "text/plain", "utf-8");
    Format outputFormat = new Format("eSciDoc-publication-item-list", "application/xml", "utf-8");
    byte[] result =
        wosTransformer.transform(
            ResourceUtil.getResourceAsString("testFiles/wos/WoS_2012.txt",
                WoSImportTester.class.getClassLoader()).getBytes("UTF-8"), inputFormat,
            outputFormat, "escidoc");

    List<PubItemVO> itemVOList = XmlTransformingService.transformToPubItemList(new String(result));

    assertEquals(5, itemVOList.size());

    logger.info("PubItemVO List successfully created.");
  }
}
