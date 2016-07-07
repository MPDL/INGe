package de.mpg.mpdl.inge;

import org.junit.Ignore;
import org.junit.Test;

import de.mpg.mpdl.inge.es.connector.OrganizationService;
import de.mpg.mpdl.inge.model.exceptions.TechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.statistics.AggregationCountCumulationFieldVO;
import de.mpg.mpdl.inge.tech.exceptions.NotFoundException;

public class TestOrganizationService extends TestBase {

  OrganizationService os = new OrganizationService();
  String test_ou_id = "test_ou";

  @Test
  public void testCreate() {
    try {
      String ou = os.createOrganization(test_ou(), test_ou_id);
      assert ou.equals(test_ou_id);
    } catch (SecurityException | NotFoundException | TechnicalException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Test
  public void testRead() {
    try {
      AffiliationVO organization = os.readOrganization(test_ou_id);
      assert organization.getDefaultMetadata().getCity().equals("Munich");
    } catch (SecurityException | NotFoundException | TechnicalException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Test
  public void testUpdate() {

    try {
      AffiliationVO organization = os.readOrganization(test_ou_id);
      organization.getDefaultMetadata().setCountryCode("DE");
      String ou = os.updateOrganization(organization, test_ou_id);
      assert ou.equals(ou);
    } catch (SecurityException | NotFoundException | TechnicalException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Ignore
  @Test
  public void testDelete() {
    try {
      String ou = os.deleteOrganization(test_ou_id);
      assert ou.equals(test_ou_id);
    } catch (SecurityException | NotFoundException | TechnicalException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
