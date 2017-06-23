package de.mpg.mpdl.inge.pubman.web.yearbook;

import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.www.services.aa.UserAccountHandler;
import de.escidoc.www.services.aa.UserGroupHandler;
import de.escidoc.www.services.om.ItemHandler;
import de.mpg.mpdl.inge.framework.ServiceLocator;
import de.mpg.mpdl.inge.model.referenceobjects.AccountUserRO;
import de.mpg.mpdl.inge.model.referenceobjects.ContextRO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.MemberVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.UserGroupVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsYearbookVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.converter.SelectItemComparator;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubContextVOPresentation;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * Bean for editing Yearbook-Items and its related User-Groups
 * 
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@ManagedBean(name = "YearbookItemEditBean")
@SessionScoped
@SuppressWarnings("serial")
public class YearbookItemEditBean extends FacesBean {
  private static final Logger logger = Logger.getLogger(YearbookItemEditBean.class);

  private static final String MAXIMUM_RECORDS = "5000";

  private final YearbookItemSessionBean yearbookItemSessionBean;
  private MdsYearbookVO yearbookMetadata;
  private String title;
  private String year;
  private List<CreatorVO> creators;
  private String startDate;
  private String endDate;
  private ArrayList<SelectItem> contextSelectItems;
  private ArrayList<ContextRO> contextIds;
  private int contextPosition;
  private OrganizationVO organization;
  private UserGroupVO userGroup;
  private List<UserGroupVO> userGroups;
  // private List<GrantVO> userGroupGrants;
  private List<SelectItem> collaboratorSelectItems;
  private List<String> collaboratorUserIds;
  private List<AccountUserVO> possibleCollaboratorsList;
  private List<AccountUserRO> collaborators;
  private List<SelectItem> selectableYears;



  public YearbookItemEditBean() throws Exception {
    this.yearbookItemSessionBean =
        (YearbookItemSessionBean) FacesTools.findBean("YearbookItemSessionBean");
    this.init();
  }

  public void init() {
    try {
      this.initContextMenu();
      if (this.yearbookItemSessionBean != null) {
        this.initYearbookMetadata();
        this.initUserGroups();
        this.initCollaborators();
      }
      this.initSelectableYears();
    } catch (final Exception e) {
      YearbookItemEditBean.logger.error("Problem reinitializing YearbookEditBean: \n", e);
    }
  }

  public void initYearbookMetadata() {
    this.yearbookMetadata = this.yearbookItemSessionBean.getYearbookItem().getYearbookMetadata();
    if (this.yearbookMetadata != null) {
      this.title = this.yearbookMetadata.getTitle();
      this.creators = this.yearbookMetadata.getCreators();
      this.organization = this.creators.get(0).getOrganization();
      this.year = this.yearbookMetadata.getYear();
      this.startDate = this.yearbookMetadata.getStartDate();
      this.endDate = this.yearbookMetadata.getEndDate();
      this.contextIds = new ArrayList<ContextRO>();
      for (final String contextId : this.yearbookMetadata.getIncludedContexts()) {
        this.contextIds.add(new ContextRO(contextId));
      }
    }
  }

  /**
   * initializes the contextSelectItems list
   */
  public void initContextMenu() {
    this.contextSelectItems = new ArrayList<SelectItem>();
    final ContextListSessionBean clsb =
        (ContextListSessionBean) FacesTools.findBean("ContextListSessionBean");
    for (final PubContextVOPresentation context : clsb.getModeratorContextList()) {
      this.contextSelectItems.add(new SelectItem(context.getReference().getObjectId(), context
          .getName() + " (" + context.getReference().getObjectId() + ")"));
    }
  }

  /**
   * initializes the collaborators for the yearbookItem
   * 
   * @throws Exception
   */
  public void initCollaborators() throws Exception {
    final UserAccountHandler uah =
        ServiceLocator.getUserAccountHandler(this.getLoginHelper().getESciDocUserHandle());
    this.possibleCollaboratorsList = new ArrayList<AccountUserVO>();
    this.collaborators = new ArrayList<AccountUserRO>();
    this.collaboratorSelectItems = new ArrayList<SelectItem>();
    final HashMap<String, String[]> filterParams = new HashMap<String, String[]>();
    filterParams.put("operation", new String[] {"searchRetrieve"});
    filterParams.put("version", new String[] {"1.1"});
    // String orgId = "escidoc:persistent25";
    filterParams.put("query",
        new String[] {"\"http://escidoc.de/core/01/structural-relations/organizational-unit\"="
            + this.getOrganization().getIdentifier()});
    final String userAccountXml = uah.retrieveUserAccounts(filterParams);
    final SearchRetrieveResponseVO<AccountUserVO> userAccounts =
        XmlTransformingService.transformToSearchRetrieveResponseAccountUser(userAccountXml);
    for (final SearchRetrieveRecordVO<AccountUserVO> record : userAccounts.getRecords()) {
      final AccountUserVO userVO = (AccountUserVO) record.getData();
      if (!userVO.getReference().getObjectId()
          .equals(this.getLoginHelper().getAccountUser().getReference().getObjectId())) {
        this.collaboratorSelectItems.add(new SelectItem(userVO.getReference().getObjectId(), userVO
            .getName() + " (" + userVO.getUserid() + ")"));
        this.possibleCollaboratorsList.add(userVO);
      }
    }
    Collections.sort(this.collaboratorSelectItems, new SelectItemComparator());
  }

  public void initUserGroups() throws Exception {
    final UserGroupHandler userGroupHandler =
        ServiceLocator.getUserGroupHandler(this.getLoginHelper().getESciDocUserHandle());
    this.collaboratorUserIds = new ArrayList<String>();
    final HashMap<String, String[]> filterParams = new HashMap<String, String[]>();
    filterParams.put("operation", new String[] {"searchRetrieve"});
    filterParams.put("version", new String[] {"1.1"});
    filterParams.put("query", new String[] {"\"/properties/name\"=\"" + this.year
        + " - Yearbook User Group for " + this.getOrganization().getName() + " ("
        + this.getOrganization().getIdentifier() + ")\" and \"/properties/active\" = true"});
    final String userGroupXml = userGroupHandler.retrieveUserGroups(filterParams);
    final SearchRetrieveResponseVO<UserGroupVO> userGroupSearchRetrieveResponse =
        XmlTransformingService.transformToSearchRetrieveResponseUserGroup(userGroupXml);
    this.userGroups = new ArrayList<UserGroupVO>();
    for (final SearchRetrieveRecordVO<UserGroupVO> record : userGroupSearchRetrieveResponse
        .getRecords()) {
      final UserGroupVO userGroup = (UserGroupVO) record.getData();
      if (userGroup != null) {
        this.userGroups.add(userGroup);
      }
    }
    if (this.userGroups.size() > 1) {
      YearbookItemEditBean.logger
          .error("More than one UserGroup active and related to the YearbookItem: \"" + this.title
              + "\" (" + this.yearbookItemSessionBean.getYearbookItem().getVersion().getObjectId()
              + ")");
      throw new Exception("More than one UserGroup active and related to the YearbookItem: \""
          + this.title + "\" ("
          + this.yearbookItemSessionBean.getYearbookItem().getVersion().getObjectId() + ")");
    } else if (this.userGroups.size() == 1) {
      this.setUserGroup(this.userGroups.get(0));
      for (final MemberVO user : this.getUserGroup().getMembers()) {
        if (user.getName().equals("user-account")) {
          this.collaboratorUserIds.add(user.getMemberId());
        }
      }
    }
    // String userGroupGrantsXml =
    // userGroupHandler.retrieveCurrentGrants(userGroups.get(0).getObjid());
    // userGroupGrants = xmlTransforming.transformToGrantVOList(userGroupGrantsXml);
  }

  /**
   * initializes the years available in the selection box
   */
  private void initSelectableYears() {
    this.selectableYears = new ArrayList<SelectItem>();
    final SimpleDateFormat calendarFormat = new SimpleDateFormat("yyyy");
    final Calendar calendar = Calendar.getInstance();
    final String currentYear = calendarFormat.format(calendar.getTime());
    if (!this.getYear().equals(currentYear)
        && !this.getYear().equals(Integer.toString(Integer.valueOf(currentYear) - 1))) {
      this.selectableYears.add(new SelectItem(this.getYear(), this.getYear()));
    }
    this.selectableYears.add(new SelectItem(currentYear, currentYear));
    try {
      boolean previousYearPossible = true;
      final ItemHandler itemHandler =
          ServiceLocator.getItemHandler(this.getLoginHelper().getESciDocUserHandle());
      final HashMap<String, String[]> filterParams = new HashMap<String, String[]>();
      final String orgId =
          this.getLoginHelper().getAccountUsersAffiliations().get(0).getReference().getObjectId();
      filterParams.put("operation", new String[] {"searchRetrieve"});
      filterParams.put("version", new String[] {"1.1"});
      filterParams
          .put(
              "query",
              new String[] {"\"/properties/context/id\"="
                  + PropertyReader.getProperty("escidoc.pubman.yearbook.context.id")
                  + " and \"/md-records/md-record/yearbook/creator/organization/identifier\"="
                  + orgId});
      filterParams.put("maximumRecords", new String[] {YearbookItemEditBean.MAXIMUM_RECORDS});
      final String xmlItemList = itemHandler.retrieveItems(filterParams);
      final SearchRetrieveResponseVO<PubItemVO> result =
          XmlTransformingService.transformToSearchRetrieveResponse(xmlItemList);
      // check if years have to be excluded from selection
      if (result.getNumberOfRecords() > 0) {
        PubItemVO yearbookPubItem = null;
        for (final SearchRetrieveRecordVO<PubItemVO> yearbookRecord : result.getRecords()) {
          yearbookPubItem = (PubItemVO) yearbookRecord.getData();
          if (yearbookPubItem != null && yearbookPubItem.getYearbookMetadata() != null) {
            if (yearbookPubItem.getYearbookMetadata().getYear() != null
                && yearbookPubItem.getYearbookMetadata().getYear()
                    .equals(Integer.toString(Integer.valueOf(currentYear) - 1))
                && yearbookPubItem.getVersion().getState().equals(ItemVO.State.RELEASED)) {
              previousYearPossible = false;
            }
          }
        }
      }
      if (previousYearPossible == true) {
        this.selectableYears.add(new SelectItem(Integer.toString(Integer.valueOf(currentYear) - 1),
            Integer.toString(Integer.valueOf(currentYear) - 1)));
      }
    } catch (final SystemException e) {
      YearbookItemEditBean.logger.error("Problem with retrieving items: \n", e);
    } catch (final RemoteException e) {
      YearbookItemEditBean.logger.error("Problem with retrieving items: \n", e);
    } catch (final ServiceException e) {
      YearbookItemEditBean.logger.error("Problem with itemHandler service: \n", e);
    } catch (final URISyntaxException e) {
      YearbookItemEditBean.logger.error("Problem getting itemHandler or property uri: \n", e);
    } catch (final IOException e) {
      YearbookItemEditBean.logger.error("Problem with getting property: \n", e);
    } catch (final TechnicalException e) {
      YearbookItemEditBean.logger.error("Problem with xml transformation: \n", e);
    } catch (final Exception e) {
      YearbookItemEditBean.logger.error("Problem getting accountUserAffiliations: \n", e);
    }
  }

  /**
   * @return the title which will be set for the yearbook when saving
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * @param newTitle (String) the changed title for the yearbook
   */
  public void setTitle(String newTitle) {
    if (newTitle != null && !newTitle.trim().equals("")) {
      this.title = newTitle.trim();
    }
  }

  /**
   * @return the year which the yearbook is related to
   */
  public String getYear() {
    return this.year;
  }

  /**
   * @param year (String) which the yearbook should relate to
   */
  public void setYear(String year) {
    this.year = year.trim();
    this.setStartDate(this.year + "-01-01");
    this.setEndDate(this.year + "-12-31");
    this.setTitle(year + " - Yearbook of " + this.organization.getName());
  }

  /**
   * @return the startDate from when on publications will be taken into account when searching for
   *         candidates
   */
  public String getStartDate() {
    return this.startDate;
  }

  /**
   * @param newStartDate (String) the date from when on publications will be taken into account when
   *        searching for candidates
   */
  public void setStartDate(String newStartDate) {
    this.startDate = newStartDate;
  }

  /**
   * @return the endDate until when on publications will be taken into account when searching for
   *         candidates
   */
  public String getEndDate() {
    return this.endDate;
  }

  /**
   * @param newEndDate (String) the Date until when on publications will be taken into account when
   *        searching for candidates
   */
  public void setEndDate(String newEndDate) {
    this.endDate = newEndDate;
  }

  /**
   * @return the organization of the yearbook
   */
  public OrganizationVO getOrganization() {
    return this.organization;
  }

  /**
   * @return the contexts which are available to the user
   */
  public ArrayList<SelectItem> getContextSelectItems() {
    return this.contextSelectItems;
  }

  /**
   * @return the contextIds
   */
  public ArrayList<ContextRO> getContextIds() {
    return this.contextIds;
  }

  /**
   * @param contextIds the contextIds to set
   */
  public void setContextIds(ArrayList<ContextRO> contextIds) {
    this.contextIds = contextIds;
  }

  /**
   * @return the index of the currently interacted context
   */
  public int getContextPosition() {
    return this.contextPosition;
  }

  /**
   * @param contextPosition the index of the currently interacted context
   */
  public void setContextPosition(int contextPosition) {
    this.contextPosition = contextPosition;
  }

  /**
   * adds a context which should be included in the yearbook
   * 
   * @return empty String (no navigation wanted)
   */
  public void addContext() {
    this.contextIds.add(this.getContextPosition() + 1, new ContextRO((String) this
        .getContextSelectItems().get(0).getValue()));
  }

  /**
   * removes a context which was included in the yearbook
   * 
   * @return empty String (no navigation wanted)
   */
  public void removeContext() {
    this.contextIds.remove(this.getContextPosition());
  }

  /**
   * @return size (int) of the contextIds list
   */
  public int getContextIdsListSize() {
    return this.contextIds.size();
  }

  /**
   * @return the collaboratorSelectItems
   */
  public List<SelectItem> getCollaboratorSelectItems() {
    return this.collaboratorSelectItems;
  }

  /**
   * @param collaboratorSelectItems the collaboratorSelectItems to set
   */
  public void setCollaboratorSelectItems(List<SelectItem> collaboratorSelectItems) {
    this.collaboratorSelectItems = collaboratorSelectItems;
  }

  public void setCollaborators(List<AccountUserRO> collaboratorUsers) {
    this.collaborators = collaboratorUsers;
  }

  public List<AccountUserRO> getCollaborators() {
    return this.collaborators;
  }

  public void setCollaboratorUserIds(List<String> collaboratorUserIds) {
    this.collaboratorUserIds = collaboratorUserIds;
    for (final AccountUserVO possibleCollaborator : this.possibleCollaboratorsList) {
      for (final String collaboratorObjectId : collaboratorUserIds) {
        if (possibleCollaborator.getReference().getObjectId().equals(collaboratorObjectId)) {
          this.getCollaborators().add(possibleCollaborator.getReference());
        }
      }
    }
  }

  public List<String> getCollaboratorUserIds() {
    return this.collaboratorUserIds;
  }

  /**
   * @return the userGroup related to the yearbook
   */
  public UserGroupVO getUserGroup() {
    return this.userGroup;
  }

  /**
   * @param userGroup (UserGroup) related to the yearbook
   */
  public void setUserGroup(UserGroupVO userGroup) {
    this.userGroup = userGroup;
  }

  public List<SelectItem> getSelectYear() {
    return this.selectableYears;
  }

  public String delete() {
    try {
      final ItemHandler itemHandler =
          ServiceLocator.getItemHandler(this.getLoginHelper().getESciDocUserHandle());
      itemHandler.delete(this.yearbookItemSessionBean.getYearbookItem().getVersion().getObjectId());
      this.yearbookItemSessionBean.initYearbook();
      final UserGroupHandler userGroupHandler =
          ServiceLocator.getUserGroupHandler(this.getLoginHelper().getESciDocUserHandle());
      userGroupHandler.delete(this.getUserGroup().getObjid());
      return "loadYearbookPage";
    } catch (final Exception e) {
      FacesBean.error(this.getMessage("Yearbook_deleteError"));
      YearbookItemEditBean.logger.error(
          "Problem accessing ItemHandler service 'itemHandler.delete()'", e);
    }

    return "";
  }

  /**
   * @return the navigation String for the yearbook page if no Problem
   */
  public String save() {
    try {
      // LoginHelper loginHelper = (LoginHelper) FacesTools.findBean(LoginHelper.class);
      // ItemHandler itemHandler =
      // ServiceLocator.getItemHandler(loginHelper.getESciDocUserHandle());
      final PubItemVO pubItem = new PubItemVO(this.yearbookItemSessionBean.getYearbookItem());
      final MdsYearbookVO mds = new MdsYearbookVO();

      // Metadata set title
      mds.setTitle(this.getTitle());
      // Metadata set creators
      final CreatorVO creatorVO = new CreatorVO();
      creatorVO.setOrganization(this.getOrganization());
      mds.getCreators().add(creatorVO);
      // Metadata set Dates
      mds.setYear(this.getYear().trim());
      mds.setStartDate(this.getStartDate().trim());
      mds.setEndDate(this.getEndDate().trim());
      // Metadata set contexts
      for (final ContextRO contextId : this.contextIds) {
        if (!contextId.getObjectId().trim().equals("")) {
          mds.getIncludedContexts().add(contextId.getObjectId().trim());
        }
      }
      pubItem.getMetadataSets().set(0, mds);
      // String itemXml = xmlTransforming.transformToItem(pubItem);
      // String updatedXml = itemHandler.update(pubItem.getVersion().getObjectId(), itemXml);
      if (this.getUserGroup() != null) {
        this.getUserGroup().setName(
            this.getYear() + " - Yearbook User Group for " + this.getOrganization().getName()
                + " (" + this.getOrganization().getIdentifier() + ")");
        this.getUserGroup().setLabel(
            this.getYear() + " - Yearbook User Group for " + this.getOrganization().getName()
                + " (" + this.getOrganization().getIdentifier() + ")");
        // TODO INGe connection
        // this.getUserGroup().updateInCoreservice(loginHelper.getESciDocUserHandle());
        // if (this.getUserGroup().getSelectors() != null
        // && !this.getUserGroup().getSelectors().getSelectors().isEmpty()) {
        // this.getUserGroup().removeSelectorsInCoreservice(this.getUserGroup().getSelectors(),
        // loginHelper.getESciDocUserHandle());
        // }
        final List<MemberVO> selectors = new ArrayList<MemberVO>();
        for (final AccountUserRO userId : this.collaborators) {
          if (!("").equals(userId.getObjectId())) {
            final MemberVO selector = new MemberVO();
            // TODO set type for INGe
            // selector.setType(Type.INTERNAL);
            // selector.setObjid(userId.getObjectId());
            selector.setName("user-account");
            selector.setMemberId(userId.getObjectId());
            selectors.add(selector);

          }
        }
        // TODO INGe connection
        // if (!selectors.getSelectors().isEmpty()) {
        // this.getUserGroup().addNewSelectorsInCoreservice(selectors,
        // loginHelper.getESciDocUserHandle());
        // System.out.println(this.getUserGroup().getSelectors().getSelectors().get(0));
        // }

      }
      this.yearbookItemSessionBean.initYearbook();
      return "loadYearbookPage";
    } catch (final ServiceException e) {
      FacesBean.error(this.getMessage("Yearbook_editError") + " (ServiceException)");
      YearbookItemEditBean.logger
          .error(
              "ServiceException thrown in ServiceLocator.getItemHandler(loginHelper.getESciDocUserHandle())",
              e);
    } catch (final URISyntaxException e) {
      FacesBean.error(this.getMessage("Yearbook_editError") + " (URISyntaxException)");
      YearbookItemEditBean.logger
          .error(
              "URISyntaxException thrown in ServiceLocator.getItemHandler(loginHelper.getESciDocUserHandle())",
              e);
    } catch (final TechnicalException e) {
      FacesBean.error(this.getMessage("Yearbook_editError") + " (TechnicalException)");
      YearbookItemEditBean.logger
          .error(
              "TechnicalException thrown while transforming the pubItem - xmlTransforming.transformToItem(pubItem)",
              e);
    } catch (final RuntimeException e) {
      FacesBean.error(this.getMessage("Yearbook_editError") + " (RuntimeException)");
      YearbookItemEditBean.logger
          .error(
              "RuntimeException thrown while removing selectors from usergroup - this.getUserGroup().removeSelectorsInCoreservice(this.getUserGroup().getSelectors(), loginHelper.getESciDocUserHandle())",
              e);
    } catch (final Exception e) {
      FacesBean.error(this.getMessage("Yearbook_editError"));
      YearbookItemEditBean.logger
          .error(
              "Exception updating the yearbookItem - itemHandler.update(pubItem.getVersion().getObjectId(), itemXml)",
              e);
    }

    return "";
  }

  public String cancel() {
    return "loadYearbookPage";
  }
}
