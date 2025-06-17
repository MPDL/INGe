package de.mpg.mpdl.inge.cslmanager;

import de.undercouch.citeproc.csl.internal.RenderContext;
import de.undercouch.citeproc.csl.internal.TokenBuffer;
import de.undercouch.citeproc.csl.internal.format.HtmlFormat;
import de.undercouch.citeproc.csl.internal.token.DisplayGroupToken;
import de.undercouch.citeproc.csl.internal.token.Token;
import de.undercouch.citeproc.output.SecondFieldAlign;

import java.util.List;

/**
 * An extended HTML formatting class for CSL citations. This class extends the standard HtmlFormat
 * class to provide specific formatting functionality for PubMan.
 */
public class CiteProcPubManHTMLFormat extends HtmlFormat {

  /**
   * Formats a bibliography. Uses the same implementation as super class, except for adding the
   * <div class="csl-entry"></div> tag
   *
   * @param buffer The token buffer containing the tokens to be formatted
   * @param ctx The render context containing styling information
   * @param index The index of the current bibliography entry
   * @return The formatted bibliography entry as an HTML string
   */
  @Override
  protected String doFormatBibliographyEntry(TokenBuffer buffer, RenderContext ctx, int index) {
    SecondFieldAlign sfa = ctx.getStyle().getBibliography().getSecondFieldAlign();
    String result;
    if (sfa != SecondFieldAlign.FALSE && !buffer.getTokens().isEmpty()) {
      int i = 0;

      List<Token> tokens;
      for (tokens = buffer.getTokens(); i < tokens.size() && ((Token) tokens.get(i)).isFirstField(); ++i) {
      }

      TokenBuffer firstBuffer = buffer.copy(0, i);
      TokenBuffer restBuffer = buffer.copy(i, tokens.size());
      String var10000 = this.format(firstBuffer);
      result = "\n    <div class=\"csl-left-margin\">" + var10000 + "</div><div class=\"csl-right-inline\">" + this.format(restBuffer)
          + "</div>\n  ";
    } else {
      result = this.format(buffer);
    }

    return result;
  }

  /**
   * Escapes special characters in a string and then applies unescaping for certain HTML tags.
   *
   * @param str The string to be escaped
   * @return The escaped string with unescaped HTML tags
   */
  @Override
  protected String escape(String str) {
    String escaped = super.escape(str);
    return unescapeHtmlTag(escaped);
  }

  /**
   * Opens a display group for the specified type.
   *
   * @param type The type of display group
   * @return null as no specific formatting is required
   */
  @Override
  protected String openDisplayGroup(DisplayGroupToken.Type type) {
    return null;
  }

  /**
   * Closes a display group for the specified type.
   *
   * @param type The type of display group
   * @return null as no specific formatting is required
   */
  @Override
  protected String closeDisplayGroup(DisplayGroupToken.Type type) {
    return null;
  }

  /**
   * Removes escaping from specific HTML tags (i, b, sub, sup). This method ensures that these
   * specific HTML tags are interpreted as actual tags in the text rather than escaped characters.
   *
   * @param citation The text where tags should be unescaped
   * @return The text with unescaped HTML tags
   */
  private static String unescapeHtmlTag(String citation) {
    String res = citation.replaceAll("&lt;(/?)(i|b|sub|sup)&gt;", "<$1$2>");
    return res;
  }
}
