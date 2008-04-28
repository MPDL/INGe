package de.mpg.escidoc.pubman.util;

import java.util.List;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.affiliation.AffiliationBean;
import de.mpg.escidoc.pubman.search.AffiliationDetail;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;

public class AffiliationVOPresentation extends AffiliationVO
{

	List<AffiliationVOPresentation> children = null;
	AffiliationVOPresentation parent = null;
	
	public AffiliationVOPresentation(AffiliationVO affiliation)
	{
		super(affiliation);
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
	
	public boolean getMps()
	{
		return ("MPS".equals(getAbbreviation()));
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
		html.append("<div class=\"affDetails\"><h1>"+labelBundle.getString("AffiliationTree_txtHeadlineDetails")+"</h1>");	
		html.append("<div class=\"formField\">");
		if( this.getDescription() != "" ) {
			html.append("<div>");
			html.append(this.getDescription());
			html.append("</div><br/>");
		}
		if( this.getAddress() != "" ) {
			html.append("<span>");
			html.append(this.getAddress());
			html.append("</span><br/><br/>");
		}
		if( this.getTelephone() != "" ) {
			html.append("<span>"+labelBundle.getString("AffiliationTree_txtPhone")+": ");
			html.append(this.getTelephone());
			html.append("</span>");
		}
		if( this.getFax() != "" ) {
			html.append("<span>, &nbsp;"+labelBundle.getString("AffiliationTree_txtFax")+": ");
			html.append(this.getFax());
			html.append("</span>");
		}
		if( this.getEmail() != "" ) {
			html.append("<span>, &nbsp;"+labelBundle.getString("AffiliationTree_txtEmail")+": ");
			html.append(this.getEmail());
			html.append("</span>");
		}
		if( this.getHomepageUrl() != null && this.getHomepageUrl().toString() != "" ) {
			html.append("<span>, &nbsp;");
			html.append(this.getHomepageUrl());
			html.append("</span>");
		}
		html.append("</div></div>");
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
    
}
