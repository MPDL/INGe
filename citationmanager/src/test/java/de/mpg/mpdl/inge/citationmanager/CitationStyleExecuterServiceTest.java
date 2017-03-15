package de.mpg.mpdl.inge.citationmanager;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO.FormatType;
import de.mpg.mpdl.inge.util.ResourceUtil;

public class CitationStyleExecuterServiceTest {

  private static final Logger logger = Logger.getLogger(CitationStyleExecuterServiceTest.class);

  private static String itemList = "";

  @Before
  public void loadItem() throws IOException {
    itemList =
        ResourceUtil.getResourceAsString("./testFiles/escidocItem.xml",
            CitationStyleExecuterServiceTest.class.getClassLoader());
  }


  @Test
  public void test() {
    byte[] exportData = null;

    /*
     * try { exportData = CitationStyleExecutorService.getOutput(itemList, new
     * ExportFormatVO(FormatType.LAYOUT, "JUS_Report", "escidoc_snippet"));
     * 
     * } catch (Exception e) { logger.error("Error when trying to find citation service.", e); }
     * logger.info(new String(exportData));
     */
    try {
      exportData =
          CitationStyleManager.getOutput(itemList, new ExportFormatVO(FormatType.LAYOUT, "APA",
              "pdf"));

    } catch (Exception e) {
      logger.error("Error when trying to find citation service.", e);
    }
    logger.info(new String(exportData));
  }


}
