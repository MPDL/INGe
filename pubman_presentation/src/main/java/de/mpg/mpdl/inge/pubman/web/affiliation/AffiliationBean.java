package de.mpg.mpdl.inge.pubman.web.affiliation;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.el.ValueExpression;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsOrganizationalUnitDetailsVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.pubman.web.ErrorPage;
import de.mpg.mpdl.inge.pubman.web.search.AffiliationDetail;
import de.mpg.mpdl.inge.pubman.web.search.SearchRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.OrganizationCriterion;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.AffiliationVOPresentation;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion;
import de.mpg.mpdl.inge.search.query.MetadataSearchQuery;
import de.mpg.mpdl.inge.util.PropertyReader;

@ManagedBean(name = "AffiliationBean")
@SessionScoped
@SuppressWarnings("serial")
public class AffiliationBean extends FacesBean {
  private static final Logger logger = Logger.getLogger(AffiliationBean.class);

  public static final String LOAD_AFFILIATION_TREE = "loadAffiliationTree";

  private static final String PROPERTY_CONTENT_MODEL =
      "escidoc.framework_access.content-model.id.publication";

  private AffiliationVOPresentation selectedAffiliation = null;

  private List<AffiliationVOPresentation> selected = null;
  private List<AffiliationVOPresentation> topLevelAffs = null;

  private Object cache = null;
  private String source = null;
  private TreeNode rootTreeNode;

  public AffiliationBean() throws Exception {
    this.setTopLevelAffs(getTopLevelAffiliations());

    rootTreeNode = new DefaultTreeNode("Root", null);
    for (AffiliationVOPresentation aff : getAffiliations()) {
      TreeNode affNode = new DefaultTreeNode(aff, rootTreeNode);
      affNode.setSelectable(false);

      loadChildTreeNodes(affNode, false);

      // ----- Remove this if tree should not be expanded from begin
      affNode.setExpanded(true);
      for (TreeNode node : affNode.getChildren()) {
        loadChildTreeNodes(node, false);
      }
      // -----
    }
  }

  public void selectNode(ActionEvent event) throws Exception {
    UIComponent component = event.getComponent();
    ValueExpression valueExpression = component.getValueExpression("text");
    String value =
        (String) valueExpression.getValue(FacesTools.getCurrentInstance().getELContext());
    logger.debug("SELECTNODE:" + value);
    if (value != null) {
      for (AffiliationVOPresentation affiliation : getAffiliations()) {
        selectedAffiliation = findAffiliationByName(value, affiliation);
        if (selectedAffiliation != null) {
          break;
        }
      }
    }
    ((AffiliationDetail) FacesTools.findBean("AffiliationDetail"))
        .setAffiliationVO(selectedAffiliation);
    logger.debug("Selected affiliation is " + selectedAffiliation);
  }

  private void setAffiliationsPath() {
    if (cache != null && cache instanceof OrganizationVO)

    {
      ((OrganizationVO) cache).setName(selectedAffiliation.getNamePath());
      ((OrganizationVO) cache).setIdentifier(selectedAffiliation.getReference().getObjectId());
      String address = "";
      if (selectedAffiliation.getDefaultMetadata().getCity() != null) {
        address += selectedAffiliation.getDefaultMetadata().getCity();
      }
      if (selectedAffiliation.getDefaultMetadata().getCity() != null
          && !selectedAffiliation.getDefaultMetadata().getCity().equals("")
          && selectedAffiliation.getDefaultMetadata().getCountryCode() != null
          && !selectedAffiliation.getDefaultMetadata().getCountryCode().equals("")) {
        address += ", ";
      }
      if (selectedAffiliation.getDefaultMetadata().getCountryCode() != null) {
        address += selectedAffiliation.getDefaultMetadata().getCountryCode();
      }
      ((OrganizationVO) cache).setAddress(address);
    }
  }

  public String startSearch() {
    if ("EditItem".equals(source)) {
      setAffiliationsPath();
      return "loadEditItem";
    }

    if ("EasySubmission".equals(source)) {
      setAffiliationsPath();
      return "loadNewEasySubmission";
    }

    if ("AdvancedSearch".equals(source)) {
      if (cache != null && cache instanceof OrganizationCriterion) {
        ((OrganizationCriterion) cache).setAffiliation(selectedAffiliation);
      }
      return "displaySearchPage";
    }

    if ("BrowseBy".equals(source)) {
      return startSearchForAffiliation(selectedAffiliation);
    }

    if (selectedAffiliation != null) {
      return startSearchForAffiliation(selectedAffiliation);
    }

    return null;
  }

  private AffiliationVOPresentation findAffiliationByName(String name,
      AffiliationVOPresentation affiliation) throws Exception {
    String affName = null;
    if (affiliation != null && affiliation.getMetadataSets().size() > 0
        && affiliation.getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO) {
      affName = ((MdsOrganizationalUnitDetailsVO) affiliation.getMetadataSets().get(0)).getName();
    }

    if (name.equals(affName)) {
      return affiliation;
    }

    for (AffiliationVOPresentation child : affiliation.getChildren()) {
      AffiliationVOPresentation result = findAffiliationByName(name, child);
      if (result != null) {
        return result;
      }
    }

    return null;
  }

  private ItemControllerSessionBean getItemControllerSessionBean() {
    return (ItemControllerSessionBean) FacesTools.findBean("ItemControllerSessionBean");
  }

  public List<AffiliationVOPresentation> getSelected() {
    return selected;
  }

  public void setSelected(List<AffiliationVOPresentation> selected) {
    this.selected = selected;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public Object getCache() {
    return cache;
  }

  public void setCache(Object cache) {
    this.cache = cache;
  }

  public AffiliationVOPresentation getSelectedAffiliation() {
    return selectedAffiliation;
  }

  public void setSelectedAffiliation(AffiliationVOPresentation selectedAffiliation) {
    this.selectedAffiliation = selectedAffiliation;
  }

  public List<AffiliationVOPresentation> getAffiliations() {
    return ((AffiliationTree) FacesTools.findBean("AffiliationTree")).getAffiliations();
  }

  public TreeNode getRootTreeNode() {

    return rootTreeNode;
  }

  public void setRootTreeNode(TreeNode rootTreeNode) {
    this.rootTreeNode = rootTreeNode;
  }


  public void onNodeExpand(NodeExpandEvent event) {
    // System.out.println("OnNodeExpand!!!!" +
    // ((AffiliationVOPresentation)event.getTreeNode().getData()).getName());
    List<TreeNode> children = event.getTreeNode().getChildren();

    if (children != null) {
      for (TreeNode childAff : children) {
        loadChildTreeNodes(childAff, false);

      }
    }


  }

  private void loadChildTreeNodes(TreeNode parent, boolean expand) {
    try {
      // parent.getChildren().clear();

      AffiliationVOPresentation parentAff = (AffiliationVOPresentation) parent.getData();

      List<AffiliationVOPresentation> childList = parentAff.getChildren();
      if (childList != null) {
        for (AffiliationVOPresentation childAff : childList) {
          // System.out.println("Loading aff " + childAff.getName());
          TreeNode childNode = new DefaultTreeNode(childAff, parent);
          childNode.setSelectable(false);
          childNode.setExpanded(expand);
        }
      }


    } catch (Exception e) {
      logger.error("Error while loading child affiliations", e);
    }
  }

  /**
   * Searches Items by Affiliation.
   * 
   * @return string, identifying the page that should be navigated to after this method call
   */
  public String startSearchForAffiliation(AffiliationVO affiliation) {
    try {
      ArrayList<MetadataSearchCriterion> criteria = new ArrayList<MetadataSearchCriterion>();
      criteria
          .add(new MetadataSearchCriterion(MetadataSearchCriterion.CriterionType.ORGANIZATION_PIDS,
              affiliation.getReference().getObjectId()));
      criteria.add(new MetadataSearchCriterion(MetadataSearchCriterion.CriterionType.OBJECT_TYPE,
          "item", MetadataSearchCriterion.LogicalOperator.AND));

      ArrayList<String> contentTypes = new ArrayList<String>();
      String contentTypeIdPublication = PropertyReader.getProperty(PROPERTY_CONTENT_MODEL);
      contentTypes.add(contentTypeIdPublication);

      MetadataSearchQuery query = new MetadataSearchQuery(contentTypes, criteria);

      String cql = query.getCqlQuery();

      // redirect to SearchResultPage which processes the query
      FacesTools.getExternalContext()
          .redirect("SearchResultListPage.jsp?" + SearchRetrieverRequestBean.parameterCqlQuery + "="
              + URLEncoder.encode(cql) + "&" + SearchRetrieverRequestBean.parameterSearchType
              + "=org");

    } catch (Exception e) {
      logger.error("Could not search for items." + "\n" + e.toString());
      ((ErrorPage) FacesTools.findBean("ErrorPage")).setException(e);

      return ErrorPage.LOAD_ERRORPAGE;
    }

    return "";
  }

  public List<AffiliationVOPresentation> getTopLevelAffiliations() {

    AffiliationTree affTree = (AffiliationTree) FacesTools.findBean("AffiliationTree");
    List<AffiliationVOPresentation> topsPres = new ArrayList<AffiliationVOPresentation>();
    topsPres = affTree.getAffiliations();
    if (topsPres != null && topsPres.size() > 0)
      return topsPres;

    List<AffiliationVO> tops = null;
    try {
      tops = this.getItemControllerSessionBean().searchTopLevelAffiliations();
    } catch (Exception e) {
      logger.error("TopLevel affiliations cannot be fetched.");
      tops = new ArrayList<AffiliationVO>();
    }

    for (int i = 0; i < tops.size(); i++) {
      topsPres.add(new AffiliationVOPresentation(tops.get(i)));
    }

    return topsPres;
  }

  public List<AffiliationVOPresentation> getTopLevelAffs() {
    return topLevelAffs;
  }

  public void setTopLevelAffs(List<AffiliationVOPresentation> topLevelAffs) {
    this.topLevelAffs = topLevelAffs;
  }
}
