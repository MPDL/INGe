package de.mpg.escidoc.pubman.affiliation;

import java.util.List;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;
import org.apache.myfaces.trinidad.event.SelectionEvent;
import org.apache.myfaces.trinidad.model.ChildPropertyTreeModel;
import org.apache.myfaces.trinidad.model.TreeModel;

import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.search.AffiliationDetail;
import de.mpg.escidoc.pubman.search.SearchResultList;
import de.mpg.escidoc.pubman.util.AffiliationVOPresentation;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;

public class AffiliationBean extends FacesBean {

	private static Logger logger = Logger.getLogger(AffiliationBean.class);
	
	public static final String LOAD_AFFILIATION_TREE = "loadAffiliationTree";
	
	private TreeModel tree;
	List<AffiliationVOPresentation> affiliations;
	private List<AffiliationVOPresentation> selected = null;
	AffiliationVOPresentation selectedAffiliation = null;
	private String source = null;
	
	private Object cache = null;

	/**
	 * Default constructor.
	 */
	public AffiliationBean() throws Exception
	{
		affiliations = CommonUtils.convertToAffiliationVOPresentationList(getItemControllerSessionBean().retrieveTopLevelAffiliations());
		tree = new ChildPropertyTreeModel(affiliations, "children");
	}
	
	public TreeModel getTree() {
		return tree;
	}

	public void setTree(TreeModel tree) {
		this.tree = tree;
	}
	
	public void select(SelectionEvent event)
	{
		logger.debug("SELECT: " + event);
	}
	
	public void selectNode(ActionEvent event) throws Exception
	{
		UIComponent component = event.getComponent();
		ValueExpression valueExpression = component.getValueExpression("text");
		String value = (String) valueExpression.getValue(FacesContext.getCurrentInstance().getELContext());
		logger.debug("SELECTNODE:" + value);
		
		if (value != null)
		{
			for (AffiliationVOPresentation affiliation : affiliations) {
				selectedAffiliation = findAffiliationByName(value, affiliation);
				if (selectedAffiliation != null)
				{
					break;
				}
			}
		}
		((AffiliationDetail) getSessionBean(AffiliationDetail.class)).setAffiliationVO(selectedAffiliation);
		logger.debug("Selected affiliation is " + selectedAffiliation);
	}
	
	public String startSearch()
	{
		if ("EditItem".equals(source))
		{
			if (cache != null && cache instanceof OrganizationVO)
			{
				((OrganizationVO)cache).setName(new TextVO(selectedAffiliation.getName()));
				((OrganizationVO)cache).setAddress(selectedAffiliation.getAddress());
				((OrganizationVO)cache).setIdentifier(selectedAffiliation.getPid());
			}
			return "loadEditItem";
		}
		if ("EasySubmission".equals(source))
		{
			if (cache != null && cache instanceof OrganizationVO)
			{
				((OrganizationVO)cache).setName(new TextVO(selectedAffiliation.getName()));
				((OrganizationVO)cache).setAddress(selectedAffiliation.getAddress());
				((OrganizationVO)cache).setIdentifier(selectedAffiliation.getPid());
			}
			return "loadNewEasySubmission";
		}
		else if (selectedAffiliation != null)
		{
			//start search by affiliation
	        SearchResultList list = (SearchResultList)getSessionBean(SearchResultList.class);
	        return list.startSearchForAffiliation(selectedAffiliation);
		}
		else
		{
			return null;
		}

	}
	
	private AffiliationVOPresentation findAffiliationByName(String name, AffiliationVOPresentation affiliation) throws Exception
	{
		if (name.equals(affiliation.getName()))
		{
			return affiliation;
		}
		else
		{
			for (AffiliationVOPresentation child : affiliation.getChildren()) {
				AffiliationVOPresentation result = findAffiliationByName(name, child);
				if (result != null)
				{
					return result;
				}
			}
		}
		return null;
	}
	
	private AffiliationSessionBean getAffiliationSessionBean()
	{
		return (AffiliationSessionBean) getSessionBean(AffiliationSessionBean.class);
	}
	
	private ItemControllerSessionBean getItemControllerSessionBean()
	{
		return (ItemControllerSessionBean) getSessionBean(ItemControllerSessionBean.class);
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
	
}
