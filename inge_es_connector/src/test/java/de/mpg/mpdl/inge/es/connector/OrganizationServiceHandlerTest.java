package de.mpg.mpdl.inge.es.connector;

import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mpg.mpdl.inge.es.dao.OrganizationDaoEs;
import de.mpg.mpdl.inge.es.spring.AppConfigIngeEsConnector;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfigIngeEsConnector.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrganizationServiceHandlerTest extends TestBase {
  private static final Logger logger = Logger.getLogger(ContextServiceHandlerTest.class);

  @Autowired
  private OrganizationDaoEs organizationDao;

  private String test_ou_id = "test_ou";

  @After
  public void tearDown() throws Exception {}

  @Test
  public void testCreate() {
    try {
      String ouId = this.organizationDao.createImmediately(test_ou_id, test_ou());
      assertTrue("Ou id differs: is <" + ouId + "> expected <" + test_ou_id + ">", ouId.equals(test_ou_id));
    } catch (IngeTechnicalException e) {
      logger.error(e);
      System.out.println(e);
    }
  }

  @Test
  public void testRead() {
    try {
      AffiliationDbVO affiliationVO = this.organizationDao.get(test_ou_id);
      assertTrue("Difference in affiliationVO", affiliationVO.equals(test_ou()));
      assert affiliationVO.equals(test_ou());
    } catch (IngeTechnicalException e) {
      logger.error(e);
      System.out.println(e);
    }
  }

  @Test
  public void testUpdate() {
    try {
      AffiliationDbVO affiliationVO = this.organizationDao.get(test_ou_id);
      affiliationVO.getMetadata().setCountryCode("DE");
      this.organizationDao.updateImmediately(test_ou_id, affiliationVO);
      AffiliationDbVO affiliationVO2 = this.organizationDao.get(test_ou_id);
      assertTrue(affiliationVO2.getMetadata().getCountryCode().equals("DE"));
    } catch (IngeTechnicalException e) {
      logger.error(e);
      System.out.println(e);
    }
  }

  @Test
  public void testZDelete() {
    try {
      String ouId = this.organizationDao.deleteImmediatly(test_ou_id);
      assertTrue("Ou id differs: is <" + ouId + "> expected <" + test_ou_id + ">", ouId.equals(test_ou_id));
    } catch (IngeTechnicalException e) {
      logger.error(e);
      System.out.println(e);
    }
  }
}
