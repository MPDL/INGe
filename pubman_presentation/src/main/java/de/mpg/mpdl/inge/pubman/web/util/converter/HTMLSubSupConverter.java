package de.mpg.mpdl.inge.pubman.web.util.converter;

import java.util.ArrayList;
import java.util.List;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;

import de.mpg.mpdl.inge.model.xmltransforming.util.HtmlUtils;

public class HTMLSubSupConverter implements Converter {
  public static final String CONVERTER_ID = "HTMLSubSupConverter";

  public HTMLSubSupConverter() {}

  @Override
  public Object getAsObject(FacesContext arg0, UIComponent arg1, String text) {
    return null;
  }

  @Override
  public String getAsString(FacesContext arg0, UIComponent arg1, Object object) {
    final String snippet = (String) object;
    final List<String> tags = new ArrayList<String>();
    tags.add("sup");
    tags.add("sub");
    tags.add("SUB");
    tags.add("SUP");

    return HtmlUtils.getShortenedHtmlSnippetWithBalancedTagsAndEscaping(snippet, snippet.length(), tags);
  }
}
