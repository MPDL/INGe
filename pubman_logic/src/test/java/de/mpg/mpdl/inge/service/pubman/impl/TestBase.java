package de.mpg.mpdl.inge.service.pubman.impl;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.springframework.beans.factory.annotation.Autowired;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;



public class TestBase {

  protected static final String ADMIN_LOGIN_NAME = "admin";
  protected static final String ADMIN_PASSWORD = "tseT";


  protected static final String DEPOSITOR_OBJECTID = "user_3000056";
  protected static final String DEPOSITOR_LOGIN_NAME = "test_depositor";
  protected static final String DEPOSITOR_PASSWORD = "tseT";

  protected static final String MODERATOR_OBJECTID = "user_3000057";
  protected static final String MODERATOR_LOGIN_NAME = "test_moderator";
  protected static final String MODERATOR_PASSWORD = "tseT";

  protected static final String DEACTIVATED_OBJECTID = "user_3000166";

  protected static final String ORG_OBJECTID_13 = "ou_persistent13";
  protected static final String ORG_OBJECTID_25 = "ou_persistent25";
  protected static final String ORG_OBJECTID_40048 = "ou_40048";



  protected static Logger logger = Logger.getLogger(TestBase.class);

  @Rule
  public final TestName name = new TestName();

  @Autowired
  protected UserAccountService userAccountService;


  /*
   * @BeforeClass public static void setUp() throws Exception { FileUtils.deleteDirectory(new
   * File("./target/es")); logger.info("Removed successfully previous Elasticsearch test index <" +
   * (new File("./target/es")).getCanonicalPath() + ">"); }
   */

  static {
    try {
      FileUtils.deleteDirectory(new File("./target/es"));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    try {
      logger.info("Removed successfully previous Elasticsearch test index <"
          + (new File("./target/es")).getCanonicalPath() + ">");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  protected static boolean havingSlept = false;

  protected void logMethodName() {

    if (!havingSlept) {
      try {
        Thread.currentThread().sleep(15000);

        havingSlept = true;
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    logger.info("--------------------------------------- Starting " + name.getMethodName()
        + "---------------------------------------");

  }

  protected String loginDepositor() {

    String token = null;
    try {
      token = userAccountService.login(DEPOSITOR_LOGIN_NAME, DEPOSITOR_PASSWORD);
    } catch (IngeTechnicalException | AuthenticationException | AuthorizationException
        | IngeApplicationException e) {
      e.printStackTrace();
      fail("Caugh exception <" + e.getClass().getSimpleName() + ">");
    }
    return token;
  }

  protected String loginModerator() {

    String token = null;
    try {
      token = userAccountService.login(MODERATOR_LOGIN_NAME, MODERATOR_PASSWORD);
    } catch (IngeTechnicalException | AuthenticationException | AuthorizationException
        | IngeApplicationException e) {
      e.printStackTrace();
      fail("Caugh exception <" + e.getClass().getSimpleName() + ">");
    }
    return token;
  }

  protected String loginAdmin() {

    String token = null;
    try {
      token = userAccountService.login(ADMIN_LOGIN_NAME, ADMIN_PASSWORD);
    } catch (IngeTechnicalException | AuthenticationException | AuthorizationException
        | IngeApplicationException e) {
      e.printStackTrace();
      fail("Caugh exception <" + e.getClass().getSimpleName() + ">");
    }
    return token;
  }
}