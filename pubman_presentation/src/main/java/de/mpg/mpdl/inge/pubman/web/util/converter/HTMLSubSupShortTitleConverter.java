package de.mpg.mpdl.inge.pubman.web.util.converter;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.model.xmltransforming.util.HtmlUtils;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;

/**
 * This converter shortens a string, escapes all html tags except sup/sub and balances the shortened
 * string
 *
 * @author haarlae1
 *
 */
public class HTMLSubSupShortTitleConverter implements Converter {
  private static final int LENGTH = 80;

  public HTMLSubSupShortTitleConverter() {}

  @Override
  public Object getAsObject(FacesContext arg0, UIComponent arg1, String text) {
    return null;
  }

  @Override
  public String getAsString(FacesContext arg0, UIComponent arg1, Object object) {
    String snippet = (String) object;
    List<String> tags = new ArrayList<>();
    tags.add("sup");
    tags.add("sub");
    tags.add("SUB");
    tags.add("SUP");

    if (HTMLSubSupShortTitleConverter.LENGTH < snippet.length()) {
      return HtmlUtils.getShortenedHtmlSnippetWithBalancedTagsAndEscaping(snippet, HTMLSubSupShortTitleConverter.LENGTH, tags) + "...";
    } else {
      return HtmlUtils.getShortenedHtmlSnippetWithBalancedTagsAndEscaping(snippet, snippet.length(), tags);
    }
  }
}
