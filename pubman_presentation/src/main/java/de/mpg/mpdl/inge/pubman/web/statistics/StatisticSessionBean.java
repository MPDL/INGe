package de.mpg.mpdl.inge.pubman.web.statistics;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.xmltransforming.StatisticLogger;
import de.mpg.mpdl.inge.pubman.web.appbase.FacesBean;
import de.mpg.mpdl.inge.util.AdminHelper;


public class StatisticSessionBean extends FacesBean {

  public static String BEAN_NAME = "StatisticSessionBean";
  private String uuid;

  // private static Logger logger = Logger.getLogger(StatisticSessionBean.class);


  public StatisticSessionBean() {
    // logNewUser();


  }


  /*
   * private void logNewUser() { try { InitialContext ic = new InitialContext(); StatisticLogger sl
   * = (StatisticLogger) ic.lookup(StatisticLogger.SERVICE_NAME); sl.logNewUser(getSessionId(),
   * getIP(), getUserAgent(), getReferer(), "pubman", AdminHelper.getAdminUserHandle()); } catch
   * (Exception e) { logger.error("Could not log new user.", e); }
   * 
   * }
   */


}
