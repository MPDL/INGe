package de.mpg.escidoc.pubman.statistics;

import java.util.UUID;

import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.services.common.StatisticLogger;
import de.mpg.escidoc.services.pubman.PubItemSimpleStatistics;
import de.mpg.escidoc.services.pubman.statistics.SimpleStatistics;
import de.mpg.escidoc.services.pubman.util.AdminHelper;


public class StatisticSessionBean extends FacesBean
{
    
    public static String BEAN_NAME = "StatisticSessionBean";
    private String uuid;
    
    private Logger logger = Logger.getLogger(StatisticSessionBean.class);
    
    
    public StatisticSessionBean()
    {
        logNewUser();
        
        
    }


    private void logNewUser()
    {
        try
        {
            InitialContext ic = new InitialContext();
            StatisticLogger sl = (StatisticLogger) ic.lookup(StatisticLogger.SERVICE_NAME);
            HttpServletRequest httpRequ = (HttpServletRequest)getExternalContext().getRequest();
            HttpSession session = (HttpSession)getExternalContext().getSession(false);
            sl.logNewUser(session.getId(), httpRequ.getRemoteAddr(), httpRequ.getHeader("User-Agent"), httpRequ.getHeader("Referer"), "pubman", AdminHelper.getAdminUserHandle());
        }
        catch (Exception e)
        {
            logger.error("Could not log new user.", e);
        }
        
        
        
        
        
        
        
        
    }
    
    
}
