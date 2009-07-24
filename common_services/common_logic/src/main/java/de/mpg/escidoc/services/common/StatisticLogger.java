package de.mpg.escidoc.services.common;

import java.util.List;

import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO.ItemAction;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordParamVO;

/**
 * 
 * TODO Description
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public interface StatisticLogger
{
    /**
     * The name of the EJB service.
     */
    public static String SERVICE_NAME = "ejb/de/mpg/escidoc/services/common/StatisticLogger";
    
    public void logNewUser(String sessionId, String ip, String userAgent, String referer, String solutionId, String userHandle) throws Exception;
    
    public void logSearch(String sessionId, String ip, String keywords, String cql, boolean loggedIn, String solutionId, String userHandle) throws Exception;
    
    public void logItemAction(String sessionId, String ip, String userAgent, ItemVO item, ItemAction action,  boolean loggedIn, String referer, String solutionId, List<StatisticReportRecordParamVO> additionalParams, String userHandle) throws Exception;
    
    public void logAuthorAction(String sessionId, String ip, String authorId, boolean loggedIn, String referer, String solutionId, List<StatisticReportRecordParamVO> additionalParams, String userHandle) throws Exception;
    
    public void logOrgAction(String sessionId, String ip, String orgId, boolean loggedIn, String referer, String solutionId, List<StatisticReportRecordParamVO> additionalParams, String userHandle) throws Exception;
}
