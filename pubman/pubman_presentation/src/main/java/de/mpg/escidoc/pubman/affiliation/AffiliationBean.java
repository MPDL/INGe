package de.mpg.escidoc.pubman.affiliation;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;
import org.apache.myfaces.trinidad.event.SelectionEvent;
import org.apache.myfaces.trinidad.model.ChildPropertyTreeModel;
import org.apache.myfaces.trinidad.model.TreeModel;

import de.mpg.escidoc.pubman.ErrorPage;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.search.AffiliationDetail;
import de.mpg.escidoc.pubman.search.SearchRetrieverRequestBean;
import de.mpg.escidoc.pubman.search.bean.criterion.OrganizationCriterion;
import de.mpg.escidoc.pubman.util.AffiliationVOPresentation;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsOrganizationalUnitDetailsVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion;
import de.mpg.escidoc.services.search.query.MetadataSearchQuery;

public class AffiliationBean extends FacesBean
{
    private static Logger logger = Logger.getLogger(AffiliationBean.class);
    public static final String LOAD_AFFILIATION_TREE = "loadAffiliationTree";
    private TreeModel tree;
    private List<AffiliationVOPresentation> selected = null;
    AffiliationVOPresentation selectedAffiliation = null;
    private String source = null;
    private Object cache = null;
    private long timestamp;
    
    private static final String PROPERTY_CONTENT_MODEL = 
        "escidoc.framework_access.content-model.id.publication";

    /**
     * Default constructor.
     */
    public AffiliationBean() throws Exception
    {
        tree = new ChildPropertyTreeModel(getAffiliations(), "children");
        timestamp = new Date().getTime();
    }

    public TreeModel getTree()
    {
        if (timestamp < ((AffiliationTree) getApplicationBean(AffiliationTree.class)).getTimestamp())
        {
            tree = new ChildPropertyTreeModel(getAffiliations(), "children");
            timestamp = new Date().getTime();
        }
        return tree;
    }

    public void setTree(TreeModel tree)
    {
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
            for (AffiliationVOPresentation affiliation : getAffiliations())
            {
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
                ((OrganizationVO) cache).setName(new TextVO(selectedAffiliation.getNamePath()));
                ((OrganizationVO) cache).setIdentifier(selectedAffiliation.getReference().getObjectId());
                String address = "";
                if (selectedAffiliation.getDefaultMetadata().getCity() != null)
                {
                    address += selectedAffiliation.getDefaultMetadata().getCity();
                }
                if (selectedAffiliation.getDefaultMetadata().getCity() != null
                        && !selectedAffiliation.getDefaultMetadata().getCity().equals("")
                        && selectedAffiliation.getDefaultMetadata().getCountryCode() != null
                        && !selectedAffiliation.getDefaultMetadata().getCountryCode().equals(""))
                {
                    address += ", ";
                }
                if (selectedAffiliation.getDefaultMetadata().getCountryCode() != null)
                {
                    address += selectedAffiliation.getDefaultMetadata().getCountryCode();
                }
                ((OrganizationVO) cache).setAddress(address);
            }
            return "loadEditItem";
        }
        else if ("EasySubmission".equals(source))
        {
            if (cache != null && cache instanceof OrganizationVO)
            {
                ((OrganizationVO) cache).setName(new TextVO(selectedAffiliation.getNamePath()));
                ((OrganizationVO) cache).setIdentifier(selectedAffiliation.getReference().getObjectId());
                String address = "";
                if (selectedAffiliation.getDefaultMetadata().getCity() != null)
                {
                    address += selectedAffiliation.getDefaultMetadata().getCity();
                }
                if (selectedAffiliation.getDefaultMetadata().getCity() != null
                        && !selectedAffiliation.getDefaultMetadata().getCity().equals("")
                        && selectedAffiliation.getDefaultMetadata().getCountryCode() != null
                        && !selectedAffiliation.getDefaultMetadata().getCountryCode().equals(""))
                {
                    address += ", ";
                }
                if (selectedAffiliation.getDefaultMetadata().getCountryCode() != null)
                {
                    address += selectedAffiliation.getDefaultMetadata().getCountryCode();
                }
                ((OrganizationVO) cache).setAddress(address);
            }
            return "loadNewEasySubmission";
        }
        
        else if ("AdvancedSearch".equals(source))
        {
            if (cache != null && cache instanceof OrganizationCriterion)
            {
                ((OrganizationCriterion) cache).setSearchString("\"" + selectedAffiliation.getName() + "\"");
            }
            return "displaySearchPage";
        }
        else if (selectedAffiliation != null)
        {
            // start search by affiliation
            
            return startSearchForAffiliation(selectedAffiliation);
        }
        else
        {
            return null;
        }
    }

    private AffiliationVOPresentation findAffiliationByName(String name, AffiliationVOPresentation affiliation)
        throws Exception
    {
        String affName = null;
        if (affiliation != null && affiliation.getMetadataSets().size() > 0
                && affiliation.getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO)
        {
            affName = ((MdsOrganizationalUnitDetailsVO) affiliation.getMetadataSets().get(0)).getName();
        }
        if (name.equals(affName))
        {
            return affiliation;
        }
        else
        {
            for (AffiliationVOPresentation child : affiliation.getChildren())
            {
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

    public List<AffiliationVOPresentation> getSelected()
    {
        return selected;
    }

    public void setSelected(List<AffiliationVOPresentation> selected)
    {
        this.selected = selected;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public Object getCache()
    {
        return cache;
    }

    public void setCache(Object cache)
    {
        this.cache = cache;
    }

    public AffiliationVOPresentation getSelectedAffiliation()
    {
        return selectedAffiliation;
    }

    public void setSelectedAffiliation(AffiliationVOPresentation selectedAffiliation)
    {
        this.selectedAffiliation = selectedAffiliation;
    }

    public List<AffiliationVOPresentation> getAffiliations()
    {
        return ((AffiliationTree) getApplicationBean(AffiliationTree.class)).getAffiliations();
    }
    
    /**
     * Searches Items by Affiliation.
     * 
     * @return string, identifying the page that should be navigated to after this method call
     */    
    public String startSearchForAffiliation(AffiliationVO affiliation)
    {
        try
        {
            ArrayList<MetadataSearchCriterion> criteria = new ArrayList<MetadataSearchCriterion>();
            criteria.add(new MetadataSearchCriterion(MetadataSearchCriterion.CriterionType.ORGANIZATION_PIDS, 
                        affiliation.getReference().getObjectId()));
            criteria.add(new MetadataSearchCriterion(MetadataSearchCriterion.CriterionType.OBJECT_TYPE, 
                    "item", MetadataSearchCriterion.LogicalOperator.AND));
            
            ArrayList<String> contentTypes = new ArrayList<String>();
            String contentTypeIdPublication = PropertyReader.getProperty(PROPERTY_CONTENT_MODEL);
            contentTypes.add(contentTypeIdPublication);
            
            MetadataSearchQuery query = new MetadataSearchQuery(contentTypes, criteria);
            
            String cql = query.getCqlQuery();
            
            //redirect to SearchResultPage which processes the query
            getExternalContext().redirect("SearchResultListPage.jsp?"
                    + SearchRetrieverRequestBean.parameterCqlQuery
                    + "="
                    + URLEncoder.encode(cql)
                    + "&"
                    + SearchRetrieverRequestBean.parameterSearchType
                    + "=org");
            
        }
        catch (Exception e)
        {
            logger.error("Could not search for items." + "\n" + e.toString());
            ((ErrorPage) getRequestBean(ErrorPage.class)).setException(e);
            
            return ErrorPage.LOAD_ERRORPAGE;
        }
     
        return "";
    }
}
