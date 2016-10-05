package de.mpg.mpdl.inge.es.es.connector;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mpg.mpdl.inge.es.service.OrganizationServiceBean;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.services.IngeServiceException;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceBeanTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestOrganizationServiceBean extends TestBase {

  @Autowired
  private OrganizationServiceBean bean;

  String test_ou_id = "test_ou";

  @Test
  public void testCreate() {
    try {
      String ou = bean.createOrganization(test_ou(), test_ou_id);
      assert ou.equals(test_ou_id);
    } catch (IngeServiceException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Test
  public void testRead() {
    try {
      AffiliationVO organization = bean.readOrganization(test_ou_id);
      assert organization.getDefaultMetadata().getCity().equals("Munich");
    } catch (IngeServiceException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Test
  public void testUpdate() {
    try {
      AffiliationVO organization = bean.readOrganization(test_ou_id);
      organization.getDefaultMetadata().setCountryCode("DE");
      String ou = bean.updateOrganization(organization, test_ou_id);
      assert ou.equals(ou);
    } catch (IngeServiceException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Test
  public void testZDelete() {
    String ou = bean.deleteOrganization(test_ou_id);
      assert ou.equals(test_ou_id);
  }

}
