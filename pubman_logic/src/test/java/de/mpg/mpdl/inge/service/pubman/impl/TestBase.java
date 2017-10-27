package de.mpg.mpdl.inge.service.pubman.impl;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.springframework.beans.factory.annotation.Autowired;

import de.mpg.mpdl.inge.service.pubman.UserAccountService;



public class TestBase {

  protected static final String ADMIN_LOGIN = "admin";
  protected static final String ADMIN_PASSWORD = "tseT";


  protected static final String USER_OBJECTID_DEPOSITOR = "user_3000056";
  protected static final String USER_OBJECTID_MODERATOR = "user_3000057";
  protected static final String DEPOSITOR_PASSWORD = "tseT";

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
        Thread.currentThread().sleep(20000);

        havingSlept = true;
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    logger.info("--------------------------------------- Starting " + name.getMethodName()
        + "---------------------------------------");

  }
}
