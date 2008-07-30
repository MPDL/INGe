package de.mpg.escidoc.pubman.util;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.affiliation.AffiliationBean;
import de.mpg.escidoc.pubman.search.AffiliationDetail;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsOrganizationalUnitDetailsVO;

public class AffiliationVOPresentation extends AffiliationVO
{

	List<AffiliationVOPresentation> children = null;
	AffiliationVOPresentation parent = null;
    private String namePath;
    private String idPath;
	
	public AffiliationVOPresentation(AffiliationVO affiliation)
	{
		super(affiliation);
		this.namePath = getDetails().getName();
		this.idPath = getReference().getObjectId();
		
	}

	public List<AffiliationVOPresentation> getChildren() throws Exception
	{
		if (children == null)
		{
			children = ((ItemControllerSessionBean)FacesContext
			        .getCurrentInstance()
			        .getExternalContext()
			        .getSessionMap()
			        .get("ItemControllerSessionBean"))
			        .retrieveChildAffiliations(this);
		}
		return children;
		
	}
	
	public MdsOrganizationalUnitDetailsVO getDetails()
	{
	    if (getMetadataSets().size() > 0 && getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO)
	    {
	        return (MdsOrganizationalUnitDetailsVO) getMetadataSets().get(0);
	    }
	    else
	    {
	        return new MdsOrganizationalUnitDetailsVO();
	    }
	}
	
	public boolean getMps()
	{
		return getDetails().getAlternativeNames().contains("MPS");
	}
	
	public boolean getTopLevel()
	{
		return (parent == null);
	}
	
	/**
	 * This returns a description of the affiliation in a html form
	 * @return html description 
	 */
	public String getHtmlDescription()
	{
		InternationalizationHelper i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
        .get(InternationalizationHelper.BEAN_NAME);
		ResourceBundle labelBundle = ResourceBundle.getBundle(i18nHelper.getSelectedLabelBundle());
			
		StringBuffer html = new StringBuffer();
		html.append("<html><head></head><body>");
		html.append("<div class=\"affDetails\"><h1>"+labelBundle.getString("AffiliationTree_txtHeadlineDetails")+"</h1>");	
		html.append("<div class=\"formField\">");
		if( getDetails().getDescriptions().size() > 0 && !"".equals(getDetails().getDescriptions().get(0)))
		{
			html.append("<div>");
			html.append(getDetails().getDescriptions().get(0));
			html.append("</div><br/>");
		}

		for (IdentifierVO identifier : getDetails().getIdentifiers())
        {
			html.append("<span>, &nbsp;");
			html.append(identifier.getId());
			html.append("</span>");
		}
		html.append("</div></div>");
		html.append("</body></html>");
		return html.toString();
	}

	public String startSearch()
	{
		((AffiliationBean) getSessionBean(AffiliationBean.class)).setSelectedAffiliation(this);
		((AffiliationDetail) getSessionBean(AffiliationDetail.class)).setAffiliationVO(this);
		return ((AffiliationBean) getSessionBean(AffiliationBean.class)).startSearch();
	}
	
	public AffiliationVOPresentation getParent() {
		return parent;
	}

	public void setParent(AffiliationVOPresentation parent) {
		this.parent = parent;
	}

    /**
     * Return any bean stored in session scope under the specified name.
     * @param cls The bean class.
     * @return the actual or new bean instance
     */
    public static synchronized Object getSessionBean(final Class<?> cls)
    {

        String name = null;

        try
        {
            name = (String) cls.getField("BEAN_NAME").get(new String());
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error getting bean name of " + cls, e);
        }
        Object result = FacesContext
                .getCurrentInstance()
                .getExternalContext()
                .getSessionMap()
                .get(name);
        if (result == null)
        {
            try
            {
                Object newBean = cls.newInstance();
                FacesContext
                        .getCurrentInstance()
                        .getExternalContext()
                        .getSessionMap()
                        .put(name, newBean);
                return newBean;
            }
            catch (Exception e)
            {
                throw new RuntimeException("Error creating new bean of type " + cls, e);
            }
        }
        else
        {
            return result;
        }
    }
    
    /**Returns the complete path to this affiliation as a string with the name of the affiliations*/
    public String getNamePath()
    { 
        return namePath;
    }

    public void setNamePath(String path)
    {
        this.namePath = path;
    }

    /**Returns the complete path to this affiliation as a string with the ids of the affiliations*/
    public String getIdPath()
    {
        return idPath;
    }

    public void setIdPath(String idPath)
    {
        this.idPath = idPath;
    }
   
    public String getName()
    {
        if (getMetadataSets().size() > 0 && getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO)
        {
            return ((MdsOrganizationalUnitDetailsVO) getMetadataSets().get(0)).getName();
        }
        else
        {
            return null;
        }
    }
    
    public List<String> getUris()
    {
        List<IdentifierVO> identifiers = getDefaultMetadata().getIdentifiers();
        List<String> uriList = new ArrayList<String>();
        
        for(IdentifierVO identifier : identifiers)
        {
            if (identifier.getType() != null && identifier.getType().equals(IdentifierVO.IdType.URI))
            {
                uriList.add(identifier.getId());
            }
        }
        return uriList;
    }
    
    
}
