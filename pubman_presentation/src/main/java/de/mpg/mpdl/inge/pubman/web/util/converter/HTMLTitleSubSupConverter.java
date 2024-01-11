package de.mpg.mpdl.inge.pubman.web.util.converter;

import de.mpg.mpdl.inge.model.xmltransforming.util.HtmlUtils;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;

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
