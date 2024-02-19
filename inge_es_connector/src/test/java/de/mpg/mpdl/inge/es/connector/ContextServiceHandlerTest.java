package de.mpg.mpdl.inge.es.connector;

import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mpg.mpdl.inge.es.dao.ContextDaoEs;
import de.mpg.mpdl.inge.es.spring.AppConfigIngeEsConnector;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfigIngeEsConnector.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ContextServiceHandlerTest extends TestBase {
  private static final Logger logger = LogManager.getLogger(ContextServiceHandlerTest.class);

  @Autowired
  private ContextDaoEs contextDao;
  private String test_context_id = "test_context";


  @After
  public void tearDown() throws Exception {}

  @Test
  public void testCreate() {
    try {
      String contextId = this.contextDao.createImmediately(test_context_id, test_context());
      assertTrue("Context id differs: is <" + contextId + "> expected <" + test_context_id + ">", contextId.equals(test_context_id));
    } catch (IngeTechnicalException e) {
      logger.error(e);
      System.out.println(e);
    }
  }

  @Test
  public void testRead() {
    try {
      ContextDbVO contextVO = this.contextDao.get(test_context_id);
      assertTrue("Difference in contextVO", contextVO.equals(test_context()));
    } catch (IngeTechnicalException e) {
      logger.error(e);
      System.out.println(e);
    }
  }

  @Test
  public void testUpdate() {
    try {
      ContextDbVO contextVO = this.contextDao.get(test_context_id);
      contextVO.setState(ContextDbVO.State.OPENED);
      this.contextDao.updateImmediately(test_context_id, contextVO);
      ContextDbVO contextVO2 = this.contextDao.get(test_context_id);
      assertTrue(contextVO2.getState().equals(ContextDbVO.State.OPENED));
    } catch (IngeTechnicalException e) {
      logger.error(e);
      System.out.println(e);
    }
  }

  @Ignore
  @Test
  public void testZDelete() {
    try {
      String contextId = this.contextDao.deleteImmediatly(test_context_id);
      assertTrue("Context id differs: is <" + contextId + "> expected <" + test_context_id + ">", contextId.equals(test_context_id));
    } catch (IngeTechnicalException e) {
      logger.error(e);
      System.out.println(e);
    }
  }
}
