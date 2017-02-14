package de.mpg.mpdl.inge.inge_validation.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
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
 * 
 * <iso:rule context="dcterms:available"> <iso:assert test=". = '' or (matches(.,
 * '^\d\d\d\d(-\d\d){0,2}$') and substring(concat(., '-01-01'), 1, 10) castable as xs:date)">
 * DateFormatIncorrect</iso:assert> </iso:rule>
 * 
 * <iso:rule context="dcterms:dateCopyrighted"> <iso:assert test=". = '' or (matches(.,
 * '^\d\d\d\d(-\d\d){0,2}$') and substring(concat(., '-01-01'), 1, 10) castable as xs:date)">
 * DateFormatIncorrect</iso:assert> </iso:rule> </iso:pattern>
 */

// TODO: dcterms:available + dcterms:dateCopyrighted
public class MdsPublicationDateFormatValidator extends ValidatorHandler<MdsPublicationVO> implements
    Validator<MdsPublicationVO> {

  public static final SimpleDateFormat SHORT = new SimpleDateFormat("yyyy");
  public static final SimpleDateFormat MEDIUM = new SimpleDateFormat("yyyy-MM");
  public static final SimpleDateFormat LONG = new SimpleDateFormat("yyyy-MM-dd");

  @Override
  public boolean validate(ValidatorContext context, MdsPublicationVO m) {

    boolean ok = true;

    if (m != null) {

      if (!checkDate(m.getDateAccepted())) {
        context.addError(ValidationError.create(ErrorMessages.DATE_FORMAT_INCORRECT).setField(
            "dateAccepted"));
        ok = false;
      }

      if (!checkDate(m.getDateCreated())) {
        context.addError(ValidationError.create(ErrorMessages.DATE_FORMAT_INCORRECT).setField(
            "dateCreated"));
        ok = false;
      }

      if (!checkDate(m.getDateModified())) {
        context.addError(ValidationError.create(ErrorMessages.DATE_FORMAT_INCORRECT).setField(
            "dateModified"));
        ok = false;
      }

      if (!checkDate(m.getDatePublishedInPrint())) {
        context.addError(ValidationError.create(ErrorMessages.DATE_FORMAT_INCORRECT).setField(
            "datePublishedInPrint"));
        ok = false;
      }

      if (!checkDate(m.getDatePublishedOnline())) {
        context.addError(ValidationError.create(ErrorMessages.DATE_FORMAT_INCORRECT).setField(
            "datePublishedOnline"));
        ok = false;
      }

      if (!checkDate(m.getDateSubmitted())) {
        context.addError(ValidationError.create(ErrorMessages.DATE_FORMAT_INCORRECT).setField(
            "dateSubmitted"));
        ok = false;
      }

      if (m.getEvent() != null) {

        String startDate = m.getEvent().getStartDate();
        String endDate = m.getEvent().getStartDate();

        if (!checkDate(startDate)) {
          context.addError(ValidationError.create(ErrorMessages.DATE_FORMAT_INCORRECT).setField(
              "startDate"));
          ok = false;
        }

        if (!checkDate(endDate)) {
          context.addError(ValidationError.create(ErrorMessages.DATE_FORMAT_INCORRECT).setField(
              "endDate"));
          ok = false;
        }

        if (endDate != null && startDate == null) {
          context.addErrorMsg(ErrorMessages.END_DATE_WITHOUT_START_DATE);
          ok = false;
        }

      } // if

    } // if

    return ok;
  }

  private boolean checkDate(String s) {

    if (s != null && s.trim().length() > 0) {
      try {
        SHORT.parse(s);
      } catch (ParseException e) {
        try {
          MEDIUM.parse(s);
        } catch (ParseException e1) {
          try {
            LONG.parse(s);
          } catch (ParseException e2) {
            return false;
          }
        }
      }
    }

    return true;
  }

}
