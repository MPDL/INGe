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

  private final YearbookItemSessionBean yearbookItemSessionBean;

  private YearbookDbVO yearbook;

  private String year;

  private List<String> contextIds;
  private List<SelectItem> contextSelectItems;
  private List<SelectItem> selectableYears;

  public YearbookItemEditBean() throws Exception {
    this.yearbookItemSessionBean = (YearbookItemSessionBean) FacesTools.findBean("YearbookItemSessionBean");

    this.init();
  }

  private void init() {
    try {
      this.initContextMenu();

      this.yearbook = this.yearbookItemSessionBean.getYearbook();

      this.initYearbookMetadata();
      this.initSelectableYears();
    } catch (final Exception e) {
      YearbookItemEditBean.logger.error("Problem reinitializing YearbookEditBean: \n", e);
    }
  }

  private void initContextMenu() {
    this.contextSelectItems = new ArrayList<SelectItem>();
    final ContextListSessionBean clsb = (ContextListSessionBean) FacesTools.findBean("ContextListSessionBean");
    for (final PubContextVOPresentation context : clsb.getYearbookContextList()) {
      if (context.getResponsibleAffiliations().stream()
          .anyMatch(i -> i.getObjectId().equals(this.yearbookItemSessionBean.getYearbook().getOrganization().getObjectId()))) {
        this.contextSelectItems.add(new SelectItem(context.getObjectId(), context.getName() + " (" + context.getObjectId() + ")"));
      }
    }
  }

  private void initSelectableYears() {
    this.selectableYears = new ArrayList<SelectItem>();
    this.selectableYears.add(new SelectItem(String.valueOf(this.yearbookItemSessionBean.getYearbook().getYear()),
        String.valueOf(this.yearbookItemSessionBean.getYearbook().getYear())));

    try {
      QueryBuilder qb = QueryBuilders.termQuery(YearbookServiceDbImpl.INDEX_ORGANIZATION_ID,
          this.yearbookItemSessionBean.getYearbook().getOrganization().getObjectId());
      SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(qb);

      SearchRetrieveResponseVO<YearbookDbVO> resp =
          ApplicationBean.INSTANCE.getYearbookService().search(srr, getLoginHelper().getAuthenticationToken());
      List<YearbookDbVO> yearbooks = resp.getRecords().stream().map(i -> i.getData()).collect(Collectors.toList());
      List<Integer> years = yearbooks.stream().map(yb -> yb.getYear()).collect(Collectors.toList());

      final Calendar calendar = Calendar.getInstance();
      final SimpleDateFormat calendarFormat = new SimpleDateFormat("yyyy");
      final String currentYear = calendarFormat.format(calendar.getTime());

      if (!years.contains(Integer.parseInt(currentYear))
          && this.yearbookItemSessionBean.getYearbook().getYear() != Integer.parseInt(currentYear)) {
        this.selectableYears.add(new SelectItem(currentYear, currentYear));
      }

      if (!years.contains(Integer.parseInt(currentYear) - 1)
          && this.yearbookItemSessionBean.getYearbook().getYear() != Integer.parseInt(currentYear) - 1) {
        this.selectableYears
            .add(new SelectItem(Integer.toString(Integer.valueOf(currentYear) - 1), Integer.toString(Integer.valueOf(currentYear) - 1)));
      }
    } catch (Exception e) {
      YearbookItemEditBean.logger.error("Problem with yearbook: \n", e);
    }
  }

  private void initYearbookMetadata() {
    this.year = String.valueOf(this.yearbook.getYear());

    this.contextIds = new ArrayList<String>();
    for (final String contextId : this.yearbook.getContextIds()) {
      this.contextIds.add(contextId);
    }
  }

  public String cancel() {
    return "loadYearbookPage";
  }

  public String delete() {
    try {
      ApplicationBean.INSTANCE.getYearbookService().delete(this.yearbookItemSessionBean.getYearbook().getObjectId(),
          this.getLoginHelper().getAuthenticationToken());
    } catch (final Exception e) {
      this.error(this.getMessage("Yearbook_deleteError"));
      YearbookItemEditBean.logger.error("Problem deleting yearbook", e);
    }

    this.info(this.getMessage("Yearbook_deletedSuccessfully"));

    return "loadYearbookModeratorPage";
  }

  public String save() {
    try {
      YearbookDbVO clonedYearbook = ModelHelper.makeClone(yearbookItemSessionBean.getYearbook());
      clonedYearbook.setContextIds(contextIds);
      clonedYearbook.setYear(Integer.parseInt(getYear()));

      final YearbookService yearbookService = ApplicationBean.INSTANCE.getYearbookService();
      YearbookDbVO updatedYearbook = yearbookService.update(clonedYearbook, getLoginHelper().getAuthenticationToken());

      this.yearbookItemSessionBean.initYearbook(updatedYearbook.getObjectId());
      this.info(this.getMessage("Yearbook_createdSuccessfully"));
      return "loadYearbookModeratorPage";
    } catch (final Exception e) {
      this.error(this.getMessage("Yearbook_editError") + " " + e.getMessage());
      YearbookItemEditBean.logger.error("Exception thrown while saving yearbook", e);
    }

    return "";
  }

  public void addContext(String context) {
    this.contextIds.add(context);
  }

  public List<String> getContextIds() {
    return this.contextIds;
  }

  public int getContextIdsListSize() {
    return this.contextIds.size();
  }

  public List<SelectItem> getContextSelectItems() {
    return this.contextSelectItems;
  }

  public List<SelectItem> getSelectYear() {
    return this.selectableYears;
  }

  public String getYear() {
    return this.year;
  }

  public void removeContext(String context) {
    this.contextIds.remove(context);
  }

  public void setContextIds(List<String> contextIds) {
    this.contextIds = contextIds;
  }

  public void setYear(String year) {
    this.year = year.trim();
  }
}
