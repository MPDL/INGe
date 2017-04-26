package de.mpg.mpdl.inge.dataacquisition;

import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;

import de.mpg.mpdl.inge.dataacquisition.valueobjects.DataSourceVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;

public class DataHandlerServiceTest {

  DataHandlerService dataHandlerService = new DataHandlerService();
  DataSourceHandlerService dataSourceHandler = new DataSourceHandlerService();

  @Before
  public void setUp() throws Exception {}


  // Aufruf aus EasySubmission.harvestData()
  @Test
  public void testBmcFullTextHtml() throws DataaquisitionException, UnsupportedEncodingException,
      TechnicalException {
    byte[] b =
        dataHandlerService.doFetch("BioMed Central", "1472-6890-9-1",
            new String[] {"bmcarticleFullTextHtml"});
    assertTrue(b != null);

    String s = new String(b, "UTF-8");

    PubItemVO pubItem = XmlTransformingService.transformToPubItem(s);
    assertTrue(pubItem != null);

    FileVO f = dataHandlerService.getComponentVO();
    assertTrue(f != null);

  }

  @Test
  public void testFetchTextualDataWithoutTransform() throws DataaquisitionException,
      TechnicalException {

    DataSourceVO currentSource = this.dataSourceHandler.getSourceByName("BioMed Central");
    this.dataHandlerService.setCurrentSource(currentSource);
    String textualData =
        dataHandlerService.fetchTextualData("1472-6890-9-1", "bmc", "application/xml", "UTF-8");
    assertTrue(textualData != null);
  }

  @Test
  public void testFetchTextualDataFromBmcAndTransform() throws DataaquisitionException,
      TechnicalException {

    DataSourceVO currentSource = this.dataSourceHandler.getSourceByName("BioMed Central");
    this.dataHandlerService.setCurrentSource(currentSource);
    String textualData =
        dataHandlerService.fetchTextualData("1471-2121-2-1", "eSciDoc-publication-item",
            "application/xml", "UTF-8");
    assertTrue(textualData != null);

    PubItemVO pubItem = XmlTransformingService.transformToPubItem(textualData);

    assertTrue(pubItem != null);

    FileVO f = dataHandlerService.getComponentVO();
    assertTrue(f != null);
  }

  @Test
  public void testFetchTextualDataFromArXivAndTransform() throws DataaquisitionException,
      TechnicalException {

    DataSourceVO currentSource = this.dataSourceHandler.getSourceByName("arxiv");
    this.dataHandlerService.setCurrentSource(currentSource);
    String textualData =
        dataHandlerService.fetchTextualData("0904.3933", "eSciDoc-publication-item",
            "application/xml", "UTF-8");
    assertTrue(textualData != null);

    PubItemVO pubItem = XmlTransformingService.transformToPubItem(textualData);

    assertTrue(pubItem != null);

    FileVO f = dataHandlerService.getComponentVO();
    assertTrue(f != null);
  }

  @Test
  public void testFetchTextualDataFromPmcAndTransform() throws DataaquisitionException,
      TechnicalException {

    DataSourceVO currentSource = this.dataSourceHandler.getSourceByName("PubMedCentral");
    this.dataHandlerService.setCurrentSource(currentSource);
    String textualData =
        dataHandlerService.fetchTextualData("2043518", "eSciDoc-publication-item",
            "application/xml", "UTF-8");
    assertTrue(textualData != null);

    PubItemVO pubItem = XmlTransformingService.transformToPubItem(textualData);
    assertTrue(pubItem != null);

    FileVO f = dataHandlerService.getComponentVO();
    assertTrue(f != null);
  }

}
