package de.mpg.mpdl.inge.pubman.web.statistics;

import de.mpg.mpdl.inge.pubman.web.appbase.FacesBean;

@SuppressWarnings("serial")
public class StatisticSessionBean extends FacesBean {

  public static String BEAN_NAME = "StatisticSessionBean";

  // private String uuid;

  public StatisticSessionBean() {}

  /*
   * private void logNewUser() { try { InitialContext ic = new InitialContext(); StatisticLogger sl
   * = (StatisticLogger) ic.lookup(StatisticLogger.SERVICE_NAME); sl.logNewUser(getSessionId(),
   * getIP(), getUserAgent(), getReferer(), "pubman", AdminHelper.getAdminUserHandle()); } catch
   * (Exception e) { logger.error("Could not log new user.", e); }
   * 
   * }
   */
}
