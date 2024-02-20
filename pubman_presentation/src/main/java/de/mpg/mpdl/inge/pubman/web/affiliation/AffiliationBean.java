package de.mpg.mpdl.inge.pubman.web.affiliation;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.pubman.web.ErrorPage;
import de.mpg.mpdl.inge.pubman.web.qaws.QAWSSessionBean;
import de.mpg.mpdl.inge.pubman.web.search.SearchRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase;
import de.mpg.mpdl.inge.pubman.web.search.criterions.stringOrHiddenId.OrganizationSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.AffiliationVOPresentation;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;
import de.mpg.mpdl.inge.service.util.SearchUtils;
import jakarta.faces.bean.ManagedBean;
import jakarta.faces.bean.SessionScoped;
import jakarta.faces.model.SelectItem;

@ManagedBean(name = "AffiliationBean")
@SessionScoped
@SuppressWarnings("serial")
public class AffiliationBean extends FacesBean {
  private static final Logger logger = LogManager.getLogger(AffiliationBean.class);

  public static final String LOAD_AFFILIATION_TREE = "loadAffiliationTree";

  private AffiliationVOPresentation selectedAffiliation = null;

  private List<AffiliationVOPresentation> selected = null;
  private List<AffiliationVOPresentation> topLevelAffs = null;

  private List<SelectItem> affiliationSelectItems;
  private Map<String, AffiliationVOPresentation> affiliationMap;

  private Object cache = null;
  private String source = null;
  private TreeNode rootTreeNode;



  public AffiliationBean() throws Exception {
    this.affiliationMap = new HashMap<>();
    this.topLevelAffs =
        CommonUtils.convertToAffiliationVOPresentationList(ApplicationBean.INSTANCE.getOrganizationService().searchTopLevelOrganizations());

    this.rootTreeNode = new DefaultTreeNode("Root", null);
    for (final AffiliationVOPresentation aff : this.topLevelAffs) {
      final TreeNode<Object> affNode = new DefaultTreeNode(aff, this.rootTreeNode);
      affNode.setSelectable(false);

      this.loadChildTreeNodes(affNode, false);

      // ----- Remove this if tree should not be expanded from begin
      affNode.setExpanded(true);
      for (final TreeNode node : affNode.getChildren()) {
        this.loadChildTreeNodes(node, false);
      }
      // -----
    }
  }


  private void setAffiliationsPath() {
    if (null != this.cache && this.cache instanceof OrganizationVO)

    {
      ((OrganizationVO) this.cache).setName(this.selectedAffiliation.getNamePath());
      ((OrganizationVO) this.cache).setIdentifier(this.selectedAffiliation.getObjectId());
      String address = "";
      if (null != this.selectedAffiliation.getMetadata().getCity()) {
        address += this.selectedAffiliation.getMetadata().getCity();
      }
      if (null != this.selectedAffiliation.getMetadata().getCity() && !this.selectedAffiliation.getMetadata().getCity().isEmpty()
          && null != this.selectedAffiliation.getMetadata().getCountryCode()
          && !this.selectedAffiliation.getMetadata().getCountryCode().isEmpty()) {
        address += ", ";
      }
      if (null != this.selectedAffiliation.getMetadata().getCountryCode()) {
        address += this.selectedAffiliation.getMetadata().getCountryCode();
      }
      ((OrganizationVO) this.cache).setAddress(address);
    }
  }

  public String startSearch() {
    if ("EditItem".equals(this.source)) {
      this.setAffiliationsPath();
      return "loadEditItem";
    }

    if ("EasySubmission".equals(this.source)) {
      this.setAffiliationsPath();
      return "loadNewEasySubmission";
    }

    if ("BrowseBy".equals(this.source)) {
      return this.startSearchForAffiliation(this.selectedAffiliation);
    }

    if (null != this.selectedAffiliation) {
      return this.startSearchForAffiliation(this.selectedAffiliation);
    }

    return "";
  }

  // private AffiliationVOPresentation findAffiliationByName(String name,
  // AffiliationVOPresentation affiliation) throws Exception {
  // String affName = null;
  // if (affiliation != null && affiliation.getMetadataSets().size() > 0
  // && affiliation.getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO) {
  // affName = ((MdsOrganizationalUnitDetailsVO) affiliation.getMetadataSets().get(0)).getName();
  // }
  //
  // if (name.equals(affName)) {
  // return affiliation;
  // }
  //
  // for (final AffiliationVOPresentation child : affiliation.getChildren()) {
  // final AffiliationVOPresentation result = this.findAffiliationByName(name, child);
  // if (result != null) {
  // return result;
  // }
  // }
  //
  // return null;
  // }



  public List<AffiliationVOPresentation> getSelected() {
    return this.selected;
  }

  public void setSelected(List<AffiliationVOPresentation> selected) {
    this.selected = selected;
  }

  public String getSource() {
    return this.source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public Object getCache() {
    return this.cache;
  }

  public void setCache(Object cache) {
    this.cache = cache;
  }

  public AffiliationVOPresentation getSelectedAffiliation() {
    return this.selectedAffiliation;
  }

  public void setSelectedAffiliation(AffiliationVOPresentation selectedAffiliation) {
    this.selectedAffiliation = selectedAffiliation;
  }


  public TreeNode getRootTreeNode() {

    return this.rootTreeNode;
  }

  public void setRootTreeNode(TreeNode rootTreeNode) {
    this.rootTreeNode = rootTreeNode;
  }


  public void onNodeExpand(NodeExpandEvent event) {
    // System.out.println("OnNodeExpand!!!!" +
    // ((AffiliationVOPresentation)event.getTreeNode().getData()).getName());
    final List<TreeNode> children = event.getTreeNode().getChildren();

    if (null != children) {
      for (final TreeNode childAff : children) {
        this.loadChildTreeNodes(childAff, false);

      }
    }


  }

  private void loadChildTreeNodes(TreeNode parent, boolean expand) {
    try {
      final AffiliationVOPresentation parentAff = (AffiliationVOPresentation) parent.getData();

      final List<AffiliationVOPresentation> childList = parentAff.getChildren();
      if (null != childList) {
        for (final AffiliationVOPresentation childAff : childList) {
          final TreeNode childNode = new DefaultTreeNode(childAff, parent);
          childNode.setSelectable(false);
          childNode.setExpanded(expand);
        }
      }
    } catch (final Exception e) {
      logger.error("Error while loading child affiliations", e);
    }
  }

  /**
   * Searches Items by Affiliation.
   *
   * @return string, identifying the page that should be navigated to after this method call
   */
  public String startSearchForAffiliation(AffiliationDbVO affiliation) {
    try {

      List<SearchCriterionBase> scList = new ArrayList<>();
      OrganizationSearchCriterion sc = new OrganizationSearchCriterion();
      sc.setHiddenId(affiliation.getObjectId());
      sc.setSearchString(affiliation.getName());
      scList.add(sc);

      Query qb = SearchCriterionBase.scListToElasticSearchQuery(scList);

      BoolQuery.Builder bqb = new BoolQuery.Builder();
      bqb.must(SearchUtils.baseElasticSearchQueryBuilder(ApplicationBean.INSTANCE.getPubItemService().getElasticSearchIndexFields(),
          PubItemServiceDbImpl.INDEX_PUBLIC_STATE, ItemVersionRO.State.RELEASED.name()));
      bqb.must(SearchUtils.baseElasticSearchQueryBuilder(ApplicationBean.INSTANCE.getPubItemService().getElasticSearchIndexFields(),
          PubItemServiceDbImpl.INDEX_VERSION_STATE, ItemVersionRO.State.RELEASED.name()));
      bqb.must(qb);

      qb = bqb.build()._toQuery();

      FacesTools.getExternalContext().redirect("SearchResultListPage.jsp?esq=" + URLEncoder.encode(qb.toString(), StandardCharsets.UTF_8)
          + "&" + SearchRetrieverRequestBean.parameterSearchType + "=org");

    } catch (final Exception e) {
      logger.error("Could not search for items." + "\n" + e);
      ((ErrorPage) FacesTools.findBean("ErrorPage")).setException(e);

      return ErrorPage.LOAD_ERRORPAGE;
    }

    return "";
  }


  public List<AffiliationVOPresentation> getTopLevelAffs() {
    return this.topLevelAffs;
  }

  public void setTopLevelAffs(List<AffiliationVOPresentation> topLevelAffs) {
    this.topLevelAffs = topLevelAffs;
  }

  /**
   * Returns SelectItems for a menu with all organizational units.
   *
   * @return
   * @throws Exception
   */
  public synchronized List<SelectItem> getAffiliationSelectItems() throws Exception {



    if (null == this.affiliationSelectItems) {

      final List<SelectItem> list = new ArrayList<>();
      list.add(new SelectItem("all", this.getLabel("EditItem_NO_ITEM_SET")));

      final List<AffiliationVOPresentation> topLevelAffs = this.topLevelAffs;
      this.addChildAffiliationsToMenu(topLevelAffs, list, 0);

      this.affiliationSelectItems = list;

      ((QAWSSessionBean) FacesTools.findBean("QAWSSessionBean")).setOrgUnitSelectItems(this.affiliationSelectItems);
    }


    return this.affiliationSelectItems;
  }

  /**
   * Adds the list of the given affiliations to the filter select.
   *
   * @param affs
   * @param affSelectItems
   * @param level
   * @throws Exception
   */
  private void addChildAffiliationsToMenu(List<AffiliationVOPresentation> affs, List<SelectItem> affSelectItems, int level)
      throws Exception {
    if (null == affs) {
      return;
    }

    StringBuilder prefixBuilder = new StringBuilder();
    for (int i = 0; i < level; i++) {
      // 2 save blanks
      prefixBuilder.append('\u00A0');
      prefixBuilder.append('\u00A0');
      prefixBuilder.append('\u00A0');
    }
    String prefix = prefixBuilder.toString();
    // 1 right angle
    prefix += '\u2514';
    for (final AffiliationVOPresentation aff : affs) {
      affSelectItems.add(new SelectItem(aff.getObjectId(), prefix + " " + aff.getName()));
      this.affiliationMap.put(aff.getObjectId(), aff);
      if (null != aff.getChildren()) {
        this.addChildAffiliationsToMenu(aff.getChildren(), affSelectItems, level + 1);
      }
    }
  }

  public Map<String, AffiliationVOPresentation> getAffiliationMap() {
    return this.affiliationMap;
  }

  public void setAffiliationMap(Map<String, AffiliationVOPresentation> affiliationMap) {
    this.affiliationMap = affiliationMap;
  }

  /**
   * Is called from JSF to reload the ou data.
   *
   * @return Just a dummy message
   * @throws Exception Any exception
   */
  public String getResetMessage() throws Exception {
    this.topLevelAffs =
        CommonUtils.convertToAffiliationVOPresentationList(ApplicationBean.INSTANCE.getOrganizationService().searchTopLevelOrganizations());
    this.affiliationSelectItems = null;
    return this.getMessage("Affiliations_reloaded");
  }



}
