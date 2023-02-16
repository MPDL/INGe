package de.mpg.mpdl.inge.service.pubman.impl;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsOrganizationalUnitDetailsVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.service.spring.AppConfigPubmanLogicTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfigPubmanLogicTest.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class OrganizationServiceTest extends TestBase {
  private static final Logger logger = Logger.getLogger(OrganizationServiceTest.class);

  @Autowired
  OrganizationService organizationService;

  @Before
  public void setUp() throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    String authenticationToken = loginAdmin();

    userAccountService.reindexAll(authenticationToken);
    organizationService.reindexAll(authenticationToken);
  }

  @Test
  public void objects() {

    super.logMethodName();

    assertTrue(organizationService != null);
  }

  @Test
  public void deleteInStateOpened()
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    super.logMethodName();

    String authenticationToken = loginAdmin();
    assertTrue(authenticationToken != null);


    AffiliationDbVO affiliationVO = organizationService.create(getAffiliationVO(), authenticationToken);
    assertTrue(affiliationVO != null);
    assertTrue(affiliationVO.getPublicStatus().equals(AffiliationDbVO.State.OPENED));
    affiliationVO = organizationService.close(affiliationVO.getObjectId(), affiliationVO.getLastModificationDate(), authenticationToken);
    assertTrue(affiliationVO.getPublicStatus().equals(AffiliationDbVO.State.CLOSED));


    organizationService.delete(affiliationVO.getObjectId(), authenticationToken);

    assertTrue(organizationService.get(affiliationVO.getObjectId(), authenticationToken) == null);

  }

  @Test
  public void deleteInStateCreated()
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    super.logMethodName();

    String authenticationToken = loginAdmin();
    assertTrue(authenticationToken != null);

    AffiliationDbVO affiliationVO = organizationService.create(getAffiliationVO(), authenticationToken);
    assertTrue(affiliationVO != null);
    assertTrue(affiliationVO.getPublicStatus().equals(AffiliationDbVO.State.OPENED));

    organizationService.delete(affiliationVO.getObjectId(), authenticationToken);

    assertTrue(organizationService.get(affiliationVO.getObjectId(), authenticationToken) == null);
  }

  @Test
  public void get() throws Exception {

    super.logMethodName();

    String authenticationToken = loginAdmin();
    assertTrue(authenticationToken != null);

    AffiliationDbVO affiliationVO = organizationService.get(ORG_OBJECTID_13, authenticationToken);
    assertTrue(affiliationVO != null);
    // assertTrue(affiliationVO.getCreationDate().equals("2007-03-22T09:23:35.562+0000"));
    assertTrue(affiliationVO.getParentAffiliation() == null);
    assertTrue(affiliationVO.getPredecessorAffiliations().size() == 1);

    affiliationVO = organizationService.get(ORG_OBJECTID_25, authenticationToken);
    assertTrue(affiliationVO != null);

    assertTrue(affiliationVO.getParentAffiliation() != null);
    assertTrue(affiliationVO.getPredecessorAffiliations().size() == 0);

    affiliationVO = organizationService.get(ORG_OBJECTID_40048, authenticationToken);
    assertTrue(affiliationVO != null);
    assertTrue(affiliationVO.getParentAffiliation() == null);
    assertTrue(affiliationVO.getPredecessorAffiliations().size() == 0);
  }

  @Test
  public void getInvalidId() throws Exception {

    super.logMethodName();

    String authenticationToken = loginAdmin();
    assertTrue(authenticationToken != null);

    AffiliationDbVO affiliationVO = organizationService.get("XXXXXXXXXXXXXXX", authenticationToken);
    assertTrue(affiliationVO == null);
  }

  @Test
  public void getWithUserToken() throws Exception {
    super.logMethodName();

    String authenticationToken = loginDepositor();
    assertTrue(authenticationToken != null);
    AffiliationDbVO affiliationVO = organizationService.get(ORG_OBJECTID_25, authenticationToken);

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

    AffiliationDbVO affiliationVO = organizationService.get(ORG_OBJECTID_13, authenticationToken);
    assertTrue(affiliationVO != null);
    assertTrue(affiliationVO.getPublicStatus().equals(AffiliationDbVO.State.OPENED));

    affiliationVO = organizationService.close(ORG_OBJECTID_13, affiliationVO.getLastModificationDate(), authenticationToken);
    assertTrue(affiliationVO.getPublicStatus().equals(AffiliationDbVO.State.CLOSED));

    /*
    try {
      affiliationVO = organizationService.open(ORG_OBJECTID_13, affiliationVO.getLastModificationDate(), authenticationToken);
    } catch (Exception e) {
      assertTrue(e instanceof AuthorizationException);
    }
    */

    affiliationVO = organizationService.open(ORG_OBJECTID_13, affiliationVO.getLastModificationDate(), authenticationToken);
    assertTrue(affiliationVO.getPublicStatus().equals(AffiliationDbVO.State.OPENED));
  }

  @Test
  public void searchTopLevelOrganizations() throws Exception {

    super.logMethodName();

    String authenticationToken = loginAdmin();
    assertTrue(authenticationToken != null);

    List<AffiliationDbVO> affiliationVOs = organizationService.searchTopLevelOrganizations();
    assertTrue(affiliationVOs != null);

    Iterator it = affiliationVOs.iterator();
    while (it.hasNext()) {
      AffiliationDbVO affiliationDbVO = (AffiliationDbVO) it.next();
      logger.info("Found <" + affiliationDbVO.getObjectId() + ">" + "<" + affiliationDbVO.getName() + ">");
    }
    assertTrue("Expected <2> affiliations - found <" + affiliationVOs.size() + ">", affiliationVOs.size() == 2);

    List<String> topLevelIds = new ArrayList<String>();
    topLevelIds.add(affiliationVOs.get(0).getObjectId());
    topLevelIds.add(affiliationVOs.get(1).getObjectId());

    assertTrue(topLevelIds.contains(ORG_OBJECTID_13) && topLevelIds.contains(ORG_OBJECTID_40048));

  }

  @Test
  public void searchChildOrganizations()
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    super.logMethodName();
    List<AffiliationDbVO> affiliationVOs = organizationService.searchChildOrganizations(ORG_OBJECTID_13);
    assertTrue(affiliationVOs != null);
    assertTrue("Expected <1> affiliations - found <" + affiliationVOs.size() + ">", affiliationVOs.size() == 1);
    assertTrue(affiliationVOs.get(0).getObjectId().equals(ORG_OBJECTID_25));
  }

  @Test
  public void searchChildOrganizationsInvalidId() throws Exception {
    super.logMethodName();
    List<AffiliationDbVO> affiliationVOs = null;
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
  public void searchSuccessors() throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    super.logMethodName();

    String authenticationToken = loginAdmin();
    assertTrue(authenticationToken != null);

    List<AffiliationDbVO> affiliationDbVOs = organizationService.searchSuccessors(ORG_OBJECTID_40048);
    assertTrue(affiliationDbVOs != null);
    assertTrue("Expected <1> affiliations - found <" + affiliationDbVOs.size() + ">", affiliationDbVOs.size() == 1);
    assertTrue("Expected <" + ORG_OBJECTID_13 + " found <" + affiliationDbVOs.get(0).getObjectId() + ">",
        affiliationDbVOs.get(0).getObjectId().equals(ORG_OBJECTID_13));
  }

  @Test
  public void getIdPathChild() throws Exception {

    super.logMethodName();

    List<String> affiliationVOs = organizationService.getIdPath(ORG_OBJECTID_25);
    assertTrue(affiliationVOs != null);
    assertTrue("Expected <2> affiliations - found <" + affiliationVOs.size() + ">", affiliationVOs.size() == 2);
    assertTrue(affiliationVOs.contains(ORG_OBJECTID_25) && affiliationVOs.contains(ORG_OBJECTID_13));
  }

  @Test
  public void getIdPathParent() throws Exception {

    super.logMethodName();

    List<String> affiliationVOs = organizationService.getIdPath(ORG_OBJECTID_13);
    assertTrue(affiliationVOs != null);
    assertTrue("Expected <1> affiliations - found <" + affiliationVOs.size() + ">", affiliationVOs.size() == 1);
    assertTrue(affiliationVOs.contains(ORG_OBJECTID_13));
  }

  @Test
  public void reindexListener() throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    super.logMethodName();

    String authenticationToken = loginAdmin();
    assertTrue(authenticationToken != null);

    organizationService.reindex(ORG_OBJECTID_25, authenticationToken);
  }

  @Test(expected = AuthorizationException.class)
  @Ignore
  public void reindexListenerInvalidToken()
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    super.logMethodName();

    organizationService.reindex(ORG_OBJECTID_25, "hsfhsfhsfhshsgfh");
  }

  private AffiliationDbVO getAffiliationVO() {

    AffiliationDbVO affiliationVO = new AffiliationDbVO();
    MdsOrganizationalUnitDetailsVO mdsOrganizationalUnitDetailsVO = new MdsOrganizationalUnitDetailsVO();
    mdsOrganizationalUnitDetailsVO.setName("Kurzes Leben");
    affiliationVO.setMetadata(mdsOrganizationalUnitDetailsVO);

    return affiliationVO;
  }
}
