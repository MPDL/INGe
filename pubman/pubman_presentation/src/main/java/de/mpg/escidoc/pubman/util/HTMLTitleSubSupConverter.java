package de.mpg.escidoc.pubman.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * Removes all sub and sup tags from a string, used for browser title
 *
 */
public class HTMLTitleSubSupConverter implements Converter{
    public static final String CONVERTER_ID = "HTMLTitleSubSupConverter";
	public HTMLTitleSubSupConverter()
	{
	}

	public Object getAsObject(FacesContext arg0, UIComponent arg1, String text) 
	{
		return null;
	}

	public String getAsString(FacesContext arg0, UIComponent arg1, Object object) 
	{
        String snippet = (String) object;
		return CommonUtils.removeSubSupIfBalanced(snippet);
	}

}

