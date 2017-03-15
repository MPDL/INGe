/*
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

package de.mpg.mpdl.inge.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.axis.message.MessageElement;
import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.citationmanager.CitationStyleExecuterService;
import de.mpg.mpdl.inge.citationmanager.CitationStyleManagerException;
import de.mpg.mpdl.inge.framework.ServiceLocator;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationResultVO;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO.FormatType;
import de.mpg.mpdl.inge.model.valueobjects.interfaces.SearchResultElement;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.wrappers.ItemVOListWrapper;
import de.mpg.mpdl.inge.search.parser.ParseException;
import de.mpg.mpdl.inge.search.query.ExportSearchQuery;
import de.mpg.mpdl.inge.search.query.ExportSearchResult;
import de.mpg.mpdl.inge.search.query.ItemContainerSearchResult;
import de.mpg.mpdl.inge.search.query.OrgUnitsSearchResult;
import de.mpg.mpdl.inge.search.query.SearchQuery;
import de.mpg.mpdl.inge.structuredexportmanager.StructuredExportManagerException;
import de.mpg.mpdl.inge.structuredexportmanager.StructuredExportService;
import de.mpg.mpdl.inge.structuredexportmanager.StructuredExportXSLTNotFoundException;
import gov.loc.www.zing.srw.RecordType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.StringOrXmlFragment;
import gov.loc.www.zing.srw.diagnostic.DiagnosticType;
import gov.loc.www.zing.srw.service.SRWPort;
import net.sf.jasperreports.engine.JRException;

public class SearchService {
  private static final Logger logger = Logger.getLogger(SearchService.class);

  /** Coreservice identifier for the 'all' lucene index database. */
  public static final String INDEXDATABASE_ALL = "escidoc_all";
  /** Coreservice identifier for the 'item_container_admin' lucene index database. */
  public static final String INDEXDATABASE_ADMIN = "item_container_admin";
  /** Coreservice identifier for the 'german' lucene index database. */
  public static final String INDEXDATABASE_EN = "escidoc_en";
  /** Coreservice identifier for the 'english' lucene index database. */
  public static final String INDEXDATABASE_DE = "escidoc_de";
  /** Coreservice identifier for the organizational unit index database. */
  public static final String INDEXDATABASE_OU = "escidocou_all";

  /** Version of the cql search request. */
  private static final String SEARCHREQUEST_VERSION = "1.1";
  /** Packing of result. */
  private static final String RECORD_PACKING = "xml";
  /**
   * Diagnostic startrecord error from the SRW interface which should not be treated as an error.
   */
  private static final String SRW_STARTRECORD_NO_ERROR = "61/StartRecord > endRecord";
  /**
   * Diagnostic sort type error from the SRW interface which should not be treated as an error.
   */
  private static final String SRW_SORT_NO_ERROR = "cannot determine sort type";

  /**
   * {@inheritDoc}
   */
  public static ItemContainerSearchResult searchForItemContainer(SearchQuery query)
      throws Exception {

    try {
      // call framework Search service
      String cqlQuery = query.getCqlQuery();

      SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType();
      searchRetrieveRequest.setVersion(SEARCHREQUEST_VERSION);
      searchRetrieveRequest.setQuery(cqlQuery);
      searchRetrieveRequest.setSortKeys(query.getCqlSortingQuery());
      searchRetrieveRequest.setMaximumRecords(query.getMaximumRecords());
      searchRetrieveRequest.setStartRecord(query.getStartRecord());
      searchRetrieveRequest.setRecordPacking(RECORD_PACKING);
      SearchRetrieveResponseType searchResult =
          performSearch(searchRetrieveRequest, INDEXDATABASE_ALL, null);
      List<SearchResultElement> resultList = transformToSearchResultList(searchResult);
      ItemContainerSearchResult result =
          new ItemContainerSearchResult(resultList, cqlQuery, searchResult.getNumberOfRecords());
      return result;
    } catch (Exception e) {
      throw new TechnicalException(e);
    }
  }

  public static ItemContainerSearchResult searchForItemContainerAdmin(SearchQuery query,
      String userHandle) throws Exception {

    try {
      // call framework Search service
      String cqlQuery = query.getCqlQuery();

      SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType();
      searchRetrieveRequest.setVersion(SEARCHREQUEST_VERSION);
      searchRetrieveRequest.setQuery(cqlQuery);
      searchRetrieveRequest.setSortKeys(query.getCqlSortingQuery());
      searchRetrieveRequest.setMaximumRecords(query.getMaximumRecords());
      searchRetrieveRequest.setStartRecord(query.getStartRecord());
      searchRetrieveRequest.setRecordPacking(RECORD_PACKING);
      SearchRetrieveResponseType searchResult =
          performSearch(searchRetrieveRequest, INDEXDATABASE_ADMIN, userHandle);
      List<SearchResultElement> resultList = transformToSearchResultList(searchResult);
      ItemContainerSearchResult result =
          new ItemContainerSearchResult(resultList, cqlQuery, searchResult.getNumberOfRecords());
      return result;
    } catch (Exception e) {
      throw new TechnicalException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  public static OrgUnitsSearchResult searchForOrganizationalUnits(SearchQuery query)
      throws Exception {
    try {
      // call framework Search service
      String cqlQuery = query.getCqlQuery();

      SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType();
      searchRetrieveRequest.setVersion(SEARCHREQUEST_VERSION);
      searchRetrieveRequest.setQuery(cqlQuery);
      searchRetrieveRequest.setSortKeys(query.getCqlSortingQuery());

      searchRetrieveRequest.setMaximumRecords(query.getMaximumRecords());
      searchRetrieveRequest.setStartRecord(query.getStartRecord());
      searchRetrieveRequest.setRecordPacking(RECORD_PACKING);

      SearchRetrieveResponseType searchResult =
          performSearch(searchRetrieveRequest, INDEXDATABASE_OU, null);
      List<AffiliationVO> resultList = transformToAffiliationList(searchResult);
      OrgUnitsSearchResult result =
          new OrgUnitsSearchResult(resultList, cqlQuery, searchResult.getNumberOfRecords());
      return result;
    } catch (ParseException f) {
      throw new ParseException();
    } catch (Exception e) {
      throw new TechnicalException(e);
    }
  }

  /**
   * Perform a search with the SRU interface.
   * 
   * @param searchRetrieveRequest SRU search request
   * @param sel index database selector
   * @return search result set
   * @throws TechnicalException if the search fails
   */
  private static SearchRetrieveResponseType performSearch(
      SearchRetrieveRequestType searchRetrieveRequest, String index, String userHandle)
      throws TechnicalException {

    SearchRetrieveResponseType searchResult = null;

    try {
      SRWPort searchHandler;
      if (userHandle == null) {
        searchHandler = ServiceLocator.getSearchHandler(index);
      } else {
        searchHandler = ServiceLocator.getSearchHandler(index, userHandle);
      }

      logger.info("Cql search string: <" + searchRetrieveRequest.getQuery() + ">");
      logger.info("Cql sorting key(s): <" + searchRetrieveRequest.getSortKeys() + ">");
      searchResult = searchHandler.searchRetrieveOperation(searchRetrieveRequest);
      logger.debug("Search result: " + searchResult.getNumberOfRecords()
          + " item(s) or container(s)");
    } catch (Exception e) {
      throw new TechnicalException(e);
    }

    // look for errors
    if (searchResult.getDiagnostics() != null) {
      // something went wrong
      for (DiagnosticType diagnostic : searchResult.getDiagnostics().getDiagnostic()) {
        if ((diagnostic.getDetails().contains(SRW_STARTRECORD_NO_ERROR))
            || (diagnostic.getDetails().contains(SRW_SORT_NO_ERROR))) {
          logger.info("SRW interface returns an error, this one is safe to ignore: "
              + diagnostic.getDetails());
        } else {
          logger.warn(diagnostic.getUri());
          logger.warn(diagnostic.getMessage());
          logger.warn(diagnostic.getDetails());
          // throw new TechnicalException("Search request failed for query: "
          // + searchRetrieveRequest.getQuery());
        }
      }
    }

    return searchResult;
  }

  /**
   * Transform the search result set into ItemContainerSearchResultVOs.
   * 
   * @param searchResult search result retrieved from the SRU interface
   * @return list of ItemContainerSearchResultVOs
   * @throws TechnicalException if transforming fails
   */
  private static List<SearchResultElement> transformToSearchResultList(
      SearchRetrieveResponseType searchResult) throws TechnicalException {

    ArrayList<SearchResultElement> resultList = new ArrayList<SearchResultElement>();
    if (searchResult.getRecords() != null) {
      for (RecordType record : searchResult.getRecords().getRecord()) {
        StringOrXmlFragment data = record.getRecordData();
        MessageElement[] messages = data.get_any();
        // Data is in the first record
        if (messages.length == 1) {
          String searchResultItem = null;
          try {
            searchResultItem = messages[0].getAsString();
          } catch (Exception e) {
            throw new TechnicalException("Error getting search result message.", e);
          }
          logger.debug("Search result: " + searchResultItem);
          SearchResultElement itemResult =
              XmlTransformingService.transformToSearchResult(searchResultItem);
          resultList.add(itemResult);
        }
      }
    }
    return resultList;
  }

  /**
   * {@inheritDoc}
   */
  public static ExportSearchResult searchAndExportItems(ExportSearchQuery query) throws Exception {

    String cqlQuery = query.getCqlQuery();
    // call framework Search service
    SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType();
    searchRetrieveRequest.setVersion(SEARCHREQUEST_VERSION);
    searchRetrieveRequest.setQuery(query.getCqlQuery());
    searchRetrieveRequest.setSortKeys(query.getCqlSortingQuery());

    searchRetrieveRequest.setMaximumRecords(query.getMaximumRecords());
    searchRetrieveRequest.setStartRecord(query.getStartRecord());

    searchRetrieveRequest.setRecordPacking(RECORD_PACKING);

    SearchRetrieveResponseType searchResult = null;

    if (query.getIndexSelector() == null) {
      searchResult = performSearch(searchRetrieveRequest, INDEXDATABASE_ALL, null);
    } else {
      searchResult = performSearch(searchRetrieveRequest, query.getIndexSelector(), null);
    }

    List<SearchResultElement> searchElements = transformToSearchResultList(searchResult);
    ExportSearchResult exportedResult =
        new ExportSearchResult(searchElements, cqlQuery, searchResult.getNumberOfRecords());
    String itemListAsString =
        XmlTransformingService.transformToItemList(transformtToItemVOListWrapper(exportedResult));

    String outputFormat = query.getOutputFormat();
    String exportFormat = query.getExportFormat();
    String cslConeId = query.getCslConeId();

    if (!checkValue(exportFormat)) {
      throw new TechnicalException("exportFormat is empty");
    }
    if (!checkValue(itemListAsString)) {
      throw new TechnicalException("itemList is empty");
    }

    byte[] exportData = null;

    // structured export
    if (StructuredExportService.isStructuredFormat(exportFormat)) {
      exportData =
          getOutput(itemListAsString, new ExportFormatVO(FormatType.STRUCTURED, exportFormat, null));
      exportedResult.setExportedResults(exportData);
      return exportedResult;
    }
    // citation style
    else if (CitationStyleExecuterService.isCitationStyle(exportFormat)) {
      if (!checkValue(outputFormat)) {
        throw new TechnicalException("outputFormat should be not empty for exportFormat:"
            + exportFormat);
      }
      outputFormat = outputFormat.trim();
      if (CitationStyleExecuterService.getMimeType(exportFormat, outputFormat) == null) {
        throw new TechnicalException("file output format: " + outputFormat + " for export format: "
            + exportFormat + " is not supported");
      }
      exportData =
          getOutput(itemListAsString, new ExportFormatVO(FormatType.LAYOUT, exportFormat,
              outputFormat, cslConeId));
      exportedResult.setExportedResults(exportData);
      return exportedResult;
    } else {
      // no export format found!!!
      throw new TechnicalException("Export format: " + exportFormat + " is not supported");
    }
  }

  /**
   * Queries an export service to get the search result in an binary format.
   * 
   * @param exportFormat export format to transform to
   * @param formatType format type to transform to
   * @param outputFormat output format to transform to
   * @param itemList the list of items to be transformed
   * @return a binary stream which contains the items in a given format (pdf, etc.)
   * @throws TechnicalException
   * @throws StructuredExportXSLTNotFoundException if the corresponding xslt is not found
   * @throws StructuredExportManagerException if structured exportmanager reports an error
   * @throws IOException if an io error occurs
   * @throws JRException if a jr error occurs
   * @throws CitationStyleManagerException if the citationstyle manager reports an error
   */
  private static byte[] getOutput(String itemList, ExportFormatVO exportFormat)
      throws TechnicalException, StructuredExportXSLTNotFoundException,
      StructuredExportManagerException, IOException, JRException, CitationStyleManagerException {

    byte[] exportData = null;

    // structured export
    if (exportFormat.getFormatType() == FormatType.LAYOUT) {
      logger.debug("Calling citationStyleHandler");

      exportData = CitationStyleExecuterService.getOutput(itemList, exportFormat);
      logger.debug("Returning from citationStyleHandler");
    } else if (exportFormat.getFormatType() == FormatType.STRUCTURED) {
      logger.debug("Calling structuredExportHandler");
      exportData = StructuredExportService.getOutput(itemList, exportFormat.getName());
      logger.debug("Returning from structuredExportHandler");
    } else {
      // no export format found!!!
      throw new TechnicalException("format Type: " + exportFormat.getFormatType()
          + " is not supported");
    }

    return exportData;
  }

  private static List<AffiliationVO> transformToAffiliationList(
      SearchRetrieveResponseType searchResult) throws Exception {
    ArrayList<AffiliationVO> resultList = new ArrayList<AffiliationVO>();
    if (searchResult.getRecords() != null) {
      for (RecordType record : searchResult.getRecords().getRecord()) {
        StringOrXmlFragment data = record.getRecordData();
        MessageElement[] messages = data.get_any();
        // Data is in the first record
        if (messages.length == 1) {
          String searchResultItem = messages[0].getAsString();
          logger.debug("Search result: " + searchResultItem);
          SearchResultElement searchResultElement =
              XmlTransformingService.transformToSearchResult(searchResultItem);
          AffiliationResultVO affiliationResultVO = (AffiliationResultVO) searchResultElement;
          resultList.add((AffiliationVO) affiliationResultVO);

        }
      }
    }
    return resultList;
  }

  private static ItemVOListWrapper transformtToItemVOListWrapper(
      ExportSearchResult exportSearchResult) {
    logger.debug("transformToItemList(List<ItemVO>)");
    if (exportSearchResult == null) {
      throw new IllegalArgumentException(SearchService.class.getSimpleName()
          + ":transformtToItemVOListWrapper is null");
    }
    // wrap the exportSearchResult list into the according wrapper class
    ItemVOListWrapper listWrapper = new ItemVOListWrapper();
    listWrapper.setItemVOList(exportSearchResult.extractItemsOfSearchResult());
    if (exportSearchResult.getTotalNumberOfResults() != null) {
      listWrapper.setNumberOfRecords(exportSearchResult.getTotalNumberOfResults().toString());
    }
    return listWrapper;
  }

  private static boolean checkValue(String str) {
    return !(str == null || str.trim().equals(""));
  }
}