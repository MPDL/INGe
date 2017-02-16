package de.mpg.mpdl.inge.inge_validation.validator;

import java.util.List;
import java.util.regex.Pattern;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO.Storage;

/*
 * <!-- Locator has to be a valid URI --> <iso:pattern name="uri_as_locator" id="uri_as_locator">
 * <iso:rule context="escidocComponents:component"> <iso:assert
 * test="not(escidocComponents:content/@xlink:href != '') or not(escidocComponents:content/@storage
 * = 'external-url') or matches(escidocComponents:content/@xlink:href,
 * '^(https?|ftp)://([0-9a-zA-Z;/?:@&amp;=+$\.,\-_!~
 * *()%]+)?(#[0-9a-zA-Z;/?:@&amp;=+$\.,\-_!~*()%]+)?$')"> LocatorIsNoUri</iso:assert> </iso:rule>
 * </iso:pattern>
 */

/*
 * escidocComponents:component -> de.mpg.mpdl.inge.model.valueobjects.FileVO
 * escidocMetadataRecords:md-records -> de.mpg.mpdl.inge.model.valueobjects.MetadataSetVO ->
 * FileVO.metadataSets escidocMetadataRecords:md-records/escidocMetadataRecords:md-record ->
 * de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO -> FileVO.getDefaultMetaData()
 * escidocMetadataRecords:md-record.file:file/dc:format ->
 * de.mpg.mpdl.inge.model.valueobjects.metadata.FormatVO -> MdsFileVO.formats
 * escidocComponents:content/@storage -> FileVO.storage escidocComponents:content/@xlink:href ->
 * FileVO.content
 */

public class UriAsLocatorValidator extends ValidatorHandler<List<FileVO>> implements
    Validator<List<FileVO>> {

  private static final String URL_PATTERN = getUrlPattern();

  @Override
  public boolean validate(ValidatorContext context, List<FileVO> files) {

    boolean ok = true;

    if (files != null && files.isEmpty() == false) {

      int i = 1;
      for (FileVO fileVO : files) {

        if (fileVO.getContent() != null //
            && fileVO.getContent().trim().length() > 0 //
            && fileVO.getStorage().equals(Storage.EXTERNAL_URL) //
            && !Pattern.matches(URL_PATTERN, fileVO.getContent())) {
          context.addError(ValidationError.create(ErrorMessages.LOCATOR_IS_NO_URI).setField(
              "file[" + i + "]"));
          ok = false;
        }

        i++;
      } // for

    } // if

    return ok;
  }

  private static String getUrlPattern() {
    String SubDomain = "(?i:[a-z0-9]|[a-z0-9][-a-z0-9]*[a-z0-9])";
    String TopDomains = //
        "(?x-i:com\\b              \n" //
            + "     |edu\\b        \n" //
            + "     |biz\\b        \n" //
            + "     |in(?:t|fo)\\b \n" //
            + "     |mil\\b        \n" //
            + "     |net\\b        \n" //
            + "     |org\\b        \n" //
            + "     |[a-z][a-z]\\b \n" // Laendercodes
            + ")                   \n";
    String Hostname = "(?:" + SubDomain + "\\.)+" + TopDomains;

    String NOT_IN = ";\"'<>()\\[\\]{}\\s\\x7F-\\xFF";
    String NOT_END = "!.,?";
    String ANYWHERE = "[^" + NOT_IN + NOT_END + "]";
    String EMBEDDED = "[" + NOT_END + "]";
    String UrlPath = "/" + ANYWHERE + "*(" + EMBEDDED + "+" + ANYWHERE + "+)*";

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
