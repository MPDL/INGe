package de.mpg.mpdl.inge.service.pubman.impl;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;
import de.mpg.mpdl.inge.service.spring.AppConfigPubmanLogicTest;
import de.mpg.mpdl.inge.util.PropertyReader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfigPubmanLogicTest.class})
public class OrganizationServiceTest {

  private static final String ADMIN_LOGIN = "admin";
  private static final String ADMIN_PASSWORD = "tseT";

  private static final String ORG_OBJECTID_13 = "ou_persistent13";
  private static final String ORG_OBJECTID_25 = "ou_persistent25";
  private static final String ORG_OBJECTID_40048 = "ou_40048";

  static private Logger logger = Logger.getLogger(OrganizationServiceTest.class);

  @Autowired
  OrganizationService organizationService;

  @Autowired
  UserAccountService userAccountService;

  @Before
  public void setUp() throws Exception {}

  @Test
  public void objects() {
    assertTrue(organizationService != null);
  }

  @Test
  public void open() throws IngeTechnicalException, AuthenticationException,
      AuthorizationException, IngeApplicationException {
    String authenticationToken = userAccountService.login(ADMIN_LOGIN, ADMIN_PASSWORD);
    assertTrue(authenticationToken != null);

  }

  @Test
  public void get() throws Exception {
    String authenticationToken = userAccountService.login(ADMIN_LOGIN, ADMIN_PASSWORD);
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
    String authenticationToken = userAccountService.login(ADMIN_LOGIN, ADMIN_PASSWORD);
    assertTrue(authenticationToken != null);

    AffiliationVO affiliationVO = organizationService.get("XXXXXXXXXXXXXXX", authenticationToken);
    assertTrue(affiliationVO == null);
  }

  @Test
  public void getWithUserToken() throws Exception {
    String authenticationToken =
        userAccountService.login(PropertyReader.getProperty("inge.depositor.loginname"),
            PropertyReader.getProperty("inge.depositor.password"));
    AffiliationVO affiliationVO = organizationService.get(ORG_OBJECTID_25, authenticationToken);

    assertTrue(affiliationVO != null);
  }

  @Test(expected = AuthenticationException.class)
  public void getWithInvalidToken() throws Exception {

    AffiliationVO affiliationVO = organizationService.get(ORG_OBJECTID_25, "afgadgfag");
  }

  @Test
  public void openAndClose() throws Exception {

    String authenticationToken = userAccountService.login(ADMIN_LOGIN, ADMIN_PASSWORD);
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

    String authenticationToken = userAccountService.login(ADMIN_LOGIN, ADMIN_PASSWORD);
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

    List<AffiliationVO> affiliationVOs =
        organizationService.searchChildOrganizations(ORG_OBJECTID_13);
    assertTrue(affiliationVOs != null);
    assertTrue("Expected <1> affiliations - found <" + affiliationVOs.size() + ">",
        affiliationVOs.size() == 1);
    assertTrue(affiliationVOs.get(0).getReference().getObjectId().equals(ORG_OBJECTID_25));
  }

  @Test
  public void searchChildOrganizationsInvalidId() throws Exception {

    List<AffiliationVO> affiliationVOs;
    try {
      affiliationVOs = organizationService.searchChildOrganizations("XXXX");
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      logger.info("Exception of class: " + e.getClass().getName());
    }
    logger.info("Hope to stop here");
  }

  @Test
  public void searchSuccessors() throws IngeTechnicalException, AuthenticationException,
      AuthorizationException, IngeApplicationException {

    String authenticationToken = userAccountService.login(ADMIN_LOGIN, ADMIN_PASSWORD);
    assertTrue(authenticationToken != null);

    List<AffiliationVO> affiliationVOs = organizationService.searchSuccessors(ORG_OBJECTID_40048);
    assertTrue(affiliationVOs != null);
    assertTrue("Expected <1> affiliations - found <" + affiliationVOs.size() + ">",
        affiliationVOs.size() == 1);
    assertTrue(affiliationVOs.get(0).getReference().getObjectId().equals(ORG_OBJECTID_13));

  }

  @Test
  public void getIdPath() throws IngeTechnicalException, AuthenticationException,
      AuthorizationException, IngeApplicationException {

    List<String> affiliationVOs = organizationService.getIdPath(ORG_OBJECTID_13);
    assertTrue(affiliationVOs != null);
    assertTrue("Expected <1> affiliations - found <" + affiliationVOs.size() + ">",
        affiliationVOs.size() == 1);
    assertTrue(affiliationVOs.get(0).equals(ORG_OBJECTID_13));

    affiliationVOs = organizationService.getIdPath(ORG_OBJECTID_25);
    assertTrue(affiliationVOs != null);
    assertTrue("Expected <2> affiliations - found <" + affiliationVOs.size() + ">",
        affiliationVOs.size() == 2);
    assertTrue(affiliationVOs.contains(ORG_OBJECTID_25) && affiliationVOs.contains(ORG_OBJECTID_13));

  }

}
