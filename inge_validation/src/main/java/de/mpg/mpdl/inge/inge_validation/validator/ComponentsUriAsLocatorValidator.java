package de.mpg.mpdl.inge.inge_validation.validator;

import java.util.List;
import java.util.regex.Pattern;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;

/*
 * <!-- Locator has to be a valid URI --> <iso:pattern name="uri_as_locator" id="uri_as_locator">
 * <iso:rule context="escidocComponents:component"> <iso:assert
 * test="not(escidocComponents:content/@xlink:href != '') or not(escidocComponents:content/@storage
 * = 'external-url') or matches(escidocComponents:content/@xlink:href,
 * '^(https?|ftp)://([0-9a-zA-Z;/?:@&amp;=+$\.,\-_!~()%]+)?(#[0-9a-zA-Z;/?:@&amp;=+$\.,\-_!~*()%]+)?
 * $')"> LocatorIsNoUri </iso:assert> </iso:rule> </iso:pattern>
 */

/*
 * escidocComponents:component -> de.mpg.mpdl.inge.model.valueobjects.FileVO
 * escidocComponents:content/@storage -> FileVO.storage escidocComponents:content/@xlink:href ->
 * FileVO.content
 */

public class ComponentsUriAsLocatorValidator extends ValidatorHandler<List<FileDbVO>> {

  public static final String URL_PATTERN = ComponentsUriAsLocatorValidator.getUrlPattern();

  @Override
  public boolean validate(ValidatorContext context, List<FileDbVO> files) {

    boolean ok = true;

    if (ValidationTools.isNotEmpty(files)) {

      int i = 1;
      for (FileDbVO fileDbVO : files) {

        if (null != fileDbVO) {
          if (ValidationTools.isNotEmpty(fileDbVO.getContent()) //
              && fileDbVO.getStorage().equals(FileDbVO.Storage.EXTERNAL_URL)) {
            if (!(fileDbVO.getContent().startsWith("http://") || fileDbVO.getContent().startsWith("https://")
                || fileDbVO.getContent().startsWith("ftp://"))) {
              context.addError(ValidationError.create(ErrorMessages.LOCATOR_IS_NO_URI).setField("file[" + i + "]"));
              ok = false;
            } else if (!Pattern.matches(ComponentsUriAsLocatorValidator.URL_PATTERN, fileDbVO.getContent())) {
              context.addError(ValidationError.create(ErrorMessages.LOCATOR_IS_NO_URI).setField("file[" + i + "]"));
              ok = false;
            }
          }
        }

        i++;
      } // for

    } // if

    return ok;
  }

  // (?x:\b((?:ftp|https?)://[-\w]+(\.\w[-\w]*)+|(?:(?i:[a-z0-9]|[a-z0-9][-a-z0-9]*[a-z0-9])\.)+(?x-i:com\b|edu\b|biz\b|in(?:t|fo)\b|mil\b|net\b|org\b|[a-z][a-z]\b))(?::\d+)?(?:/[^;\"'<>()\[\]{}\s\x7F-\xFF!.,?]*([!.,?]+[^;\"'<>()\[\]{}\s\x7F-\xFF!.,?]+)*
  // )?)
  // bzw. -> ohne ()
  // (?x:\b((?:ftp|https?)://[-\w]+(\.\w[-\w]*)+|(?:(?i:[a-z0-9]|[a-z0-9][-a-z0-9]*[a-z0-9])\.)+(?x-i:com\b|edu\b|biz\b|in(?:t|fo)\b|mil\b|net\b|org\b|[a-z][a-z]\b))(?::\d+)?(?:/[^;\"'<>\[\]{}\s\x7F-\xFF!.,?]*([!.,?]+[^;\"'<>\[\]{}\s\x7F-\xFF!.,?]+)*
  // )?)
  private static String getUrlPattern() {
    final String SubDomain = "(?i:[a-z0-9]|[a-z0-9][-a-z0-9]*[a-z0-9])";
    final String TopDomains = //
        "(?x-i:com\\b              \n" //
            + "     |edu\\b        \n" //
            + "     |biz\\b        \n" //
            + "     |in(?:t|fo)\\b \n" //
            + "     |mil\\b        \n" //
            + "     |net\\b        \n" //
            + "     |org\\b        \n" //
            + "     |[a-z][a-z]\\b \n" // Laendercodes
            + ")                   \n";
    final String Hostname = "(?:" + SubDomain + "\\.)+" + TopDomains;

    //    final String NOT_IN = ";\"'<>()\\[\\]{}\\s\\x7F-\\xFF";
    final String NOT_IN = ";\"'<>\\[\\]{}\\s\\x7F-\\xFF";
    final String NOT_END = "!.,?";
    final String ANYWHERE = "[^" + NOT_IN + NOT_END + "]";
    final String EMBEDDED = "[" + NOT_END + "]";
    final String UrlPath = "/" + ANYWHERE + "*(" + EMBEDDED + "+" + ANYWHERE + "+)*";

    return "(?x:                                               \n" //
        + "  \\b                                               \n" //
        + "  ## Hostname-Teil erkennen                         \n" //
        + "  (                                                 \n" //
        + "    (?: ftp | http s? ): // [-\\w]+(\\.\\w[-\\w]*)+ \n" //
        + "   |                                                \n" //
        + "    " + Hostname + "                                \n" //
        + "  )                                                 \n" //
        + "  # Optionale Portnummer zulassen                   \n" //
        + "  (?:  :\\d+ )?                                     \n" //
        + "                                                    \n" //
        + "  # Rest der URL ist optional und beginnt mit /     \n" //
        + " (?: " + UrlPath + ")?                              \n" + ")";
  }

}
