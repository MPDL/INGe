package de.mpg.mpdl.inge.pubman.web.yearbook;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.model.SelectItem;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import de.escidoc.www.services.oum.OrganizationalUnitHandler;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransforming;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO.OrderFilter;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.pubman.web.appbase.FacesBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.search.SearchRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.util.AffiliationVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.PubItemVOPresentation;
import de.mpg.mpdl.inge.framework.ServiceLocator;
import de.mpg.mpdl.inge.search.Search;
import de.mpg.mpdl.inge.search.query.ItemContainerSearchResult;
import de.mpg.mpdl.inge.search.query.MetadataSearchQuery;
import de.mpg.mpdl.inge.search.query.PlainCqlQuery;

public class YearbookCandidatesSessionBean extends FacesBean {
  public static final String BEAN_NAME = "YearbookCandidatesSessionBean";
  private static final Logger logger = Logger.getLogger(YearbookCandidatesSessionBean.class);



  private String selectedOrgUnit;



  /**
   * A list with the menu entries for the org units filter menu.
   */
  private List<SelectItem> orgUnitSelectItems;

  private final YearbookItemSessionBean yisb;

  private final PubItemListSessionBean pilsb;

  @EJB
  private XmlTransforming xmlTransforming;


  public YearbookCandidatesSessionBean() {
    yisb = (YearbookItemSessionBean) getSessionBean(YearbookItemSessionBean.class);
    pilsb = (PubItemListSessionBean) getSessionBean(PubItemListSessionBean.class);
    pilsb.setSelectedSortBy(PubItemListSessionBean.SORT_CRITERIA.CREATION_DATE.name());
    pilsb.setSelectedSortOrder(OrderFilter.ORDER_ASCENDING);
  }


  public String getSelectedOrgUnit() {
    return selectedOrgUnit;
  }

  public void setSelectedOrgUnit(String selectedOrgUnit) {
    this.selectedOrgUnit = selectedOrgUnit;
  }

  public List<SelectItem> getOrgUnitSelectItems() {
    if (orgUnitSelectItems == null || orgUnitSelectItems.size() == 0) {
      try {
        orgUnitSelectItems = new ArrayList<SelectItem>();
        orgUnitSelectItems = new ArrayList<SelectItem>();
        orgUnitSelectItems.add(new SelectItem("all", "-"));
        OrganizationalUnitHandler ouHandler = ServiceLocator.getOrganizationalUnitHandler();
        String topLevelOU =
            ouHandler.retrieve(yisb.getYearbookItem().getYearbookMetadata().getCreators().get(0)
                .getOrganization().getIdentifier());
        AffiliationVO affVO = xmlTransforming.transformToAffiliation(topLevelOU);
        List<AffiliationVOPresentation> affList = new ArrayList<AffiliationVOPresentation>();
        affList.add(new AffiliationVOPresentation(affVO));
        addChildAffiliationsToMenu(affList, orgUnitSelectItems, 0);
      } catch (Exception e) {
        logger.error("Error retrieving org units", e);
      }


    }
    return orgUnitSelectItems;
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
    for (AffiliationVOPresentation aff : affs) {
      affSelectItems.add(new SelectItem(aff.getReference().getObjectId(), prefix + " "
          + aff.getName()));
      addChildAffiliationsToMenu(aff.getChildren(), affSelectItems, level + 1);
    }
  }
}
