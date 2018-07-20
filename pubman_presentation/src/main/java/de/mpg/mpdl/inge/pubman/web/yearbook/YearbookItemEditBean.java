package de.mpg.mpdl.inge.pubman.web.yearbook;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import de.mpg.mpdl.inge.model.db.valueobjects.YearbookDbVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubContextVOPresentation;
import de.mpg.mpdl.inge.service.pubman.YearbookService;
import de.mpg.mpdl.inge.service.pubman.impl.YearbookServiceDbImpl;
import de.mpg.mpdl.inge.util.ModelHelper;

/**
 * Bean for editing Yearbook-Items and its related User-Groups
 * 
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@ManagedBean(name = "YearbookItemEditBean")
@ViewScoped
@SuppressWarnings("serial")
public class YearbookItemEditBean extends FacesBean {
  private static final Logger logger = Logger.getLogger(YearbookItemEditBean.class);

  //  private static final String MAXIMUM_RECORDS = "5000";

  private final YearbookItemSessionBean yearbookItemSessionBean;
  private YearbookDbVO yearbook;

  private String year;

  private List<SelectItem> contextSelectItems;
  private List<String> contextIds;
  private int contextPosition;
  private List<SelectItem> selectableYears;



  public YearbookItemEditBean() throws Exception {
    this.yearbookItemSessionBean = (YearbookItemSessionBean) FacesTools.findBean("YearbookItemSessionBean");
    this.init();
  }

  public void init() {
    try {
      this.initContextMenu();
      if (this.yearbookItemSessionBean != null) {
        this.yearbook = yearbookItemSessionBean.getYearbook();
        this.initYearbookMetadata();
      }
      this.initSelectableYears();
    } catch (final Exception e) {
      YearbookItemEditBean.logger.error("Problem reinitializing YearbookEditBean: \n", e);
    }
  }

  public void initYearbookMetadata() {

    if (yearbook != null) {

      // this.creators = this.yearbookMetadata.getCreators();
      this.year = String.valueOf(this.yearbook.getYear());
      this.contextIds = new ArrayList<String>();
      for (final String contextId : this.yearbook.getContextIds()) {
        this.contextIds.add(contextId);
      }
    }
  }

  /**
   * initializes the contextSelectItems list
   */
  public void initContextMenu() {
    this.contextSelectItems = new ArrayList<SelectItem>();
    final ContextListSessionBean clsb = (ContextListSessionBean) FacesTools.findBean("ContextListSessionBean");
    for (final PubContextVOPresentation context : clsb.getYearbookContextList()) {
      if (context.getResponsibleAffiliations().stream()
          .anyMatch(i -> i.getObjectId().equals(this.yearbookItemSessionBean.getYearbook().getOrganization().getObjectId()))) {
        this.contextSelectItems.add(new SelectItem(context.getObjectId(), context.getName() + " (" + context.getObjectId() + ")"));
      }

    }
  }

  /**
   * initializes the collaborators for the yearbookItem
   * 
   * @throws Exception
   */
  /*
   * public void initCollaborators() throws Exception { final UserAccountHandler uah =
   * ServiceLocator.getUserAccountHandler(this.getLoginHelper().getESciDocUserHandle());
   * this.possibleCollaboratorsList = new ArrayList<AccountUserVO>(); this.collaborators = new
   * ArrayList<AccountUserRO>(); this.collaboratorSelectItems = new ArrayList<SelectItem>(); final
   * HashMap<String, String[]> filterParams = new HashMap<String, String[]>();
   * filterParams.put("operation", new String[] {"searchRetrieve"}); filterParams.put("version", new
   * String[] {"1.1"}); // String orgId = "escidoc:persistent25"; filterParams.put("query", new
   * String[] {"\"http://escidoc.de/core/01/structural-relations/organizational-unit\"=" +
   * this.getOrganization().getIdentifier()}); final String userAccountXml =
   * uah.retrieveUserAccounts(filterParams); final SearchRetrieveResponseVO<AccountUserVO>
   * userAccounts =
   * XmlTransformingService.transformToSearchRetrieveResponseAccountUser(userAccountXml); for (final
   * SearchRetrieveRecordVO<AccountUserVO> record : userAccounts.getRecords()) { final AccountUserVO
   * userVO = (AccountUserVO) record.getData(); if (!userVO.getReference().getObjectId()
   * .equals(this.getLoginHelper().getAccountUser().getReference().getObjectId())) {
   * this.collaboratorSelectItems.add(new SelectItem(userVO.getReference().getObjectId(), userVO
   * .getName() + " (" + userVO.getUserid() + ")")); this.possibleCollaboratorsList.add(userVO); } }
   * Collections.sort(this.collaboratorSelectItems, new SelectItemComparator()); }
   * 
   * public void initUserGroups() throws Exception { final UserGroupHandler userGroupHandler =
   * ServiceLocator.getUserGroupHandler(this.getLoginHelper().getESciDocUserHandle());
   * this.collaboratorUserIds = new ArrayList<String>(); final HashMap<String, String[]>
   * filterParams = new HashMap<String, String[]>(); filterParams.put("operation", new String[]
   * {"searchRetrieve"}); filterParams.put("version", new String[] {"1.1"});
   * filterParams.put("query", new String[] {"\"/properties/name\"=\"" + this.year +
   * " - Yearbook User Group for " + this.getOrganization().getName() + " (" +
   * this.getOrganization().getIdentifier() + ")\" and \"/properties/active\" = true"}); final
   * String userGroupXml = userGroupHandler.retrieveUserGroups(filterParams); final
   * SearchRetrieveResponseVO<UserGroupVO> userGroupSearchRetrieveResponse =
   * XmlTransformingService.transformToSearchRetrieveResponseUserGroup(userGroupXml);
   * this.userGroups = new ArrayList<UserGroupVO>(); for (final SearchRetrieveRecordVO<UserGroupVO>
   * record : userGroupSearchRetrieveResponse .getRecords()) { final UserGroupVO userGroup =
   * (UserGroupVO) record.getData(); if (userGroup != null) { this.userGroups.add(userGroup); } } if
   * (this.userGroups.size() > 1) { YearbookItemEditBean.logger
   * .error("More than one UserGroup active and related to the YearbookItem: \"" + this.title +
   * "\" (" + this.yearbookItemSessionBean.getYearbookItem().getObjectId() + ")");
   * throw new Exception("More than one UserGroup active and related to the YearbookItem: \"" +
   * this.title + "\" (" + this.yearbookItemSessionBean.getYearbookItem().getObjectId()
   * + ")"); } else if (this.userGroups.size() == 1) { this.setUserGroup(this.userGroups.get(0));
   * for (final MemberVO user : this.getUserGroup().getMembers()) { if
   * (user.getName().equals("user-account")) { this.collaboratorUserIds.add(user.getMemberId()); } }
   * } // String userGroupGrantsXml = //
   * userGroupHandler.retrieveCurrentGrants(userGroups.get(0).getObjid()); // userGroupGrants =
   * xmlTransforming.transformToGrantVOList(userGroupGrantsXml); }
   */

  /**
   * initializes the years available in the selection box
   */
  private void initSelectableYears() {
    this.selectableYears = new ArrayList<SelectItem>();
    final SimpleDateFormat calendarFormat = new SimpleDateFormat("yyyy");
    final Calendar calendar = Calendar.getInstance();
    final String currentYear = calendarFormat.format(calendar.getTime());
    this.selectableYears.add(new SelectItem(String.valueOf(yearbookItemSessionBean.getYearbook().getYear()),
        String.valueOf(yearbookItemSessionBean.getYearbook().getYear())));

    try {
      QueryBuilder qb = QueryBuilders.termQuery(YearbookServiceDbImpl.INDEX_ORGANIZATION_ID,
          yearbookItemSessionBean.getYearbook().getOrganization().getObjectId());
      SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(qb);
      SearchRetrieveResponseVO<YearbookDbVO> resp =
          ApplicationBean.INSTANCE.getYearbookService().search(srr, getLoginHelper().getAuthenticationToken());
      List<YearbookDbVO> yearbooks = resp.getRecords().stream().map(i -> i.getData()).collect(Collectors.toList());

      List<Integer> years = yearbooks.stream().map(yb -> yb.getYear()).collect(Collectors.toList());

      if (!years.contains(Integer.parseInt(currentYear))
          && yearbookItemSessionBean.getYearbook().getYear() != Integer.parseInt(currentYear)) {
        this.selectableYears.add(new SelectItem(currentYear, currentYear));
      }
      if (!years.contains(Integer.parseInt(currentYear) - 1)
          && yearbookItemSessionBean.getYearbook().getYear() != Integer.parseInt(currentYear) - 1) {
        this.selectableYears
            .add(new SelectItem(Integer.toString(Integer.valueOf(currentYear) - 1), Integer.toString(Integer.valueOf(currentYear) - 1)));
      }



    } catch (Exception e) {
      YearbookItemEditBean.logger.error("Problem with yearbook: \n", e);
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
  }


  /**
   * @return the contexts which are available to the user
   */
  public List<SelectItem> getContextSelectItems() {
    return this.contextSelectItems;
  }

  /**
   * @return the contextIds
   */
  public List<String> getContextIds() {
    return this.contextIds;
  }

  /**
   * @param contextIds the contextIds to set
   */
  public void setContextIds(List<String> contextIds) {
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
    this.contextIds.add(this.getContextPosition() + 1, (String) this.getContextSelectItems().get(0).getValue());
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


  public List<SelectItem> getSelectYear() {
    return this.selectableYears;
  }

  public String delete() {
    try {
      ApplicationBean.INSTANCE.getYearbookService().delete(this.yearbookItemSessionBean.getYearbook().getObjectId(),
          this.getLoginHelper().getAuthenticationToken());
      /*
       * final ItemHandler itemHandler =
       * ServiceLocator.getItemHandler(this.getLoginHelper().getESciDocUserHandle());
       * itemHandler.delete
       * (this.yearbookItemSessionBean.getYearbookItem().getObjectId());
       * this.yearbookItemSessionBean.initYearbook(); final UserGroupHandler userGroupHandler =
       * ServiceLocator.getUserGroupHandler(this.getLoginHelper().getESciDocUserHandle());
       * userGroupHandler.delete(this.getUserGroup().getObjid()); return "loadYearbookPage";
       */

    } catch (final Exception e) {
      this.error(this.getMessage("Yearbook_deleteError"));
      YearbookItemEditBean.logger.error("Problem deleting yearbook", e);
    }
    this.info(this.getMessage("Yearbook_deleteSuccessful"));
    return "loadYearbookModeratorPage";
  }

  /**
   * @return the navigation String for the yearbook page if no Problem
   */
  public String save() {
    try {



      final YearbookService yearbookService = ApplicationBean.INSTANCE.getYearbookService();
      YearbookDbVO clonedYearbook = ModelHelper.makeClone(yearbookItemSessionBean.getYearbook());
      clonedYearbook.setContextIds(contextIds);
      clonedYearbook.setYear(Integer.parseInt(getYear()));


      YearbookDbVO updatedYearbook = yearbookService.update(clonedYearbook, getLoginHelper().getAuthenticationToken());

      yearbookItemSessionBean.initYearbook(updatedYearbook.getObjectId());
      this.info(this.getMessage("Yearbook_createdSuccessfully"));
      return "loadYearbookModeratorPage";


    } catch (final Exception e) {
      this.error(this.getMessage("Yearbook_editError") + " " + e.getMessage());
      YearbookItemEditBean.logger.error("Exception thrown while saving yearbook", e);
    }

    return "";
  }

  public String cancel() {
    return "loadYearbookPage";
  }
}
