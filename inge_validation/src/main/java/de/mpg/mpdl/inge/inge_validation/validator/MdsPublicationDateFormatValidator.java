package de.mpg.mpdl.inge.inge_validation.validator;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;

/*
 * <!-- Entered dates have to be in the format YYYY, YYYY-MM or YYYY-MM-DD --> <iso:pattern
 * name="correct_date_format" id="correct_date_format"> <!-- Publication dates --> <iso:rule
 * context=
 * "dcterms:created|dcterms:modified|dcterms:issued|dcterms:dateAccepted|dcterms:dateSubmitted|escidoc:published-online"
 * > <iso:assert test=". = '' or (matches(., '^\d\d\d\d(-\d\d){0,2}$') and substring(concat(.,
 * '-01-01'), 1, 10) castable as xs:date)"> DateFormatIncorrect</iso:assert> </iso:rule>
 *
 * <!-- Event dates --> <iso:rule context="escidoc:start-date"> <iso:assert test=". = '' or
 * (matches(., '^\d\d\d\d(-\d\d){0,2}$') and substring(concat(., '-01-01'), 1, 10) castable as
 * xs:date)"> DateFormatIncorrect</iso:assert> </iso:rule>
 *
 * <iso:rule context="escidoc:end-date"> <iso:assert test=". = '' or (matches(.,
 * '^\d\d\d\d(-\d\d){0,2}$') and substring(concat(., '-01-01'), 1, 10) castable as xs:date)">
 * DateFormatIncorrect</iso:assert>
 *
 * <iso:assert test=". = '' or ../escidoc:start-date != ''"> EndDateWithoutStartDate</iso:assert>
 * </iso:rule>
 */
public class MdsPublicationDateFormatValidator extends ValidatorHandler<MdsPublicationVO> implements Validator<MdsPublicationVO> {

  @Override
  public boolean validate(ValidatorContext context, MdsPublicationVO m) {

    boolean ok = true;

    if (!ValidationTools.checkDate(m.getDateAccepted())) {
      context.addError(
          ValidationError.create(ErrorMessages.DATE_FORMAT_INCORRECT).setField("dateAccepted").setInvalidValue(m.getDateAccepted()));
      ok = false;
    }

    if (!ValidationTools.checkDate(m.getDateCreated())) {
      context.addError(
          ValidationError.create(ErrorMessages.DATE_FORMAT_INCORRECT).setField("dateCreated").setInvalidValue(m.getDateCreated()));
      ok = false;
    }

    if (!ValidationTools.checkDate(m.getDateModified())) {
      context.addError(
          ValidationError.create(ErrorMessages.DATE_FORMAT_INCORRECT).setField("dateModified").setInvalidValue(m.getDateModified()));
      ok = false;
    }

    if (!ValidationTools.checkDate(m.getDatePublishedInPrint())) {
      context.addError(ValidationError.create(ErrorMessages.DATE_FORMAT_INCORRECT).setField("datePublishedInPrint")
          .setInvalidValue(m.getDatePublishedInPrint()));
      ok = false;
    }

    if (!ValidationTools.checkDate(m.getDatePublishedOnline())) {
      context.addError(ValidationError.create(ErrorMessages.DATE_FORMAT_INCORRECT).setField("datePublishedOnline")
          .setInvalidValue(m.getDatePublishedOnline()));
      ok = false;
    }

    if (!ValidationTools.checkDate(m.getDateSubmitted())) {
      context.addError(
          ValidationError.create(ErrorMessages.DATE_FORMAT_INCORRECT).setField("dateSubmitted").setInvalidValue(m.getDateSubmitted()));
      ok = false;
    }

    if (null != m.getLegalCase()) {

      if (!ValidationTools.checkDate(m.getLegalCase().getDatePublished())) {
        context.addError(ValidationError.create(ErrorMessages.DATE_FORMAT_INCORRECT).setField("datePublished")
            .setInvalidValue(m.getLegalCase().getDatePublished()));
        ok = false;
      }

    } // if

    if (null != m.getEvent()) {

      String startDate = m.getEvent().getStartDate();
      String endDate = m.getEvent().getEndDate();

      if (!ValidationTools.checkDate(startDate)) {
        context.addError(ValidationError.create(ErrorMessages.DATE_FORMAT_INCORRECT).setField("startDate").setInvalidValue(startDate));
        ok = false;
      }

      if (!ValidationTools.checkDate(endDate)) {
        context.addError(ValidationError.create(ErrorMessages.DATE_FORMAT_INCORRECT).setField("endDate").setInvalidValue(endDate));
        ok = false;
      }

      if (null != endDate && null == startDate) {
        context.addErrorMsg(ErrorMessages.END_DATE_WITHOUT_START_DATE);
        ok = false;
      }

    } // if

    return ok;
  }

}
