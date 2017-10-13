package de.mpg.mpdl.inge.pubman.web.yearbook;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.escidoc.www.services.oum.OrganizationalUnitHandler;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO.OrderFilter;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.AffiliationVOPresentation;

@ManagedBean(name = "YearbookCandidatesSessionBean")
@SessionScoped
@SuppressWarnings("serial")
public class YearbookCandidatesSessionBean extends FacesBean {
  private static final Logger logger = Logger.getLogger(YearbookCandidatesSessionBean.class);

  private String selectedOrgUnit;
  private List<SelectItem> orgUnitSelectItems;
  private final YearbookItemSessionBean yisb;
  private final PubItemListSessionBean pilsb;

  public YearbookCandidatesSessionBean() {
    this.yisb = (YearbookItemSessionBean) FacesTools.findBean("YearbookItemSessionBean");
    this.pilsb = (PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean");
    this.pilsb.setSelectedSortBy(PubItemListSessionBean.SORT_CRITERIA.CREATION_DATE.name());
    this.pilsb.setSelectedSortOrder(OrderFilter.ORDER_ASCENDING);
  }


  public String getSelectedOrgUnit() {
    return this.selectedOrgUnit;
  }

  public void setSelectedOrgUnit(String selectedOrgUnit) {
    this.selectedOrgUnit = selectedOrgUnit;
  }

  public List<SelectItem> getOrgUnitSelectItems() {
    if (this.orgUnitSelectItems == null || this.orgUnitSelectItems.size() == 0) {
      try {
        this.orgUnitSelectItems = new ArrayList<SelectItem>();
        this.orgUnitSelectItems = new ArrayList<SelectItem>();
        this.orgUnitSelectItems.add(new SelectItem("all", "-"));

        final AffiliationVO affVO =
            ApplicationBean.INSTANCE.getOrganizationService().get(
                yisb.getYearbook().getOrganization().getObjectId(), null);
        final List<AffiliationVOPresentation> affList = new ArrayList<AffiliationVOPresentation>();
        affList.add(new AffiliationVOPresentation(affVO));
        YearbookCandidatesSessionBean.addChildAffiliationsToMenu(affList, this.orgUnitSelectItems,
            0);
      } catch (final Exception e) {
        YearbookCandidatesSessionBean.logger.error("Error retrieving org units", e);
      }
    }

    return this.orgUnitSelectItems;
  }

  public void setOrgUnitSelectItems(List<SelectItem> orgUnitSelectItems) {
    this.orgUnitSelectItems = orgUnitSelectItems;
  }

  private static void addChildAffiliationsToMenu(List<AffiliationVOPresentation> affs,
      List<SelectItem> affSelectItems, int level) throws Exception {
    if (affs == null) {
      return;
    }

    String prefix = "";
    for (int i = 0; i < level; i++) {
      // 2 save blanks
      prefix += '\u00A0';
      prefix += '\u00A0';
      prefix += '\u00A0';
    }
    // 1 right angle
    prefix += '\u2514';
    for (final AffiliationVOPresentation aff : affs) {
      affSelectItems.add(new SelectItem(aff.getReference().getObjectId(), prefix + " "
          + aff.getName()));
      YearbookCandidatesSessionBean.addChildAffiliationsToMenu(aff.getChildren(), affSelectItems,
          level + 1);
    }
  }
}
