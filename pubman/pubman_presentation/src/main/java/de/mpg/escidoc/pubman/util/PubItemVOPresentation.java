package de.mpg.escidoc.pubman.util;

import java.util.ResourceBundle;

import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import de.mpg.escidoc.pubman.appbase.Internationalized;
import de.mpg.escidoc.services.common.valueobjects.PubItemResultVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.SearchHitVO;

/**
 * Wrapper class for items to be used in the presentation.
 * @author franke
 * @author $Author: $
 * @version: $Revision: 1641 $ $LastChangedDate: 2007-12-04 16:52:04 +0100 (Di, 04 Dez 2007)$
 */
public class PubItemVOPresentation extends PubItemVO implements Internationalized {

	private boolean selected = false;
	private boolean shortView = true;
    
    /**
     * List of hits. Every hit in files contains the file reference and the text fragments of the search hit.
     */
    private java.util.List<SearchHitVO> searchHitList = new java.util.ArrayList<SearchHitVO>();
    
    //For handling the resource bundles (i18n)
    protected Application application = FacesContext.getCurrentInstance().getApplication();
    //get the selected language...
    protected InternationalizationHelper i18nHelper = (InternationalizationHelper)FacesContext
	    .getCurrentInstance()
	    .getExternalContext()
	    .getApplicationMap()
	    .get("InternationalizationHelper.BEAN_NAME");

	public PubItemVOPresentation(PubItemVO item)
	{
		super(item);
		if (item instanceof PubItemResultVO)
		{
			this.searchHitList = ((PubItemResultVO)item).getSearchHitList();
		}
	}
	
	public boolean getSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean getShortView() {
		return shortView;
	}

	public void setShortView(boolean shortView) {
		this.shortView = shortView;
	}

	public boolean getMediumView() {
		return !shortView;
	}

	public void setMediumView(boolean mediumView) {
		this.shortView = !mediumView;
	}

    /**
     * Distinguish between Persons and organization as creators and returns them formatted as string.
     * @return String the  formatted creators
     */
    public String getCreators()
    {
        StringBuffer creators = new StringBuffer();

        if (getMetadata().getCreators() != null)
        {
            for (int i = 0; i < getMetadata().getCreators().size(); i++)
            {
                if (getMetadata().getCreators().get(i).getPerson() != null)
                {
                    if (getMetadata().getCreators().get(i).getPerson().getFamilyName() != null)
                    {
                        creators.append(getMetadata().getCreators().get(i).getPerson().getFamilyName());
                    }
                    if (getMetadata()
                            .getCreators()
                            .get(i)
                            .getPerson()
                            .getFamilyName() != null
                        && getMetadata()
                            .getCreators()
                            .get(i)
                            .getPerson()
                            .getGivenName() != null)
                    {
                        creators.append(", ");
                    }
                    if (getMetadata().getCreators().get(i).getPerson().getGivenName() != null)
                    {
                        creators.append(getMetadata().getCreators().get(i).getPerson().getGivenName());
                    }
                }
                else if (getMetadata().getCreators().get(i).getOrganization() != null)
                {
                    if (getMetadata().getCreators().get(i).getOrganization().getName().getValue() != null)
                    {
                        creators.append(
                                getMetadata().getCreators().get(i).getOrganization().getName().getValue());
                    }
                }
                if (i < getMetadata().getCreators().size() - 1)
                {
                    creators.append("; ");
                }
            }
        }
        return creators.toString();
    }
    
    /**
     * Returns the newest date of the metadata date section.
     * @return the latest date
     */
    public String getLatestDate()
    {
        if (getMetadata().getDatePublishedInPrint() != null && !"".equals(getMetadata().getDatePublishedInPrint()))
        {
        	return getLabel("ViewItem_lblDatePublishedInPrint") + getMetadata().getDatePublishedInPrint();
        }
        else if (getMetadata().getDatePublishedOnline() != null && !"".equals(getMetadata().getDatePublishedOnline()))
        {
        	return getLabel("ViewItem_lblDatePublishedOnline") + getMetadata().getDatePublishedOnline();
        }
        else if (getMetadata().getDateAccepted() != null && !"".equals(getMetadata().getDateAccepted()))
        {
        	return getLabel("ViewItem_lblDateAccepted") + getMetadata().getDateAccepted();
        }
        else if (getMetadata().getDateSubmitted() != null && !"".equals(getMetadata().getDateSubmitted()))
        {
        	return getLabel("ViewItem_lblDateSubmitted") + getMetadata().getDateSubmitted();
        }
        else if (getMetadata().getDateModified() != null && !"".equals(getMetadata().getDateModified()))
        {
        	return getLabel("ViewItem_lblDateModified") + getMetadata().getDateModified();
        }
        else if (getMetadata().getDateCreated() != null && !"".equals(getMetadata().getDateCreated()))
        {
        	return getLabel("ViewItem_lblDateCreated") + getMetadata().getDateCreated();
        }
        else
        {
        	return null;
        }
    }
    
    /*
     * (non-Javadoc)
     * @see de.mpg.escidoc.pubman.appbase.Internationalized#getLabel(java.lang.String)
     */
    public String getLabel(String placeholder)
    {
        return ResourceBundle.getBundle(i18nHelper.getSelectedLabelBundle()).getString(placeholder);
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.escidoc.pubman.appbase.Internationalized#getMessage(java.lang.String)
     */
    public String getMessage(String placeholder)
    {
        return ResourceBundle.getBundle(i18nHelper.getSelectedMessagesBundle()).getString(placeholder);
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.escidoc.pubman.appbase.Internationalized#bindComponentLabel(javax.faces.component.UIComponent, java.lang.String)
     */
    public void bindComponentLabel(UIComponent component, String placeholder)
    {
        ValueExpression value = FacesContext
            .getCurrentInstance()
            .getApplication()
            .getExpressionFactory()
            .createValueExpression(FacesContext.getCurrentInstance().getELContext(), "#{lbl." + placeholder + "}", String.class);
        component.setValueExpression("value", value); 
    }

	public void switchToMediumView()
    {
    	shortView = false;
    }
    
    public void switchToShortView()
    {
    	shortView = true;
    }
    
    public void select(ValueChangeEvent event)
    {
    	selected = ((Boolean)event.getNewValue()).booleanValue();
    }

	public java.util.List<SearchHitVO> getSearchHitList() {
		return searchHitList;
	}
    
}
