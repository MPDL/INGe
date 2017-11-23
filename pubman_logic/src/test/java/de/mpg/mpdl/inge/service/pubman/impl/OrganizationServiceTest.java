package de.mpg.mpdl.inge.service.pubman.impl;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsOrganizationalUnitDetailsVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.service.spring.AppConfigPubmanLogicTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfigPubmanLogicTest.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OrganizationServiceTest extends TestBase {

  @Autowired
  OrganizationService organizationService;

  @Test
  public void objects() {

    super.logMethodName();

    assertTrue(organizationService != null);
  }

  @Test(expected = AuthorizationException.class)
  public void deleteInStateClosed() throws IngeTechnicalException, AuthenticationException,
      AuthorizationException, IngeApplicationException {
    super.logMethodName();

    String authenticationToken = loginAdmin();
    assertTrue(authenticationToken != null);

    AffiliationVO affiliationVO = organizationService.get(ORG_OBJECTID_25, authenticationToken);
    assertTrue(affiliationVO != null);
    assertTrue(affiliationVO.getPublicStatus().equals("CLOSED"));

    organizationService.delete(ORG_OBJECTID_25, authenticationToken);

    assertTrue(organizationService.get(ORG_OBJECTID_25, authenticationToken) == null);
  }

  @Test
  public void deleteInStateCreated() throws IngeTechnicalException, AuthenticationException,
      AuthorizationException, IngeApplicationException {
    super.logMethodName();

    String authenticationToken = loginAdmin();
    assertTrue(authenticationToken != null);

    AffiliationVO affiliationVO =
        organizationService.create(getAffiliationVO(), authenticationToken);
    assertTrue(affiliationVO != null);
    assertTrue(affiliationVO.getPublicStatus().equals("CREATED"));

    organizationService.delete(affiliationVO.getReference().getObjectId(), authenticationToken);

    assertTrue(organizationService.get(affiliationVO.getReference().getObjectId(),
        authenticationToken) == null);
  }

  @Test
  public void get() throws Exception {

    super.logMethodName();

    String authenticationToken = loginAdmin();
    assertTrue(authenticationToken != null);

    AffiliationVO affiliationVO = organizationService.get(ORG_OBJECTID_13, authenticationToken);
    assertTrue(affiliationVO != null);
    assertTrue(affiliationVO.getChildAffiliations().size() == 0);
    // assertTrue(affiliationVO.getCreationDate().equals("2007-03-22T09:23:35.562+0000"));
    assertTrue(affiliationVO.getParentAffiliations().size() == 0);
    assertTrue(affiliationVO.getPredecessorAffiliations().size() == 1);

    affiliationVO = organizationService.get(ORG_OBJECTID_25, authenticationToken);
    assertTrue(affiliationVO != null);
    assertTrue(affiliationVO.getChildAffiliations().size() == 0);
    assertTrue(affiliationVO.getParentAffiliations().size() == 1);
    assertTrue(affiliationVO.getPredecessorAffiliations().size() == 0);

    affiliationVO = organizationService.get(ORG_OBJECTID_40048, authenticationToken);
    assertTrue(affiliationVO != null);
    assertTrue(affiliationVO.getChildAffiliations().size() == 0);
    assertTrue(affiliationVO.getParentAffiliations().size() == 0);
    assertTrue(affiliationVO.getPredecessorAffiliations().size() == 0);
  }

  @Test
  public void getInvalidId() throws Exception {

    super.logMethodName();

    String authenticationToken = loginAdmin();
    assertTrue(authenticationToken != null);

    AffiliationVO affiliationVO = organizationService.get("XXXXXXXXXXXXXXX", authenticationToken);
    assertTrue(affiliationVO == null);
  }

  @Test
  public void getWithUserToken() throws Exception {
    super.logMethodName();

    String authenticationToken = loginDepositor();
    assertTrue(authenticationToken != null);
    AffiliationVO affiliationVO = organizationService.get(ORG_OBJECTID_25, authenticationToken);

    assertTrue(affiliationVO != null);
  }

  @Test(expected = AuthenticationException.class)
  public void getWithInvalidToken() throws Exception {
    super.logMethodName();
    organizationService.get(ORG_OBJECTID_25, "afgadgfag");
  }

  @Test
  public void openAndClose() throws Exception {

    super.logMethodName();

    String authenticationToken = loginAdmin();
    assertTrue(authenticationToken != null);

    AffiliationVO affiliationVO = organizationService.get(ORG_OBJECTID_13, authenticationToken);
    assertTrue(affiliationVO != null);
    assertTrue(affiliationVO.getPublicStatus().equals("OPENED"));

    affiliationVO =
        organizationService.close(ORG_OBJECTID_13, affiliationVO.getLastModificationDate(),
            authenticationToken);
    assertTrue(affiliationVO.getPublicStatus().equals("CLOSED"));

    try {
      affiliationVO =
          organizationService.open(ORG_OBJECTID_13, affiliationVO.getLastModificationDate(),
              authenticationToken);
    } catch (Exception e) {
      assertTrue(e instanceof AuthorizationException);
    }
    assertTrue(affiliationVO.getPublicStatus().equals("CLOSED"));
  }

  @Test
  public void searchTopLevelOrganizations() throws Exception {

    super.logMethodName();

    String authenticationToken = loginAdmin();
    assertTrue(authenticationToken != null);

    List<AffiliationVO> affiliationVOs = organizationService.searchTopLevelOrganizations();
    assertTrue(affiliationVOs != null);
    assertTrue("Expected <2> affiliations - found <" + affiliationVOs.size() + ">",
        affiliationVOs.size() == 2);

    List<String> topLevelIds = new ArrayList<String>();
    topLevelIds.add(affiliationVOs.get(0).getReference().getObjectId());
    topLevelIds.add(affiliationVOs.get(1).getReference().getObjectId());

    assertTrue(topLevelIds.contains(ORG_OBJECTID_13) && topLevelIds.contains(ORG_OBJECTID_40048));

  }

  @Test
  public void searchChildOrganizations() throws IngeTechnicalException, AuthenticationException,
      AuthorizationException, IngeApplicationException {
    super.logMethodName();
    List<AffiliationVO> affiliationVOs =
        organizationService.searchChildOrganizations(ORG_OBJECTID_13);
    assertTrue(affiliationVOs != null);
    assertTrue("Expected <1> affiliations - found <" + affiliationVOs.size() + ">",
        affiliationVOs.size() == 1);
    assertTrue(affiliationVOs.get(0).getReference().getObjectId().equals(ORG_OBJECTID_25));
  }

  @Test
  public void searchChildOrganizationsInvalidId() throws Exception {
    super.logMethodName();
    List<AffiliationVO> affiliationVOs = null;
    try {
      affiliationVOs = organizationService.searchChildOrganizations("XXXX");
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      logger.info("Exception of class: " + e.getClass().getName());
    }
    assertTrue(affiliationVOs.size() == 0);
    logger.info("Hope to stop here");
  }

  @Test
  public void searchSuccessors() throws IngeTechnicalException, AuthenticationException,
      AuthorizationException, IngeApplicationException {

    super.logMethodName();

    String authenticationToken = loginAdmin();
    assertTrue(authenticationToken != null);

    List<AffiliationVO> affiliationVOs = organizationService.searchSuccessors(ORG_OBJECTID_40048);
    assertTrue(affiliationVOs != null);
    assertTrue("Expected <1> affiliations - found <" + affiliationVOs.size() + ">",
        affiliationVOs.size() == 1);
    assertTrue(affiliationVOs.get(0).getReference().getObjectId().equals(ORG_OBJECTID_13));
  }

  @Test
  public void getIdPathChild() throws Exception {

    super.logMethodName();

    List<String> affiliationVOs = organizationService.getIdPath(ORG_OBJECTID_25);
    assertTrue(affiliationVOs != null);
    assertTrue("Expected <2> affiliations - found <" + affiliationVOs.size() + ">",
        affiliationVOs.size() == 2);
    assertTrue(affiliationVOs.contains(ORG_OBJECTID_25) && affiliationVOs.contains(ORG_OBJECTID_13));
  }

  @Test
  public void getIdPathParent() throws Exception {

    super.logMethodName();

    List<String> affiliationVOs = organizationService.getIdPath(ORG_OBJECTID_13);
    assertTrue(affiliationVOs != null);
    assertTrue("Expected <1> affiliations - found <" + affiliationVOs.size() + ">",
        affiliationVOs.size() == 1);
    assertTrue(affiliationVOs.contains(ORG_OBJECTID_13));
  }

  @Test
  public void reindexListener() throws IngeTechnicalException, AuthenticationException,
      AuthorizationException, IngeApplicationException {

    super.logMethodName();

    String authenticationToken = loginAdmin();
    assertTrue(authenticationToken != null);

    organizationService.reindex(ORG_OBJECTID_25, authenticationToken);
  }

  @Test(expected = AuthorizationException.class)
  public void reindexListenerInvalidToken() throws IngeTechnicalException, AuthenticationException,
      AuthorizationException, IngeApplicationException {

    super.logMethodName();

    organizationService.reindex(ORG_OBJECTID_25, "hsfhsfhsfhshsgfh");
  }

  private AffiliationVO getAffiliationVO() {

    AffiliationVO affiliationVO = new AffiliationVO();
    MdsOrganizationalUnitDetailsVO mdsOrganizationalUnitDetailsVO =
        new MdsOrganizationalUnitDetailsVO();
    mdsOrganizationalUnitDetailsVO.setName("Kurzes Leben");
    affiliationVO.setDefaultMetadata(mdsOrganizationalUnitDetailsVO);

    return affiliationVO;
  }

}
