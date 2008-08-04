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

package de.mpg.escidoc.services.common.xmltransforming;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.management.j2ee.statistics.Stats;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.rpc.ServiceException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.RemoteBinding;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.referenceobjects.AffiliationRO;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.AffiliationPathVO;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.ContainerVO;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.common.valueobjects.ExportFormatVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.GrantVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.LockVO;
import de.mpg.escidoc.services.common.valueobjects.PidTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemResultVO;
import de.mpg.escidoc.services.common.valueobjects.RelationVO;
import de.mpg.escidoc.services.common.valueobjects.TaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.TocItemVO;
import de.mpg.escidoc.services.common.valueobjects.TocVO;
import de.mpg.escidoc.services.common.valueobjects.ValueObject;
import de.mpg.escidoc.services.common.valueobjects.VersionHistoryEntryVO;
import de.mpg.escidoc.services.common.valueobjects.RelationVO.RelationType;
import de.mpg.escidoc.services.common.valueobjects.face.FaceItemVO;
import de.mpg.escidoc.services.common.valueobjects.face.MdsFaceVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportDefinitionVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportParamsVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordVO;
import de.mpg.escidoc.services.common.xmltransforming.exceptions.MarshallingException;
import de.mpg.escidoc.services.common.xmltransforming.exceptions.UnmarshallingException;
import de.mpg.escidoc.services.common.xmltransforming.wrappers.AffiliationPathVOListWrapper;
import de.mpg.escidoc.services.common.xmltransforming.wrappers.AffiliationROListWrapper;
import de.mpg.escidoc.services.common.xmltransforming.wrappers.AffiliationVOListWrapper;
import de.mpg.escidoc.services.common.xmltransforming.wrappers.ContainerVOListWrapper;
import de.mpg.escidoc.services.common.xmltransforming.wrappers.ContextVOListWrapper;
import de.mpg.escidoc.services.common.xmltransforming.wrappers.EventVOListWrapper;
import de.mpg.escidoc.services.common.xmltransforming.wrappers.ExportFormatVOListWrapper;
import de.mpg.escidoc.services.common.xmltransforming.wrappers.GrantVOListWrapper;
import de.mpg.escidoc.services.common.xmltransforming.wrappers.ItemVOListWrapper;
import de.mpg.escidoc.services.common.xmltransforming.wrappers.MemberListWrapper;
import de.mpg.escidoc.services.common.xmltransforming.wrappers.StatisticReportDefinitionVOListWrapper;
import de.mpg.escidoc.services.common.xmltransforming.wrappers.StatisticReportWrapper;
import de.mpg.escidoc.services.common.xmltransforming.wrappers.URLWrapper;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * EJB implementation of interface {@link XmlTransforming}.
 * 
 * @author Johannes Mueller (initial creation)
 * @version $Revision: 635 $ $LastChangedDate: 2007-11-21 17:12:27 +0100 (Wed, 21 Nov 2007) $Author: mfranke $
 * @revised by MuJ: 21.08.2007
 */
@Stateless
@Remote
@RemoteBinding(jndiBinding = XmlTransforming.SERVICE_NAME)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class XmlTransformingBean implements XmlTransforming
{
    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(XmlTransformingBean.class);

    /**
     * {@inheritDoc}
     */
    public AccountUserVO transformToAccountUser(String user) throws TechnicalException, UnmarshallingException
    {
        logger.debug("transformToAccountUser(String) - String user=" + user);
        if (user == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToAccountUser:user is null");
        }
        AccountUserVO userVO = null;
        try
        {
            // unmarshal AccountUserVO from String
            IBindingFactory bfact = BindingDirectory.getFactory(AccountUserVO.class);
            IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
            StringReader sr = new StringReader(user);
            userVO = (AccountUserVO)uctx.unmarshalDocument(sr, null);
        }
        catch (JiBXException e)
        {
            // throw a new UnmarshallingException, log the root cause of the JiBXException first
            logger.error(e.getRootCause());
            throw new UnmarshallingException(user, e);
        }
        catch (java.lang.ClassCastException e)
        {
            throw new TechnicalException(e);
        }
        return userVO;
    }

    /**
     * {@inheritDoc}
     */
    public List<AffiliationVO> transformToAffiliationList(String organizationalUnitList) throws TechnicalException, UnmarshallingException
    {
        logger.debug("transformToAffiliationList(String) - String oranizationalUnitList=" + organizationalUnitList);
        if (organizationalUnitList == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToAffiliationList:organizationalUnitList is null");
        }
        AffiliationVOListWrapper affiliationVOListWrapper;
        try
        {
            // unmarshal AffiliationVOListWrapper from String
            IBindingFactory bfact = BindingDirectory.getFactory("AffiliationVO_input", AffiliationVOListWrapper.class);
            IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
            StringReader sr = new StringReader(organizationalUnitList);
            affiliationVOListWrapper = (AffiliationVOListWrapper)uctx.unmarshalDocument(sr, null);
        }
        catch (JiBXException e)
        {
            // throw a new UnmarshallingException, log the root cause of the JiBXException first
            logger.error(e.getRootCause());
            throw new UnmarshallingException(organizationalUnitList, e);
        }
        catch (ClassCastException e)
        {
            throw new TechnicalException(e);
        }
        // unwrap the List<AffiliationVO>
        List<AffiliationVO> affiliationList = affiliationVOListWrapper.getAffiliationVOList();
        return affiliationList;
    }

    /**
     * {@inheritDoc}
     */
    public List<AffiliationRO> transformToParentAffiliationList(String parentOrganizationalUnitList) throws TechnicalException, UnmarshallingException
    {
        logger.debug("transformToParentAffiliationList(String) - String parentOrganizationalUnitList=" + parentOrganizationalUnitList);
        if (parentOrganizationalUnitList == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToAffiliationList:organizationalUnitList is null");
        }
        AffiliationROListWrapper affiliationROListWrapper;
        try
        {
            // unmarshal AffiliationVOListWrapper from String
            IBindingFactory bfact = BindingDirectory.getFactory("AffiliationVO_input", AffiliationROListWrapper.class);
            IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
            StringReader sr = new StringReader(parentOrganizationalUnitList);
            affiliationROListWrapper = (AffiliationROListWrapper)uctx.unmarshalDocument(sr, null);
        }
        catch (JiBXException e)
        {
            // throw a new UnmarshallingException, log the root cause of the JiBXException first
            logger.error(e.getRootCause());
            throw new UnmarshallingException(parentOrganizationalUnitList, e);
        }
        catch (ClassCastException e)
        {
            throw new TechnicalException(e);
        }
        // unwrap the List<AffiliationVO>
        List<AffiliationRO> affiliationList = affiliationROListWrapper.getAffiliationROList();
        return affiliationList;
    }

    /**
     * {@inheritDoc}
     */
    public List<AffiliationPathVO> transformToAffiliationPathList(String pathList) throws TechnicalException, UnmarshallingException
    {
        logger.debug("transformToAffiliationPathList(String) - String pathList=" + pathList);
        if (pathList == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToAffiliationPathList:pathList is null");
        }
        List<AffiliationPathVO> resultList;
        AffiliationPathVOListWrapper affiliationPathVOListWrapper = null;
        try
        {
            // unmarshal AffiliationPathVOListWrapper from String
            IBindingFactory bfact = BindingDirectory.getFactory("AffiliationVO_input", AffiliationPathVOListWrapper.class);
            IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
            StringReader sr = new StringReader(pathList);
            affiliationPathVOListWrapper = (AffiliationPathVOListWrapper)uctx.unmarshalDocument(sr, null);
        }
        catch (JiBXException e)
        {
            // throw a new UnmarshallingException, log the root cause of the JiBXException first
            logger.error(e.getRootCause());
            throw new UnmarshallingException(pathList, e);
        }
        catch (ClassCastException e)
        {
            throw new TechnicalException(e);
        }
        // unwrap the List<AffiliationPathVO>
        resultList = affiliationPathVOListWrapper.getAffiliationPathVOList();
        return resultList;
    }

    /**
     * {@inheritDoc}
     */
    public final List<ExportFormatVO> transformToExportFormatVOList(String formatList) throws TechnicalException
    {
        logger.debug("transformToExportFormatVOList(String) - String formatList=" + formatList);
        if (formatList == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToExportFormatVOList:formatList is null");
        }
        ExportFormatVOListWrapper exportFormatVOListWrapper = null;
        try
        {
            // unmarshall ExportFormatVOListWrapper from String
            IBindingFactory bfact = BindingDirectory.getFactory("ExportFormatVOListWrapper", ExportFormatVOListWrapper.class);
            IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
            StringReader sr = new StringReader(formatList);
            exportFormatVOListWrapper = (ExportFormatVOListWrapper)uctx.unmarshalDocument(sr, null);
        }
        catch (JiBXException e)
        {
            // throw a new UnmarshallingException, log the root cause of the JiBXException first
            logger.error(e.getRootCause());
            throw new UnmarshallingException(formatList, e);
        }
        catch (ClassCastException e)
        {
            throw new TechnicalException(e);
        }
        // unwrap the List<ExportFormatVO>
        List<ExportFormatVO> exportFormatVOList = exportFormatVOListWrapper.getExportFormatVOList();
        return exportFormatVOList;
    }

    /**
     * {@inheritDoc}
     */
    public String transformToExportParams(ExportFormatVO exportFormat) throws TechnicalException, MarshallingException
    {
        // TODO FrM: Implement
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public final List<GrantVO> transformToGrantVOList(String formatList) throws TechnicalException
    {
        logger.debug("transformToGrantVOList(String) - String formatList=" + formatList);
        if (formatList == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToGrantVOList:formatList is null");
        }
        GrantVOListWrapper grantVOListWrapper = null;
        try
        {
            // unmarshall GrantVOListWrapper from String
            IBindingFactory bfact = BindingDirectory.getFactory("GrantVOListWrapper", GrantVOListWrapper.class);
            IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
            StringReader sr = new StringReader(formatList);
            grantVOListWrapper = (GrantVOListWrapper)uctx.unmarshalDocument(sr, null);
        }
        catch (JiBXException e)
        {
            // throw a new UnmarshallingException, log the root cause of the JiBXException first
            logger.error(e.getRootCause(), e);
            throw new UnmarshallingException(formatList, e);
        }
        catch (ClassCastException e)
        {
            throw new TechnicalException(e);
        }
        // unwrap the List<GrantVO>
        List<GrantVO> grantVOList = grantVOListWrapper.getGrantVOList();
        return grantVOList;
    }

    /**
     * {@inheritDoc}
     */
    public String transformToFilterTaskParam(FilterTaskParamVO filterTaskParamVO) throws TechnicalException, MarshallingException
    {
        logger.debug("transformToFilterTaskParam(FilterTaskParamVO)");
        if (filterTaskParamVO == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToFilterTaskParam:filterTaskParamVO is null");
        }
        StringWriter sw = null;
        try
        {
            // marshal XML from FilterTaskParamVO
            IBindingFactory bfact = BindingDirectory.getFactory(FilterTaskParamVO.class);
            IMarshallingContext mctx = bfact.createMarshallingContext();
            mctx.setIndent(2);
            sw = new StringWriter();
            mctx.setOutput(sw);
            mctx.marshalDocument(filterTaskParamVO);
        }
        catch (JiBXException e)
        {
            throw new MarshallingException(filterTaskParamVO.getClass().getSimpleName(), e);
        }
        catch (java.lang.ClassCastException e)
        {
            throw new TechnicalException(e);
        }
        String filterTaskParam = null;
        if (sw != null)
        {
            filterTaskParam = sw.toString().trim();
            if (logger.isDebugEnabled())
            {
                logger.debug("transformToFilterTaskParam(FilterTaskParamVO) - result: String filterTaskParam=" + filterTaskParam);
            }
            return filterTaskParam;
        }
        throw new TechnicalException("Marshalling result is null");
    }

    /**
     * {@inheritDoc}
     */
    public LockVO transformToLockVO(String lockInformation) throws TechnicalException, UnmarshallingException
    {
        logger.debug("transformToLockVO(String) - String lockInformation=" + lockInformation);
        if (lockInformation == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToLockVO:lockInformation is null");
        }
        // TODO MuJ: Implement if needed.
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public AffiliationVO transformToAffiliation(String organizationalUnit) throws TechnicalException, UnmarshallingException
    {
        logger.debug("transformToAffiliation(String) - String organizationalUnit=" + organizationalUnit);
        if (organizationalUnit == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToAffiliation:organizationalUnit is null");
        }
        AffiliationVO affiliationVO = null;
        try
        {
            // unmarshal AffiliationVO from String
            IBindingFactory bfact = BindingDirectory.getFactory("AffiliationVO_input", AffiliationVO.class);
            IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
            StringReader sr = new StringReader(organizationalUnit);
            affiliationVO = (AffiliationVO)uctx.unmarshalDocument(sr, null);
        }
        catch (JiBXException e)
        {
            // throw a new UnmarshallingException, log the root cause of the JiBXException first
            logger.error(e.getRootCause());
            throw new UnmarshallingException(organizationalUnit, e);
        }
        catch (java.lang.ClassCastException e)
        {
            throw new TechnicalException(e);
        }
        catch (java.lang.reflect.UndeclaredThrowableException e)
        {
            throw new UnmarshallingException(organizationalUnit, new TechnicalException("An UndeclaredThrowableException occured in " + getClass().getSimpleName() + ":transformToAffiliation", e
                    .getUndeclaredThrowable()));
        }
        return affiliationVO;
    }

    /**
     * {@inheritDoc}
     */
    public String transformToOrganizationalUnit(AffiliationVO affiliationVO) throws TechnicalException, MarshallingException
    {
        logger.debug("transformToOrganizationalUnit(AffiliationVO)");
        if (affiliationVO == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToOrganizationalUnit:affiliationVO is null");
        }
        String utf8ou = null;
        try
        {
            IBindingFactory bfact = BindingDirectory.getFactory("AffiliationVO_output", AffiliationVO.class);
            // marshal object (with nice indentation, as UTF-8)
            IMarshallingContext mctx = bfact.createMarshallingContext();
            mctx.setIndent(2);
            StringWriter sw = new StringWriter();
            mctx.setOutput(sw);
            mctx.marshalDocument(affiliationVO, "UTF-8", null, sw);
            utf8ou = sw.toString().trim();
        }
        catch (JiBXException e)
        {
            throw new MarshallingException(affiliationVO.getClass().getSimpleName(), e);
        }
        catch (ClassCastException e)
        {
            throw new TechnicalException(e);
        }
        if (logger.isDebugEnabled())
        {
            logger.debug("transformToOrganizationalUnit(AffiliationVO) - result: String utf8ou=" + utf8ou);
        }
        return utf8ou;
    }

    /**
     * {@inheritDoc}
     */
    public String transformToItem(ItemVO itemVO) throws TechnicalException
    {
        logger.debug("transformToItem(PubItemVO)");
        if (itemVO == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToItem:pubItemVO is null");
        }
        String utf8item = null;
        try
        {
            IBindingFactory bfact = BindingDirectory.getFactory("PubItemVO_PubCollectionVO_output", PubItemVO.class);
            // marshal object (with nice indentation, as UTF-8)
            IMarshallingContext mctx = bfact.createMarshallingContext();
            mctx.setIndent(2);
            StringWriter sw = new StringWriter();
            mctx.setOutput(sw);
            mctx.marshalDocument(itemVO, "UTF-8", null, sw);
            // use the following call to omit the leading "<?xml" tag of the generated XML
            // mctx.marshalDocument(pubItemVO);
            utf8item = sw.toString().trim();
        }
        catch (JiBXException e)
        {
            throw new MarshallingException(itemVO.getClass().getSimpleName(), e);
        }
        catch (ClassCastException e)
        {
            throw new TechnicalException(e);
        }
        if (logger.isDebugEnabled())
        {
            logger.debug("transformToItem(ItemVO) - result: String utf8item=" + utf8item);
        }
        return utf8item;
    }

    /**
     * {@inheritDoc}
     */
    public ContextVO transformToContext(String context) throws TechnicalException
    {
        logger.debug("transformToPubCollection(String) - String context=" + context);
        if (context == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToPubContext:context is null");
        }
        ContextVO contextVO = null;
        try
        {
            // unmarshal ContextVO from String
            IBindingFactory bfact = BindingDirectory.getFactory("PubItemVO_PubCollectionVO_input", ContextVO.class);
            IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
            StringReader sr = new StringReader(context);
            contextVO = (ContextVO)uctx.unmarshalDocument(sr, null);
        }
        catch (JiBXException e)
        {
            // throw a new UnmarshallingException, log the root cause of the JiBXException first
            logger.error(e.getRootCause());
            throw new UnmarshallingException(context, e);
        }
        catch (ClassCastException e)
        {
            throw new TechnicalException(context, e);
        }
        return contextVO;
    }

    /**
     * {@inheritDoc}
     */
    public List<ContextVO> transformToContextList(String contextList) throws TechnicalException
    {
        logger.debug("transformToPubCollectionList(String) - String contextList=" + contextList);
        if (contextList == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToPubCollectionList:contextList is null");
        }
        ContextVOListWrapper pubContextVOListWrapper = null;
        try
        {
            // unmarshal ContextVOListWrapper from String
            IBindingFactory bfact = BindingDirectory.getFactory("PubItemVO_PubCollectionVO_input", ContextVOListWrapper.class);
            IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
            StringReader sr = new StringReader(contextList);
            pubContextVOListWrapper = (ContextVOListWrapper)uctx.unmarshalDocument(sr, null);
        }
        catch (JiBXException e)
        {
            // throw a new UnmarshallingException, log the root cause of the JiBXException first
            logger.error(e.getRootCause());
            throw new UnmarshallingException(contextList, e);
        }
        catch (ClassCastException e)
        {
            throw new TechnicalException(e);
        }
        // unwrap the List<ContextVO>
        List<ContextVO> pubContextList = pubContextVOListWrapper.getContextVOList();
        return pubContextList;
    }

    /**
     * {@inheritDoc}
     */
    public ItemVO transformToItem(String item) throws TechnicalException
    {
        logger.debug("transformToPubItem(String) - String item=" + item);
        if (item == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToPubItem:item is null");
        }
        PubItemVO pubItemVO = null;
        try
        {
            IBindingFactory bfact = BindingDirectory.getFactory("PubItemVO_PubCollectionVO_input", ItemVO.class);
            IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
            StringReader sr = new StringReader(item);
            pubItemVO = (PubItemVO)uctx.unmarshalDocument(sr, null);
        }
        catch (JiBXException e)
        {
            // throw a new UnmarshallingException, log the root cause of the JiBXException first
            logger.error(e.getRootCause());
            throw new UnmarshallingException(item, e);
        }
        catch (ClassCastException e)
        {
            throw new TechnicalException(e);
        }
        return pubItemVO;
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends ItemVO> transformToItemList(String itemListXml) throws TechnicalException
    {
        logger.debug("transformToPubItemList(String) - String itemList=\n" + itemListXml);
        if (itemListXml == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToPubItemList:itemList is null");
        }
        ItemVOListWrapper itemVOListWrapper = null;
        try
        {
            // unmarshal ItemVOListWrapper from String
            IBindingFactory bfact = BindingDirectory.getFactory("PubItemVO_PubCollectionVO_input", ItemVOListWrapper.class);
            IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
            StringReader sr = new StringReader(itemListXml);
            Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
            itemVOListWrapper = (ItemVOListWrapper)unmarshalledObject;
        }
        catch (JiBXException e)
        {
            // throw a new UnmarshallingException, log the root cause of the JiBXException first
            logger.error(e.getRootCause());
            throw new UnmarshallingException(itemListXml, e);
        }
        catch (ClassCastException e)
        {
            throw new TechnicalException(e);
        }
        // unwrap the List<ItemVO>
        List<? extends ItemVO> itemList = itemVOListWrapper.getItemVOList();

        return itemList;
    }
    
   

    /**
     * {@inheritDoc}
     */
    public PubItemResultVO transformToPubItemResultVO(String searchResultItem) throws TechnicalException
    {
        if (searchResultItem == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToPubItemResultVO:searchResultItem is null");
        }
        PubItemResultVO pubItemResultVO = null;
        try
        {
            // unmarshall PubItemResultVO from String
            IBindingFactory bfact = BindingDirectory.getFactory("PubItemVO_PubCollectionVO_input", PubItemResultVO.class);
            IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
            StringReader sr = new StringReader(searchResultItem);
            pubItemResultVO = (PubItemResultVO)uctx.unmarshalDocument(sr, "UTF-8");

        }
        catch (JiBXException e)
        {
            // throw a new UnmarshallingException, log the root cause of the JiBXException first
            logger.error(e.getRootCause());
            throw new UnmarshallingException(searchResultItem, e);
        }
        catch (ClassCastException e)
        {
            throw new TechnicalException(e);
        }
        return pubItemResultVO;
    }

    /**
     * {@inheritDoc}
     */
    public String transformToItemList(List<? extends ItemVO> itemVOList) throws TechnicalException
    {
        logger.debug("transformToItemList(List<ItemVO>)");
        if (itemVOList == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToItemList:pubItemVOList is null");
        }
        // wrap the item list into the according wrapper class
        ItemVOListWrapper listWrapper = new ItemVOListWrapper();
        listWrapper.setItemVOList(itemVOList);
        // transform the wrapper class into XML
        String utf8itemList = null;
        try
        {
            IBindingFactory bfact = BindingDirectory.getFactory("PubItemVO_PubCollectionVO_output", ItemVOListWrapper.class);
            // marshal object (with nice indentation, as UTF-8)
            IMarshallingContext mctx = bfact.createMarshallingContext();
            mctx.setIndent(2);
            StringWriter sw = new StringWriter();
            mctx.setOutput(sw);
            mctx.marshalDocument(listWrapper, "UTF-8", null, sw);
            utf8itemList = sw.toString().trim();
        }
        catch (JiBXException e)
        {
            throw new MarshallingException(itemVOList.getClass().getSimpleName(), e);
        }
        catch (ClassCastException e)
        {
            throw new TechnicalException(e);
        }
        if (logger.isDebugEnabled())
        {
            logger.debug("transformToItemList(List<ItemVO>) - result: String utf8itemList=\n" + utf8itemList);
        }
        return utf8itemList;
    }

    /**
     * {@inheritDoc}
     */
    public String transformToTaskParam(TaskParamVO taskParamVO) throws TechnicalException, MarshallingException
    {
        logger.debug("transformToTaskParam(TaskParamVO)");
        if (taskParamVO == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToTaskParam:taskParamVO is null");
        }
        StringWriter sw = null;
        try
        {
            // marshal XML from TaskParamVO
            IBindingFactory bfact = BindingDirectory.getFactory(TaskParamVO.class);
            IMarshallingContext mctx = bfact.createMarshallingContext();
            mctx.setIndent(2);
            sw = new StringWriter();
            mctx.setOutput(sw);
            mctx.marshalDocument(taskParamVO);
        }
        catch (JiBXException e)
        {
            throw new MarshallingException(taskParamVO.getClass().getSimpleName(), e);
        }
        catch (java.lang.ClassCastException e)
        {
            throw new TechnicalException(e);
        }
        String taskParam = null;
        if (sw != null)
        {
            taskParam = sw.toString().trim();
        }
        if (logger.isDebugEnabled())
        {
            logger.debug("transformToTaskParam(TaskParamVO) - result: String taskParam=" + taskParam);
        }
        return taskParam;
    }

    /**
     * {@inheritDoc}
     */
    public String transformToPidTaskParam(PidTaskParamVO pidTaskParamVO) throws TechnicalException, MarshallingException
    {
        logger.debug("transformToPidTaskParam(PidTaskParamVO)");
        if (pidTaskParamVO == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToPidTaskParam:pidTaskParamVO is null");
        }
        StringWriter sw = null;
        try
        {
            // marshal XML from PidTaskParamVO
            IBindingFactory bfact = BindingDirectory.getFactory(PidTaskParamVO.class);
            IMarshallingContext mctx = bfact.createMarshallingContext();
            mctx.setIndent(2);
            sw = new StringWriter();
            mctx.setOutput(sw);
            mctx.marshalDocument(pidTaskParamVO);
        }
        catch (JiBXException e)
        {
            throw new MarshallingException(pidTaskParamVO.getClass().getSimpleName(), e);
        }
        catch (java.lang.ClassCastException e)
        {
            throw new TechnicalException(e);
        }
        String pidTaskParam = null;
        if (sw != null)
        {
            pidTaskParam = sw.toString().trim();
        }
        if (logger.isDebugEnabled())
        {
            logger.debug("transformToPidTaskParam(PidTaskParamVO) - result: String pidTaskParam=" + pidTaskParam);
        }
        return pidTaskParam;
    }

    /**
     * {@inheritDoc}
     */
    public URL transformUploadResponseToFileURL(String uploadResponse) throws TechnicalException, UnmarshallingException, URISyntaxException
    {
        logger.debug("transformUploadResponseToFileURL(String) - String uploadResponse=" + uploadResponse);
        if (uploadResponse == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformUploadResponseToFileURL:uploadResponse is null");
        }
        URLWrapper urlWrapper = null;
        URL url = null;
        try
        {
            // unmarshal the url String from upload response into a URLWrapper object
            IBindingFactory bfact = BindingDirectory.getFactory(URLWrapper.class);
            IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
            StringReader sr = new StringReader(uploadResponse);
            urlWrapper = (URLWrapper)uctx.unmarshalDocument(sr, null);
            // extract the string from the wrapper and transform it to a URL

            logger.debug("URL: " + ServiceLocator.getFrameworkUrl() + ":" + urlWrapper.getUrlString());

            url = new URL(ServiceLocator.getFrameworkUrl() + urlWrapper.getUrlString());
        }
        catch (JiBXException e)
        {
            // throw a new UnmarshallingException, log the root cause of the JiBXException first
            logger.error(e, e.getRootCause());
            throw new UnmarshallingException(uploadResponse, e);
        }
        catch (MalformedURLException e)
        {
            throw new TechnicalException(e);
        }
        catch (java.lang.ClassCastException e)
        {
            throw new TechnicalException(e);
        }
        catch (ServiceException e)
        {
            throw new TechnicalException(e);
        }
        return url;
    }

    /**
     * {@inheritDoc}
     */
    public List<VersionHistoryEntryVO> transformToEventVOList(String versionList) throws TechnicalException
    {
        logger.debug("transformToPubItemVersionVOList(String) - String versionList=\n" + versionList);
        if (versionList == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToPubItemVersionVOList:versionList is null");
        }
        EventVOListWrapper eventVOListWrapper = null;
        try
        {
            // unmarshal EventVOListWrapper from String
            IBindingFactory bfact = BindingDirectory.getFactory("PubItemVO_PubCollectionVO_input", EventVOListWrapper.class);
            IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
            StringReader sr = new StringReader(versionList);
            eventVOListWrapper = (EventVOListWrapper)uctx.unmarshalDocument(sr, null);
        }
        catch (JiBXException e)
        {
            // throw a new UnmarshallingException, log the root cause of the JiBXException first
            logger.error(e.getRootCause());
            throw new UnmarshallingException(versionList, e);
        }
        catch (ClassCastException e)
        {
            throw new TechnicalException(e);
        }
        // unwrap the List<VersionHistoryEntryVO>
        List<VersionHistoryEntryVO> eventList = eventVOListWrapper.getEventVOList();
        return eventList;
    }
    
    /**
     * Return the child of the node selected by the xPath.
     * 
     * @param node The node.
     * @param xPath The xPath.
     * @return The child of the node selected by the xPath.
     * @throws TransformerException If anything fails.
     */
    private static Node selectSingleNode(final Node node, final String xpathExpression) throws TransformerException
    {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        try
        {
            return (Node)xPath.evaluate(xpathExpression, node, XPathConstants.NODE);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Return the list of children of the node selected by the xPath.
     * 
     * @param node The node.
     * @param xPath The xPath.
     * @return The list of children of the node selected by the xPath.
     * @throws TransformerException If anything fails.
     */
    public static NodeList selectNodeList(final Node node, final String xpathExpression) throws TransformerException
    {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        try
        {
            return (NodeList)xPath.evaluate(xpathExpression, node, XPathConstants.NODESET);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    

    /**
     * {@inheritDoc}
     */
    public List<RelationVO> transformToRelationVOList(String relationsXml) throws UnmarshallingException
    {
        logger.debug("transformToRelationVOList(String) - String relationsXml=\n" + relationsXml);
        if (relationsXml == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToRelationVOList:relationsXml is null");
        }
        List<RelationVO> relations = new ArrayList<RelationVO>();

        try
        {
            Document relationsDoc = getDocument(relationsXml, true);
            NodeList subjects = selectNodeList(relationsDoc, "//*[local-name() = 'Description' and namespace-uri() = 'http://www.w3.org/1999/02/22-rdf-syntax-ns#']");
            logger.debug("subjects length:" + subjects.getLength());
            for (int i = 1; i <= subjects.getLength(); i++)
            {
                String descriptionXpath = "//*[local-name() = 'Description' and namespace-uri() = 'http://www.w3.org/1999/02/22-rdf-syntax-ns#'][" + i + "]";
                logger.debug("descriptionXpath: " + descriptionXpath);
                String subject = getAttributeValue(relationsDoc, descriptionXpath, "rdf:about");
                String subjectObjectId = subject.substring(subject.lastIndexOf('/') + 1);
                ItemRO subjectRef = new ItemRO(subjectObjectId);

                NodeList subjectRelations = selectNodeList(relationsDoc, "//*[local-name() = 'Description' and namespace-uri() = 'http://www.w3.org/1999/02/22-rdf-syntax-ns#'][" + i + "]/*");
                for (int j = 1; j <= subjectRelations.getLength(); j++)
                {
                    String relationXpath = "//*[local-name() = 'Description' and namespace-uri() = 'http://www.w3.org/1999/02/22-rdf-syntax-ns#'][" + i + "]/*[" + j + "]";
                    logger.debug("relationXpath: " + relationXpath);
                    Node relation = selectSingleNode(relationsDoc, relationXpath);
                    if (relation.getLocalName() == "isRevisionOf")
                    {
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
                }
            }
        }
        catch (Exception e)
        {
            throw new UnmarshallingException(relationsXml, e);
        }

        return relations;
    }

    /**
     * Parse the given xml String into a Document.
     * 
     * @param xml The xml String.
     * @param namespaceAwareness namespace awareness (default is false)
     * @return The Document.
     * @throws Exception If anything fails.
     */
    protected static Document getDocument(final String xml, final boolean namespaceAwareness) throws Exception
    {
        if (xml == null)
        {
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

    /**
     * Return the text value of the selected attribute.
     * 
     * @param node The node.
     * @param xPath The xpath to select the node containint the attribute,
     * @param attributeName The name of the attribute.
     * @return The text value of the selected attribute.
     * @throws Exception If anything fails.
     */
    public static String getAttributeValue(final Node node, final String xPath, final String attributeName) throws Exception
    {
        if (node == null)
        {
            throw new IllegalArgumentException(":getAttributeValue:node is null");
        }
        if (xPath == null)
        {
            throw new IllegalArgumentException(":getAttributeValue:xPath is null");
        }
        if (attributeName == null)
        {
            throw new IllegalArgumentException(":getAttributeValue:attributeName is null");
        }
        String result = null;
        Node attribute = selectSingleNode(node, xPath);
        if (attribute.hasAttributes())
        {
            result = attribute.getAttributes().getNamedItem(attributeName).getTextContent();
        }
        return result;
    }
    
    public PubItemVO transformToPubItem(String itemXml) throws TechnicalException
    {
        ItemVO itemVO = transformToItem(itemXml);
        if (itemVO.getMetadataSets().size() > 0 && itemVO.getMetadataSets().get(0) instanceof MdsPublicationVO)
        {
            return new PubItemVO(itemVO);
        }
        else
        {
            logger.warn("Cannot transform item xml to PubItemVO");
            return null;
        }
    }

    public List<PubItemVO> transformToPubItemList(String itemList) throws TechnicalException
    {
        List<? extends ItemVO> list = transformToItemList(itemList);
        List<PubItemVO> newList = new ArrayList<PubItemVO>();
        for (ItemVO itemVO : list)
        {
            PubItemVO pubItemVO = new PubItemVO(itemVO);
            newList.add(pubItemVO);
        }
        return newList;
    }
    
    public FaceItemVO transformToFaceItem(String itemXml) throws TechnicalException
    {
        ItemVO itemVO = transformToItem(itemXml);
        if (itemVO.getMetadataSets().size() > 0 && itemVO.getMetadataSets().get(0) instanceof MdsFaceVO)
        {
            return new FaceItemVO(itemVO);
        }
        else
        {
            logger.warn("Cannot transform item xml to FaceItemVO");
            return null;
        }
    }

    public List<FaceItemVO> transformToFaceItemList(String itemList) throws TechnicalException
    {
        List<? extends ItemVO> list = transformToItemList(itemList);
        List<FaceItemVO> newList = new ArrayList<FaceItemVO>();
        for (ItemVO itemVO : list)
        {
            FaceItemVO faceItemVO = new FaceItemVO(itemVO);
            newList.add(faceItemVO);
        }
        return newList;
    }
    
    /**
     * {@inheritDoc}
     */
    public List<? extends ValueObject> transformToMemberList(String memberList) throws TechnicalException
    {
        logger.debug("transformToMemberList(String) - String memberList=\n" + memberList);
        if (memberList == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToMemberList:memberList is null");
        }
        MemberListWrapper mListWrapper = null;
        try
        {
            // unmarshal MemberListWrapper from String
            IBindingFactory bfact = BindingDirectory.getFactory("PubItemVO_PubCollectionVO_input", MemberListWrapper.class);
            IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
            StringReader sr = new StringReader(memberList);
            Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
            mListWrapper = (MemberListWrapper)unmarshalledObject;
        }
        catch (JiBXException e)
        {
            // throw a new UnmarshallingException, log the root cause of the JiBXException first
            logger.error(e.getRootCause());
            throw new UnmarshallingException(memberList, e);
        }
        catch (ClassCastException e)
        {
            throw new TechnicalException(e);
        }
        // unwrap the List<ContainerVO>
        List<? extends ValueObject> memList = mListWrapper.getMemberList();

        return memList;
    }
    
    /**
     * {@inheritDoc}
     */
    public String transformToMemberList(List<? extends ValueObject> memberList) throws TechnicalException
    {
        logger.debug("transformToMemberList(List<ValueObject>)");
        if (memberList == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToMemberList:VOList is null");
        }
        // wrap the item list into the according wrapper class
        MemberListWrapper mListWrapper = new MemberListWrapper();
        mListWrapper.setMemberList(memberList);
        // transform the wrapper class into XML
        String utf8memberList = null;
        try
        {
            IBindingFactory bfact = BindingDirectory.getFactory("PubItemVO_PubCollectionVO_output",MemberListWrapper.class);
            // marshal object (with nice indentation, as UTF-8)
            IMarshallingContext mctx = bfact.createMarshallingContext();
            mctx.setIndent(2);
            StringWriter sw = new StringWriter();
            mctx.setOutput(sw);
            mctx.marshalDocument(mListWrapper, "UTF-8", null, sw);
            utf8memberList = sw.toString().trim();
        }
        catch (JiBXException e)
        {
            throw new MarshallingException(memberList.getClass().getSimpleName(), e);
        }
        catch (ClassCastException e)
        {
            throw new TechnicalException(e);
        }
        if (logger.isDebugEnabled())
        {
            logger.debug("transformToMemberList(List<VO>) - result: String utf8memberList=\n" + utf8memberList);
        }
        return utf8memberList;
    }

    
    /**
     * {@inheritDoc}
     */
    public String transformToContainer(ContainerVO containerVO) throws TechnicalException
    {
        logger.debug("transformToContainer(ContainerVO)");
        if (containerVO == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToContainer:containerVO is null");
        }
        String utf8container = null;
        try
        {
            IBindingFactory bfact = BindingDirectory.getFactory("ContainerVO_output", ContainerVO.class);
            // marshal object (with nice indentation, as UTF-8)
            IMarshallingContext mctx = bfact.createMarshallingContext();
            mctx.setIndent(2);
            StringWriter sw = new StringWriter();
            mctx.setOutput(sw);
            mctx.marshalDocument(containerVO, "UTF-8", null, sw);
            // use the following call to omit the leading "<?xml" tag of the generated XML
            // mctx.marshalDocument(containerVO);
            utf8container = sw.toString().trim();
        }
        catch (JiBXException e)
        {
            throw new MarshallingException(containerVO.getClass().getSimpleName(), e);
        }
        catch (ClassCastException e)
        {
            throw new TechnicalException(e);
        }
        if (logger.isDebugEnabled())
        {
            logger.debug("transformToContainer(ContainerVO) - result: String utf8container=" + utf8container);
        }
        return utf8container;
    }
    
    /**
     * {@inheritDoc}
     */
    public ContainerVO transformToContainer(String container) throws TechnicalException
    {
        logger.debug("transformToContainer(String) - String container=" + container);
        if (container == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToContainer:container is null");
        }
        ContainerVO containerVO = null;
        try
        {
            IBindingFactory bfact = BindingDirectory.getFactory("ContainerVO_input", ContainerVO.class);
            IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
            StringReader sr = new StringReader(container);
            containerVO = (ContainerVO)uctx.unmarshalDocument(sr, null);
        }
        catch (JiBXException e)
        {
            // throw a new UnmarshallingException, log the root cause of the JiBXException first
            logger.error(e.getRootCause());
            logger.error(e.getMessage());
            throw new UnmarshallingException(container, e);
        }
        catch (ClassCastException e)
        {
            throw new TechnicalException(e);
        }
        return containerVO;
    }
    
    /**
     * {@inheritDoc}
     */
    public String transformToContainerList(List<? extends ContainerVO> containerVOList) throws TechnicalException
    {
        logger.debug("transformToContainerList(List<ContainerVO>)");
        if (containerVOList == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToContainerList:containerVOList is null");
        }
        // wrap the item list into the according wrapper class
        ContainerVOListWrapper listWrapper = new ContainerVOListWrapper();
        listWrapper.setContainerVOList(containerVOList);
        // transform the wrapper class into XML
        String utf8containerList = null;
        try
        {
            IBindingFactory bfact = BindingDirectory.getFactory("ContainerVO_output", ContainerVOListWrapper.class);
            // marshal object (with nice indentation, as UTF-8)
            IMarshallingContext mctx = bfact.createMarshallingContext();
            mctx.setIndent(2);
            StringWriter sw = new StringWriter();
            mctx.setOutput(sw);
            mctx.marshalDocument(listWrapper, "UTF-8", null, sw);
            utf8containerList = sw.toString().trim();
        }
        catch (JiBXException e)
        {
            throw new MarshallingException(containerVOList.getClass().getSimpleName(), e);
        }
        catch (ClassCastException e)
        {
            throw new TechnicalException(e);
        }
        if (logger.isDebugEnabled())
        {
            logger.debug("transformToContainerList(List<ContainerVO>) - result: String utf8containerList=\n" + utf8containerList);
        }
        return utf8containerList;
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends ContainerVO> transformToContainerList(String containerList) throws TechnicalException
    {
        logger.debug("transformToContainerList(String) - String containerList=\n" + containerList);
        if (containerList == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToContainerList:containerList is null");
        }
        ContainerVOListWrapper containerVOListWrapper = null;
        try
        {
            // unmarshal ContainerVOListWrapper from String
            IBindingFactory bfact = BindingDirectory.getFactory("ContainerVO_input", ContainerVOListWrapper.class);
            IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
            StringReader sr = new StringReader(containerList);
            Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
            containerVOListWrapper = (ContainerVOListWrapper)unmarshalledObject;
        }
        catch (JiBXException e)
        {
            // throw a new UnmarshallingException, log the root cause of the JiBXException first
            logger.error(e.getRootCause());
            throw new UnmarshallingException(containerList, e);
        }
        catch (ClassCastException e)
        {
            throw new TechnicalException(e);
        }
        // unwrap the List<ContainerVO>
        List<? extends ContainerVO> contList = containerVOListWrapper.getContainerVOList();

        return contList;
    }
    
    
    
    public List<StatisticReportRecordVO> transformToStatisticReportRecordList (String statisticReportXML) throws TechnicalException
    {
        
            logger.debug("transformToStatisticReportRecordList(String) - String containerList=\n" + statisticReportXML);
            if (statisticReportXML == null)
            {
                throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToContainerList:containerList is null");
            }
            StatisticReportWrapper statisticReportWrapper = null;
            try
            {
                // unmarshal StatisticReport from String
                IBindingFactory bfact = BindingDirectory.getFactory("StatisticReport", StatisticReportWrapper.class);
                IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
                StringReader sr = new StringReader(statisticReportXML);
                Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
                statisticReportWrapper = (StatisticReportWrapper)unmarshalledObject;
            }
            catch (JiBXException e)
            {
                // throw a new UnmarshallingException, log the root cause of the JiBXException first
                logger.error(e.getRootCause());
                throw new UnmarshallingException(statisticReportXML, e);
            }
            catch (ClassCastException e)
            {
                throw new TechnicalException(e);
            }
           

            if (statisticReportWrapper.getStatisticReportRecordVOList() != null)
            {
                return statisticReportWrapper.getStatisticReportRecordVOList();
            }
            else return new ArrayList<StatisticReportRecordVO>();
           
        
    }

    public String transformToStatisticReportParameters(StatisticReportParamsVO statisticReportParams)throws TechnicalException
    {
        logger.debug("transformToStatisticReportParameters()");
        if (statisticReportParams == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToStatisticReportParameters:StatisticReportParamsVO is null");
        }
        String utf8container = null;
        try
        {
            IBindingFactory bfact = BindingDirectory.getFactory("StatisticReport", StatisticReportParamsVO.class);
            // marshal object (with nice indentation, as UTF-8)
            IMarshallingContext mctx = bfact.createMarshallingContext();
            mctx.setIndent(2);
            StringWriter sw = new StringWriter();
            mctx.setOutput(sw);
            mctx.marshalDocument(statisticReportParams, "UTF-8", null, sw);
            // use the following call to omit the leading "<?xml" tag of the generated XML
            // mctx.marshalDocument(containerVO);
            utf8container = sw.toString().trim();
        }
        catch (JiBXException e)
        {
            throw new MarshallingException(statisticReportParams.getClass().getSimpleName(), e);
        }
        catch (ClassCastException e)
        {
            throw new TechnicalException(e);
        }
        if (logger.isDebugEnabled())
        {
            logger.debug("transformToStatisticReportParameters() - result: String utf8container=" + utf8container);
        }
        return utf8container;
    }

    public List<StatisticReportDefinitionVO> transformToStatisticReportDefinitionList(String reportDefinitionList)
            throws TechnicalException
    {

        logger.debug("transformToStatisticReportDefinitionList(String) - String reportDefinitionList=\n" + reportDefinitionList);
        if (reportDefinitionList == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToStatisticReportDefinitionList: reportDefinitionList is null");
        }
        StatisticReportDefinitionVOListWrapper statisticReportDefinitionVOWrapper = null;
        try
        {
            // unmarshal StatisticReport from String
            IBindingFactory bfact = BindingDirectory.getFactory("StatisticReport", StatisticReportDefinitionVOListWrapper.class);
            IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
            StringReader sr = new StringReader(reportDefinitionList);
            Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
            statisticReportDefinitionVOWrapper = (StatisticReportDefinitionVOListWrapper)unmarshalledObject;
        }
        catch (JiBXException e)
        {
            // throw a new UnmarshallingException, log the root cause of the JiBXException first
            logger.error(e.getRootCause());
            throw new UnmarshallingException(reportDefinitionList, e);
        }
        catch (ClassCastException e)
        {
            throw new TechnicalException(e);
        }
       
        if (statisticReportDefinitionVOWrapper.getRepDefList() != null)
        {
            return statisticReportDefinitionVOWrapper.getRepDefList();
        }
        else return new ArrayList<StatisticReportDefinitionVO>();
        
    }

    public String transformToStatisticReportDefinition(StatisticReportDefinitionVO reportDef) throws TechnicalException
    {
        logger.debug("transformToStatisticReportDefinition()");
        if (reportDef == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToStatisticReportDefinition:reportDef is null");
        }
        String utf8container = null;
        try
        {
            IBindingFactory bfact = BindingDirectory.getFactory("StatisticReport", StatisticReportDefinitionVO.class);
            // marshal object (with nice indentation, as UTF-8)
            IMarshallingContext mctx = bfact.createMarshallingContext();
            mctx.setIndent(2);
            StringWriter sw = new StringWriter();
            mctx.setOutput(sw);
            mctx.marshalDocument(reportDef, "UTF-8", null, sw);
            // use the following call to omit the leading "<?xml" tag of the generated XML
            // mctx.marshalDocument(containerVO);
            utf8container = sw.toString().trim();
        }
        catch (JiBXException e)
        {
            throw new MarshallingException(reportDef.getClass().getSimpleName(), e);
        }
        catch (ClassCastException e)
        {
            throw new TechnicalException(e);
        }
        if (logger.isDebugEnabled())
        {
            logger.debug("transformToStatisticReportDefinition() - result: String utf8container=" + utf8container);
        }
        return utf8container;
    }

    public StatisticReportDefinitionVO transformToStatisticReportDefinition(String reportDefXML)
            throws TechnicalException
    {
        logger.debug("transformToStatisticReportDefinition(String) - String reportDefinition=\n" + reportDefXML);
        if (reportDefXML == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToStatisticReportDefinition: reportDefXML is null");
        }
        StatisticReportDefinitionVO statisticReportDefinitionVO = null;
        try
        {
            // unmarshal StatisticReport from String
            IBindingFactory bfact = BindingDirectory.getFactory("StatisticReport", StatisticReportDefinitionVO.class);
            IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
            StringReader sr = new StringReader(reportDefXML);
            Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
            statisticReportDefinitionVO = (StatisticReportDefinitionVO)unmarshalledObject;
        }
        catch (JiBXException e)
        {
            // throw a new UnmarshallingException, log the root cause of the JiBXException first
            logger.error(e.getRootCause());
            throw new UnmarshallingException(reportDefXML, e);
        }
        catch (ClassCastException e)
        {
            throw new TechnicalException(e);
        }
       

        return statisticReportDefinitionVO;
    }
    
    public TocItemVO transformToTocItemVO(String tocXML) throws TechnicalException
    {
        logger.debug("transformToTovItemVO(String) - String toc=\n" + tocXML);
        if (tocXML == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToTocItemVO: tocXML is null");
        }
        TocItemVO tocItemVO = null;
        try
        {
            // unmarshal StatisticReport from String
            IBindingFactory bfact = BindingDirectory.getFactory("TocItemVO_input", TocItemVO.class);
            IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
            StringReader sr = new StringReader(tocXML);
            Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
            tocItemVO = (TocItemVO)unmarshalledObject;
        }
        catch (JiBXException e)
        {
            // throw a new UnmarshallingException, log the root cause of the JiBXException first
            logger.error(e.getRootCause());
            throw new UnmarshallingException(tocXML, e);
        }
        catch (ClassCastException e)
        {
            throw new TechnicalException(e);
        }
        
        
        return tocItemVO;
    }
    
    public String transformToTocItem(TocItemVO tocItemVO) throws TechnicalException
    {
        logger.debug("transformToToc()");
        if (tocItemVO == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToToc:tocItemVO is null");
        }
        String utf8container = null;
        try
        {
            IBindingFactory bfact = BindingDirectory.getFactory("TocItemVO_output", TocItemVO.class);
            // marshal object (with nice indentation, as UTF-8)
            IMarshallingContext mctx = bfact.createMarshallingContext();
            mctx.setIndent(2);
            StringWriter sw = new StringWriter();
            mctx.setOutput(sw);
            mctx.marshalDocument(tocItemVO, "UTF-8", null, sw);
            // use the following call to omit the leading "<?xml" tag of the generated XML
            // mctx.marshalDocument(containerVO);
            utf8container = sw.toString().trim();
        }
        catch (JiBXException e)
        {
            throw new MarshallingException(tocItemVO.getClass().getSimpleName(), e);
        }
        catch (ClassCastException e)
        {
            throw new TechnicalException(e);
        }
        if (logger.isDebugEnabled())
        {
            logger.debug("transformToToc() - result: String utf8container=" + utf8container);
        }
        return utf8container;
    }
    
    public TocVO transformToTocVO(String tocXML) throws TechnicalException
    {
        logger.debug("transformToToVO(String) - String toc=\n" + tocXML);
        if (tocXML == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformToTocVO: tocXML is null");
        }
        TocVO tocVO = null;
        try
        {
            // unmarshal StatisticReport from String
            IBindingFactory bfact = BindingDirectory.getFactory("TocItemVO_input", TocItemVO.class);
            IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
            StringReader sr = new StringReader(tocXML);
            Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
            tocVO = (TocVO)unmarshalledObject;
        }
        catch (JiBXException e)
        {
            // throw a new UnmarshallingException, log the root cause of the JiBXException first
            logger.error(e.getRootCause());
            throw new UnmarshallingException(tocXML, e);
        }
        catch (ClassCastException e)
        {
            throw new TechnicalException(e);
        }
        
        
        return tocVO;
    }
    
}
