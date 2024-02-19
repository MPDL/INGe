package de.mpg.mpdl.inge.es.connector;

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

import de.mpg.mpdl.inge.es.dao.PubItemDaoEs;
import de.mpg.mpdl.inge.es.spring.AppConfigIngeEsConnector;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfigIngeEsConnector.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ItemServiceHandlerTest extends TestBase {
  private static final Logger logger = LogManager.getLogger(ItemServiceHandlerTest.class);

  @Autowired
  private PubItemDaoEs itemDao;
  private String test_item_id = "test_item";

  @After
  public void tearDown() throws Exception {}

  @Test
  @Ignore
  public void test1Create() {
    try {
      String contextId = this.itemDao.createImmediately(test_item_id, test_item());
      assert contextId.equals(test_item_id);
    } catch (IngeTechnicalException e) {
      logger.error(e);
      System.out.println(e);
    }
  }

  @Test
  @Ignore
  public void test1Read() {
    try {
      ItemVersionVO pubItemVO = this.itemDao.get(test_item_id);
      assert pubItemVO.equals(test_item());
    } catch (IngeTechnicalException e) {
      logger.error(e);
      System.out.println(e);
    }
  }

  @Test
  public void test2Create() throws Exception {
   
      ItemVersionVO pubItemVO2 = test_item();
      pubItemVO2.setObjectId(test_item_id);
      String contextId = this.itemDao.createImmediately(test_item_id, pubItemVO2);
      assert contextId.equals(test_item_id);
    
  }

  @Test
  @Ignore
  public void test2Read() {
    try {
      ItemVersionVO pubItemVO = this.itemDao.get(test_item_id);
      assert pubItemVO.equals(create_item());
    } catch (Exception e) {
      logger.error(e);
      System.out.println(e);
    }
  }

  @Test
  public void testUpdate() throws Exception {

    ItemVersionVO pubItemVO = this.itemDao.get(test_item_id);
    pubItemVO.getObject().setObjectPid("testPid");
    this.itemDao.updateImmediately(test_item_id, pubItemVO);
    ItemVersionVO pubItemVO2 = this.itemDao.get(test_item_id);

  }

  @Ignore
  @Test
  public void testZDelete() {
    try {
    String itemId = this.itemDao.deleteImmediatly(test_item_id);
    assert itemId.equals(test_item_id);
  } catch (Exception e) {
    logger.error(e);
    System.out.println(e);
  }
  }
}
