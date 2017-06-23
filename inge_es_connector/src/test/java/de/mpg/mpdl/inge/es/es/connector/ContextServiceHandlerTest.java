package de.mpg.mpdl.inge.es.es.connector;

import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;
import org.junit.After;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mpg.mpdl.inge.es.dao.ContextDaoEs;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;

@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ContextServiceHandlerTest extends TestBase {
  private static final Logger logger = Logger.getLogger(ContextServiceHandlerTest.class);

  @Autowired
  private ContextDaoEs contextDao;
  private String test_context_id = "test_context";


  @After
  public void tearDown() throws Exception {}

  @Test
  public void testCreate() {
    try {
      String contextId = this.contextDao.create(test_context_id, test_context());
      assert contextId.equals(test_context_id);
    } catch (IngeTechnicalException e) {
      logger.error(e);
      System.out.println(e);
    }
  }

  @Test
  public void testRead() {
    try {
      ContextVO contextVO = this.contextDao.get(test_context_id);
      assert contextVO.equals(test_context());
    } catch (IngeTechnicalException e) {
      logger.error(e);
      System.out.println(e);
    }
  }

  @Test
  public void testUpdate() {
    try {
      ContextVO contextVO = this.contextDao.get(test_context_id);
      contextVO.setState(ContextVO.State.CREATED);
      this.contextDao.update(test_context_id, contextVO);
      ContextVO contextVO2 = this.contextDao.get(test_context_id);
      assert contextVO2.getState().equals(ContextVO.State.CREATED);
    } catch (IngeTechnicalException e) {
      logger.error(e);
      System.out.println(e);
    }
  }

  @Ignore
  @Test
  public void testZDelete() {
    String contextId = this.contextDao.delete(test_context_id);
    assert contextId.equals(test_context_id);
  }
}
