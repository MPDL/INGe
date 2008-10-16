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

package de.mpg.escidoc.services.common;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.referenceobjects.AffiliationRO;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.AffiliationPathVO;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.ContainerResultVO;
import de.mpg.escidoc.services.common.valueobjects.ContainerVO;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.common.valueobjects.ExportFormatVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.GrantVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.LockVO;
import de.mpg.escidoc.services.common.valueobjects.PidTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.ItemResultVO;
import de.mpg.escidoc.services.common.valueobjects.RelationVO;
import de.mpg.escidoc.services.common.valueobjects.ResultVO;
import de.mpg.escidoc.services.common.valueobjects.TaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.TocItemVO;
import de.mpg.escidoc.services.common.valueobjects.TocVO;
import de.mpg.escidoc.services.common.valueobjects.ValueObject;
import de.mpg.escidoc.services.common.valueobjects.VersionHistoryEntryVO;
import de.mpg.escidoc.services.common.valueobjects.face.FaceItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportDefinitionVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportParamsVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordVO;
import de.mpg.escidoc.services.common.xmltransforming.exceptions.MarshallingException;
import de.mpg.escidoc.services.common.xmltransforming.exceptions.UnmarshallingException;

/**
 * The interface with the XML transforming methods.
 * 
 * @author Johannes Mueller
 * @revised by MuJ: 05.09.2007
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $ by $Author: jmueller $
 * @updated 05-Sep-2007 17:10:18
 */
public interface XmlTransforming
{

    /**
     * The name of the EJB service.
     */
    public static final String SERVICE_NAME = "ejb/de/mpg/escidoc/services/pubman/XmlTransforming";

    /**
     * Transforms an XML String that is valid according to "${xsd.soap.useraccount.useraccount}"
     * (user-account.xsd) to the corresponding <code>AccountUserVO</code>.
     * 
     * @param user XML String that is valid according to "${xsd.soap.useraccount.useraccount}"
     *            (user-account.xsd)
     * @return The corresponding <code>AccountUserVO</code>
     * @throws TechnicalException
     * @throws UnmarshallingException
     */
    public AccountUserVO transformToAccountUser(String user) throws TechnicalException, UnmarshallingException;
    public String transformToAccountUser(AccountUserVO accountUserVO) throws TechnicalException;
    public List<AccountUserVO> transformToAccountUserVOList(String accountUserListXml) throws TechnicalException;

    /**
     * Transforms an XML String that is valid according to "${xsd.soap.ou.ou}"
     * (organizational-unit.xsd) to the corresponding <code>AffiliationVO</code>.
     * 
     * @param organizationalUnit XML String that is valid according to
     *            "${xsd.soap.ou.ou}" (organizational-unit)
     * @return The corresponding <code>AffiliationVO</code>
     * @throws TechnicalException
     * @throws UnmarshallingException
     */
    public AffiliationVO transformToAffiliation(String organizationalUnit) throws TechnicalException, UnmarshallingException;

    /**
     * Transforms an XML String that is valid according to "http://www.escidoc. de/schemas/organizationalunitlist/0.6"
     * (organizational-unit-list.xsd) to the corresponding <code>List&lt;AffiliationVO></code>.
     * 
     * @param organizationalUnitList XML String that is valid according to "http://www.escidoc.
     *            de/schemas/organizationalunitlist/0.2" (organizational-unit-list.xsd)
     * @return The corresponding <code>List&lt;AffiliationVO></code>
     * @throws TechnicalException
     * @throws UnmarshallingException
     */
    public List<AffiliationVO> transformToAffiliationList(String organizationalUnitList) throws TechnicalException, UnmarshallingException;

    /**
     * Transforms an XML String that is valid according to "http://www.escidoc. de/schemas/organizationalunitlist/0.6"
     * (organizational-unit.xsd) to the corresponding <code>List&lt;AffiliationRO></code>.
     * 
     * @param parentOrganizationalUnitList XML String that is valid according to "http://www.escidoc.
     *            de/schemas/organizationalunitlist/0.6" (organizational-unit.xsd)
     * @return The corresponding <code>List&lt;AffiliationRO></code>
     * @throws TechnicalException
     * @throws UnmarshallingException
     */
    public List<AffiliationRO> transformToParentAffiliationList(String parentOrganizationalUnitList) throws TechnicalException, UnmarshallingException;

    /**
     * Transforms an XML String that is valid according to "http://www.escidoc.
     * de/schemas/organizationalunitpathlist/0.2" (organizational-unit-path-list.xsd) to the corresponding
     * <code>List&lt;AffiliationPath></code>.
     * 
     * @param pathList XML String that is valid according to
     *            "${xsd.soap.ou.oupathlist}" (organizational-unit-path-list.xsd)
     * @return The corresponding <code>List&lt;AffiliationPath></code>
     * @throws TechnicalException
     * @throws UnmarshallingException
     */
    public List<AffiliationPathVO> transformToAffiliationPathList(String pathList) throws TechnicalException, UnmarshallingException;

    /**
     * This method gets as input a XML (as String) containing the existing citation styles and their file formats. It
     * returns a list of <code>LayoutFormatVOs</code> according to the input.
     * 
     * @param xml XML (as String) containing the existing citation styles and their file formats
     * @return List of <code>LayoutFormatVOs</code> according to the input
     * @throws TechnicalException
     * @throws UnmarshallingException
     */
    public List<ExportFormatVO> transformToExportFormatVOList(String xml) throws TechnicalException, UnmarshallingException;

    /**
     * Transforms a given <code>ExportFormatVO</code> to corresponding XML.
     * 
     * @param exportFormat The <code>ExportFormatVO</code>
     * @return The corresponding XML
     * @throws TechnicalException,
     * @throws MarshallingException
     */
    public String transformToExportParams(ExportFormatVO exportFormat) throws TechnicalException, MarshallingException;

    /**
     * Transforms a given <code>FilterTaskParamVO</code> to corresponding XML that is valid according to (filter.xsd,
     * filter-containers.xsd, filter-contexts.xsd, filter-items.xsd, or filter-organizational-units.xsd respectively).
     * 
     * @param filter A <code>FilterTaskParamVO</code>
     * @return Corresponding XML that is valid according to (filter.xsd, filter-containers.xsd, filter-contexts.xsd,
     *         filter-items.xsd, or filter-organizational-units.xsd respectively)
     * @throws TechnicalException
     * @throws MarshallingException
     */
    public String transformToFilterTaskParam(FilterTaskParamVO filter) throws TechnicalException, MarshallingException;

    /**
     * Transforms an XML String that is valid according to "http://www.escidoc.de/schemas/grants/0.2" (grants.xsd) to
     * the corresponding <code>GrantVO</code>.
     * 
     * @param xmlGrants XML String that is valid according to "http://www.escidoc.de/schemas/grants/0.2" (grants.xsd)
     * @return The corresponding <code>GrantVO</code>
     */
    public java.util.List<GrantVO> transformToGrantVOList(String xmlGrants) throws TechnicalException;
    
    /**
     * Transforms an <code>GrantVO</code> to
     * the corresponding xml that is valid according to "http://www.escidoc.de/schemas/grants/0.2" (grants.xsd)
     * 
     * @param grantVO The GrantVO object
     * @return XML String that is valid according to "http://www.escidoc.de/schemas/grants/0.2" (grants.xsd)
     */
    public String transformToGrant(GrantVO grantVO) throws TechnicalException;

    
    /**
     * Transforms an XML String that is valid according to "http://www.escidoc.de/schemas/grants/0.2" (grants.xsd) to
     * the corresponding <code>GrantVO</code>.
     * 
     * @param xmlGrants XML String that is valid according to "http://www.escidoc.de/schemas/grants/0.2" (grants.xsd)
     * @return The corresponding <code>GrantVO</code>
     */
    public GrantVO transformToGrantVO(String xmlGrant) throws TechnicalException;
    
    /**
     * Transforms a given <code>PubItemVO</code> to corresponding XML that is valid according to "http://www.escidoc.
     * de/schemas/item/0.3" (item.xsd).
     * 
     * @param pubItem A <code>PubItemVO</code>
     * @return Corresponding XML that is valid according to "http://www.escidoc.de/schemas/item/0.3" (item.xsd)
     * @throws TechnicalException
     */
    public String transformToItem(ItemVO pubItem) throws TechnicalException;

    /**
     * Transforms a given <code>List&lt;ItemVO></code> to corresponding XML that is valid according to "http://www.
     * escidoc.de/schemas/itemlist/0.2" (item-list.xsd).
     * 
     * @param itemVOList A <code>List&lt;ItemVO></code>
     * @return Corresponding XML that is valid according to "http://www.escidoc.de/schemas/itemlist/0.2" (item-list.xsd)
     * @throws TechnicalException
     */
    public String transformToItemList(List<? extends ItemVO> itemVOList) throws TechnicalException;

    /**
     * Not implemented yet.
     * 
     * @param lockInformation lockInformation
     * @return Nothing valuable.
     * @throws TechnicalException
     * @throws UnmarshallingException
     */
    public LockVO transformToLockVO(String lockInformation) throws TechnicalException, UnmarshallingException;

    /**
     * Transforms a given <code>AffiliationVO</code> to corresponding XML that is valid according to
     * "${xsd.soap.ou.ou}" (organizational-unit.xsd).
     * 
     * @param affiliation A <code>AffiliationVO</code>
     * @return Corresponding XML that is valid according to
     * "${xsd.soap.ou.ou}" (organizational-unit.xsd)
     * @throws TechnicalException
     * @throws MarshallingException
     */
    public String transformToOrganizationalUnit(AffiliationVO affiliation) throws TechnicalException, MarshallingException;

    /**
     * Transforms an XML String that is valid according to "${xsd.soap.context.context}" (context.xsd) to
     * the corresponding <code>ContextVO</code>.
     * 
     * @param context XML String that is valid according to "${xsd.soap.context.context}" (context.xsd)
     * @return The corresponding <code>ContextVO</code>
     * @throws TechnicalException
     */
    public ContextVO transformToContext(String context) throws TechnicalException;

    /**
     * Transforms an XML String that is valid according to "http://www.escidoc.de/schemas/contextlist/0.2"
     * (context-list.xsd) to the corresponding <code>List&lt;ContextVO></code>.
     * 
     * @param contextList XML String that is valid according to "http://www.escidoc.de/schemas/contextlist/0.2"
     *            (context-list.xsd)
     * @return The corresponding <code>List&lt;ContextVO></code>
     * @throws TechnicalException
     */
    public List<ContextVO> transformToContextList(String contextList) throws TechnicalException;

    /**
     * Transforms an XML String that is valid according to "http://www.escidoc.de/schemas/item/0.2" (item. xsd) to the
     * corresponding <code>ItemVO</code>.
     * 
     * @param item XML String that is valid according to "http://www.escidoc.de/schemas/item/0.2" (item. xsd)
     * @return The corresponding <code>ItemVO</code>
     * @throws TechnicalException
     */
    public ItemVO transformToItem(String item) throws TechnicalException;

    /**
     * Transforms an XML String that is valid according to "http://www.escidoc.de/schemas/itemlist/0.2" (item-list.xsd)
     * to the corresponding <code>List&lt;ItemVO></code>.
     * 
     * @param itemList XML String that is valid according to "http://www.escidoc.de/schemas/itemlist/0.2"
     *            (item-list.xsd)
     * @return The corresponding <code>List&lt;ItemVO></code>
     * @throws TechnicalException
     */
    public List<? extends ItemVO> transformToItemList(String itemList) throws TechnicalException;

    /**
     * Transforms an XML String that is valid according to "${xsd.soap.searchresult.searchresult}"
     * (search-result.xsd) to the corresponding <code>ItemResultVO</code>.
     * 
     * @param searchResultItem XML String that is valid according to "${xsd.soap.searchresult.searchresult}"
     *            (search-result.xsd)
     * @return The corresponding <code>ItemResultVO</code>
     * @throws TechnicalException
     */
    public ItemResultVO transformToItemResultVO(String searchResultItem) throws TechnicalException;

    /**
     * Transforms a given <code>TaskParamVO</code> to corresponding XML that is valid according to (filter.xsd,
     * filter- containers.xsd, filter-contexts.xsd, filter-items.xsd, or filter-organizational-units.xsd respectively).
     * 
     * @param taskParam A <code>TaskParamVO</code>
     * @return Corresponding XML that is valid according to (filter.xsd, filter-containers.xsd, filter-contexts.xsd,
     *         filter-items.xsd, or filter-organizational-units.xsd respectively)
     * @throws TechnicalException
     * @throws MarshallingException
     */
    public String transformToTaskParam(TaskParamVO taskParam) throws TechnicalException, MarshallingException;

    /**
     * Transforms a given <code>PidTaskParamVO</code> to corresponding XML that is valid according to (filter.xsd,
     * filter- containers.xsd, filter-contexts.xsd, filter-items.xsd, or filter-organizational-units.xsd respectively).
     * 
     * @param pidTaskParam A <code>PidTaskParamVO</code>
     * @return Corresponding XML that is valid according to (filter.xsd, filter-containers.xsd, filter-contexts.xsd,
     *         filter-items.xsd, or filter-organizational-units.xsd respectively)
     * @throws TechnicalException
     * @throws MarshallingException
     */
    public String transformToPidTaskParam(PidTaskParamVO pidTaskParam) throws TechnicalException, MarshallingException;

    /**
     * Transforms an XML String that is valid according to "http://www.escidoc.de/schemas/stagingfile/0.2"
     * (staging-file.xsd) to the corresponding <code>java.net.URL</code>.
     * 
     * @param uploadResponse XML String that is valid according to "http://www.escidoc.de/schemas/stagingfile/0.2"
     *            (staging-file.xsd)
     * @return The corresponding <code>java.net.URL</code>
     * @throws TechnicalException
     * @throws UnmarshallingException
     */
    public URL transformUploadResponseToFileURL(String uploadResponse) throws TechnicalException, UnmarshallingException, URISyntaxException;
    
    /**
     * Transforms an XML String that is valid according to "http://www.escidoc.
     * de/schemas/..." (version-history.xsd) to the corresponding
     * <code>List<VersionHistoryEntryVO></code>.
     * @return The corresponding <code>List<VersionHistoryEntryVO></code>
     * 
     * @param versionList XML String that is valid according to
     * "http://www.escidoc.de/schemas/..." (version-history.xsd)
     * @exception TechnicalException
     */
    public List<VersionHistoryEntryVO> transformToEventVOList(String versionList) throws TechnicalException;

    /**
     * Transforms an XML String to the corresponding <code>List<RelationVO></code>.
     * @return The corresponding <code>List<RelationVO></code>
     * 
     * @param relationList
     * @return The corresponding <code>List<RelationVO></code>
     * @throws UnmarshallingException
     */
    public List<RelationVO> transformToRelationVOList(String relationList) throws UnmarshallingException;
    
    public PubItemVO transformToPubItem(String itemXml) throws TechnicalException;
    
    public List<PubItemVO> transformToPubItemList(String itemList) throws TechnicalException;
    
    public FaceItemVO transformToFaceItem(String itemXml) throws TechnicalException;
    
    public List<FaceItemVO> transformToFaceItemList(String itemList) throws TechnicalException;
    
    public List<? extends ValueObject> transformToMemberList(String memberListXml) throws TechnicalException;
    
    public String transformToMemberList(List<? extends ValueObject> memberList) throws TechnicalException;
    
    public ContainerVO transformToContainer(String containerXml) throws TechnicalException;
    
    /**
     * Transform a xml string to a container list.
     * @param containerList  xml string of containers
     * @return  list of containers
     * @throws TechnicalException  if transforming fails
     */
    public List<? extends ContainerVO> transformToContainerList(String containerList) throws TechnicalException;
    
    /**
     *  Transform a xml string to a container result.
     * @param containerResult  xml string of container search result.
     * @return  container search result
     * @throws TechnicalException  if transforming fails
     */
    public ContainerResultVO transformToContainerResult( String containerResult ) throws TechnicalException;
    
    public String transformToContainer(ContainerVO containerVO) throws TechnicalException;
    
    public String transformToContainerList(List<? extends ContainerVO> containerVOList) throws TechnicalException;
    
    public List<StatisticReportRecordVO> transformToStatisticReportRecordList (String statisticReportXML) throws TechnicalException;
    
    public String transformToStatisticReportParameters (StatisticReportParamsVO statisticReportParams) throws TechnicalException;
    
    public List<StatisticReportDefinitionVO> transformToStatisticReportDefinitionList(String reportDefinitionList) throws TechnicalException;
    
    public String transformToStatisticReportDefinition(StatisticReportDefinitionVO reportDef) throws TechnicalException;
    
    public StatisticReportDefinitionVO transformToStatisticReportDefinition(String reportDefXML) throws TechnicalException;
    
    public TocItemVO transformToTocItemVO(String tocXML) throws TechnicalException;
    
    public String transformToTocItem(TocItemVO tocItemVO) throws TechnicalException;
    
    public TocVO transformToTocVO(String tocXML) throws TechnicalException;
    
    public ResultVO transformToResult(String resultXml) throws TechnicalException;
}