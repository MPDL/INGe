package de.mpg.mpdl.inge.cslmanager;

import de.undercouch.citeproc.csl.internal.RenderContext;
import de.undercouch.citeproc.csl.internal.TokenBuffer;
import de.undercouch.citeproc.csl.internal.format.HtmlFormat;
import de.undercouch.citeproc.csl.internal.token.DisplayGroupToken;
import de.undercouch.citeproc.csl.internal.token.Token;
import de.undercouch.citeproc.output.SecondFieldAlign;

import java.util.List;

public class CiteProcPubManHTMLFormat extends HtmlFormat {

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

  @Override
  protected String escape(String str) {
    String escaped = super.escape(str);
    return unescapeHtmlTag(escaped);
  }

  @Override
  protected String openDisplayGroup(DisplayGroupToken.Type type) {
    return null;
  }

  @Override
  protected String closeDisplayGroup(DisplayGroupToken.Type type) {
    return null;
  }


  /**
   * Unescapes all i, b, sub, sup tags in a string
   * 
   * @param citation
   * @return
   */
  private static String unescapeHtmlTag(String citation) {
    String res = citation.replaceAll("&lt;(/?)(i|b|sub|sup|)&gt;", "<$1$2>");
    return res;
  }



}
