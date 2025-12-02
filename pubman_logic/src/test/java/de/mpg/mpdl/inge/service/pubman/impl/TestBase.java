package de.mpg.mpdl.inge.service.pubman.impl;

import static org.junit.Assert.fail;

import de.mpg.mpdl.inge.util.PropertyReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.springframework.beans.factory.annotation.Autowired;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.aa.Principal;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;
import org.testcontainers.elasticsearch.ElasticsearchContainer;


public class TestBase {

  protected static final String ADMIN_LOGIN_NAME = "admin";
  protected static final String ADMIN_PASSWORD = "myPassword";


  protected static final String DEPOSITOR_OBJECTID = "user_3000056";
  protected static final String DEPOSITOR_LOGIN_NAME = "test_depositor";
  protected static final String DEPOSITOR_PASSWORD = "myPassword";

  protected static final String MODERATOR_OBJECTID = "user_3000057";
  protected static final String MODERATOR_LOGIN_NAME = "test_moderator";
  protected static final String MODERATOR_PASSWORD = "myPassword";

  protected static final String DEACTIVATED_OBJECTID = "user_3000166";

  protected static final String ORG_OBJECTID_13 = "ou_persistent13";
  protected static final String ORG_OBJECTID_25 = "ou_persistent25";
  protected static final String ORG_OBJECTID_40048 = "ou_40048";

  static ElasticsearchContainer elasticsearchContainer =
      new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:9.2.1").withEnv("xpack.security.enabled", "false");
  static {
    elasticsearchContainer.start();
    PropertyReader.getProperties().setProperty(PropertyReader.INGE_ES_REST_HOST_PORT,
        "http://" + elasticsearchContainer.getHttpHostAddress());
  }

  private static final Logger logger = LogManager.getLogger(TestBase.class);

  @Rule
  public final TestName name = new TestName();

  @Autowired
  protected UserAccountService userAccountService;

  protected static boolean havingSlept = false;

  protected void logMethodName() {

    if (!havingSlept) {
      try {
        Thread.currentThread();
        Thread.sleep(15000);

        havingSlept = true;
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    logger.info("--------------------------------------- Starting " + this.getClass().getSimpleName() + "." + name.getMethodName()
        + "---------------------------------------");

  }

  protected String loginDepositor() {

    Principal principal = null;
    try {
      principal = userAccountService.login(DEPOSITOR_LOGIN_NAME, DEPOSITOR_PASSWORD);
    } catch (IngeTechnicalException | AuthenticationException e) {
      e.printStackTrace();
      fail("Caugh exception <" + e.getClass().getSimpleName() + ">");
    }
    return principal.getJwToken();
  }

  protected String loginModerator() {

    Principal principal = null;
    try {
      principal = userAccountService.login(MODERATOR_LOGIN_NAME, MODERATOR_PASSWORD);
    } catch (IngeTechnicalException | AuthenticationException e) {
      e.printStackTrace();
      fail("Caugh exception <" + e.getClass().getSimpleName() + ">");
    }
    return principal.getJwToken();
  }

  protected String loginAdmin() {

    Principal principal = null;
    try {
      principal = userAccountService.login(ADMIN_LOGIN_NAME, ADMIN_PASSWORD);
    } catch (IngeTechnicalException | AuthenticationException e) {
      e.printStackTrace();
      fail("Caugh exception <" + e.getClass().getSimpleName() + ">");
    }
    return principal.getJwToken();
  }
}
