package de.mpg.mpdl.inge.transformation;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.transformation.transformations.otherFormats.ris.RISImport;
import de.mpg.mpdl.inge.transformation.transformations.otherFormats.ris.RISTransformation;
import de.mpg.mpdl.inge.transformation.valueObjects.Format;
import de.mpg.mpdl.inge.util.ResourceUtil;

public class RISImportTester {
  private static final Logger logger = Logger.getLogger(RISImportTester.class);

  RISTransformation risTransformer = new RISTransformation();

  /**
   * @param args
   */
  public static void main(String[] args) throws Exception {
    // TODO Auto-generated method stub
    RISImport imp = new RISImport();

    Transformation transformation = new TransformationService();
    Format inputFormat = new Format("RIS", "text/plain", "UTF-8");
    Format outputFormat = new Format("eSciDoc-publication-item-list", "application/xml", "UTF-8");

    InputStream inputStream =
        ResourceUtil.getResourceAsStream("/home/kurt/Dokumente/RIS_utf8.txt",
            RISImportTester.class.getClassLoader());
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] buffer = new byte[2048];
    int read;

    while ((read = inputStream.read(buffer)) != -1) {
      baos.write(buffer, 0, read);
    }

    transformation.transform(baos.toByteArray(), inputFormat, outputFormat, "escidoc");
    imp.transformRIS2XML(new String(baos.toByteArray(), "utf-8"));
  }

  @Test
  public void risListTransformation() throws Exception {
    logger.info("Transform RIS list to xml format");
    Format inputFormat = new Format("RIS", "text/plain", "utf-8");
    Format outputFormat = new Format("eSciDoc-publication-item-list", "application/xml", "utf-8");
    byte[] result =
        risTransformer.transform(
            ResourceUtil.getResourceAsString("testFiles/ris/RIS.txt",
                RISImportTester.class.getClassLoader()).getBytes("UTF-8"), inputFormat,
            outputFormat, "escidoc");

    List<PubItemVO> itemVOList = XmlTransformingService.transformToPubItemList(new String(result));
    logger.info("PubItemVO List successfully created.");
  }
}
