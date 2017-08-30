package de.mpg.mpdl.inge.service.pubman.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;
import de.mpg.mpdl.inge.service.spring.AppConfigPubmanLogicTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfigPubmanLogicTest.class})
public class ContextServiceTest {

  @Autowired
  ContextService contextService;

  @Autowired
  UserAccountService userAccountService;

  private static final String ADMIN_LOGIN = "admin";
  private static final String ADMIN_PASSWORD = "tseT";

  @Test
  public void objects() {
    assertTrue(contextService != null);
  }

  @Test
  @Ignore
  public void open() throws Exception {
    String authenticationToken = userAccountService.login(ADMIN_LOGIN, ADMIN_PASSWORD);
    assertTrue(authenticationToken != null);

    ContextVO contextVO = contextService.get("ctx_persistent3", authenticationToken);
  }

  @Test
  @Ignore
  public void close() {
    fail("Not yet implemented");
  }

}
