package de.mpg.mpdl.inge.service.pubman.impl;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;
import de.mpg.mpdl.inge.service.spring.AppConfigPubmanLogic;
import de.mpg.mpdl.inge.util.PropertyReader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfigPubmanLogic.class})
public class UserAccountServiceTest {

  private static final String ADMIN_LOGIN = "admin";
  private static final String ADMIN_PASSWORD = "tseT";
  private static final String USER_OBJECTID_MODERATOR = "user_3000165";
  private static final String USER_OBJECTID_DEPOSITOR = "user_3000056";
  private static final String USER_OBJECTID_DEACTIVATED = "user_3000056";
  @Autowired
  UserAccountService userAccountService;

  @Test
  public void objects() {
    assertTrue(userAccountService != null);
  }

  @Test
  public void login() {
    String username = PropertyReader.getProperty("inge.depositor.loginname");
    String password = PropertyReader.getProperty("inge.depositor.password");
    String token = null;
    try {
      token = userAccountService.login(username, password);
    } catch (IngeTechnicalException | AuthenticationException | AuthorizationException
        | IngeApplicationException e) {
      e.printStackTrace();
      fail("Caugh exception <" + e.getClass().getSimpleName() + ">");
    }
    assertTrue(token != null);
  }

  @Test(expected = AuthenticationException.class)
  public void loginWrongPassword() throws Exception {
    String username = PropertyReader.getProperty("inge.depositor.loginname");
    String password = "xxxxxx";

    userAccountService.login(username, password);
  }


  @Test(expected = AuthenticationException.class)
  public void loginInvalidUser() throws Exception {
    String username = "user_does_not_exists";
    String password = PropertyReader.getProperty("inge.depositor.password");

    userAccountService.login(username, password);
  }

  @Test
  public void get() throws Exception {
    String authenticationToken =
        userAccountService.login(PropertyReader.getProperty("inge.depositor.loginname"),
            PropertyReader.getProperty("inge.depositor.password"));
    assertTrue(authenticationToken != null);

    AccountUserVO accountUserVO = userAccountService.get(authenticationToken);

    assertTrue("Got no accountUserVO object", accountUserVO != null);
    assertTrue("Affiliation list size does not match.", accountUserVO.getAffiliations().size() == 1);
    assertTrue("Wrong affiliation in list.", accountUserVO.getAffiliations().get(0).getObjectId()
        .equalsIgnoreCase("ou_persistent25"));
  }

  @Test
  public void removeGrants() throws Exception {
    String authenticationToken = userAccountService.login(ADMIN_LOGIN, ADMIN_PASSWORD);
    assertTrue(authenticationToken != null);

    AccountUserVO accountUserGrantsToBeRemoved =
        userAccountService.get(USER_OBJECTID_DEPOSITOR, authenticationToken);

    List<GrantVO> grants = accountUserGrantsToBeRemoved.getGrants();
    int sizeBeforeRemove = grants.size();
    assertTrue(sizeBeforeRemove > 0);

    userAccountService.removeGrants(USER_OBJECTID_DEPOSITOR,
        accountUserGrantsToBeRemoved.getLastModificationDate(), new GrantVO[] {grants.get(0)},
        authenticationToken);

    assertTrue("Expected <" + (sizeBeforeRemove - 1) + "> grants - found <"
        + userAccountService.get(USER_OBJECTID_DEPOSITOR, authenticationToken).getGrants().size()
        + ">", userAccountService.get(USER_OBJECTID_DEPOSITOR, authenticationToken).getGrants()
        .size() + 1 == sizeBeforeRemove);
  }

  @Test
  public void addGrants() throws Exception {
    String authenticationToken = userAccountService.login(ADMIN_LOGIN, ADMIN_PASSWORD);
    assertTrue(authenticationToken != null);

    AccountUserVO accountUserGrantsToBeAdded =
        userAccountService.get(USER_OBJECTID_MODERATOR, authenticationToken);

    List<GrantVO> grants = accountUserGrantsToBeAdded.getGrants();
    int sizeBeforeAdd = grants.size();
    assertTrue(sizeBeforeAdd >= 0);

    userAccountService.addGrants(USER_OBJECTID_MODERATOR, accountUserGrantsToBeAdded
        .getLastModificationDate(), new GrantVO[] {new GrantVO("MODERATOR", "ctx_persistent3")},
        authenticationToken);

    assertTrue(
        "Expected <"
            + (sizeBeforeAdd + 1)
            + "> grants - found <"
            + userAccountService.get(USER_OBJECTID_MODERATOR, authenticationToken).getGrants()
                .size() + ">", userAccountService.get(USER_OBJECTID_MODERATOR, authenticationToken)
            .getGrants().size() - 1 == sizeBeforeAdd);
  }

  @Test
  public void changePasswordByUser() throws Exception {
    String authenticationToken =
        userAccountService.login(PropertyReader.getProperty("inge.depositor.loginname"),
            PropertyReader.getProperty("inge.depositor.password"));
    assertTrue(authenticationToken != null);

    AccountUserVO accountUserPwdToBeChanged =
        userAccountService.get(USER_OBJECTID_DEPOSITOR, authenticationToken);

    userAccountService.changePassword(USER_OBJECTID_DEPOSITOR,
        accountUserPwdToBeChanged.getLastModificationDate(), "newPassword", authenticationToken);

    assertTrue(userAccountService.login("test_depositor", "newPassword") != null);
  }

  @Test
  public void changePasswordByAdmin() throws Exception {
    String authenticationToken = userAccountService.login(ADMIN_LOGIN, ADMIN_PASSWORD);
    assertTrue(authenticationToken != null);

    AccountUserVO accountUserPwdToBeChanged =
        userAccountService.get(USER_OBJECTID_DEPOSITOR, authenticationToken);

    userAccountService.changePassword(USER_OBJECTID_DEPOSITOR,
        accountUserPwdToBeChanged.getLastModificationDate(), "newPassword", authenticationToken);

    String newPassword =
        userAccountService.get(USER_OBJECTID_DEPOSITOR, authenticationToken).getPassword();

    assertTrue(newPassword != null);
  }

  @Test
  public void deactivateByAdmin() throws Exception {
    String authenticationToken = userAccountService.login(ADMIN_LOGIN, ADMIN_PASSWORD);
    assertTrue(authenticationToken != null);

    AccountUserVO accountUserToBeDeactivated =
        userAccountService.get(USER_OBJECTID_DEPOSITOR, authenticationToken);

    userAccountService.deactivate(USER_OBJECTID_DEPOSITOR,
        accountUserToBeDeactivated.getLastModificationDate(), authenticationToken);

    assertFalse(userAccountService.get(USER_OBJECTID_DEPOSITOR, authenticationToken).isActive());

  }

  @Test
  public void deactivateByOwner() throws Exception {

    String username = PropertyReader.getProperty("inge.depositor.loginname");
    String password = PropertyReader.getProperty("inge.depositor.password");

    String authenticationToken = userAccountService.login(username, password);
    assertTrue(authenticationToken != null);

    AccountUserVO accountUserToBeDeactivated =
        userAccountService.get(USER_OBJECTID_DEPOSITOR, authenticationToken);

    userAccountService.deactivate(USER_OBJECTID_DEPOSITOR,
        accountUserToBeDeactivated.getLastModificationDate(), authenticationToken);

    assertFalse(userAccountService.get(USER_OBJECTID_DEPOSITOR, authenticationToken).isActive());
  }

  @Test
  public void activateByAdmin() throws Exception {
    String authenticationToken = userAccountService.login(ADMIN_LOGIN, ADMIN_PASSWORD);
    assertTrue(authenticationToken != null);

    AccountUserVO accountUserToBeActivated =
        userAccountService.get(USER_OBJECTID_DEACTIVATED, authenticationToken);

    userAccountService.deactivate(USER_OBJECTID_DEACTIVATED,
        accountUserToBeActivated.getLastModificationDate(), authenticationToken);

    assertTrue(userAccountService.get(USER_OBJECTID_DEACTIVATED, authenticationToken).isActive());

  }

}
