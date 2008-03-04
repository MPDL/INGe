package de.mpg.escidoc.pubman.util;

import java.util.List;

import javax.faces.context.FacesContext;

import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.affiliation.AffiliationBean;
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
	
	public String getImage()
	{
		if ("MPS".equals(getAbbreviation()))
		{
			return "minerva.png";
		}
		else if (parent == null)
		{
			return "affiliations_tree.gif";
		}
		else
		{
			return "documents.gif";
		}
	}

	public String startSearch()
	{
		((AffiliationBean) getSessionBean(AffiliationBean.class)).setSelectedAffiliation(this);
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
