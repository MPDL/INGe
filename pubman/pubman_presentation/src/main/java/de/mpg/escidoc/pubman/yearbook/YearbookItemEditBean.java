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

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyPublishedException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.www.services.aa.UserAccountHandler;
import de.escidoc.www.services.aa.UserGroupHandler;
import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.contextList.ContextListSessionBean;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.PubContextVOPresentation;
import de.mpg.escidoc.pubman.util.SelectItemComparator;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.referenceobjects.AccountUserRO;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.GrantVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.SearchRetrieveRecordVO;
import de.mpg.escidoc.services.common.valueobjects.SearchRetrieveResponseVO;
import de.mpg.escidoc.services.common.valueobjects.intelligent.grants.Grant;
import de.mpg.escidoc.services.common.valueobjects.intelligent.usergroup.Selector;
import de.mpg.escidoc.services.common.valueobjects.intelligent.usergroup.Selectors;
import de.mpg.escidoc.services.common.valueobjects.intelligent.usergroup.UserGroup;
import de.mpg.escidoc.services.common.valueobjects.intelligent.usergroup.UserGroupList;
import de.mpg.escidoc.services.common.valueobjects.intelligent.usergroup.Selector.Type;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsYearbookVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Bean for editing Yearbook-Items and its related User-Groups
 * 
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class YearbookItemEditBean extends FacesBean
{
    private static final Logger logger = Logger.getLogger(YearbookItemEditBean.class);
    public static final String BEAN_NAME = "YearbookItemEditBean";
    private static final String MAXIMUM_RECORDS = "5000";
    
    private YearbookItemSessionBean yearbookItemSessionBean;
    private LoginHelper loginHelper;
    private XmlTransformingBean xmlTransforming;
    private MdsYearbookVO yearbookMetadata;
    private String title;
    private String year;
    private List<CreatorVO> creators;
    private String startDate ;
    private String endDate;
    private ArrayList<SelectItem> contextSelectItems;
    private ArrayList<ContextRO> contextIds;
    private int contextPosition;
    private OrganizationVO organization;
    private UserGroup userGroup;
    private List<UserGroup> userGroups;
    private List<GrantVO> userGroupGrants;
    private List<SelectItem> collaboratorSelectItems;
    private List<String> collaboratorUserIds;
    private List<AccountUserVO> possibleCollaboratorsList;
    private List<AccountUserRO> collaborators;
    private List<SelectItem> selectableYears;
    
    
    

    public YearbookItemEditBean() throws Exception
    {
        this.yearbookItemSessionBean = (YearbookItemSessionBean)getSessionBean(YearbookItemSessionBean.class);
        this.loginHelper = (LoginHelper)getSessionBean(LoginHelper.class);
        xmlTransforming = new XmlTransformingBean();
        initialize();
    }
    
    public void initialize()
    {
        try
        {
            this.initContextMenu();
            if (this.yearbookItemSessionBean != null)
            {
                this.initYearbookMetadata();
                this.initUserGroups();
                this.initCollaborators();
            }
            initSelectableYears();
        }
        catch (Exception e)
        {
            logger.error("Problem reinitializing YearbookEditBean: \n", e);
        }
    }
    
    public void initYearbookMetadata()
    {
        this.yearbookMetadata = this.yearbookItemSessionBean.getYearbookItem().getYearbookMetadata();
        if (this.yearbookMetadata != null)
        {
            this.title = this.yearbookMetadata.getTitle().getValue();
            this.creators = this.yearbookMetadata.getCreators();
            this.organization = this.creators.get(0).getOrganization();
            this.year = this.yearbookMetadata.getYear();
            this.startDate = this.yearbookMetadata.getStartDate();
            this.endDate = this.yearbookMetadata.getEndDate();
            this.contextIds = new ArrayList<ContextRO>();
            for (String contextId : this.yearbookMetadata.getIncludedContexts())
            {
                this.contextIds.add(new ContextRO(contextId));
            }
        }
    }
    
    /**
     * initializes the contextSelectItems list
     */
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
    
    /**
     * initializes the collaborators for the yearbookItem
     * @throws Exception
     */
    public void initCollaborators() throws Exception
    {
        UserAccountHandler uah = ServiceLocator.getUserAccountHandler(loginHelper.getESciDocUserHandle());
        this.possibleCollaboratorsList = new ArrayList<AccountUserVO>();
        this.collaborators = new ArrayList<AccountUserRO>();
        collaboratorSelectItems = new ArrayList<SelectItem>();
        HashMap<String, String[]> filterParams = new HashMap<String, String[]>();
        filterParams.put("operation", new String[] { "searchRetrieve" });
        filterParams.put("version", new String[] { "1.1" });
        // String orgId = "escidoc:persistent25";
        filterParams.put("query",
                new String[] { "\"http://escidoc.de/core/01/structural-relations/organizational-unit\"="
                        + this.getOrganization().getIdentifier() });
        String userAccountXml = uah.retrieveUserAccounts(filterParams);
        SearchRetrieveResponseVO userAccounts = xmlTransforming.transformToSearchRetrieveResponseAccountUser(userAccountXml);
        for (SearchRetrieveRecordVO record : userAccounts.getRecords())
        {
            AccountUserVO userVO = (AccountUserVO)record.getData();
            if (!userVO.getReference().getObjectId().equals(loginHelper.getAccountUser().getReference().getObjectId()))
            {
                collaboratorSelectItems.add(new SelectItem(userVO.getReference().getObjectId(), userVO.getName() + " ("
                        + userVO.getUserid() + ")"));
                this.possibleCollaboratorsList.add(userVO);
            }
        }
        Collections.sort(collaboratorSelectItems, new SelectItemComparator());
    }
    
    public void initUserGroups() throws Exception
    {
        UserGroupHandler userGroupHandler = ServiceLocator.getUserGroupHandler(loginHelper.getESciDocUserHandle());
        this.collaboratorUserIds = new ArrayList<String>();
        HashMap<String, String[]> filterParams = new HashMap<String, String[]>();
        filterParams.put("operation", new String[] { "searchRetrieve" });
        filterParams.put("version", new String[] { "1.1" });
        filterParams.put("query", new String[] { "\"/properties/name\"=\"" + this.year + " - Yearbook User Group for " + getOrganization().getName().getValue() + " ("
                + getOrganization().getIdentifier() + ")\" and \"/properties/active\" = true"});
        String userGroupXml = userGroupHandler.retrieveUserGroups(filterParams);
        SearchRetrieveResponseVO userGroupSearchRetrieveResponse = xmlTransforming.transformToSearchRetrieveResponseUserGroup(userGroupXml);
        this.userGroups = new ArrayList<UserGroup>();
        for (SearchRetrieveRecordVO record : userGroupSearchRetrieveResponse.getRecords())
        {
            UserGroup userGroup = (UserGroup)record.getData();
            if (userGroup != null)
            {
                userGroups.add(userGroup);
            }
        }
        if (userGroups.size() > 1 )
        {
            logger.error("More than one UserGroup active and related to the YearbookItem: \"" + this.title + "\" (" + this.yearbookItemSessionBean.getYearbookItem().getVersion().getObjectId() + ")");
            throw new Exception("More than one UserGroup active and related to the YearbookItem: \"" + this.title + "\" (" + this.yearbookItemSessionBean.getYearbookItem().getVersion().getObjectId() + ")");
        }
        else if (userGroups.size() == 1 )
        {
            this.setUserGroup(userGroups.get(0));
            for (Selector user : this.getUserGroup().getSelectors().getSelectors())
            {
                if (user.getName().equals("user-account"))
                {
                    this.collaboratorUserIds.add(user.getString());
                }
            }
        }
        String userGroupGrantsXml = userGroupHandler.retrieveCurrentGrants(userGroups.get(0).getObjid());
        userGroupGrants = xmlTransforming.transformToGrantVOList(userGroupGrantsXml);
    }
    
    /**
     * initializes the years available in the selection box
     */
    private void initSelectableYears()
    {
        this.selectableYears = new ArrayList<SelectItem>();
        SimpleDateFormat calendarFormat = new SimpleDateFormat("yyyy");
        Calendar calendar = Calendar.getInstance();
        String currentYear = calendarFormat.format(calendar.getTime());
        if (!this.getYear().equals(currentYear) && !this.getYear().equals(Integer.toString(Integer.valueOf(currentYear) - 1)))
        {
            this.selectableYears.add(new SelectItem(this.getYear(), this.getYear()));
        }
        this.selectableYears.add(new SelectItem(currentYear, currentYear));
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
            filterParams.put("maximumRecords", new String[] { YearbookItemEditBean.MAXIMUM_RECORDS });
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
                                        .equals(Integer.toString(Integer.valueOf(currentYear) - 1)) 
                                && yearbookPubItem.getVersion().getState().equals(ItemVO.State.RELEASED))
                        {
                            previousYearPossible = false;
                        }
                    }
                }
            }
            if (previousYearPossible == true) 
            {
                this.selectableYears.add(new SelectItem(Integer.toString(Integer.valueOf(currentYear) - 1), Integer.toString(Integer.valueOf(currentYear) - 1)));
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
    }
    
    /**
     * @return the title which will be set for the yearbook when saving
     */
    public String getTitle()
    {
        return this.title;
    }
    
    /**
     * @param newTitle (String) the changed title for the yearbook
     */
    public void setTitle(String newTitle)
    {
        if (newTitle != null && !newTitle.trim().equals(""))
        {
            this.title = newTitle.trim();
        }
    }
    
    /**
     * @return the year which the yearbook is related to
     */
    public String getYear()
    {
        return this.year;
    }
    
    /**
     * @param year (String) which the yearbook should relate to
     */
    public void setYear(String year)
    {
        this.year = year.trim();
        this.setStartDate(this.year + "-01-01");
        this.setEndDate(this.year + "-12-31");
        this.setTitle(year + " - Yearbook of " + this.organization.getName().getValue());
    }
    
    /**
     * @return the startDate from when on publications will be taken into account when searching for candidates
     */
    public String getStartDate()
    {
        return startDate;
    }
    
    /**
     * @param newStartDate (String) the date from when on publications will be taken into account when searching for candidates
     */
    public void setStartDate(String newStartDate)
    {
        this.startDate = newStartDate;
    }

    /**
     * @return the endDate until when on publications will be taken into account when searching for candidates
     */
    public String getEndDate()
    {
        return endDate;
    }
    
    /**
     * @param newEndDate (String) the Date until when on publications will be taken into account when searching for candidates
     */
    public void setEndDate(String newEndDate)
    {
        this.endDate = newEndDate;
    }

    /**
     * @return the organization of the yearbook
     */
    public OrganizationVO getOrganization()
    {
        return organization;
    }
    
    /**
     * @return the contexts which are available to the user 
     */
    public ArrayList<SelectItem> getContextSelectItems()
    {
        return contextSelectItems;
    }
    
    /**
     * @return the contextIds 
     */
    public ArrayList<ContextRO> getContextIds()
    {
        return contextIds;
    }

    /**
     * @param contextIds the contextIds to set
     */
    public void setContextIds(ArrayList<ContextRO> contextIds)
    {
        this.contextIds = contextIds;
    }
    
    /**
     * @return the index of the currently interacted context
     */
    public int getContextPosition()
    {
        return this.contextPosition;
    }
    
    /**
     * @param contextPosition the index of the currently interacted context
     */
    public void setContextPosition(int contextPosition)
    {
        this.contextPosition = contextPosition;
    }

    /**
     * adds a context which should be included in the yearbook
     * @return empty String (no navigation wanted)
     */
    public String addContext()
    {
        contextIds.add(getContextPosition() + 1, new ContextRO((String)getContextSelectItems().get(0).getValue()));
        return "";
    }
    
    /**
     * removes a context which was included in the yearbook
     * @return empty String (no navigation wanted)
     */
    public String removeContext()
    {
        contextIds.remove(getContextPosition());
        return "";
    }
    
    /**
     * @return size (int) of the contextIds list
     */
    public int getContextIdsListSize ()
    {
        return this.contextIds.size();
    }

    /**
     * @return the collaboratorSelectItems
     */
    public List<SelectItem> getCollaboratorSelectItems()
    {
        return collaboratorSelectItems;
    }

    /**
     * @param collaboratorSelectItems the collaboratorSelectItems to set
     */
    public void setCollaboratorSelectItems(List<SelectItem> collaboratorSelectItems)
    {
        this.collaboratorSelectItems = collaboratorSelectItems;
    }
    
    public void setCollaborators(List<AccountUserRO> collaboratorUsers)
    {
        this.collaborators = collaboratorUsers;
    }

    public List<AccountUserRO> getCollaborators()
    {
        return collaborators;
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
    
    /**
     * @return the userGroup related to the yearbook
     */
    public UserGroup getUserGroup()
    {
        return userGroup;
    }

    /**
     * @param userGroup (UserGroup) related to the yearbook
     */
    public void setUserGroup(UserGroup userGroup)
    {
        this.userGroup = userGroup;
    }
    
    public List<SelectItem> getSelectYear()
    {
        return this.selectableYears;
    }
    
    public String delete()
    {
        try
        {
            ItemHandler itemHandler = ServiceLocator.getItemHandler(loginHelper.getESciDocUserHandle());
            itemHandler.delete(this.yearbookItemSessionBean.getYearbookItem().getVersion().getObjectId());
            this.yearbookItemSessionBean.initYearbook();
            UserGroupHandler userGroupHandler = ServiceLocator.getUserGroupHandler(loginHelper.getESciDocUserHandle());
            userGroupHandler.delete(this.getUserGroup().getObjid());
            return "loadYearbookPage";
        }
        catch (Exception e)
        {
            error(getMessage("Yearbook_deleteError"));
            logger.error("Problem accessing ItemHandler service 'itemHandler.delete()'", e);
        }
        return "";
    }

    /**
     * @return the navigation String for the yearbook page if no Problem 
     */
    public String save()
    {
        try
        {
            LoginHelper loginHelper = (LoginHelper)getSessionBean(LoginHelper.class);
            ItemHandler itemHandler = ServiceLocator.getItemHandler(loginHelper.getESciDocUserHandle());
            PubItemVO pubItem = new PubItemVO(this.yearbookItemSessionBean.getYearbookItem());
            MdsYearbookVO mds = new MdsYearbookVO();
            
            // Metadata set title
            mds.setTitle(new TextVO(getTitle()));
            // Metadata set creators
            CreatorVO creatorVO = new CreatorVO();
            creatorVO.setOrganization(this.getOrganization());
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
            pubItem.getMetadataSets().set(0, mds);
            String itemXml = xmlTransforming.transformToItem(pubItem);
            String updatedXml = itemHandler.update(pubItem.getVersion().getObjectId(), itemXml);
            if (this.getUserGroup() != null)
            {
                this.getUserGroup().setName(this.getYear() +  " - Yearbook User Group for " + getOrganization().getName() + " ("
                        + getOrganization().getIdentifier() + ")");
                this.getUserGroup().setLabel(this.getYear() +  " - Yearbook User Group for " + getOrganization().getName() + " ("
                        + getOrganization().getIdentifier() + ")");
                this.getUserGroup().updateInCoreservice(loginHelper.getESciDocUserHandle());
                if (this.getUserGroup().getSelectors() != null && !this.getUserGroup().getSelectors().getSelectors().isEmpty())
                {
                    this.getUserGroup().removeSelectorsInCoreservice(this.getUserGroup().getSelectors(), loginHelper.getESciDocUserHandle());
                }
                Selectors selectors = new Selectors();
                for (AccountUserRO userId : collaborators)
                {
                    if (!("").equals(userId.getObjectId()))
                    {
                        Selector selector = new Selector();
                        selector.setType(Type.INTERNAL);
                        selector.setObjid(userId.getObjectId());
                        selector.setName("user-account");
                        selector.setString(userId.getObjectId());
                        selectors.getSelectors().add(selector);
                        
                    }
                }
                if (!selectors.getSelectors().isEmpty())
                {
                    this.getUserGroup().addNewSelectorsInCoreservice(selectors, loginHelper.getESciDocUserHandle());
                    System.out.println(this.getUserGroup().getSelectors().getSelectors().get(0));
                }
                
            }
            this.yearbookItemSessionBean.initYearbook();
            return "loadYearbookPage";
        }
        catch (ServiceException e)
        {
            error(getMessage("Yearbook_editError") + " (ServiceException)");
            logger.error("ServiceException thrown in ServiceLocator.getItemHandler(loginHelper.getESciDocUserHandle())", e);
        }
        catch (URISyntaxException e)
        {
            error(getMessage("Yearbook_editError") + " (URISyntaxException)");
            logger.error("URISyntaxException thrown in ServiceLocator.getItemHandler(loginHelper.getESciDocUserHandle())", e);
        }
        catch (TechnicalException e)
        {
            error(getMessage("Yearbook_editError") + " (TechnicalException)");
            logger.error("TechnicalException thrown while transforming the pubItem - xmlTransforming.transformToItem(pubItem)", e);
        }
        catch (RuntimeException e)
        {
            error(getMessage("Yearbook_editError") + " (RuntimeException)");
            logger.error("RuntimeException thrown while removing selectors from usergroup - this.getUserGroup().removeSelectorsInCoreservice(this.getUserGroup().getSelectors(), loginHelper.getESciDocUserHandle())", e);
        }
        catch (Exception e)
        {
            error(getMessage("Yearbook_editError"));
            logger.error("Exception updating the yearbookItem - itemHandler.update(pubItem.getVersion().getObjectId(), itemXml)", e);
        }
        return "";
    }
    
    public String cancel()
    {
        return "loadYearbookPage";
    }
    
    /**
     * Return any bean stored in session scope under the specified name.
     * @param cls The bean class.
     * @return the actual or new bean instance
     */
    public static synchronized Object getSessionBean(final Class<?> cls)
    {

        String name = null;

        try
        {
            name = (String) cls.getField("BEAN_NAME").get(new String());
            if (FacesBean.class.getName().equals(name))
            {
                logger.warn("Bean class " + cls.getName() + " appears to have no individual BEAN_NAME.");
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error getting bean name of " + cls, e);
        }
        Object result = FacesContext
                .getCurrentInstance()
                .getExternalContext()
                .getSessionMap()
                .get(name);
        
        logger.debug("Getting bean " + name + ": " + result);

        if (result == null)
        {
            try
            {
                logger.debug("Creating new session bean: " + name);
                Object newBean = cls.newInstance();
                FacesContext
                        .getCurrentInstance()
                        .getExternalContext()
                        .getSessionMap()
                        .put(name, newBean);
                return newBean;
            }
            catch (Exception e)
            {
                throw new RuntimeException("Error creating new bean of type " + cls, e);
            }
        }
        else
        {
            return result;
        }
    }
}