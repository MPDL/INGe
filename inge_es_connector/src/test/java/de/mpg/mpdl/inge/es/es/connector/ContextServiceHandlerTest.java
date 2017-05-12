package de.mpg.mpdl.inge.es.es.connector;

import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;
import org.junit.After;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import de.mpg.mpdl.inge.dao.ContextDao;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO.State;
import de.mpg.mpdl.inge.services.IngeServiceException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ContextServiceHandlerTest extends TestBase {
  private static final Logger LOG = Logger.getLogger(ContextServiceHandlerTest.class);

  @Autowired
  private ContextDao<QueryBuilder> contextDao;
  private String test_context_id = "test_context";


  @After
  public void tearDown() throws Exception {}

  @Test
  public void testCreate() {
    try {
      String contextId = this.contextDao.create(test_context_id, test_context());
      assert contextId.equals(test_context_id);
    } catch (IngeServiceException e) {
      LOG.error(e);
      System.out.println(e);
    }
  }

  @Test
  public void testRead() {
    try {
      ContextVO contextVO = this.contextDao.get(test_context_id);
      assert contextVO.equals(test_context());
    } catch (IngeServiceException e) {
      LOG.error(e);
      System.out.println(e);
    }
  }

  @Test
  public void testUpdate() {
    try {
      ContextVO contextVO = this.contextDao.get(test_context_id);
      contextVO.setState(State.CREATED);
      this.contextDao.update(test_context_id, contextVO);
      ContextVO contextVO2 = this.contextDao.get(test_context_id);
      assert contextVO2.getState().equals(State.CREATED);
    } catch (IngeServiceException e) {
      LOG.error(e);
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