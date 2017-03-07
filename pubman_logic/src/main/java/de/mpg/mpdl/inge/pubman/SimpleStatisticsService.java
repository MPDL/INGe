/*
 * 
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */
package de.mpg.mpdl.inge.pubman;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import de.escidoc.www.services.sm.AggregationDefinitionHandler;
import de.escidoc.www.services.sm.ReportDefinitionHandler;
import de.escidoc.www.services.sm.ReportHandler;
import de.mpg.mpdl.inge.framework.ServiceLocator;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.statistics.AggregationDefinitionVO;
import de.mpg.mpdl.inge.model.valueobjects.statistics.StatisticReportDefinitionVO;
import de.mpg.mpdl.inge.model.valueobjects.statistics.StatisticReportParamsVO;
import de.mpg.mpdl.inge.model.valueobjects.statistics.StatisticReportRecordDecimalParamValueVO;
import de.mpg.mpdl.inge.model.valueobjects.statistics.StatisticReportRecordParamVO;
import de.mpg.mpdl.inge.model.valueobjects.statistics.StatisticReportRecordStringParamValueVO;
import de.mpg.mpdl.inge.model.valueobjects.statistics.StatisticReportRecordVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.util.AdminHelper;
import de.mpg.mpdl.inge.util.ResourceUtil;

/**
 * 
 * Implementation of PubItemSimpleStatistics
 * 
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class SimpleStatisticsService {
  private static final Logger logger = Logger.getLogger(SimpleStatisticsService.class);

  public static String REPORTDEFINITION_NUMBER_OF_ITEM_RETRIEVALS_ALL_USERS;
  public static String REPORTDEFINITION_FILE_DOWNLOADS_PER_ITEM_ALL_USERS;
  public static String REPORTDEFINITION_FILE_DOWNLOADS_PER_FILE_ALL_USERS;
  public static String REPORTDEFINITION_NUMBER_OF_ITEM_RETRIEVALS_ANONYMOUS;
  public static String REPORTDEFINITION_FILE_DOWNLOADS_PER_ITEM_ANONYMOUS;
  public static String REPORTDEFINITION_FILE_DOWNLOADS_PER_FILE_ANONYMOUS;

  private static final String REPORTDEFINITION_FILE = "report-definition-list.xml";

  public static List<StatisticReportRecordVO> getStatisticReportRecord(String reportDefinitionId,
      String objectId, AccountUserVO accountUser) throws Exception {
    if (reportDefinitionId == null || objectId == null) {
      throw new IllegalArgumentException("Arguments are null!");
    }

    StatisticReportParamsVO repParams = new StatisticReportParamsVO();
    repParams.setReportDefinitionId(reportDefinitionId);

    StatisticReportRecordParamVO param =
        new StatisticReportRecordParamVO("object_id", new StatisticReportRecordStringParamValueVO(
            objectId));
    repParams.getParamList().add(param);

    ReportHandler repHandler;
    if (accountUser == null || accountUser.getHandle() == null)
      repHandler = ServiceLocator.getReportHandler();
    else
      repHandler = ServiceLocator.getReportHandler(accountUser.getHandle());

    String xmlParams = XmlTransformingService.transformToStatisticReportParameters(repParams);
    String xmlReport = repHandler.retrieve(xmlParams);

    return XmlTransformingService.transformToStatisticReportRecordList(xmlReport);
  }

  /**
   * {@inheritDoc}
   */
  public static String getNumberOfItemOrFileRequests(String reportDefinitionId, String objectId,
      AccountUserVO accountUser) throws Exception {
    List<StatisticReportRecordVO> reportRecordList =
        getStatisticReportRecord(reportDefinitionId, objectId, accountUser);

    int requests = 0;

    // Search for parameter with name "itemrequests" or "filerequests" in records
    // go through records and accumulate requests for different versions
    for (StatisticReportRecordVO record : reportRecordList) {
      List<StatisticReportRecordParamVO> parameterList = record.getParamList();

      for (StatisticReportRecordParamVO parameter : parameterList) {
        if (parameter.getName().equals("itemrequests")
            || parameter.getName().equals("filerequests")) {
          StatisticReportRecordDecimalParamValueVO decimalvalue =
              (StatisticReportRecordDecimalParamValueVO) parameter.getParamValue();
          requests += decimalvalue.getDecimalValue();
        }
      }
    }

    return String.valueOf(requests);
  }

  /**
   * {@inheritDoc}
   */
  public static void initReportDefinitionsInFramework() {
    try {
      logger.info("Initializing statistic aggregation definitions in framework database");

      AggregationDefinitionHandler adh =
          ServiceLocator.getAggregationDefinitionHandler(AdminHelper.getAdminUserHandle());

      String srwResponseXml = adh.retrieveAggregationDefinitions(new HashMap<String, String[]>());
      List<AggregationDefinitionVO> aggList =
          XmlTransformingService.transformToStatisticAggregationDefinitionList(srwResponseXml);

      String aggregationTableName = null;
      for (AggregationDefinitionVO aggDef : aggList) {
        if (aggDef.getName().equals("pubman item statistics without version")) {
          logger.info("Pubman statistic aggregation definition already exists with id "
              + aggDef.getObjectId());
          aggregationTableName = aggDef.getAggregationTables().get(0).getName();
          logger.info("Pubman aggregated table name:" + aggregationTableName);

          break;
        }
      }

      // No aggregation found, create one
      if (aggregationTableName == null) {
        logger.info("No pubman aggregation definition found, creating one");

        String aggregationDefinitionXml =
            ResourceUtil.getResourceAsString("pubman_object_stats_aggregation.xml",
                SimpleStatisticsService.class.getClassLoader());
        String createdAggDefXml = adh.create(aggregationDefinitionXml);

        AggregationDefinitionVO aggCreated =
            XmlTransformingService.transformToStatisticAggregationDefinition(createdAggDefXml);

        logger.info("Pubman aggregation definition created with id " + aggCreated.getObjectId());
        aggregationTableName = aggCreated.getAggregationTables().get(0).getName();
        logger.info("Pubman aggregated table name:" + aggregationTableName);
      }

      logger.info("Initializing statistical report definitions in framework database");

      ReportDefinitionHandler repDefHandler =
          ServiceLocator.getReportDefinitionHandler(AdminHelper.getAdminUserHandle());

      String repDefFrameworkListXML =
          repDefHandler.retrieveReportDefinitions(new HashMap<String, String[]>());

      List<StatisticReportDefinitionVO> repDefFrameworkList =
          XmlTransformingService.transformToStatisticReportDefinitionList(repDefFrameworkListXML);

      List<StatisticReportDefinitionVO> repDefFileList = retrieveReportDefinitionListFromFile();

      // Creating a Hash Map with ReportDefinitions from Framework and sql as key
      HashMap<String, StatisticReportDefinitionVO> repDefFrameworkMap =
          new HashMap<String, StatisticReportDefinitionVO>();

      for (StatisticReportDefinitionVO repDef : repDefFrameworkList) {
        repDefFrameworkMap.put(repDef.getSql(), repDef);
      }

      for (StatisticReportDefinitionVO repDefFile : repDefFileList) {
        String sql = repDefFile.getSql().replaceAll("pubman_object_stats", aggregationTableName);
        StatisticReportDefinitionVO repDefFW = repDefFrameworkMap.get(sql);
        String repDefObjectId;

        // Report Definition already existing
        if (repDefFW != null) {
          repDefObjectId = repDefFW.getObjectId();
          logger.info("Report Definition already  existing: " + repDefFW.getObjectId() + " --- "
              + repDefFW.getSql());
        } else {// Report Definition does not exist yet
          // create and set
          String repDefFileXML =
              XmlTransformingService.transformToStatisticReportDefinition(repDefFile).replaceAll(
                  "pubman_object_stats", aggregationTableName);;

          String repDefFWXMLNew = repDefHandler.create(repDefFileXML);

          StatisticReportDefinitionVO repDefFWNew =
              XmlTransformingService.transformToStatisticReportDefinition(repDefFWXMLNew);

          repDefObjectId = repDefFWNew.getObjectId();
          logger.info("Created new report definition and added to Map: "
              + repDefFWNew.getObjectId() + " --- " + repDefFWNew.getSql());
        }

        if (repDefFile.getName().equals("Item retrievals, all users")) {
          REPORTDEFINITION_NUMBER_OF_ITEM_RETRIEVALS_ALL_USERS = repDefObjectId;
        } else if (repDefFile.getName().equals("File downloads per Item, all users")) {
          REPORTDEFINITION_FILE_DOWNLOADS_PER_ITEM_ALL_USERS = repDefObjectId;
        } else if (repDefFile.getName().equals("File downloads, all users")) {
          REPORTDEFINITION_FILE_DOWNLOADS_PER_FILE_ALL_USERS = repDefObjectId;
        } else if (repDefFile.getName().equals("Item retrievals, anonymous users")) {
          REPORTDEFINITION_NUMBER_OF_ITEM_RETRIEVALS_ANONYMOUS = repDefObjectId;
        } else if (repDefFile.getName().equals("File downloads per Item, anonymous users")) {
          REPORTDEFINITION_FILE_DOWNLOADS_PER_ITEM_ANONYMOUS = repDefObjectId;
        } else if (repDefFile.getName().equals("File downloads, anonymous users")) {
          REPORTDEFINITION_FILE_DOWNLOADS_PER_FILE_ANONYMOUS = repDefObjectId;
        }
      }
    } catch (Exception e) {
      logger
          .error(
              "Statistic report definitions could not be initialized! Statistic system may not work properly. ",
              e);
    }
  }

  private static List<StatisticReportDefinitionVO> retrieveReportDefinitionListFromFile()
      throws Exception {
    String repDefListXML =
        ResourceUtil.getResourceAsString(REPORTDEFINITION_FILE,
            SimpleStatisticsService.class.getClassLoader());

    String[] repDefs = repDefListXML.split("\n");

    List<StatisticReportDefinitionVO> repDefVOList = new ArrayList<StatisticReportDefinitionVO>();

    for (String repDefXml : repDefs) {
      repDefVOList.add(XmlTransformingService.transformToStatisticReportDefinition(repDefXml));
    }

    return repDefVOList;
  }
}
