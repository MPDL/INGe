/*
 *
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License"). You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */
/*
 * Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
package de.mpg.escidoc.services.common.statistics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.RemoteBinding;

import com.maxmind.geoip.Country;
import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;
import com.maxmind.geoip.Region;

import de.mpg.escidoc.services.common.StatisticLogger;
import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO.ItemAction;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticRecordVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordParamVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordStringParamValueVO;
import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * TODO Description
 * 
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@Remote
@RemoteBinding(jndiBinding = StatisticLogger.SERVICE_NAME)
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class StatisticLoggerBean implements StatisticLogger
{
    private final static String PROPERTY_GEOIP_FILE_LOCATION = "escidoc.statistics.max_mind_geo_lite_city_db.location";
    private static LookupService ipLookUpService = null;
    private static boolean geoIpCountryOnly = false;
    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(StatisticLoggerBean.class);

    public StatisticLoggerBean()
    {
        // Check if there is already a copy of geo ip country db
        File geopIpCopyFile = new File(System.getProperty("java.io.tmpdir"), "GeoIPCopy.dat");
        if (ipLookUpService == null || (geoIpCountryOnly && !geopIpCopyFile.exists()))
        {
            try
            {
                //First try to find GeoIpCityLite db
                String dir = PropertyReader.getProperty(PROPERTY_GEOIP_FILE_LOCATION);
                ipLookUpService = new LookupService(dir, LookupService.GEOIP_MEMORY_CACHE);
                logger.info("Geo IP City db found at: " + dir);
                logger.info("Geo IP City functionality is activated.");
                geoIpCountryOnly = false;
            }
            catch (Exception e)
            {
                //if GeoIpCityLite db is not found, continue witg GeoIpLite db (countries only)
                getStandardIPLookupService(geopIpCopyFile);
            }
            
        }
    }

    private void getStandardIPLookupService(File geopIpCopyFile)
    {
        // Make a copy of the country db file, because GeoIP API requires a file object that cannot be
        // created from a file within a jar
        try
        {
            logger.info("Trying to save GeoIp db file to: " + geopIpCopyFile.getPath());
            if (geopIpCopyFile.exists())
            {
                geopIpCopyFile.delete();
                geopIpCopyFile.createNewFile();
            }
            geopIpCopyFile.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(geopIpCopyFile);
            InputStream is = ResourceUtil.getResourceAsStream("statistics/GeoIP.dat");
            byte[] buf = new byte[1024];
            int i = 0;
            while ((i = is.read(buf)) != -1)
            {
                fos.write(buf, 0, i);
            }
            fos.flush();
            fos.close();
            geoIpCountryOnly = true;
            ipLookUpService = new LookupService(geopIpCopyFile, LookupService.GEOIP_MEMORY_CACHE);
            logger.info("Geo ip db file saved to: " + geopIpCopyFile.getPath());
            logger.info("Geo IP Country functionality is activated. For city functionality please provide GeoLiteCity.db and add location to pubman.properties.");
        }
        catch (Exception e)
        {
            logger.error("Cannot find any Geo IP lookup database. Statistic data is stored with unknown geo locations.", e);
        }
    }

    public void logNewUser(String sessionId, String ip, String userAgent, String referer, String solutionId,
            String userHandle) throws Exception
    {
        logger.debug("Logging new user for statistics...");
        StatisticRecordVO statisticRecord = new StatisticRecordVO();
        statisticRecord.setScope("2");
        List<StatisticReportRecordParamVO> paramList = new ArrayList<StatisticReportRecordParamVO>();
        statisticRecord.setParamList(paramList);
        StatisticReportRecordParamVO actionParam = new StatisticReportRecordParamVO();
        actionParam.setName("action");
        actionParam.setParamValue(new StatisticReportRecordStringParamValueVO("new-user"));
        paramList.add(actionParam);
        paramList.addAll(getUserStats(sessionId, ip));
        StatisticReportRecordParamVO refererParam = new StatisticReportRecordParamVO();
        refererParam.setName("referer");
        refererParam.setParamValue(new StatisticReportRecordStringParamValueVO(referer));
        paramList.add(refererParam);
        StatisticReportRecordParamVO solutionParam = new StatisticReportRecordParamVO();
        solutionParam.setName("solution");
        solutionParam.setParamValue(new StatisticReportRecordStringParamValueVO(solutionId));
        paramList.add(solutionParam);
        StatisticReportRecordParamVO userAgentParam = new StatisticReportRecordParamVO();
        userAgentParam.setName("userAgent");
        userAgentParam.setParamValue(new StatisticReportRecordStringParamValueVO(userAgent));
        paramList.add(userAgentParam);
        String[] browsers = UserAgentAnalyser.getBrowser(userAgent);
        StatisticReportRecordParamVO browserParam = new StatisticReportRecordParamVO();
        browserParam.setName("browser");
        if (browsers.length > 0)
        {
            browserParam.setParamValue(new StatisticReportRecordStringParamValueVO(browsers[browsers.length - 1]));
        }
        else
        {
            browserParam.setParamValue(new StatisticReportRecordStringParamValueVO("unknown"));
        }
        paramList.add(browserParam);
        String[] os = UserAgentAnalyser.getOS(userAgent);
        StatisticReportRecordParamVO osParam = new StatisticReportRecordParamVO();
        osParam.setName("os");
        if (os.length > 0)
        {
            osParam.setParamValue(new StatisticReportRecordStringParamValueVO(os[os.length - 1]));
        }
        else
        {
            osParam.setParamValue(new StatisticReportRecordStringParamValueVO("unknown"));
        }
        paramList.add(osParam);
        statisticRecord.createInCoreservice(userHandle);
    }

    private List<StatisticReportRecordParamVO> getUserStats(String sessionId, String ip) throws Exception
    {
        List<StatisticReportRecordParamVO> paramList = new ArrayList<StatisticReportRecordParamVO>();
        StatisticReportRecordParamVO sessionIdParam = new StatisticReportRecordParamVO();
        sessionIdParam.setName("sessionId");
        sessionIdParam.setParamValue(new StatisticReportRecordStringParamValueVO(sessionId));
        paramList.add(sessionIdParam);
        StatisticReportRecordParamVO ipParam = new StatisticReportRecordParamVO();
        ipParam.setName("ip");
        ipParam.setParamValue(new StatisticReportRecordStringParamValueVO(ip));
        paramList.add(ipParam);
        String country = "unknown";
        String region = "unknown";
        String city = "unknown";
        String longitude = "unknown";
        String latitude = "unknown";
        // Location object can only be used with GeoIpLiteCity, not with GeoIpLite
        Location loc = null;
        if (ipLookUpService != null && !geoIpCountryOnly)
        {
            loc = ipLookUpService.getLocation(ip);
            // country = "--";
            // region = "--";
            // city = "--";
            // longitude = "--";
            // latitude = "--";
        }
        if (loc != null && loc.countryCode != null)
        {
            // use GeoIpCityLite
            country = loc.countryCode;
            logger.debug("IP " + ip + "was mapped to country " + loc.countryCode);
        }
        else if (geoIpCountryOnly && ipLookUpService != null)
        {
            // use GeoIpLite
            Country c = ipLookUpService.getCountry(ip);
            if (c != null && c.getCode() != null && !c.getCode().equals("--"))
            {
                country = c.getCode();
            }
            logger.debug("IP " + ip + "was mapped to country " + c.getCode());
        }
        StatisticReportRecordParamVO countryParam = new StatisticReportRecordParamVO();
        countryParam.setName("countryCode");
        countryParam.setParamValue(new StatisticReportRecordStringParamValueVO(country));
        paramList.add(countryParam);
        if (loc != null && loc.region != null)
        {
            region = loc.region;
        }
        StatisticReportRecordParamVO regionParam = new StatisticReportRecordParamVO();
        regionParam.setName("region");
        regionParam.setParamValue(new StatisticReportRecordStringParamValueVO(region));
        paramList.add(regionParam);
        if (loc != null && loc.city != null)
        {
            city = loc.city;
        }
        StatisticReportRecordParamVO cityParam = new StatisticReportRecordParamVO();
        cityParam.setName("city");
        cityParam.setParamValue(new StatisticReportRecordStringParamValueVO(city));
        paramList.add(cityParam);
        if (loc != null)
        {
            longitude = String.valueOf(loc.longitude);
        }
        StatisticReportRecordParamVO longitudeParam = new StatisticReportRecordParamVO();
        longitudeParam.setName("longitude");
        longitudeParam.setParamValue(new StatisticReportRecordStringParamValueVO(longitude));
        paramList.add(longitudeParam);
        if (loc != null)
        {
            latitude = String.valueOf(loc.latitude);
        }
        StatisticReportRecordParamVO latitudeParam = new StatisticReportRecordParamVO();
        latitudeParam.setName("latitude");
        latitudeParam.setParamValue(new StatisticReportRecordStringParamValueVO(latitude));
        paramList.add(latitudeParam);
        return paramList;
    }

    public void logSearch(String sessionId, String ip, String keywords, String cql, boolean loggedIn,
            String solutionId, String userHandle) throws Exception
    {
        logger.debug("Logging new search with keywwords" + keywords);
        StatisticRecordVO statisticRecord = new StatisticRecordVO();
        statisticRecord.setScope("2");
        List<StatisticReportRecordParamVO> paramList = new ArrayList<StatisticReportRecordParamVO>();
        statisticRecord.setParamList(paramList);
        StatisticReportRecordParamVO actionParam = new StatisticReportRecordParamVO();
        actionParam.setName("action");
        actionParam.setParamValue(new StatisticReportRecordStringParamValueVO("search"));
        paramList.add(actionParam);
        StatisticReportRecordParamVO keywordParam = new StatisticReportRecordParamVO();
        keywordParam.setName("keywords");
        keywordParam.setParamValue(new StatisticReportRecordStringParamValueVO(keywords));
        paramList.add(keywordParam);
        StatisticReportRecordParamVO cqlParam = new StatisticReportRecordParamVO();
        cqlParam.setName("cql");
        cqlParam.setParamValue(new StatisticReportRecordStringParamValueVO(cql));
        paramList.add(cqlParam);
        paramList.addAll(getUserStats(sessionId, ip));
        StatisticReportRecordParamVO loggedInParam = new StatisticReportRecordParamVO();
        loggedInParam.setName("loggedIn");
        loggedInParam.setParamValue(new StatisticReportRecordStringParamValueVO(String.valueOf(loggedIn)));
        paramList.add(loggedInParam);
        StatisticReportRecordParamVO solutionIdParam = new StatisticReportRecordParamVO();
        solutionIdParam.setName("solution");
        solutionIdParam.setParamValue(new StatisticReportRecordStringParamValueVO(solutionId));
        paramList.add(solutionIdParam);
        statisticRecord.createInCoreservice(userHandle);
    }

    public void logItemAction(String sessionId, String ip, ItemVO item, ItemAction action, boolean loggedIn,
            String referer, String solutionId, List<StatisticReportRecordParamVO> additionalParams, String userHandle)
            throws Exception
    {
        logger.debug("Logging new action " + action.toString() + "for item" + item.getVersion().getObjectId());
        StatisticRecordVO statisticRecord = new StatisticRecordVO();
        statisticRecord.setScope("2");
        List<StatisticReportRecordParamVO> paramList = new ArrayList<StatisticReportRecordParamVO>();
        statisticRecord.setParamList(paramList);
        StatisticReportRecordParamVO actionParam = new StatisticReportRecordParamVO();
        actionParam.setName("action");
        actionParam.setParamValue(new StatisticReportRecordStringParamValueVO(action.toString().toLowerCase()));
        paramList.add(actionParam);
        StatisticReportRecordParamVO itemIdParam = new StatisticReportRecordParamVO();
        itemIdParam.setName("itemId");
        itemIdParam.setParamValue(new StatisticReportRecordStringParamValueVO(item.getVersion().getObjectId()));
        paramList.add(itemIdParam);
        StatisticReportRecordParamVO pidParam = new StatisticReportRecordParamVO();
        pidParam.setName("pid");
        pidParam.setParamValue(new StatisticReportRecordStringParamValueVO(item.getVersion().getPid()));
        paramList.add(pidParam);
        StatisticReportRecordParamVO contextParam = new StatisticReportRecordParamVO();
        contextParam.setName("contextId");
        contextParam.setParamValue(new StatisticReportRecordStringParamValueVO(item.getContext().getObjectId()));
        paramList.add(contextParam);
        paramList.addAll(getUserStats(sessionId, ip));
        StatisticReportRecordParamVO loggedInParam = new StatisticReportRecordParamVO();
        loggedInParam.setName("loggedIn");
        loggedInParam.setParamValue(new StatisticReportRecordStringParamValueVO(String.valueOf(loggedIn)));
        paramList.add(loggedInParam);
        StatisticReportRecordParamVO solutionIdParam = new StatisticReportRecordParamVO();
        solutionIdParam.setName("solution");
        solutionIdParam.setParamValue(new StatisticReportRecordStringParamValueVO(solutionId));
        paramList.add(solutionIdParam);
        StatisticReportRecordParamVO itemStatusParam = new StatisticReportRecordParamVO();
        itemStatusParam.setName("itemStatus");
        itemStatusParam.setParamValue(new StatisticReportRecordStringParamValueVO(item.getPublicStatus().toString()));
        paramList.add(itemStatusParam);
        StatisticReportRecordParamVO refererParam = new StatisticReportRecordParamVO();
        refererParam.setName("referer");
        refererParam.setParamValue(new StatisticReportRecordStringParamValueVO(referer));
        paramList.add(refererParam);
        if (additionalParams != null)
        {
            paramList.addAll(additionalParams);
        }
        statisticRecord.createInCoreservice(userHandle);
    }

    public void logAuthorAction(String sessionId, String ip, String authorId, boolean loggedIn, String referer,
            String solutionId, List<StatisticReportRecordParamVO> additionalParams, String userHandle) throws Exception
    {
        StatisticRecordVO statisticRecord = new StatisticRecordVO();
        statisticRecord.setScope("2");
        List<StatisticReportRecordParamVO> paramList = new ArrayList<StatisticReportRecordParamVO>();
        statisticRecord.setParamList(paramList);
        StatisticReportRecordParamVO actionParam = new StatisticReportRecordParamVO();
        actionParam.setName("action");
        actionParam.setParamValue(new StatisticReportRecordStringParamValueVO("author-retrieve-item"));
        paramList.add(actionParam);
        StatisticReportRecordParamVO authorIdParam = new StatisticReportRecordParamVO();
        authorIdParam.setName("authorId");
        authorIdParam.setParamValue(new StatisticReportRecordStringParamValueVO(authorId));
        paramList.add(authorIdParam);
        paramList.addAll(getUserStats(sessionId, ip));
        StatisticReportRecordParamVO loggedInParam = new StatisticReportRecordParamVO();
        loggedInParam.setName("loggedIn");
        loggedInParam.setParamValue(new StatisticReportRecordStringParamValueVO(String.valueOf(loggedIn)));
        paramList.add(loggedInParam);
        StatisticReportRecordParamVO solutionIdParam = new StatisticReportRecordParamVO();
        solutionIdParam.setName("solution");
        solutionIdParam.setParamValue(new StatisticReportRecordStringParamValueVO(solutionId));
        paramList.add(solutionIdParam);
        StatisticReportRecordParamVO refererParam = new StatisticReportRecordParamVO();
        refererParam.setName("referer");
        refererParam.setParamValue(new StatisticReportRecordStringParamValueVO(referer));
        paramList.add(refererParam);
        if (additionalParams != null)
        {
            paramList.addAll(additionalParams);
        }
        statisticRecord.createInCoreservice(userHandle);
    }

    public void logOrgAction(String sessionId, String ip, String orgId, boolean loggedIn, String referer,
            String solutionId, List<StatisticReportRecordParamVO> additionalParams, String userHandle) throws Exception
    {
        StatisticRecordVO statisticRecord = new StatisticRecordVO();
        statisticRecord.setScope("2");
        List<StatisticReportRecordParamVO> paramList = new ArrayList<StatisticReportRecordParamVO>();
        statisticRecord.setParamList(paramList);
        StatisticReportRecordParamVO actionParam = new StatisticReportRecordParamVO();
        actionParam.setName("action");
        actionParam.setParamValue(new StatisticReportRecordStringParamValueVO("organization-retrieve-item"));
        paramList.add(actionParam);
        StatisticReportRecordParamVO orgIdParam = new StatisticReportRecordParamVO();
        orgIdParam.setName("orgId");
        orgIdParam.setParamValue(new StatisticReportRecordStringParamValueVO(orgId));
        paramList.add(orgIdParam);
        paramList.addAll(getUserStats(sessionId, ip));
        StatisticReportRecordParamVO loggedInParam = new StatisticReportRecordParamVO();
        loggedInParam.setName("loggedIn");
        loggedInParam.setParamValue(new StatisticReportRecordStringParamValueVO(String.valueOf(loggedIn)));
        paramList.add(loggedInParam);
        StatisticReportRecordParamVO solutionIdParam = new StatisticReportRecordParamVO();
        solutionIdParam.setName("solution");
        solutionIdParam.setParamValue(new StatisticReportRecordStringParamValueVO(solutionId));
        paramList.add(solutionIdParam);
        StatisticReportRecordParamVO refererParam = new StatisticReportRecordParamVO();
        refererParam.setName("referer");
        refererParam.setParamValue(new StatisticReportRecordStringParamValueVO(referer));
        paramList.add(refererParam);
        if (additionalParams != null)
        {
            paramList.addAll(additionalParams);
        }
        statisticRecord.createInCoreservice(userHandle);
    }
    
     public String ipToCountry(String ip) { 
         String c = ""; 
         c += ipLookUpService.getCountry(ip).getCode();
         //c +=" - ";
         //c += ipLookUpService.getRegion(ip).countryCode;
         c += " - "; 
         //Location loc = ipLookUpService.getLocation(ip); 
         //c+=loc.countryCode; 
//         c+=loc.region; 
//         c+=loc.city; 
//         c+=loc.postalCode;
//         c+=loc.longitude; 
         return c; 
         }
     
}
