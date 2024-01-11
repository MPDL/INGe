package de.mpg.mpdl.inge.pubman.web.util.converter;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.model.xmltransforming.util.HtmlUtils;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;

public class HTMLSubSupBrConverter implements Converter {
  public static final String CONVERTER_ID = "HTMLSubSupBrConverter";

  public HTMLSubSupBrConverter() {}

  @Override
  public Object getAsObject(FacesContext arg0, UIComponent arg1, String text) {
    return null;
  }

  @Override
  public String getAsString(FacesContext arg0, UIComponent arg1, Object object) {
    final String snippet = (String) object;
    List<String> tags = new ArrayList<String>();
    tags.add("sup");
    tags.add("sub");
    tags.add("SUB");
    tags.add("SUP");
    tags.add("br");
    tags.add("BR");

    List<String> unbalanced = new ArrayList<String>();
    unbalanced.add("br");
    unbalanced.add("BR");

    return HtmlUtils.getShortenedHtmlSnippetWithBalancedTagsAndEscaping(snippet, snippet.length(), tags, unbalanced);
  }
}
