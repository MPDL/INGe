package de.mpg.mpdl.inge.es.es.connector;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.mpg.mpdl.inge.es.handler.ItemServiceHandler;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.services.IngeServiceException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestItemServiceHandler extends TestBase {
  private static final Logger LOG = Logger.getLogger(TestItemServiceHandler.class);

  private ItemServiceHandler itemServiceHandler;
  private String test_item_id = "test_item";

  @Before
  public void setUp() throws Exception {
    this.itemServiceHandler = new ItemServiceHandler();
  }

  @After
  public void tearDown() throws Exception {}

  @Test
  public void test1Create() {
    try {
      String contextId = this.itemServiceHandler.createItem(test_item(), test_item_id);
      assert contextId.equals(test_item_id);
    } catch (IngeServiceException e) {
      LOG.error(e);
      System.out.println(e);
    }
  }

  @Test
  public void test1Read() {
    try {
      PubItemVO pubItemVO = this.itemServiceHandler.readItem(test_item_id);
      assert pubItemVO.equals(test_item());
    } catch (IngeServiceException e) {
      LOG.error(e);
      System.out.println(e);
    }
  }

  @Test
  public void test2Create() {
    try {
      String contextId = this.itemServiceHandler.createItem(create_item(), test_item_id);
      assert contextId.equals(test_item_id);
    } catch (Exception e) {
      LOG.error(e);
      System.out.println(e);
    }
  }

  @Test
  public void test2Read() {
    try {
      PubItemVO pubItemVO = this.itemServiceHandler.readItem(test_item_id);
      assert pubItemVO.equals(create_item());
    } catch (Exception e) {
      LOG.error(e);
      System.out.println(e);
    }
  }

  @Ignore
  @Test
  public void testUpdate() {
    try {
      PubItemVO pubItemVO = this.itemServiceHandler.readItem(test_item_id);
      pubItemVO.setPid("testPid");
      this.itemServiceHandler.updateItem(pubItemVO, test_item_id);
      PubItemVO pubItemVO2 = this.itemServiceHandler.readItem(test_item_id);
      assert pubItemVO2.getPid().equals("testPid");
    } catch (IngeServiceException e) {
      LOG.error(e);
      System.out.println(e);
    }
  }

  @Ignore
  @Test
  public void testZDelete() {
    String itemId = this.itemServiceHandler.deleteItem(test_item_id);
    assert itemId.equals(test_item_id);
  }

}
