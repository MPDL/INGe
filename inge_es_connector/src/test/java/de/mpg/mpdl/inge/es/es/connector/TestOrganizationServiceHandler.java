package de.mpg.mpdl.inge.es.es.connector;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.mpg.mpdl.inge.es.handler.OrganizationServiceHandler;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.services.IngeServiceException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestOrganizationServiceHandler extends TestBase {
  private static final Logger LOG = Logger.getLogger(TestContextServiceHandler.class);

  private OrganizationServiceHandler organizationServiceHandler;
  private String test_ou_id = "test_ou";
 
  @Before
  public void setUp() throws Exception {
    this.organizationServiceHandler = new OrganizationServiceHandler();
  }

  @After
  public void tearDown() throws Exception {}
  
  @Test
  public void testCreate() {
    try {
      String ouId = this.organizationServiceHandler.createOrganization(test_ou(), test_ou_id);
      assert ouId.equals(test_ou_id);
    } catch (IngeServiceException e) {
      LOG.error(e);
      System.out.println(e);
    }
  }

  @Test
  public void testRead() {
    try {
      AffiliationVO affiliationVO = this.organizationServiceHandler.readOrganization(test_ou_id);
      assert affiliationVO.equals(test_ou());
    } catch (IngeServiceException e) {
      LOG.error(e);
      System.out.println(e);
    }
  }

  @Test
  public void testUpdate() {
    try {
      AffiliationVO affiliationVO = this.organizationServiceHandler.readOrganization(test_ou_id);
      affiliationVO.getDefaultMetadata().setCountryCode("DE");
      this.organizationServiceHandler.updateOrganization(affiliationVO, test_ou_id);
      AffiliationVO affiliationVO2 = this.organizationServiceHandler.readOrganization(test_ou_id);
      assert affiliationVO2.getDefaultMetadata().getCountryCode().equals("DE");
    } catch (IngeServiceException e) {
      LOG.error(e);
      System.out.println(e);
    }
  }

  @Ignore
  @Test
  public void testZDelete() {
    String ouId = this.organizationServiceHandler.deleteOrganization(test_ou_id);
    assert ouId.equals(test_ou_id);
  }

}
