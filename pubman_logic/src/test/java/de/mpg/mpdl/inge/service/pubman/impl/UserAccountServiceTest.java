package de.mpg.mpdl.inge.service.pubman.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;
import de.mpg.mpdl.inge.service.spring.AppConfigPubmanLogic;
import de.mpg.mpdl.inge.util.PropertyReader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfigPubmanLogic.class})
public class UserAccountServiceTest {

  @Autowired
  UserAccountService userAccountService;

  @Test
  public void testObjects() {
    assertTrue(userAccountService != null);
  }

  @Test
  public void testLogin() {
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
  public void testLoginWrongPassword() throws Exception {
    String username = PropertyReader.getProperty("inge.depositor.loginname");
    String password = "xxxxxx";

    userAccountService.login(username, password);
  }


  @Test(expected = AuthenticationException.class)
  public void testLoginInvalidUser() throws Exception {
    String username = "user_does_not_exists";
    String password = PropertyReader.getProperty("inge.depositor.password");

    userAccountService.login(username, password);
  }

  @Test
  public void testGet() throws Exception {
    String authenticationToken =
        userAccountService.login(PropertyReader.getProperty("inge.depositor.loginname"),
            PropertyReader.getProperty("inge.depositor.password"));

    assertTrue(authenticationToken != null);

    AccountUserVO accountUserVO = null;
    accountUserVO = userAccountService.get(authenticationToken);

    assertTrue("Got no accountUserVO object", accountUserVO != null);
    assertTrue("Affiliation list size does not match.", accountUserVO.getAffiliations().size() == 1);
    assertTrue("Wrong affiliation in list.", accountUserVO.getAffiliations().get(0).getObjectId()
        .equalsIgnoreCase("ou_persistent25"));



  }



}
