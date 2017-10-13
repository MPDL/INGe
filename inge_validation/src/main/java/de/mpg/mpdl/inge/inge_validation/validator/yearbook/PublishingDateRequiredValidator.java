package de.mpg.mpdl.inge.inge_validation.validator.yearbook;

import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;

/*
 * <iso:pattern name="publishing_date_and_year" id="publishing_date_and_year"> <iso:rule
 * context="publication:publication[@type != 'http://purl.org/eprint/type/Thesis' and @type !=
 * 'http://purl.org/escidoc/metadata/ves/publication-types/journal' and @type !=
 * 'http://purl.org/escidoc/metadata/ves/publication-types/series']"> <iso:assert
 * test="dcterms:issued != '' or escidoc:published-online != ''">PublishingDateNotProvided</iso:
 * assert> </iso:rule> </iso:pattern>
 */

public class PublishingDateRequiredValidator extends ValidatorHandler<MdsPublicationVO> implements
    Validator<MdsPublicationVO> {

  @Override
  public boolean validate(ValidatorContext context, MdsPublicationVO m) {

    if (m != null) {

      if (m.getDatePublishedOnline() == null //
          && m.getDatePublishedInPrint() == null) {
        context.addErrorMsg(ErrorMessages.PUBLISHING_DATE_NOT_PROVIDED);

        return false;
      }

    }

    return true;
  }

}
