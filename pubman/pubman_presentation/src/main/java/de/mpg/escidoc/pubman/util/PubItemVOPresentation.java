package de.mpg.escidoc.pubman.util;

import javax.faces.event.ValueChangeEvent;

import de.mpg.escidoc.services.common.valueobjects.PubItemVO;

/**
 * Wrapper class for items to be used in the presentation.
 * @author franke
 * @author $Author: $
 * @version: $Revision: 1641 $ $LastChangedDate: 2007-12-04 16:52:04 +0100 (Di, 04 Dez 2007)$
 */
public class PubItemVOPresentation extends PubItemVO {

	private boolean selected = false;
	private boolean shortView = true;

	public PubItemVOPresentation(PubItemVO item)
	{
		super(item);
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
}
