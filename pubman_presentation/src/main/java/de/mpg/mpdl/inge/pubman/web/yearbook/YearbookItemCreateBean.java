package de.mpg.mpdl.inge.pubman.web.yearbook;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.YearbookDbVO;
import de.mpg.mpdl.inge.model.referenceobjects.AccountUserRO;
import de.mpg.mpdl.inge.model.referenceobjects.ContextRO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.AffiliationVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubContextVOPresentation;
import de.mpg.mpdl.inge.service.pubman.YearbookService;
import de.mpg.mpdl.inge.service.pubman.impl.YearbookServiceDbImpl;

@ManagedBean(name = "YearbookItemCreateBean")
@SessionScoped
@SuppressWarnings("serial")
public class YearbookItemCreateBean extends FacesBean {
  private static final Logger logger = Logger.getLogger(YearbookItemCreateBean.class);

  private static final String MAXIMUM_RECORDS = "5000";

  private AffiliationVOPresentation affiliation;
  private List<AccountUserRO> collaborators;
  // private List<AccountUserVO> possibleCollaboratorsList;
  private List<ContextRO> contextIds;
  private List<SelectItem> contextSelectItems;
  private final List<SelectItem> selectableYears;
  // private List<SelectItem> userAccountSelectItems;
  // private List<String> collaboratorUserIds;
  private String context;
  private String endDate;
  private String startDate;
  private String title;
  private String year;
  private final YearbookItemSessionBean yisb;
  private int contextPosition;
  private int userPosition;

  public YearbookItemCreateBean() throws Exception {
    this.selectableYears = new ArrayList<SelectItem>();

    List<String> orgIds = YearbookUtils.getYearbookOrganizationIds(this.getLoginHelper().getAccountUser());
    this.setAffiliation(new AffiliationVOPresentation(ApplicationBean.INSTANCE.getOrganizationService().get(orgIds.get(0), null)));


    this.yisb = (YearbookItemSessionBean) FacesTools.findBean("YearbookItemSessionBean");
    this.initContextMenu();
    // this.initUserAccountMenu();
    this.initMetadata();
    this.contextIds = new ArrayList<ContextRO>();
    this.contextIds.add(new ContextRO((String) this.contextSelectItems.get(0).getValue()));
    this.collaborators = new ArrayList<AccountUserRO>();
  }

  private void initMetadata() {
    final SimpleDateFormat calendarFormat = new SimpleDateFormat("yyyy");
    final Calendar calendar = Calendar.getInstance();
    final String currentYear = calendarFormat.format(calendar.getTime());
    try {

      QueryBuilder qb = QueryBuilders.termQuery(YearbookServiceDbImpl.INDEX_ORGANIZATION_ID, getAffiliation().getReference().getObjectId());
      SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(qb);
      SearchRetrieveResponseVO<YearbookDbVO> resp =
          ApplicationBean.INSTANCE.getYearbookService().search(srr, getLoginHelper().getAuthenticationToken());
      List<YearbookDbVO> yearbooks = resp.getRecords().stream().map(i -> i.getData()).collect(Collectors.toList());


      List<Integer> years = yearbooks.stream().map(yb -> yb.getYear()).collect(Collectors.toList());

      if (!years.contains(Integer.parseInt(currentYear))) {
        this.selectableYears.add(new SelectItem(currentYear, currentYear));
      }
      if (!years.contains(Integer.parseInt(currentYear) - 1)) {
        this.selectableYears
            .add(new SelectItem(Integer.toString(Integer.valueOf(currentYear) - 1), Integer.toString(Integer.valueOf(currentYear) - 1)));
      }

    } catch (final Exception e) {
      YearbookItemCreateBean.logger.error("Problem with yearbook: \n", e);
    }

    if (this.getYear() == null) {
      this.setYear(currentYear);
    }
    if (this.getStartDate() == null) {
      this.setStartDate(currentYear + "-01-01");
    }
    if (this.getEndDate() == null) {
      this.setEndDate(currentYear + "-12-31");
    }
    if (this.getTitle() == null) {
      this.setTitle(currentYear + " - Yearbook of " + this.getAffiliation().getName());
    }
  }

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void addContext() {
    this.contextIds.add(this.getContextPosition() + 1, new ContextRO((String) this.getContextSelectItems().get(0).getValue()));
  }

  public void removeContext() {
    this.contextIds.remove(this.getContextPosition());
  }

  /**
   * @return the year which should be represented by the yearbook
   */
  public String getYear() {
    return this.year;
  }

  /**
   * @param newYear (yyyy) the new year which should be represented by the yearbook
   */
  public void setYear(String newYear) {
    this.year = newYear;
    this.setTitle(this.getYear() + " - Yearbook of " + this.getAffiliation().getName());
    this.setStartDate(this.year + "-01-01");
    this.setEndDate(this.year + "-12-31");
  }

  /**
   * @return the date from which the publication are considerd as yeabook-candidates
   */
  public String getStartDate() {
    return this.startDate;
  }

  /**
   * @param newStartDate (yyyy-MM-dd) the date from which the publication are considerd as
   *        yeabook-candidates
   */
  public void setStartDate(String newStartDate) {
    this.startDate = newStartDate;
  }

  /**
   * @return the date until when the publication are considerd as yeabook-candidates
   */
  public String getEndDate() {
    return this.endDate;
  }

  /**
   * @param newEndDate (yyyy-MM-dd) the date until when the publication are considerd as
   *        yeabook-candidates
   */
  public void setEndDate(String newEndDate) {
    this.endDate = newEndDate;
  }

  public String save() {
    try {

      final YearbookService yearbookService = ApplicationBean.INSTANCE.getYearbookService();

      YearbookDbVO yearbook = new YearbookDbVO();
      AffiliationDbVO ou = new AffiliationDbVO();
      ou.setObjectId(getAffiliation().getReference().getObjectId());
      yearbook.setOrganization(ou);
      yearbook.setYear(Integer.parseInt(getYear()));

      for (final ContextRO contextId : this.contextIds) {
        if (!contextId.getObjectId().trim().equals("")) {
          yearbook.getContextIds().add(contextId.getObjectId().trim());
        }
      }

      yearbook = yearbookService.create(yearbook, getLoginHelper().getAuthenticationToken());
      this.info(this.getMessage("Yearbook_createdSuccessfully"));

      this.yisb.initYearbook(yearbook.getObjectId());

      return "loadYearbookPage";
    } catch (final Exception e) {
      this.error(this.getMessage("Yearbook_creationError") + " " + e.getMessage());
      YearbookItemCreateBean.logger.error("Error while creating yearbook", e);
    }

    return "";
  }

  /**
   * @param affiliation
   */
  public void setAffiliation(AffiliationVOPresentation affiliation) {
    this.affiliation = affiliation;
  }

  public AffiliationVOPresentation getAffiliation() {
    return this.affiliation;
  }

  public void setContextSelectItems(List<SelectItem> contextSelectItems) {
    this.contextSelectItems = contextSelectItems;
  }

  public List<SelectItem> getContextSelectItems() {
    return this.contextSelectItems;
  }

  /*
   * public void setUserAccountSelectItems(List<SelectItem> userAccountSelectItems) {
   * this.userAccountSelectItems = userAccountSelectItems; }
   * 
   * public List<SelectItem> getUserAccountSelectItems() { return this.userAccountSelectItems; }
   */

  public void setContextPosition(int contextPosition) {
    this.contextPosition = contextPosition;
  }

  public int getContextPosition() {
    return this.contextPosition;
  }

  /*
   * public void initUserAccountMenu() throws Exception { final UserAccountHandler uah =
   * ServiceLocator.getUserAccountHandler(this.getLoginHelper().getESciDocUserHandle());
   * this.collaboratorUserIds = new ArrayList<String>(); this.possibleCollaboratorsList = new
   * ArrayList<AccountUserVO>(); this.userAccountSelectItems = new ArrayList<SelectItem>(); final
   * HashMap<String, String[]> filterParams = new HashMap<String, String[]>();
   * filterParams.put("operation", new String[] {"searchRetrieve"}); filterParams.put("version", new
   * String[] {"1.1"}); // String orgId = "escidoc:persistent25"; filterParams.put("query", new
   * String[] {"\"http://escidoc.de/core/01/structural-relations/organizational-unit\"=" +
   * this.getAffiliation().getReference().getObjectId()}); filterParams.put("maximumRecords", new
   * String[] {YearbookItemCreateBean.MAXIMUM_RECORDS}); final String uaList =
   * uah.retrieveUserAccounts(filterParams); final SearchRetrieveResponseVO result =
   * XmlTransformingService.transformToSearchRetrieveResponseAccountUser(uaList); final
   * List<SearchRetrieveRecordVO> results = result.getRecords(); for (final SearchRetrieveRecordVO
   * rec : results) { final AccountUserVO userVO = (AccountUserVO) rec.getData(); if
   * (!userVO.getReference().getObjectId()
   * .equals(this.getLoginHelper().getAccountUser().getReference().getObjectId())) {
   * this.userAccountSelectItems.add(new SelectItem(userVO.getReference().getObjectId(), userVO
   * .getName() + " (" + userVO.getUserid() + ")")); this.possibleCollaboratorsList.add(userVO); } }
   * Collections.sort(this.userAccountSelectItems, new SelectItemComparator());
   * 
   * }
   */

  public void initContextMenu() {
    this.contextSelectItems = new ArrayList<SelectItem>();
    final ContextListSessionBean clsb = (ContextListSessionBean) FacesTools.findBean("ContextListSessionBean");
    for (final PubContextVOPresentation context : clsb.getModeratorContextList()) {
      this.contextSelectItems
          .add(new SelectItem(context.getReference().getObjectId(), context.getName() + " (" + context.getReference().getObjectId() + ")"));
    }
  }

  public void setUserPosition(int userPosition) {
    this.userPosition = userPosition;
  }

  public int getUserPosition() {
    return this.userPosition;
  }

  public void setContext(String context) {
    this.context = context;
  }

  public String getContext() {
    return this.context;
  }

  /*
   * public void setCollaboratorUserIds(List<String> collaboratorUserIds) { this.collaboratorUserIds
   * = collaboratorUserIds; for (final AccountUserVO possibleCollaborator :
   * this.possibleCollaboratorsList) { for (final String collaboratorObjectId : collaboratorUserIds)
   * { if (possibleCollaborator.getReference().getObjectId().equals(collaboratorObjectId)) {
   * this.getCollaborators().add(possibleCollaborator.getReference()); } } } }
   * 
   * public List<String> getCollaboratorUserIds() { return this.collaboratorUserIds; }
   */

  public void setCollaborators(List<AccountUserRO> collaboratorUserIds) {
    this.collaborators = collaboratorUserIds;
  }

  public List<AccountUserRO> getCollaborators() {
    return this.collaborators;
  }

  public void setContextIds(List<ContextRO> contextIds) {
    this.contextIds = contextIds;
  }

  public List<ContextRO> getContextIds() {
    return this.contextIds;
  }

  public int getContextIdsListSize() {
    return this.contextIds.size();
  }

  public List<SelectItem> getSelectYear() {
    return this.selectableYears;
  }
}
