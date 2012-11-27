package de.mpg.escidoc.pubman.yearbook;

import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.www.services.aa.UserAccountHandler;
import de.escidoc.www.services.om.ContextHandler;
import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.contextList.ContextListSessionBean;
import de.mpg.escidoc.pubman.itemList.ItemList;
import de.mpg.escidoc.pubman.util.AffiliationVOPresentation;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.PubContextVOPresentation;
import de.mpg.escidoc.pubman.util.SelectItemComparator;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.referenceobjects.AccountUserRO;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.SearchRetrieveRecordVO;
import de.mpg.escidoc.services.common.valueobjects.SearchRetrieveResponseVO;
import de.mpg.escidoc.services.common.valueobjects.intelligent.grants.Grant;
import de.mpg.escidoc.services.common.valueobjects.intelligent.usergroup.Selector;
import de.mpg.escidoc.services.common.valueobjects.intelligent.usergroup.Selector.Type;
import de.mpg.escidoc.services.common.valueobjects.intelligent.usergroup.Selectors;
import de.mpg.escidoc.services.common.valueobjects.intelligent.usergroup.UserGroup;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsYearbookVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.search.query.MetadataDateSearchCriterion;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.CriterionType;

public class YearbookItemCreateBean extends FacesBean
{
    private static final Logger logger = Logger.getLogger(YearbookItemCreateBean.class);
    private static final String MAXIMUM_RECORDS = "5000";
    public static final String BEAN_NAME = "YearbookItemCreateBean";
    
    private String title;
    private List<ContextRO> contextIds;
    private String year;
    private String startDate;
    private String endDate;
    private List<AccountUserRO> collaborators;
    private AffiliationVOPresentation affiliation;
    private List<SelectItem> contextSelectItems;
    private List<SelectItem> userAccountSelectItems;
    private String context;
    private int contextPosition;
    private int userPosition;
    private LoginHelper loginHelper;
    private XmlTransforming xmlTransforming;
    private YearbookItemSessionBean yisb;
    private List<AccountUserVO> possibleCollaboratorsList;
    private List<String> collaboratorUserIds;
    private List<SelectItem> selectableYears;

    public YearbookItemCreateBean() throws Exception
    {
        xmlTransforming = new XmlTransformingBean();
        this.selectableYears = new ArrayList<SelectItem>();
        loginHelper = (LoginHelper)getSessionBean(LoginHelper.class);
        this.setAffiliation(loginHelper.getAccountUsersAffiliations().get(0));
        this.yisb = (YearbookItemSessionBean)getSessionBean(YearbookItemSessionBean.class);
        initContextMenu();
        initUserAccountMenu();
        initMetadata();
        contextIds = new ArrayList<ContextRO>();
        contextIds.add(new ContextRO((String)contextSelectItems.get(0).getValue()));
        collaborators = new ArrayList<AccountUserRO>();
    }

    private void initMetadata()
    {
        SimpleDateFormat calendarFormat = new SimpleDateFormat("yyyy");
        Calendar calendar = Calendar.getInstance();
        String currentYear = calendarFormat.format(calendar.getTime());
        selectableYears.add(new SelectItem(currentYear, currentYear));
        try
        {
            boolean previousYearPossible = true;
            ItemHandler itemHandler = ServiceLocator.getItemHandler(loginHelper.getESciDocUserHandle());
            HashMap<String, String[]> filterParams = new HashMap<String, String[]>();
            String orgId = loginHelper.getAccountUsersAffiliations().get(0).getReference().getObjectId();
            filterParams.put("operation", new String[] { "searchRetrieve" });
            filterParams.put("version", new String[] { "1.1" });
            filterParams.put(
                    "query",
                    new String[] { "\"/properties/context/id\"="
                            + PropertyReader.getProperty("escidoc.pubman.yearbook.context.id")
                            + " and \"/md-records/md-record/yearbook/creator/organization/identifier\"=" + orgId });
            filterParams.put("maximumRecords", new String[] { YearbookItemCreateBean.MAXIMUM_RECORDS });
            String xmlItemList = itemHandler.retrieveItems(filterParams);
            SearchRetrieveResponseVO result = xmlTransforming.transformToSearchRetrieveResponse(xmlItemList);
            // check if years have to be excluded from selection
            if (result.getNumberOfRecords() > 0)
            {
                PubItemVO yearbookPubItem = null;
                for (SearchRetrieveRecordVO yearbookRecord : result.getRecords())
                {
                    yearbookPubItem = (PubItemVO)yearbookRecord.getData();
                    if (yearbookPubItem != null && yearbookPubItem.getYearbookMetadata() != null)
                    {
                        if (yearbookPubItem.getYearbookMetadata().getYear() != null
                                && yearbookPubItem.getYearbookMetadata().getYear()
                                        .equals(Integer.toString(Integer.valueOf(currentYear) - 1)))
                        {
                            previousYearPossible = false;
                        }
                    }
                }
            }
            if (previousYearPossible == true) 
            {
                selectableYears.add(new SelectItem(Integer.toString(Integer.valueOf(currentYear) - 1), Integer.toString(Integer.valueOf(currentYear) - 1)));
            }
        }
        catch (SystemException e)
        {
            logger.error("Problem with retrieving items: \n", e);
        }
        catch (RemoteException e)
        {
            logger.error("Problem with retrieving items: \n", e);
        }
        catch (ServiceException e)
        {
            logger.error("Problem with itemHandler service: \n", e);
        }
        catch (URISyntaxException e)
        {
            logger.error("Problem getting itemHandler or property uri: \n", e);
        }
        catch (IOException e)
        {
            logger.error("Problem with getting property: \n", e);
        }
        catch (TechnicalException e)
        {
            logger.error("Problem with xml transformation: \n", e);
        }
        catch (Exception e)
        {
            logger.error("Problem getting accountUserAffiliations: \n", e);
        }
        
        if (this.getYear() == null)
        {
            this.setYear(currentYear);
        }
        if (this.getStartDate() == null)
        {
            this.setStartDate(currentYear + "-01-01");
        }
        if (this.getEndDate() == null)
        {
            this.setEndDate(currentYear + "-12-31");
        }
        if (this.getTitle() == null )
        {
            this.setTitle(currentYear + " - Yearbook of " + this.getAffiliation().getName());
        }
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String addContext()
    {
        contextIds.add(getContextPosition() + 1, new ContextRO((String)getContextSelectItems().get(0).getValue()));
        return "";
    }

    public String removeContext()
    {
        contextIds.remove(getContextPosition());
        return "";
    }

    /**
     * @return the year which should be represented by the yearbook
     */
    public String getYear()
    {
        return this.year;
    }

    /**
     * @param newYear (yyyy) the new year which should be represented by the yearbook
     */
    public void setYear(String newYear)
    {
        this.year = newYear;
        this.setTitle(this.getYear() + " - Yearbook of " + this.getAffiliation().getName());
        this.setStartDate(this.year + "-01-01");
        this.setEndDate(this.year + "-12-31");
    }

    /**
     * @return the date from which the publication are considerd as yeabook-candidates
     */
    public String getStartDate()
    {
        return this.startDate;
    }

    /**
     * @param newStartDate (yyyy-MM-dd) the date from which the publication are considerd as yeabook-candidates
     */
    public void setStartDate(String newStartDate)
    {
        this.startDate = newStartDate;
    }

    /**
     * @return the date until when the publication are considerd as yeabook-candidates
     */
    public String getEndDate()
    {
        return this.endDate;
    }

    /**
     * @param newEndDate (yyyy-MM-dd) the date until when the publication are considerd as yeabook-candidates
     */
    public void setEndDate(String newEndDate)
    {
        this.endDate = newEndDate;
    }

    public String save()
    {
        try
        {
            loginHelper = (LoginHelper)getSessionBean(LoginHelper.class);
            ItemHandler ih = ServiceLocator.getItemHandler(loginHelper.getESciDocUserHandle());
            PubItemVO pubItem = new PubItemVO();
            pubItem.setContentModel(PropertyReader.getProperty("escidoc.pubman.yearbook.content-model.id"));
            pubItem.setContext(new ContextRO(PropertyReader.getProperty("escidoc.pubman.yearbook.context.id")));
            MdsYearbookVO mds = new MdsYearbookVO();
            pubItem.getMetadataSets().add(mds);
            // Metadata set title
            mds.setTitle(new TextVO(getTitle()));
            // Metadata set creators
            CreatorVO creatorVO = new CreatorVO();
            OrganizationVO orgUnit = new OrganizationVO();
            orgUnit.setName(new TextVO(getAffiliation().getDefaultMetadata().getName()));
            orgUnit.setIdentifier(getAffiliation().getReference().getObjectId());
            creatorVO.setOrganization(orgUnit);
            mds.getCreators().add(creatorVO);
            // Metadata set Dates
            mds.setYear(this.getYear().trim());
            mds.setStartDate(this.getStartDate().trim());
            mds.setEndDate(this.getEndDate().trim());
            // Metadata set contexts
            for (ContextRO contextId : contextIds)
            {
                if (!contextId.getObjectId().trim().equals(""))
                {
                    mds.getIncludedContexts().add(contextId.getObjectId().trim());
                }
            }
            HashMap<String, String[]> filterParams = new HashMap<String, String[]>();
            String yearbookContextId = PropertyReader.getProperty("escidoc.pubman.yearbook.context.id");
            filterParams.put("operation", new String[] { "searchRetrieve" });
            filterParams.put("version", new String[] { "1.1" });
            filterParams.put("query", new String[] { "\"/properties/context/id\"=" + yearbookContextId
                    + " and \"/md-records/md-record/yearbook/creator/organization/identifier\"="
                    + getAffiliation().getReference().getObjectId() });
            filterParams.put("maximumRecords", new String[] { YearbookItemCreateBean.MAXIMUM_RECORDS });
            String xmlItemList = ih.retrieveItems(filterParams);
            SearchRetrieveResponseVO result = xmlTransforming.transformToSearchRetrieveResponse(xmlItemList);
            if (result.getNumberOfRecords() > 0)
            {
                PubItemVO yearbookPubItem = null;
                for (SearchRetrieveRecordVO yearbookRecord : result.getRecords())
                {
                    yearbookPubItem = (PubItemVO)yearbookRecord.getData();
                    if (yearbookPubItem != null && yearbookPubItem.getYearbookMetadata() != null)
                    {
                        if (yearbookPubItem.getYearbookMetadata().getYear() != null
                                && yearbookPubItem.getYearbookMetadata().getYear().equals(this.getYear()))
                        {
                            error("A yearbook related to this organization object id already exists for this Year");
                            return "";
                        }
                        else if (yearbookPubItem.getYearbookMetadata().getYear() != null
                                && yearbookPubItem.getYearbookMetadata().getYear()
                                        .equals(Integer.toString((Integer.valueOf(this.getYear()) - 1)))
                                && !yearbookPubItem.getPublicStatus().equals(ItemVO.State.RELEASED))
                        {
                            error("A yearbook related to this organization object id already exists for the previous year and has not been released until now");
                            return "";
                        }
                    }
                }
            }
            String itemXml = xmlTransforming.transformToItem(pubItem);
            String updatedXml = ih.create(itemXml);
            pubItem = xmlTransforming.transformToPubItem(updatedXml);
            info(getMessage("Yearbook_createdSuccessfully"));
            UserGroup ug = new UserGroup();
            ug.setName(this.getYear() +  " - Yearbook User Group for " + getAffiliation().getDefaultMetadata().getName() + " ("
                    + getAffiliation().getReference().getObjectId() + ")");
            ug.setLabel(this.getYear() +  " - Yearbook User Group for " + getAffiliation().getDefaultMetadata().getName() + " ("
                    + getAffiliation().getReference().getObjectId() + ")");
            ug.createInCoreservice(loginHelper.getESciDocUserHandle());
            if (!this.collaborators.isEmpty() && this.collaborators.get(0) != null
                    && this.collaborators.get(0).getObjectId() != null)
            {
                for (AccountUserRO userId : collaborators)
                {
                    if (!("").equals(userId.getObjectId()))
                    {
                        Selector selector = new Selector();
                        selector.setType(Type.INTERNAL);
                        selector.setObjid(userId.getObjectId());
                        selector.setName("user-account");
                        selector.setString(userId.getObjectId());
                        Selectors selectors = new Selectors();
                        selectors.getSelectors().add(selector);
                        ug.addNewSelectorsInCoreservice(selectors, loginHelper.getESciDocUserHandle());
                    }
                }
                // Create collaborator grant
                Grant grant = new Grant();
                grant.setAssignedOn(pubItem.getVersion().getObjectId());
                grant.setGrantedTo(ug.getObjid());
                grant.setGrantType("user-group");
                grant.setRole(Grant.CoreserviceRole.COLLABORATOR_MODIFIER.getRoleId());
                grant.createInCoreservice(loginHelper.getESciDocUserHandle(), "Grant for Yearbook created");
                info(getMessage("Yearbook_grantsAdded"));
            }
            yisb.initYearbook();
            return "loadYearbookPage";
        }
        catch (Exception e)
        {
            error(getMessage("Yearbook_creationError"));
            logger.error("Error while creating yearbook", e);
            return "";
        }
    }

    /**
     * @param affiliation
     */
    public void setAffiliation(AffiliationVOPresentation affiliation)
    {
        this.affiliation = affiliation;
    }

    public AffiliationVOPresentation getAffiliation()
    {
        return this.affiliation;
    }

    public void setContextSelectItems(List<SelectItem> contextSelectItems)
    {
        this.contextSelectItems = contextSelectItems;
    }

    public List<SelectItem> getContextSelectItems()
    {
        return this.contextSelectItems;
    }

    public void setUserAccountSelectItems(List<SelectItem> userAccountSelectItems)
    {
        this.userAccountSelectItems = userAccountSelectItems;
    }

    public List<SelectItem> getUserAccountSelectItems()
    {
        return this.userAccountSelectItems;
    }

    public void setContextPosition(int contextPosition)
    {
        this.contextPosition = contextPosition;
    }

    public int getContextPosition()
    {
        return this.contextPosition;
    }

    public void initUserAccountMenu() throws Exception
    {
        UserAccountHandler uah = ServiceLocator.getUserAccountHandler(loginHelper.getESciDocUserHandle());
        this.collaboratorUserIds = new ArrayList<String>();
        this.possibleCollaboratorsList = new ArrayList<AccountUserVO>();
        userAccountSelectItems = new ArrayList<SelectItem>();
        HashMap<String, String[]> filterParams = new HashMap<String, String[]>();
        filterParams.put("operation", new String[] { "searchRetrieve" });
        filterParams.put("version", new String[] { "1.1" });
        // String orgId = "escidoc:persistent25";
        filterParams.put("query",
                new String[] { "\"http://escidoc.de/core/01/structural-relations/organizational-unit\"="
                        + getAffiliation().getReference().getObjectId() });
        filterParams.put("maximumRecords", new String[] { YearbookItemCreateBean.MAXIMUM_RECORDS });
        String uaList = uah.retrieveUserAccounts(filterParams);
        SearchRetrieveResponseVO result = xmlTransforming.transformToSearchRetrieveResponseAccountUser(uaList);
        List<SearchRetrieveRecordVO> results = result.getRecords();
        for (SearchRetrieveRecordVO rec : results)
        {
            AccountUserVO userVO = (AccountUserVO)rec.getData();
            if (!userVO.getReference().getObjectId().equals(loginHelper.getAccountUser().getReference().getObjectId()))
            {
                userAccountSelectItems.add(new SelectItem(userVO.getReference().getObjectId(), userVO.getName() + " ("
                        + userVO.getUserid() + ")"));
                this.possibleCollaboratorsList.add(userVO);
            }
        }
        Collections.sort(userAccountSelectItems, new SelectItemComparator());
        /*
         * for(PubContextVOPresentation context : clsb.getModeratorContextList()) { userAccountSelectItems.add(new
         * SelectItem(context.getReference().getObjectId(), context.getName() + " (" +
         * context.getReference().getObjectId() + ")")); }
         */
    }

    public void initContextMenu()
    {
        this.contextSelectItems = new ArrayList<SelectItem>();
        ContextListSessionBean clsb = (ContextListSessionBean)getSessionBean(ContextListSessionBean.class);
        for (PubContextVOPresentation context : clsb.getModeratorContextList())
        {
            this.contextSelectItems.add(new SelectItem(context.getReference().getObjectId(), context.getName() + " ("
                    + context.getReference().getObjectId() + ")"));
        }
    }

    public void setUserPosition(int userPosition)
    {
        this.userPosition = userPosition;
    }

    public int getUserPosition()
    {
        return userPosition;
    }

    public void setContext(String context)
    {
        this.context = context;
    }

    public String getContext()
    {
        return this.context;
    }

    public void setCollaboratorUserIds(List<String> collaboratorUserIds)
    {
        this.collaboratorUserIds = collaboratorUserIds;
        for (AccountUserVO possibleCollaborator : this.possibleCollaboratorsList)
        {
            for (String collaboratorObjectId : collaboratorUserIds)
            {
                if (possibleCollaborator.getReference().getObjectId().equals(collaboratorObjectId))
                {
                    this.getCollaborators().add(possibleCollaborator.getReference());
                }
            }
        }
    }

    public List<String> getCollaboratorUserIds()
    {
        return this.collaboratorUserIds;
    }

    public void setCollaborators(List<AccountUserRO> collaboratorUserIds)
    {
        this.collaborators = collaboratorUserIds;
    }

    public List<AccountUserRO> getCollaborators()
    {
        return collaborators;
    }

    public void setContextIds(List<ContextRO> contextIds)
    {
        this.contextIds = contextIds;
    }

    public List<ContextRO> getContextIds()
    {
        return this.contextIds;
    }
    
    public int getContextIdsListSize ()
    {
        return this.contextIds.size();
    }

    public List<SelectItem> getSelectYear()
    {
        return this.selectableYears;
    }
}
