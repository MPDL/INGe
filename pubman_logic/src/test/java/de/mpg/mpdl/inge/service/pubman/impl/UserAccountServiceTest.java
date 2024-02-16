package de.mpg.mpdl.inge.service.pubman.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.service.aa.Principal;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.spring.AppConfigPubmanLogicTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfigPubmanLogicTest.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserAccountServiceTest extends TestBase {

  @Before
  public void setUp() throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    String authenticationToken = loginAdmin();

    userAccountService.reindexAll(authenticationToken);
  }

  @Test
  public void objects() {
    super.logMethodName();

    assertTrue(userAccountService != null);
  }

  @Test
  public void get() throws Exception {
    super.logMethodName();

    String token = null;

    token = loginAdmin();
    assertTrue(token != null);

    AccountUserDbVO accountUserVO = userAccountService.get(DEPOSITOR_OBJECTID, token);

    assertTrue(accountUserVO != null);
    assertTrue(accountUserVO.getAffiliation() != null);
    assertTrue(accountUserVO.getAffiliation().getObjectId().equals(ORG_OBJECTID_25));
    assertTrue(accountUserVO.getGrantList().size() == 2);
    assertTrue(accountUserVO.getName().equals("Test Depositor"));

    assertTrue("Expected null password", accountUserVO.getPassword() == null);
    assertTrue(accountUserVO.getObjectId().equals(DEPOSITOR_OBJECTID));
  }

  @Test
  public void getInvalidId() throws Exception {
    super.logMethodName();

    String token = null;

    token = loginAdmin();
    assertTrue(token != null);

    AccountUserDbVO accountUserVO = userAccountService.get("fgsdgsgdgadfgd", token);

    assertTrue(accountUserVO == null);
  }

  @Test
  public void login() {
    super.logMethodName();

    Principal principal = null;
    try {
      principal = userAccountService.login(DEPOSITOR_LOGIN_NAME, DEPOSITOR_PASSWORD);
    } catch (IngeTechnicalException | AuthenticationException e) {
      e.printStackTrace();
      fail("Caugh exception <" + e.getClass().getSimpleName() + ">");
    }
    assertTrue(principal.getJwToken() != null);
  }

  @Test(expected = AuthenticationException.class)
  public void loginWrongPassword() throws Exception {
    super.logMethodName();

    String username = DEPOSITOR_LOGIN_NAME;
    String password = "xxxxxx";

    userAccountService.login(username, password);
  }


  @Test(expected = AuthenticationException.class)
  public void loginInvalidUser() throws Exception {
    super.logMethodName();

    String username = "user_does_not_exists";
    String password = DEPOSITOR_PASSWORD;

    userAccountService.login(username, password);
  }

  @Test
  public void getDepositor() throws Exception {
    super.logMethodName();

    Principal principal = userAccountService.login(DEPOSITOR_LOGIN_NAME, DEPOSITOR_PASSWORD);

    assertTrue(principal != null);

    AccountUserDbVO accountUserVO = userAccountService.get(principal.getJwToken());

    assertTrue("Got no accountUserVO object", accountUserVO != null);
    assertTrue("Affiliation list size does not match.", accountUserVO.getAffiliation() != null);
    assertTrue("Wrong affiliation in list.", accountUserVO.getAffiliation().getObjectId().equalsIgnoreCase("ou_persistent25"));
  }

  @Test
  public void getModerator() throws Exception {
    super.logMethodName();

    Principal principal = userAccountService.login(MODERATOR_LOGIN_NAME, MODERATOR_PASSWORD);
    assertTrue(principal != null);

    AccountUserDbVO accountUserVO = userAccountService.get(principal.getJwToken());

    assertTrue("Got no accountUserVO object", accountUserVO != null);
    assertTrue("Affiliation list size does not match.", accountUserVO.getAffiliation() != null);
    assertTrue("Wrong affiliation in list.", accountUserVO.getAffiliation().getObjectId().equalsIgnoreCase("ou_persistent25"));
  }

  @Test
  public void removeGrants() throws Exception {
    super.logMethodName();

    Principal principal = userAccountService.login(ADMIN_LOGIN_NAME, ADMIN_PASSWORD);
    assertTrue(principal != null);

    AccountUserDbVO accountUserGrantsToBeRemoved = userAccountService.get(DEPOSITOR_OBJECTID, principal.getJwToken());

    List<GrantVO> grants = accountUserGrantsToBeRemoved.getGrantList();
    int sizeBeforeRemove = grants.size();
    assertTrue(sizeBeforeRemove > 0);

    userAccountService.removeGrants(DEPOSITOR_OBJECTID, accountUserGrantsToBeRemoved.getLastModificationDate(),
        new GrantVO[] {grants.get(0)}, principal.getJwToken());

    assertTrue(
        "Expected <" + (sizeBeforeRemove - 1) + "> grants - found <"
            + userAccountService.get(DEPOSITOR_OBJECTID, principal.getJwToken()).getGrantList().size() + ">",
        userAccountService.get(DEPOSITOR_OBJECTID, principal.getJwToken()).getGrantList().size() + 1 == sizeBeforeRemove);
  }

  @Test
  public void addGrants() throws Exception {
    super.logMethodName();

    String authenticationToken = loginAdmin();
    assertTrue(authenticationToken != null);

    AccountUserDbVO accountUserGrantsToBeAdded = userAccountService.get(MODERATOR_OBJECTID, authenticationToken);

    List<GrantVO> grants = accountUserGrantsToBeAdded.getGrantList();
    int sizeBeforeAdd = grants.size();
    assertTrue(sizeBeforeAdd >= 0);

    userAccountService.addGrants(MODERATOR_OBJECTID, accountUserGrantsToBeAdded.getLastModificationDate(),
        new GrantVO[] {new GrantVO("DEPOSITOR", "ctx_persistent3")}, authenticationToken);

    assertTrue(
        "Expected <" + (sizeBeforeAdd + 1) + "> grants - found <"
            + userAccountService.get(MODERATOR_OBJECTID, authenticationToken).getGrantList().size() + ">",
        userAccountService.get(MODERATOR_OBJECTID, authenticationToken).getGrantList().size() - 1 == sizeBeforeAdd);

  }

  @Test
  public void activateByAdmin() throws Exception {
    super.logMethodName();

    Principal principal = userAccountService.login(ADMIN_LOGIN_NAME, ADMIN_PASSWORD);
    assertTrue(principal != null);

    AccountUserDbVO accountUserToBeActivated = userAccountService.get(DEACTIVATED_OBJECTID, principal.getJwToken());

    accountUserToBeActivated =
        userAccountService.activate(DEACTIVATED_OBJECTID, accountUserToBeActivated.getLastModificationDate(), principal.getJwToken());

    assertTrue(accountUserToBeActivated.isActive());
  }

  @Test
  public void deactivateByAdmin() throws Exception {
    super.logMethodName();

    String authenticationToken = loginAdmin();
    assertTrue(authenticationToken != null);

    AccountUserDbVO accountUserToBeDeactivated = userAccountService.get(DEPOSITOR_OBJECTID, authenticationToken);

    accountUserToBeDeactivated =
        userAccountService.deactivate(DEPOSITOR_OBJECTID, accountUserToBeDeactivated.getLastModificationDate(), authenticationToken);

    assertFalse(accountUserToBeDeactivated.isActive());
  }

  @Test(expected = AuthorizationException.class)
  public void deactivateByOwner() throws Exception {
    super.logMethodName();

    String username = DEPOSITOR_LOGIN_NAME;
    String password = DEPOSITOR_PASSWORD;

    Principal principal = userAccountService.login(username, password);
    assertTrue(principal != null);

    AccountUserDbVO accountUserToBeDeactivated = userAccountService.get(DEPOSITOR_OBJECTID, principal.getJwToken());

    accountUserToBeDeactivated =
        userAccountService.deactivate(DEPOSITOR_OBJECTID, accountUserToBeDeactivated.getLastModificationDate(), principal.getJwToken());
  }

  @Test
  @Ignore
  public void changePasswordByUser() throws Exception {
    super.logMethodName();

    Principal principal = userAccountService.login(DEPOSITOR_LOGIN_NAME, DEPOSITOR_PASSWORD);
    assertTrue(principal != null);
    String newDepositorPassword = "myPassword";

    AccountUserDbVO accountUserPwdToBeChanged = userAccountService.get(DEPOSITOR_OBJECTID, principal.getJwToken());

    userAccountService.changePassword(DEPOSITOR_OBJECTID, accountUserPwdToBeChanged.getLastModificationDate(), newDepositorPassword, true,
        principal.getJwToken());

    assertTrue(userAccountService.login("test_depositor", newDepositorPassword) != null);
  }

  @Test
  @Ignore
  public void changePasswordByAdmin() throws Exception {
    super.logMethodName();

    Principal principal = userAccountService.login(ADMIN_LOGIN_NAME, ADMIN_PASSWORD);
    assertTrue(principal != null);

    AccountUserDbVO accountUserPwdToBeChanged = userAccountService.get(DEPOSITOR_OBJECTID, principal.getJwToken());

    String veryNewDepositorPassword = "veryNewDepositorPassword";
    userAccountService.changePassword(DEPOSITOR_OBJECTID, accountUserPwdToBeChanged.getLastModificationDate(), veryNewDepositorPassword,
        true, principal.getJwToken());

    Principal principalNew = userAccountService.login(DEPOSITOR_LOGIN_NAME, veryNewDepositorPassword);

    assertTrue(principalNew != null);
  }

}
