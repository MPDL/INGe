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

package de.mpg.mpdl.inge.model.xmltransforming;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

import de.mpg.mpdl.inge.model.valueobjects.AffiliationResultVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemResultVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.PidServiceResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchResultVO;
import de.mpg.mpdl.inge.model.valueobjects.interfaces.SearchResultElement;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.model.xmltransforming.util.FileVOCreationDateComparator;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.exceptions.MarshallingException;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.exceptions.UnmarshallingException;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.wrappers.ItemVOListWrapper;

/**
 * @author Johannes Mueller (initial creation)
 * @version $Revision$ $LastChangedDate$Author: mfranke $
 * @revised by MuJ: 21.08.2007
 */
public class XmlTransformingService {
  private static final Logger logger = Logger.getLogger(XmlTransformingService.class);

  /**
   * {@inheritDoc}
   */
  /*
  public static AccountUserVO transformToAccountUser(String user) throws TechnicalException, UnmarshallingException {
    logger.debug("transformToAccountUser(String) - String user=" + user);
    if (user == null) {
      throw new IllegalArgumentException(XmlTransformingService.class.getSimpleName() + ":transformToAccountUser:user is null");
    }
    AccountUserVO userVO = null;
    try {
      // unmarshal AccountUserVO from String
      IBindingFactory bfact = BindingDirectory.getFactory(AccountUserVOListWrapper.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(user);
      userVO = (AccountUserVO) uctx.unmarshalDocument(sr, null);
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(user, e);
    } catch (java.lang.ClassCastException e) {
      throw new TechnicalException(e);
    }
    return userVO;
  }
  */

  /**
   * {@inheritDoc}
   */
  /*
  public static List<AffiliationVO> transformToAffiliationList(String organizationalUnitList)
      throws TechnicalException, UnmarshallingException {
    logger.debug("transformToAffiliationList(String) - String oranizationalUnitList=" + organizationalUnitList);
    if (organizationalUnitList == null) {
      throw new IllegalArgumentException(
          XmlTransformingService.class.getSimpleName() + ":transformToAffiliationList:organizationalUnitList is null");
    }
    AffiliationVOListWrapper affiliationVOListWrapper;
    try {
      // unmarshal AffiliationVOListWrapper from String
      IBindingFactory bfact = BindingDirectory.getFactory("AffiliationVO_input", AffiliationVOListWrapper.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(organizationalUnitList);
      affiliationVOListWrapper = (AffiliationVOListWrapper) uctx.unmarshalDocument(sr, null);
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(organizationalUnitList, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    // unwrap the List<AffiliationVO>
    List<AffiliationVO> affiliationList = affiliationVOListWrapper.getAffiliationVOList();
    return affiliationList;
  }
  */

  /**
   * {@inheritDoc}
   */
  /*
  public static List<AffiliationRO> transformToParentAffiliationList(String parentOrganizationalUnitList)
      throws TechnicalException, UnmarshallingException {
    logger.debug("transformToParentAffiliationList(String) - String parentOrganizationalUnitList=" + parentOrganizationalUnitList);
    if (parentOrganizationalUnitList == null) {
      throw new IllegalArgumentException(
          XmlTransformingService.class.getSimpleName() + ":transformToAffiliationList:organizationalUnitList is null");
    }
    AffiliationROListWrapper affiliationROListWrapper;
    try {
      // unmarshal AffiliationVOListWrapper from String
      IBindingFactory bfact = BindingDirectory.getFactory("AffiliationVO_input", AffiliationROListWrapper.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(parentOrganizationalUnitList);
      affiliationROListWrapper = (AffiliationROListWrapper) uctx.unmarshalDocument(sr, null);
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(parentOrganizationalUnitList, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    // unwrap the List<AffiliationVO>
    List<AffiliationRO> affiliationList = affiliationROListWrapper.getAffiliationROList();
    return affiliationList;
  }
  */

  /**
   * {@inheritDoc}
   */
  /*
  public static List<AffiliationRO> transformToSuccessorAffiliationList(String successorOrganizationalUnitList)
      throws TechnicalException, UnmarshallingException {
    logger.debug("transformToSuccessorAffiliationList(String) - String successorOrganizationalUnitList=" + successorOrganizationalUnitList);
    if (successorOrganizationalUnitList == null) {
      throw new IllegalArgumentException(
          XmlTransformingService.class.getSimpleName() + ":transformToAffiliationList:organizationalUnitList is null");
    }
    SuccessorROListWrapper successorROListWrapper;
    try {
      // unmarshal AffiliationVOListWrapper from String
      IBindingFactory bfact = BindingDirectory.getFactory("AffiliationVO_input", SuccessorROListWrapper.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(successorOrganizationalUnitList);
      successorROListWrapper = (SuccessorROListWrapper) uctx.unmarshalDocument(sr, null);
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(successorOrganizationalUnitList, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    // unwrap the List<AffiliationVO>
    List<AffiliationRO> affiliationList = successorROListWrapper.getAffiliationROList();
    return affiliationList;
  }
  */

  /**
   * {@inheritDoc}
   */
  /*
  public static List<AffiliationPathVO> transformToAffiliationPathList(String pathList) throws TechnicalException, UnmarshallingException {
    logger.debug("transformToAffiliationPathList(String) - String pathList=" + pathList);
    if (pathList == null) {
      throw new IllegalArgumentException(XmlTransformingService.class.getSimpleName() + ":transformToAffiliationPathList:pathList is null");
    }
    List<AffiliationPathVO> resultList;
    AffiliationPathVOListWrapper affiliationPathVOListWrapper = null;
    try {
      // unmarshal AffiliationPathVOListWrapper from String
      IBindingFactory bfact = BindingDirectory.getFactory("AffiliationVO_input", AffiliationPathVOListWrapper.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(pathList);
      affiliationPathVOListWrapper = (AffiliationPathVOListWrapper) uctx.unmarshalDocument(sr, null);
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(pathList, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    // unwrap the List<AffiliationPathVO>
    resultList = affiliationPathVOListWrapper.getAffiliationPathVOList();
    return resultList;
  }
  */

  /**
   * {@inheritDoc}
   */
  /*
  public static List<GrantVO> transformToGrantVOList(String formatList) throws TechnicalException {
    logger.debug("transformToGrantVOList(String) - String formatList=" + formatList);
    if (formatList == null) {
      throw new IllegalArgumentException(XmlTransformingService.class.getSimpleName() + ":transformToGrantVOList:formatList is null");
    }
    GrantVOListWrapper grantVOListWrapper = null;
    try {
      // unmarshall GrantVOListWrapper from String
      IBindingFactory bfact = BindingDirectory.getFactory("GrantVOListWrapper", GrantVOListWrapper.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(formatList);
      grantVOListWrapper = (GrantVOListWrapper) uctx.unmarshalDocument(sr, null);
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause(), e);
      throw new UnmarshallingException(formatList, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    // unwrap the List<GrantVO>
    List<GrantVO> grantVOList = grantVOListWrapper.getGrantVOList();
    return grantVOList;
  }
  */

  /**
   * {@inheritDoc}
   */
  /*
  public static String transformToFilterTaskParam(FilterTaskParamVO filterTaskParamVO) throws TechnicalException, MarshallingException {
    logger.debug("transformToFilterTaskParam(FilterTaskParamVO)");
    if (filterTaskParamVO == null) {
      throw new IllegalArgumentException(
          XmlTransformingService.class.getSimpleName() + ":transformToFilterTaskParam:filterTaskParamVO is null");
    }
  
    StringWriter sw = null;
    try {
      // marshal XML from FilterTaskParamVO
      IBindingFactory bfact = BindingDirectory.getFactory(FilterTaskParamVO.class);
      IMarshallingContext mctx = bfact.createMarshallingContext();
      mctx.setIndent(2);
      sw = new StringWriter();
      mctx.setOutput(sw);
      mctx.marshalDocument(filterTaskParamVO);
    } catch (JiBXException e) {
      throw new MarshallingException(FilterTaskParamVO.class.getSimpleName(), e);
    } catch (java.lang.ClassCastException e) {
      throw new TechnicalException(e);
    }
  
    return sw.toString().trim();
  }
  */

  /**
   * {@inheritDoc}
   */
  /*
  public static AffiliationVO transformToAffiliation(String organizationalUnit) throws TechnicalException, UnmarshallingException {
    logger.debug("transformToAffiliation(String) - String organizationalUnit=" + organizationalUnit);
    if (organizationalUnit == null) {
      throw new IllegalArgumentException(
          XmlTransformingService.class.getSimpleName() + ":transformToAffiliation:organizationalUnit is null");
    }
    AffiliationVO affiliationVO = null;
    try {
      // unmarshal AffiliationVO from String
      IBindingFactory bfact = BindingDirectory.getFactory("AffiliationVO_input", AffiliationVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(organizationalUnit);
      affiliationVO = (AffiliationVO) uctx.unmarshalDocument(sr, null);
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(organizationalUnit, e);
    } catch (java.lang.ClassCastException e) {
      throw new TechnicalException(e);
    } catch (java.lang.reflect.UndeclaredThrowableException e) {
      throw new UnmarshallingException(organizationalUnit,
          new TechnicalException(
              "An UndeclaredThrowableException occured in " + XmlTransformingService.class.getSimpleName() + ":transformToAffiliation",
              e.getUndeclaredThrowable()));
    }
    return affiliationVO;
  }
  */

  /**
   * {@inheritDoc}
   */
  /*
  public static String transformToOrganizationalUnit(AffiliationVO affiliationVO) throws TechnicalException, MarshallingException {
    logger.debug("transformToOrganizationalUnit(AffiliationVO)");
    if (affiliationVO == null) {
      throw new IllegalArgumentException(
          XmlTransformingService.class.getSimpleName() + ":transformToOrganizationalUnit:affiliationVO is null");
    }
    String utf8ou = null;
    try {
      IBindingFactory bfact = BindingDirectory.getFactory("AffiliationVO_output", AffiliationVO.class);
      // marshal object (with nice indentation, as UTF-8)
      IMarshallingContext mctx = bfact.createMarshallingContext();
      mctx.setIndent(2);
      StringWriter sw = new StringWriter();
      mctx.setOutput(sw);
      mctx.marshalDocument(affiliationVO, "UTF-8", null, sw);
      utf8ou = sw.toString().trim();
    } catch (JiBXException e) {
      throw new MarshallingException(AffiliationVO.class.getSimpleName(), e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    if (logger.isDebugEnabled()) {
      logger.debug("transformToOrganizationalUnit(AffiliationVO) - result: String utf8ou=" + utf8ou);
    }
    return utf8ou;
  }
  */

  /**
   * {@inheritDoc}
   */
  public static String transformToItem(ItemVO itemVO) throws TechnicalException {
    logger.debug("transformToItem(PubItemVO)");
    if (itemVO == null) {
      throw new IllegalArgumentException(XmlTransformingService.class.getSimpleName() + ":transformToItem:pubItemVO is null");
    }
    String utf8item = null;
    try {
      IBindingFactory bfact = BindingDirectory.getFactory("PubItemVO_PubCollectionVO_output", ItemVO.class);
      // marshal object (with nice indentation, as UTF-8)
      IMarshallingContext mctx = bfact.createMarshallingContext();
      mctx.setIndent(2);
      StringWriter sw = new StringWriter();
      mctx.setOutput(sw);
      mctx.marshalDocument(itemVO, "UTF-8", null, sw);
      // use the following call to omit the leading "<?xml" tag of the generated XML
      // mctx.marshalDocument(pubItemVO);
      utf8item = sw.toString().trim();
    } catch (JiBXException e) {
      throw new MarshallingException(ItemVO.class.getSimpleName(), e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    if (logger.isDebugEnabled()) {
      logger.debug("transformToItem(ItemVO) - result: String utf8item=" + utf8item);
    }
    return utf8item;
  }

  /**
   * {@inheritDoc}
   */
  /*
  public static ContextVO transformToContext(String context) throws TechnicalException {
    logger.debug("transformToPubCollection(String) - String context=" + context);
    if (context == null) {
      throw new IllegalArgumentException(XmlTransformingService.class.getSimpleName() + ":transformToPubContext:context is null");
    }
    ContextVO contextVO = null;
    try {
      // unmarshal ContextVO from String
      IBindingFactory bfact = BindingDirectory.getFactory("PubItemVO_PubCollectionVO_input", ContextVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(context);
      contextVO = (ContextVO) uctx.unmarshalDocument(sr, null);
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(context, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(context, e);
    }
    return contextVO;
  }
  */

  /**
   * {@inheritDoc}
   */
  /*
  public static List<ContextVO> transformToContextList(String contextList) throws TechnicalException {
    logger.debug("transformToPubCollectionList(String) - String contextList=" + contextList);
    if (contextList == null) {
      throw new IllegalArgumentException(
          XmlTransformingService.class.getSimpleName() + ":transformToPubCollectionList:contextList is null");
    }
  
    logger.debug("transformed contextList =" + contextList);
  
    SearchRetrieveResponseVO<ContextVO> response = null;
    try {
      // unmarshal ContextVOListWrapper from String
      IBindingFactory bfact = BindingDirectory.getFactory("PubItemVO_PubCollectionVO_input", ContextVOListWrapper.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(contextList);
      response = (SearchRetrieveResponseVO) uctx.unmarshalDocument(sr, null);
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(contextList, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    List<ContextVO> ctxList = new ArrayList<ContextVO>();
  
    if (response.getRecords() != null) {
      for (SearchRetrieveRecordVO<ContextVO> s : response.getRecords()) {
        ctxList.add((ContextVO) s.getData());
      }
    }
    return ctxList;
  }
  */

  /**
   * {@inheritDoc}
   */
  public static ItemVO transformToItem(String item) throws TechnicalException {
    logger.debug("transformToPubItem(String) - String item=" + item);
    if (item == null) {
      throw new IllegalArgumentException(XmlTransformingService.class.getSimpleName() + ":transformToPubItem:item is null");
    }
    ItemVO itemVO = null;
    try {
      IBindingFactory bfact = BindingDirectory.getFactory("PubItemVO_PubCollectionVO_input", ItemVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(item);
      itemVO = (ItemVO) uctx.unmarshalDocument(sr, null);
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error("Error transforming item", e);
      throw new UnmarshallingException(item, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    Collections.sort(itemVO.getFiles(), new FileVOCreationDateComparator());
    return itemVO;
  }

  /**
   * {@inheritDoc}
   */
  private static List<? extends ItemVO> transformToItemList(String itemListXml) throws TechnicalException {
    logger.debug("transformToPubItemList(String) - String itemList=\n" + itemListXml);
    if (itemListXml == null) {
      throw new IllegalArgumentException(XmlTransformingService.class.getSimpleName() + ":transformToPubItemList:itemList is null");
    }
    ItemVOListWrapper itemVOListWrapper = null;
    try {
      // unmarshal ItemVOListWrapper from String
      IBindingFactory bfact = BindingDirectory.getFactory("PubItemVO_PubCollectionVO_input", ItemVOListWrapper.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(itemListXml);
      Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
      itemVOListWrapper = (ItemVOListWrapper) unmarshalledObject;
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(itemListXml, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    // unwrap the List<ItemVO>
    List<? extends ItemVO> itemList = itemVOListWrapper.getItemVOList();

    return itemList;
  }

  /**
   * {@inheritDoc}
   */
  /*
  public static ItemVOListWrapper transformSearchRetrieveResponseToItemList(String itemListXml) throws TechnicalException {
    logger.debug("transformSearchRetrieveResponseToItemList(String) - String itemList=\n" + itemListXml);
    if (itemListXml == null) {
      throw new IllegalArgumentException(
          XmlTransformingService.class.getSimpleName() + ":transformSearchRetrieveResponseToItemList:itemList is null");
    }
    SearchRetrieveResponseVO response = transformToSearchRetrieveResponse(itemListXml);
    List<SearchRetrieveRecordVO> records = response.getRecords();
  
    ItemVOListWrapper pubItemList = new ItemVOListWrapper();
  
    pubItemList.setNumberOfRecords(response.getNumberOfRecords() + "");
    List<PubItemVO> list = new ArrayList<PubItemVO>();
    pubItemList.setItemVOList(list);
  
    if (records == null) {
      return pubItemList;
    }
  
    for (SearchRetrieveRecordVO record : records) {
      list.add((PubItemVO) record.getData());
    }
  
    return pubItemList;
  }
  */

  /**
   * {@inheritDoc}
   */
  /*
  public static List<ContextVO> transformSearchRetrieveResponseToContextList(String contextListXml) throws TechnicalException {
    logger.debug("transformSearchRetrieveResponseToContextList(String) - String contextList=\n" + contextListXml);
    if (contextListXml == null) {
      throw new IllegalArgumentException(
          XmlTransformingService.class.getSimpleName() + ":transformSearchRetrieveResponseToContextList:contextList is null");
    }
  
    logger.debug("transformed contextList =" + contextListXml);
  
    SearchRetrieveResponseVO response = transformToSearchRetrieveResponse(contextListXml);
    List<SearchRetrieveRecordVO> records = response.getRecords();
  
    List<ContextVO> pubContextList = new ArrayList<ContextVO>();
  
    if (records == null) {
      return pubContextList;
    }
  
    for (SearchRetrieveRecordVO record : records) {
      pubContextList.add((ContextVO) record.getData());
    }
  
    return pubContextList;
  }
  */

  /**
   * {@inheritDoc}
   */
  /*
  @Deprecated
  public static ItemResultVO transformToItemResultVO(String searchResultItem) throws TechnicalException {
    if (searchResultItem == null) {
      throw new IllegalArgumentException(
          XmlTransformingService.class.getSimpleName() + ":transformToItemResultVO:searchResultItem is null");
    }
  
    SearchResultElement searchResultElement = transformToSearchResult(searchResultItem);
    if (!(searchResultElement instanceof ItemResultVO)) {
      throw new TechnicalException("XML not in the right format");
    }
  
    return (ItemResultVO) searchResultElement;
  }
  */

  /**
   * {@inheritDoc}
   */
  /*
  public static SearchResultElement transformToSearchResult(String searchResultXml) throws TechnicalException {
    if (searchResultXml == null) {
      throw new IllegalArgumentException(XmlTransformingService.class.getSimpleName() + ":transformToSearchResult:searchResultXml is null");
    }
    SearchResultVO searchResultVO = null;
    try {
      // unmarshall PubItemResultVO from String
      IBindingFactory bfact = BindingDirectory.getFactory("SearchResultVO_input", SearchResultVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(searchResultXml);
      searchResultVO = (SearchResultVO) uctx.unmarshalDocument(sr, "UTF-8");
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(searchResultXml, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    return convertToVO(searchResultVO);
  }
  */

  /**
   * Converts a {@link SearchResultVO} into an instantiation of {@link SearchResultElement}. May be
   * a - {@link ItemResultVO} - {@link ContainerResultVO} - {@link AffiliationResultVO}
   * 
   * @param searchResultVO The original VO.
   * 
   * @return The new VO.
   */
  /*
  private static SearchResultElement convertToVO(SearchResultVO searchResultVO) throws TechnicalException {
    Searchable searchable = searchResultVO.getResultVO();
    List<SearchHitVO> searchHits = searchResultVO.getSearchHitList();
  
    if (searchable instanceof ItemVO) {
      ItemResultVO itemResultVO = new ItemResultVO((ItemVO) searchable);
      itemResultVO.getSearchHitList().addAll(searchHits);
      itemResultVO.setScore(searchResultVO.getScore());
      return itemResultVO;
    } else if (searchable instanceof AffiliationVO) {
      AffiliationResultVO affiliationResultVO = new AffiliationResultVO((AffiliationVO) searchable);
      affiliationResultVO.getSearchHitList().addAll(searchHits);
      affiliationResultVO.setScore(searchResultVO.getScore());
  
      return affiliationResultVO;
    }
    throw new TechnicalException("Search result is of unknown type");
  }
  */

  public static String transformToItemList(List<? extends ItemVO> itemVOList) throws TechnicalException {
    logger.debug("transformToItemList(List<ItemVO>)");
    if (itemVOList == null) {
      throw new IllegalArgumentException(XmlTransformingService.class.getSimpleName() + ":transformToItemList:pubItemVOList is null");
    }
    // wrap the item list into the according wrapper class
    ItemVOListWrapper listWrapper = new ItemVOListWrapper();
    listWrapper.setItemVOList(itemVOList);

    return transformToItemList(listWrapper);
  }

  public static String transformToItemList(ItemVOListWrapper itemListWrapper) throws TechnicalException {
    // transform the wrapper class into XML
    String utf8itemList = null;
    try {
      IBindingFactory bfact = BindingDirectory.getFactory("PubItemVO_PubCollectionVO_output", ItemVOListWrapper.class);
      // marshal object (with nice indentation, as UTF-8)
      IMarshallingContext mctx = bfact.createMarshallingContext();
      mctx.setIndent(2);
      StringWriter sw = new StringWriter();
      mctx.setOutput(sw);
      mctx.marshalDocument(itemListWrapper, "UTF-8", null, sw);
      utf8itemList = sw.toString().trim();
      // <sub>, <sup>, <br>
      utf8itemList = utf8itemList.replaceAll("&lt;br>", "&lt;br&gt;");
      utf8itemList = utf8itemList.replaceAll("&lt;BR>", "&lt;BR&gt;");
      utf8itemList = utf8itemList.replaceAll("&lt;sub>", "&lt;sub&gt;");
      utf8itemList = utf8itemList.replaceAll("&lt;/sub>", "&lt;/sub&gt;");
      utf8itemList = utf8itemList.replaceAll("&lt;sup>", "&lt;sup&gt;");
      utf8itemList = utf8itemList.replaceAll("&lt;/sup>", "&lt;/sup&gt;");
      utf8itemList = utf8itemList.replaceAll("&lt;SUB>", "&lt;SUB&gt;");
      utf8itemList = utf8itemList.replaceAll("&lt;/SUB>", "&lt;/SUB&gt;");
      utf8itemList = utf8itemList.replaceAll("&lt;SUP>", "&lt;SUP&gt;");
      utf8itemList = utf8itemList.replaceAll("&lt;/SUP>", "&lt;/SUP&gt;");
    } catch (JiBXException e) {
      throw new MarshallingException(ItemVOListWrapper.class.getSimpleName(), e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    if (logger.isDebugEnabled()) {
      logger.debug("transformToItemList(List<ItemVO>) - result: String utf8itemList=\n" + utf8itemList);
    }
    return utf8itemList;
  }

  /**
   * {@inheritDoc}
   */
  /*
  public static String transformToTaskParam(TaskParamVO taskParamVO) throws TechnicalException, MarshallingException {
    logger.debug("transformToTaskParam(TaskParamVO)");
    if (taskParamVO == null) {
      throw new IllegalArgumentException(XmlTransformingService.class.getSimpleName() + ":transformToTaskParam:taskParamVO is null");
    }
    StringWriter sw = null;
    try {
      // marshal XML from TaskParamVO
      IBindingFactory bfact = BindingDirectory.getFactory(TaskParamVO.class);
      IMarshallingContext mctx = bfact.createMarshallingContext();
      mctx.setIndent(2);
      sw = new StringWriter();
      mctx.setOutput(sw);
      mctx.marshalDocument(taskParamVO);
    } catch (JiBXException e) {
      throw new MarshallingException(TaskParamVO.class.getSimpleName(), e);
    } catch (java.lang.ClassCastException e) {
      throw new TechnicalException(e);
    }
    String taskParam = null;
    if (sw != null) {
      taskParam = sw.toString().trim();
    }
    if (logger.isDebugEnabled()) {
      logger.debug("transformToTaskParam(TaskParamVO) - result: String taskParam=" + taskParam);
    }
    return taskParam;
  }
  */

  /**
   * {@inheritDoc}
   */
  /*
  public static URL transformUploadResponseToFileURL(String uploadResponse)
      throws TechnicalException, UnmarshallingException, URISyntaxException {
    logger.debug("transformUploadResponseToFileURL(String) - String uploadResponse=" + uploadResponse);
    if (uploadResponse == null) {
      throw new IllegalArgumentException(
          XmlTransformingService.class.getSimpleName() + ":transformUploadResponseToFileURL:uploadResponse is null");
    }
    URLWrapper urlWrapper = null;
    URL url = null;
    try {
      // unmarshal the url String from upload response into a URLWrapper object
      IBindingFactory bfact = BindingDirectory.getFactory(URLWrapper.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(uploadResponse);
      urlWrapper = (URLWrapper) uctx.unmarshalDocument(sr, null);
      // extract the string from the wrapper and transform it to a URL
  
      // logger.debug("URL: " + PropertyReader.getFrameworkUrl() + ":" + urlWrapper.getUrlString());
  
      url = new URL(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL) + urlWrapper.getUrlString());
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e, e.getRootCause());
      throw new UnmarshallingException(uploadResponse, e);
    } catch (MalformedURLException e) {
      throw new TechnicalException(e);
    } catch (java.lang.ClassCastException e) {
      throw new TechnicalException(e);
    }
    return url;
  }
  */

  /**
   * {@inheritDoc}
   */
  /*
  public static List<VersionHistoryEntryVO> transformToEventVOList(String versionList) throws TechnicalException {
    logger.debug("transformToPubItemVersionVOList(String) - String versionList=\n" + versionList);
    if (versionList == null) {
      throw new IllegalArgumentException(
          XmlTransformingService.class.getSimpleName() + ":transformToPubItemVersionVOList:versionList is null");
    }
    EventVOListWrapper eventVOListWrapper = null;
    try {
      // unmarshal EventVOListWrapper from String
      IBindingFactory bfact = BindingDirectory.getFactory("PubItemVO_PubCollectionVO_input", EventVOListWrapper.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(versionList);
      eventVOListWrapper = (EventVOListWrapper) uctx.unmarshalDocument(sr, null);
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(versionList, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    // unwrap the List<VersionHistoryEntryVO>
    List<VersionHistoryEntryVO> eventList = eventVOListWrapper.getEventVOList();
    return eventList;
  }
  */

  /**
   * Return the child of the node selected by the xPath.
   * 
   * @param node The node.
   * @param xPath The xPath.
   * @return The child of the node selected by the xPath.
   * @throws TransformerException If anything fails.
   */
  /*
  private static Node selectSingleNode(final Node node, final String xpathExpression) throws TransformerException {
    XPathFactory factory = XPathFactory.newInstance();
    XPath xPath = factory.newXPath();
    try {
      return (Node) xPath.evaluate(xpathExpression, node, XPathConstants.NODE);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  */

  /**
   * Return the list of children of the node selected by the xPath.
   * 
   * @param node The node.
   * @param xPath The xPath.
   * @return The list of children of the node selected by the xPath.
   * @throws TransformerException If anything fails.
   */
  /*
  public static NodeList selectNodeList(final Node node, final String xpathExpression) throws TransformerException {
    XPathFactory factory = XPathFactory.newInstance();
    XPath xPath = factory.newXPath();
    try {
      return (NodeList) xPath.evaluate(xpathExpression, node, XPathConstants.NODESET);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  */


  /**
   * {@inheritDoc}
   */
  /*
  public static List<RelationVO> transformToRelationVOList(String relationsXml) throws UnmarshallingException {
    logger.debug("transformToRelationVOList(String) - String relationsXml=\n" + relationsXml);
    if (relationsXml == null) {
      throw new IllegalArgumentException(XmlTransformingService.class.getSimpleName() + ":transformToRelationVOList:relationsXml is null");
    }
    List<RelationVO> relations = new ArrayList<RelationVO>();
  
    try {
      Document relationsDoc = getDocument(relationsXml, true);
      NodeList subjects = selectNodeList(relationsDoc,
          "//*[local-name() = 'Description' and namespace-uri() = 'http://www.w3.org/1999/02/22-rdf-syntax-ns#']");
      logger.debug("subjects length:" + subjects.getLength());
      for (int i = 1; i <= subjects.getLength(); i++) {
        String descriptionXpath =
            "//*[local-name() = 'Description' and namespace-uri() = 'http://www.w3.org/1999/02/22-rdf-syntax-ns#'][" + i + "]";
        logger.debug("descriptionXpath: " + descriptionXpath);
        String subject = getAttributeValue(relationsDoc, descriptionXpath, "rdf:about");
        String subjectObjectId = subject.substring(subject.lastIndexOf('/') + 1);
        ItemRO subjectRef = new ItemRO(subjectObjectId);
  
        NodeList subjectRelations = selectNodeList(relationsDoc,
            "//*[local-name() = 'Description' and namespace-uri() = 'http://www.w3.org/1999/02/22-rdf-syntax-ns#'][" + i + "]/*");
        for (int j = 1; j <= subjectRelations.getLength(); j++) {
          String relationXpath = "//*[local-name() = 'Description' and namespace-uri() = 'http://www.w3.org/1999/02/22-rdf-syntax-ns#']["
              + i + "]/*[" + j + "]";
          logger.debug("relationXpath: " + relationXpath);
          Node relation = selectSingleNode(relationsDoc, relationXpath);
          if (relation.getLocalName() == "isRevisionOf") {
            String predicate = relation.getAttributes().getNamedItem("rdf:resource").getTextContent();
            String predicateObjectId = predicate.substring(predicate.lastIndexOf('/') + 1);
            ItemRO predicateRef = new ItemRO(predicateObjectId);
  
            // add relation to list
            logger.debug(subjectObjectId + " isRevisionOf " + predicateObjectId);
            RelationVO relationVO = new RelationVO();
            relationVO.setSourceItemRef(subjectRef);
            relationVO.settype(RelationType.ISREVISIONOF);
            relationVO.setTargetItemRef(predicateRef);
            relations.add(relationVO);
          }
          if (relation.getLocalName() == "member") {
            // using ItemRefs as workarund here, although it can be containers
            String object = relation.getAttributes().getNamedItem("rdf:resource").getTextContent();
            String objectObjectId = object.substring(object.lastIndexOf('/') + 1);
            ItemRO objectRef = new ItemRO(objectObjectId);
  
            // add relation to list
            logger.debug(subjectObjectId + " hasMember " + objectObjectId);
            RelationVO relationVO = new RelationVO();
            relationVO.setSourceItemRef(subjectRef);
            relationVO.settype(RelationType.HASMEMBER);
            relationVO.setTargetItemRef(objectRef);
            relations.add(relationVO);
          }
        }
      }
    } catch (Exception e) {
      throw new UnmarshallingException(relationsXml, e);
    }
  
    return relations;
  }
  */

  /**
   * Parse the given xml String into a Document.
   * 
   * @param xml The xml String.
   * @param namespaceAwareness namespace awareness (default is false)
   * @return The Document.
   * @throws Exception If anything fails.
   */
  /*
  protected static Document getDocument(final String xml, final boolean namespaceAwareness) throws Exception {
    if (xml == null) {
      throw new IllegalArgumentException(":getDocument:xml is null");
    }
    String charset = "UTF-8";
    Document result = null;
    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    docBuilderFactory.setNamespaceAware(namespaceAwareness);
    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
    result = docBuilder.parse(new ByteArrayInputStream(xml.getBytes(charset)));
    result.getDocumentElement().normalize();
    return result;
  }
  */

  /**
   * Return the text value of the selected attribute.
   * 
   * @param node The node.
   * @param xPath The xpath to select the node containint the attribute,
   * @param attributeName The name of the attribute.
   * @return The text value of the selected attribute.
   * @throws Exception If anything fails.
   */
  /*
  public static String getAttributeValue(final Node node, final String xPath, final String attributeName) throws Exception {
    if (node == null) {
      throw new IllegalArgumentException(":getAttributeValue:node is null");
    }
    if (xPath == null) {
      throw new IllegalArgumentException(":getAttributeValue:xPath is null");
    }
    if (attributeName == null) {
      throw new IllegalArgumentException(":getAttributeValue:attributeName is null");
    }
    String result = null;
    Node attribute = selectSingleNode(node, xPath);
    if (attribute.hasAttributes()) {
      result = attribute.getAttributes().getNamedItem(attributeName).getTextContent();
    }
    return result;
  }
  */

  public static PubItemVO transformToPubItem(String itemXml) throws TechnicalException {
    ItemVO itemVO = transformToItem(itemXml);
    if (itemVO.getMetadataSets().size() > 0 && itemVO.getMetadataSets().get(0) instanceof MdsPublicationVO) {
      return new PubItemVO(itemVO);
    } else {
      logger.warn("Cannot transform item xml to PubItemVO");
      return null;
    }
  }

  public static List<PubItemVO> transformToPubItemList(String itemList) throws TechnicalException {
    List<? extends ItemVO> list = transformToItemList(itemList);
    List<PubItemVO> newList = new ArrayList<PubItemVO>();
    for (ItemVO itemVO : list) {
      PubItemVO pubItemVO = new PubItemVO(itemVO);
      newList.add(pubItemVO);
    }
    return newList;
  }

  /**
   * {@inheritDoc}
   */
  /*
  public static List<? extends ValueObject> transformToMemberList(String memberList) throws TechnicalException {
    logger.debug("transformToMemberList(String) - String memberList=\n" + memberList);
    if (memberList == null) {
      throw new IllegalArgumentException(XmlTransformingService.class.getSimpleName() + ":transformToMemberList:memberList is null");
    }
    MemberListWrapper mListWrapper = null;
    try {
      // unmarshal MemberListWrapper from String
      IBindingFactory bfact = BindingDirectory.getFactory("PubItemVO_PubCollectionVO_input", MemberListWrapper.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(memberList);
      Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
      mListWrapper = (MemberListWrapper) unmarshalledObject;
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(memberList, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    // unwrap the List<ContainerVO>
    List<? extends ValueObject> memList = mListWrapper.getMemberList();
  
    return memList;
  }
  */

  /**
   * {@inheritDoc}
   */
  /*
  public static ResultVO transformToResult(String resultXml) throws TechnicalException {
    logger.debug("transformToResult(String) - String result=" + resultXml);
    if (resultXml == null) {
      throw new IllegalArgumentException(XmlTransformingService.class.getSimpleName() + ":transformToResult:result is null");
    }
    ResultVO resultVO = null;
    try {
      IBindingFactory bfact = BindingDirectory.getFactory("PubItemVO_PubCollectionVO_input", ResultVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(resultXml);
      resultVO = (ResultVO) uctx.unmarshalDocument(sr, null);
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error("Error transforming result", e);
      throw new UnmarshallingException(resultXml, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    return resultVO;
  }
  */

  public static FileVO transformToFileVO(String fileXML) throws TechnicalException {
    logger.debug("transformToFileVO(String) - String file=\n" + fileXML);
    if (fileXML == null) {
      throw new IllegalArgumentException(XmlTransformingService.class.getSimpleName() + ":transformTofileVO: fileXML is null");
    }
    FileVO fileVO = null;
    try {
      IBindingFactory bfact = BindingDirectory.getFactory("PubItemVO_PubCollectionVO_input", FileVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(fileXML);
      Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
      fileVO = (FileVO) unmarshalledObject;
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(fileXML, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }

    return fileVO;
  }

  public static PidServiceResponseVO transformToPidServiceResponse(String pidServiceResponseXml) throws TechnicalException {
    logger.debug("transformToPidServiceResponse(String) - String pidServiceResponse=\n" + pidServiceResponseXml);
    if (pidServiceResponseXml == null) {
      throw new IllegalArgumentException(
          XmlTransformingService.class.getSimpleName() + ":transformToPidServiceResponse: pidServiceResponseXml is null");
    }
    PidServiceResponseVO pidServiceResponseVO = null;

    try {
      // unmarshal pidServiceResponse from String
      IBindingFactory bfact = BindingDirectory.getFactory(PidServiceResponseVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(pidServiceResponseXml);
      Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
      pidServiceResponseVO = (PidServiceResponseVO) unmarshalledObject;
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(pidServiceResponseXml, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }

    return pidServiceResponseVO;
  }

  /*
  public static SearchRetrieveResponseVO transformToSearchRetrieveResponse(String searchRetrieveResponseXml) throws TechnicalException {
    logger.debug("transformToSearchRetrieveResponse(String) - String searchRetrieveResponse=\n" + searchRetrieveResponseXml);
    if (searchRetrieveResponseXml == null) {
      throw new IllegalArgumentException(
          XmlTransformingService.class.getSimpleName() + ":transformToSearchRetrieveResponse: searchRetrieveResponseXml is null");
    }
    SearchRetrieveResponseVO searchRetrieveResponseVO = null;
  
    try {
      // unmarshal pidServiceResponse from String
      IBindingFactory bfact = BindingDirectory.getFactory("PubItemVO_PubCollectionVO_input", SearchRetrieveResponseVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(searchRetrieveResponseXml);
      Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
      searchRetrieveResponseVO = (SearchRetrieveResponseVO) unmarshalledObject;
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(searchRetrieveResponseXml, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
  
    return searchRetrieveResponseVO;
  }
  */

  /*
  public static SearchRetrieveResponseVO transformToSearchRetrieveResponseOrganizationVO(String searchRetrieveResponseXml)
      throws TechnicalException {
    logger.debug("transformToSearchRetrieveResponse(String) - String searchRetrieveResponse=\n" + searchRetrieveResponseXml);
    if (searchRetrieveResponseXml == null) {
      throw new IllegalArgumentException(
          XmlTransformingService.class.getSimpleName() + ":transformToSearchRetrieveResponse: searchRetrieveResponseXml is null");
    }
    SearchRetrieveResponseVO searchRetrieveResponseVO = null;
  
    try {
      // unmarshal pidServiceResponse from String
      IBindingFactory bfact = BindingDirectory.getFactory("AffiliationVO_input", SearchRetrieveResponseVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(searchRetrieveResponseXml);
      Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
      searchRetrieveResponseVO = (SearchRetrieveResponseVO) unmarshalledObject;
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(searchRetrieveResponseXml, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
  
    return searchRetrieveResponseVO;
  }
  */

  /*
  public static SearchRetrieveResponseVO transformToSearchRetrieveResponseUserGroup(String searchRetrieveResponseXml)
      throws TechnicalException {
    return transformToSearchRetrieveResponseGrant(searchRetrieveResponseXml);
  }
  *
  
  public static SearchRetrieveResponseVO transformToSearchRetrieveResponseGrant(String searchRetrieveResponseXml)
      throws TechnicalException {
    logger.debug("transformToSearchRetrieveResponse(String) - String searchRetrieveResponse=\n" + searchRetrieveResponseXml);
    if (searchRetrieveResponseXml == null) {
      throw new IllegalArgumentException(
          XmlTransformingService.class.getSimpleName() + ":transformToSearchRetrieveResponse: searchRetrieveResponseXml is null");
    }
    SearchRetrieveResponseVO searchRetrieveResponseVO = null;
  
    try {
      // unmarshal pidServiceResponse from String
      IBindingFactory bfact = BindingDirectory.getFactory("binding", SearchRetrieveResponseVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(searchRetrieveResponseXml);
      Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
      searchRetrieveResponseVO = (SearchRetrieveResponseVO) unmarshalledObject;
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(searchRetrieveResponseXml, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
  
    return searchRetrieveResponseVO;
  }
  
  public static SearchRetrieveResponseVO transformToSearchRetrieveResponseGrantVO(String searchRetrieveResponseXml)
      throws TechnicalException {
    logger.debug("transformToSearchRetrieveResponse(String) - String searchRetrieveResponse=\n" + searchRetrieveResponseXml);
    if (searchRetrieveResponseXml == null) {
      throw new IllegalArgumentException(
          XmlTransformingService.class.getSimpleName() + ":transformToSearchRetrieveResponse: searchRetrieveResponseXml is null");
    }
    SearchRetrieveResponseVO searchRetrieveResponseVO = null;
  
    try {
      // unmarshal pidServiceResponse from String
      IBindingFactory bfact = BindingDirectory.getFactory("GrantVOListWrapper", SearchRetrieveResponseVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(searchRetrieveResponseXml);
      Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
      searchRetrieveResponseVO = (SearchRetrieveResponseVO) unmarshalledObject;
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(searchRetrieveResponseXml, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
  
    return searchRetrieveResponseVO;
  }
  
  /*
  public static SearchRetrieveResponseVO transformToSearchRetrieveResponseAccountUser(String searcRetrieveResponseXml)
      throws TechnicalException {
    logger.debug("transformToSearchRetrieveResponse(String) - String searchRetrieveResponse=\n" + searcRetrieveResponseXml);
    if (searcRetrieveResponseXml == null) {
      throw new IllegalArgumentException(
          XmlTransformingService.class.getSimpleName() + ":transformToSearchRetrieveResponse: searchRetrieveResponseXml is null");
    }
    SearchRetrieveResponseVO searchRetrieveResponseVO = null;
  
    try {
      // unmarshal pidServiceResponse from String
      IBindingFactory bfact = BindingDirectory.getFactory("AccountUserVO", SearchRetrieveResponseVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(searcRetrieveResponseXml);
      Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
      searchRetrieveResponseVO = (SearchRetrieveResponseVO) unmarshalledObject;
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(searcRetrieveResponseXml, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
  
    return searchRetrieveResponseVO;
  }
  */

  /**
   * {@inheritDoc}
   */
  /*
  public static List<UserAttributeVO> transformToUserAttributesList(String userAttributesList) throws TechnicalException {
    logger.debug("transformToUserAttributesList(String) - String userAttributesList=\n" + userAttributesList);
    if (userAttributesList == null) {
      throw new IllegalArgumentException(
          XmlTransformingService.class.getSimpleName() + ":transformToUserAttributesList:userAttributesList is null");
    }
    UserAttributesWrapper listWrapper = null;
    try {
      // unmarshal MemberListWrapper from String
      IBindingFactory bfact = BindingDirectory.getFactory(UserAttributesWrapper.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(userAttributesList);
      Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
      listWrapper = (UserAttributesWrapper) unmarshalledObject;
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(userAttributesList, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
  
    return listWrapper.getUserAttributes();
  }
  */
}
