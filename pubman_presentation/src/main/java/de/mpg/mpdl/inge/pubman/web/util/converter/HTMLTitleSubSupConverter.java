package de.mpg.mpdl.inge.pubman.web.util.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import de.mpg.mpdl.inge.model.xmltransforming.util.HtmlUtils;

/**
 * Removes all sub and sup tags from a string, used for browser title
 * 
 */
public class HTMLTitleSubSupConverter implements Converter {
  public static final String CONVERTER_ID = "HTMLTitleSubSupConverter";

  public HTMLTitleSubSupConverter() {}

  @Override
  public Object getAsObject(FacesContext arg0, UIComponent arg1, String text) {
    return null;
  }

  @Override
  public String getAsString(FacesContext arg0, UIComponent arg1, Object object) {
    final String snippet = (String) object;
    return HtmlUtils.removeSubSupIfBalanced(snippet);
  }
}
