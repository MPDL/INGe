package de.mpg.mpdl.inge.pubman.web.yearbook;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.YearbookDbVO;
import de.mpg.mpdl.inge.model.referenceobjects.AccountUserRO;
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
@ViewScoped
@SuppressWarnings("serial")
public class YearbookItemCreateBean extends FacesBean {
  private static final Logger logger = Logger.getLogger(YearbookItemCreateBean.class);

  private final YearbookItemSessionBean yearbookItemSessionBean;

  private Map<String, AffiliationVOPresentation> affiliations;

  private List<AccountUserRO> collaborators;
  private List<String> contextIds;
  private List<SelectItem> contextSelectItems;
  private List<SelectItem> organizationSelectItems;
  private List<SelectItem> selectableYears;

  private String context;
  private String selectedOrgId;
  private String endDate;
  private String startDate;
  private String title;
  private String year;

  private int contextPosition;
  private int userPosition;

  public YearbookItemCreateBean() throws Exception {
    this.yearbookItemSessionBean = (YearbookItemSessionBean) FacesTools.findBean("YearbookItemSessionBean");

    this.affiliations = new HashMap<>();
    List<String> orgIds = YearbookUtils.getYearbookOrganizationIds(this.getLoginHelper().getAccountUser());
    for (String orgId : orgIds) {
      this.affiliations.put(orgId, new AffiliationVOPresentation(ApplicationBean.INSTANCE.getOrganizationService().get(orgId, null)));
    }
    this.selectedOrgId = orgIds.get(0);

    this.initMetadata();
  }

  private void initMetadata() {
    this.initOrganizationMenu();
    this.initContextMenu();

    this.collaborators = new ArrayList<AccountUserRO>();
    this.selectableYears = new ArrayList<SelectItem>();

    try {
      QueryBuilder qb = QueryBuilders.termQuery(YearbookServiceDbImpl.INDEX_ORGANIZATION_ID, this.selectedOrgId);
      SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(qb);
      SearchRetrieveResponseVO<YearbookDbVO> resp =
          ApplicationBean.INSTANCE.getYearbookService().search(srr, getLoginHelper().getAuthenticationToken());
      List<YearbookDbVO> yearbooks = resp.getRecords().stream().map(i -> i.getData()).collect(Collectors.toList());
      List<Integer> years = yearbooks.stream().map(yb -> yb.getYear()).collect(Collectors.toList());

      final SimpleDateFormat calendarFormat = new SimpleDateFormat("yyyy");
      final Calendar calendar = Calendar.getInstance();
      final String currentYear = calendarFormat.format(calendar.getTime());

      if (!years.contains(Integer.parseInt(currentYear))) {
        this.selectableYears.add(new SelectItem(currentYear, currentYear));
      }
      if (!years.contains(Integer.parseInt(currentYear) - 1)) {
        this.selectableYears
            .add(new SelectItem(Integer.toString(Integer.valueOf(currentYear) - 1), Integer.toString(Integer.valueOf(currentYear) - 1)));
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
        this.setTitle(currentYear + " - Yearbook of " + this.affiliations.get(this.selectedOrgId).getName());
      }
    } catch (final Exception e) {
      YearbookItemCreateBean.logger.error("Problem with yearbook: \n", e);
    }
  }

  private void initOrganizationMenu() {
    this.organizationSelectItems = new ArrayList<SelectItem>();
    for (AffiliationDbVO aff : this.affiliations.values()) {
      this.organizationSelectItems.add(new SelectItem(aff.getObjectId(), aff.getName()));
    }
  }

  private void initContextMenu() {
    this.contextSelectItems = new ArrayList<SelectItem>();
    final ContextListSessionBean clsb = (ContextListSessionBean) FacesTools.findBean("ContextListSessionBean");
    for (final PubContextVOPresentation context : clsb.getYearbookContextList()) {
      if (context.getResponsibleAffiliations().stream().anyMatch(i -> i.getObjectId().equals(this.selectedOrgId))) {
        this.contextSelectItems.add(new SelectItem(context.getObjectId(), context.getName() + " (" + context.getObjectId() + ")"));
      }
    }
  }

  // Wird nur 1x während der Lebenszeit des Beans aufgerufen
  @PostConstruct
  public void postConstruct() {
    this.contextIds = new ArrayList<String>();
    this.contextIds.add((String) this.contextSelectItems.get(0).getValue());
  }

  public void addContext(String context) {
    this.contextIds.add(context);
  }

  public void changeOrganization() {
    this.initMetadata();
  }

  public void removeContext(String context) {
    this.contextIds.remove(context);
  }

  public String save() {
    try {
      final YearbookService yearbookService = ApplicationBean.INSTANCE.getYearbookService();

      YearbookDbVO yearbook = new YearbookDbVO();
      AffiliationDbVO ou = new AffiliationDbVO();
      ou.setObjectId(this.selectedOrgId);
      yearbook.setOrganization(ou);
      yearbook.setYear(Integer.parseInt(getYear()));

      for (final String contextId : this.contextIds) {
        yearbook.getContextIds().add(contextId);
      }

      yearbook = yearbookService.create(yearbook, getLoginHelper().getAuthenticationToken());
      this.info(this.getMessage("Yearbook_createdSuccessfully"));

      this.yearbookItemSessionBean.initYearbook(yearbook.getObjectId());

      return "loadYearbookPage";
    } catch (final Exception e) {
      this.error(this.getMessage("Yearbook_creationError") + " " + e.getMessage());
      YearbookItemCreateBean.logger.error("Error while creating yearbook", e);
    }

    return "";
  }

  public Map<String, AffiliationVOPresentation> getAffiliations() {
    return this.affiliations;
  }

  public List<AccountUserRO> getCollaborators() {
    return this.collaborators;
  }

  public String getContext() {
    return this.context;
  }

  public List<String> getContextIds() {
    return this.contextIds;
  }

  public int getContextIdsListSize() {
    return this.contextIds.size();
  }

  public int getContextPosition() {
    return this.contextPosition;
  }

  public List<SelectItem> getContextSelectItems() {
    return this.contextSelectItems;
  }

  public String getEndDate() {
    return this.endDate;
  }

  public List<SelectItem> getOrganizationSelectItems() {
    return this.organizationSelectItems;
  }

  public String getSelectedOrgId() {
    return this.selectedOrgId;
  }

  public List<SelectItem> getSelectYear() {
    return this.selectableYears;
  }

  public String getStartDate() {
    return this.startDate;
  }

  public String getTitle() {
    return this.title;
  }

  public int getUserPosition() {
    return this.userPosition;
  }

  public String getYear() {
    return this.year;
  }

  public void setAffiliations(Map<String, AffiliationVOPresentation> affiliations) {
    this.affiliations = affiliations;
  }

  public void setCollaborators(List<AccountUserRO> collaboratorUserIds) {
    this.collaborators = collaboratorUserIds;
  }

  public void setContext(String context) {
    this.context = context;
  }

  public void setContextIds(List<String> contextIds) {
    this.contextIds = contextIds;
  }

  public void setContextPosition(int contextPosition) {
    this.contextPosition = contextPosition;
  }

  public void setContextSelectItems(List<SelectItem> contextSelectItems) {
    this.contextSelectItems = contextSelectItems;
  }

  public void setEndDate(String newEndDate) {
    this.endDate = newEndDate;
  }

  public void setOrganizationSelectItems(List<SelectItem> organizationSelectItems) {
    this.organizationSelectItems = organizationSelectItems;
  }

  public void setSelectedOrgId(String selectedOrgId) {
    this.selectedOrgId = selectedOrgId;
  }

  public void setStartDate(String newStartDate) {
    this.startDate = newStartDate;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setUserPosition(int userPosition) {
    this.userPosition = userPosition;
  }

  public void setYear(String newYear) {
    this.year = newYear;
    this.setTitle(this.getYear() + " - Yearbook of " + this.affiliations.get(this.selectedOrgId).getName());
    this.setStartDate(this.year + "-01-01");
    this.setEndDate(this.year + "-12-31");
  }
}
