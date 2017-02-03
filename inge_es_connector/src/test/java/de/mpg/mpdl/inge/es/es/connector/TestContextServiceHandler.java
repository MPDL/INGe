package de.mpg.mpdl.inge.es.es.connector;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.mpg.mpdl.inge.es.handler.ContextServiceHandler;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO.State;
import de.mpg.mpdl.inge.services.IngeServiceException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestContextServiceHandler extends TestBase {
  private static final Logger LOG = Logger.getLogger(TestContextServiceHandler.class);

  private ContextServiceHandler contextServiceHandler;
  private String test_context_id = "test_context";
 
  @Before
  public void setUp() throws Exception {
    this.contextServiceHandler = new ContextServiceHandler();
  }

  @After
  public void tearDown() throws Exception {}

  @Test
  public void testCreate() {
    try {
      String contextId = this.contextServiceHandler.createContext(test_context(), test_context_id);
      assert contextId.equals(test_context_id);
    } catch (IngeServiceException e) {
      LOG.error(e);
      System.out.println(e);
    }
  }

  @Test
  public void testRead() {
    try {
      ContextVO contextVO = this.contextServiceHandler.readContext(test_context_id);
      assert contextVO.equals(test_context());
    } catch (IngeServiceException e) {
      LOG.error(e);
      System.out.println(e);
    }
  }

  @Test
  public void testUpdate() {
    try {
      ContextVO contextVO = this.contextServiceHandler.readContext(test_context_id);
      contextVO.setState(State.CREATED);
      this.contextServiceHandler.updateContext(contextVO, test_context_id);
      ContextVO contextVO2 = this.contextServiceHandler.readContext(test_context_id);
      assert contextVO2.getState().equals(State.CREATED);
    } catch (IngeServiceException e) {
      LOG.error(e);
      System.out.println(e);
    }
  }

  @Ignore
  @Test
  public void testZDelete() {
    String contextId = this.contextServiceHandler.deleteContext(test_context_id);
    assert contextId.equals(test_context_id);
  }

}
