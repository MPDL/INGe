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

import de.mpg.mpdl.inge.dao.PubItemDao;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.services.IngeServiceException;

@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ItemServiceHandlerTest extends TestBase {
  private static final Logger LOG = Logger.getLogger(ItemServiceHandlerTest.class);

  @Autowired
  private PubItemDao<QueryBuilder> itemDao;
  private String test_item_id = "test_item";

  @After
  public void tearDown() throws Exception {}

  @Test
  public void test1Create() {
    try {
      String contextId = this.itemDao.create(test_item_id, test_item());
      assert contextId.equals(test_item_id);
    } catch (IngeServiceException e) {
      LOG.error(e);
      System.out.println(e);
    }
  }

  @Test
  public void test1Read() {
    try {
      PubItemVO pubItemVO = this.itemDao.get(test_item_id);
      assert pubItemVO.equals(test_item());
    } catch (IngeServiceException e) {
      LOG.error(e);
      System.out.println(e);
    }
  }

  @Test
  public void test2Create() {
    try {
      String contextId = this.itemDao.create(test_item_id, create_item());
      assert contextId.equals(test_item_id);
    } catch (Exception e) {
      LOG.error(e);
      System.out.println(e);
    }
  }

  @Test
  public void test2Read() {
    try {
      PubItemVO pubItemVO = this.itemDao.get(test_item_id);
      assert pubItemVO.equals(create_item());
    } catch (Exception e) {
      LOG.error(e);
      System.out.println(e);
    }
  }

  @Test
  public void testUpdate() {
    try {
      PubItemVO pubItemVO = this.itemDao.get(test_item_id);
      pubItemVO.setPid("testPid");
      this.itemDao.update(test_item_id, pubItemVO);
      PubItemVO pubItemVO2 = this.itemDao.get(test_item_id);
      assert pubItemVO2.getPid().equals("testPid");
    } catch (IngeServiceException e) {
      LOG.error(e);
      System.out.println(e);
    }
  }

  @Ignore
  @Test
  public void testZDelete() {
    String itemId = this.itemDao.delete(test_item_id);
    assert itemId.equals(test_item_id);
  }
}
