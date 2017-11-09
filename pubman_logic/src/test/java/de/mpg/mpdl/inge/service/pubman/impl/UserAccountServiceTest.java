package de.mpg.mpdl.inge.service.pubman.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.spring.AppConfigPubmanLogicTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfigPubmanLogicTest.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserAccountServiceTest extends TestBase {

  // password may change during the tests depending on the test order
  private static String actualDepositorPassword = "tseT";

  @Test
  public void objects() {
    assertTrue(userAccountService != null);
  }

  @Test
  public void login() {
    String token = null;
    try {
      token = userAccountService.login(DEPOSITOR_USER_NAME, actualDepositorPassword);
    } catch (IngeTechnicalException | AuthenticationException | AuthorizationException
        | IngeApplicationException e) {
      e.printStackTrace();
      fail("Caugh exception <" + e.getClass().getSimpleName() + ">");
    }
    assertTrue(token != null);
  }

  @Test(expected = AuthenticationException.class)
  public void loginWrongPassword() throws Exception {
    String username = DEPOSITOR_USER_NAME;
    String password = "xxxxxx";

    userAccountService.login(username, password);
  }


  @Test(expected = AuthenticationException.class)
  public void loginInvalidUser() throws Exception {
    String username = "user_does_not_exists";
    String password = DEPOSITOR_PASSWORD;

    userAccountService.login(username, password);
  }

  @Test
  public void getDepositor() throws Exception {
    String authenticationToken =
        userAccountService.login(DEPOSITOR_USER_NAME, actualDepositorPassword);

    assertTrue(authenticationToken != null);

    AccountUserVO accountUserVO = userAccountService.get(authenticationToken);

    assertTrue("Got no accountUserVO object", accountUserVO != null);
    assertTrue("Affiliation list size does not match.", accountUserVO.getAffiliations().size() == 1);
    assertTrue("Wrong affiliation in list.", accountUserVO.getAffiliations().get(0).getObjectId()
        .equalsIgnoreCase("ou_persistent25"));
  }

  @Test
  public void getModerator() throws Exception {
    String authenticationToken = userAccountService.login(MODERATOR_USER_NAME, MODERATOR_PASSWORD);
    assertTrue(authenticationToken != null);

    AccountUserVO accountUserVO = userAccountService.get(authenticationToken);

    assertTrue("Got no accountUserVO object", accountUserVO != null);
    assertTrue("Affiliation list size does not match.", accountUserVO.getAffiliations().size() == 1);
    assertTrue("Wrong affiliation in list.", accountUserVO.getAffiliations().get(0).getObjectId()
        .equalsIgnoreCase("ou_persistent25"));
  }

  @Test
  public void removeGrants() throws Exception {
    String authenticationToken = userAccountService.login(ADMIN_USER_NAME, ADMIN_PASSWORD);
    assertTrue(authenticationToken != null);

    AccountUserVO accountUserGrantsToBeRemoved =
        userAccountService.get(DEPOSITOR_OBJECTID, authenticationToken);

    List<GrantVO> grants = accountUserGrantsToBeRemoved.getGrants();
    int sizeBeforeRemove = grants.size();
    assertTrue(sizeBeforeRemove > 0);

    userAccountService.removeGrants(DEPOSITOR_OBJECTID,
        accountUserGrantsToBeRemoved.getLastModificationDate(), new GrantVO[] {grants.get(0)},
        authenticationToken);

    assertTrue(
        "Expected <" + (sizeBeforeRemove - 1) + "> grants - found <"
            + userAccountService.get(DEPOSITOR_OBJECTID, authenticationToken).getGrants().size()
            + ">",
        userAccountService.get(DEPOSITOR_OBJECTID, authenticationToken).getGrants().size() + 1 == sizeBeforeRemove);
  }

  @Test
  public void addGrants() throws Exception {
    String authenticationToken = loginAdmin();
    assertTrue(authenticationToken != null);

    AccountUserVO accountUserGrantsToBeAdded =
        userAccountService.get(MODERATOR_OBJECTID, authenticationToken);

    List<GrantVO> grants = accountUserGrantsToBeAdded.getGrants();
    int sizeBeforeAdd = grants.size();
    assertTrue(sizeBeforeAdd >= 0);

    userAccountService.addGrants(MODERATOR_OBJECTID, accountUserGrantsToBeAdded
        .getLastModificationDate(), new GrantVO[] {new GrantVO("DEPOSITOR", "ctx_persistent3")},
        authenticationToken);

    assertTrue(
        "Expected <" + (sizeBeforeAdd + 1) + "> grants - found <"
            + userAccountService.get(MODERATOR_OBJECTID, authenticationToken).getGrants().size()
            + ">", userAccountService.get(MODERATOR_OBJECTID, authenticationToken).getGrants()
            .size() - 1 == sizeBeforeAdd);
  }

  @Test
  public void activateByAdmin() throws Exception {
    String authenticationToken = userAccountService.login(ADMIN_USER_NAME, ADMIN_PASSWORD);
    assertTrue(authenticationToken != null);

    AccountUserVO accountUserToBeActivated =
        userAccountService.get(DEACTIVATED_OBJECTID, authenticationToken);

    accountUserToBeActivated =
        userAccountService.activate(DEACTIVATED_OBJECTID,
            accountUserToBeActivated.getLastModificationDate(), authenticationToken);

    assertTrue(accountUserToBeActivated.isActive());
  }

  @Test
  public void deactivateByAdmin() throws Exception {
    String authenticationToken = loginAdmin();
    assertTrue(authenticationToken != null);

    AccountUserVO accountUserToBeDeactivated =
        userAccountService.get(DEPOSITOR_OBJECTID, authenticationToken);

    accountUserToBeDeactivated =
        userAccountService.deactivate(DEPOSITOR_OBJECTID,
            accountUserToBeDeactivated.getLastModificationDate(), authenticationToken);

    assertFalse(accountUserToBeDeactivated.isActive());
  }

  @Test(expected = AuthorizationException.class)
  public void deactivateByOwner() throws Exception {

    String username = DEPOSITOR_USER_NAME;
    String password = actualDepositorPassword;

    String authenticationToken = userAccountService.login(username, password);
    assertTrue(authenticationToken != null);

    AccountUserVO accountUserToBeDeactivated =
        userAccountService.get(DEPOSITOR_OBJECTID, authenticationToken);

    accountUserToBeDeactivated =
        userAccountService.deactivate(DEPOSITOR_OBJECTID,
            accountUserToBeDeactivated.getLastModificationDate(), authenticationToken);
  }

  @Test
  public void changePasswordByUser() throws Exception {
    String authenticationToken =
        userAccountService.login(DEPOSITOR_USER_NAME, actualDepositorPassword);
    assertTrue(authenticationToken != null);
    actualDepositorPassword = "newPassword";

    AccountUserVO accountUserPwdToBeChanged =
        userAccountService.get(DEPOSITOR_OBJECTID, authenticationToken);

    userAccountService.changePassword(DEPOSITOR_OBJECTID,
        accountUserPwdToBeChanged.getLastModificationDate(), actualDepositorPassword,
        authenticationToken);

    assertTrue(userAccountService.login("test_depositor", actualDepositorPassword) != null);
  }

  @Test
  public void changePasswordByAdmin() throws Exception {
    String authenticationToken = userAccountService.login(ADMIN_USER_NAME, ADMIN_PASSWORD);
    assertTrue(authenticationToken != null);

    AccountUserVO accountUserPwdToBeChanged =
        userAccountService.get(DEPOSITOR_OBJECTID, authenticationToken);

    actualDepositorPassword = "anotherPassword";
    userAccountService.changePassword(DEPOSITOR_OBJECTID,
        accountUserPwdToBeChanged.getLastModificationDate(), actualDepositorPassword,
        authenticationToken);

    String userAuthenticationToken =
        userAccountService.login(DEPOSITOR_USER_NAME, actualDepositorPassword);

    assertTrue(userAuthenticationToken != null);
  }

}
