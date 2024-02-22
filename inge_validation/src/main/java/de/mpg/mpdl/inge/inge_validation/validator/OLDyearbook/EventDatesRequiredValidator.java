package de.mpg.mpdl.inge.inge_validation.validator.OLDyearbook;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.metadata.EventVO;

public class EventDatesRequiredValidator extends ValidatorHandler<EventVO> {

  @Override
  public boolean validate(ValidatorContext context, EventVO e) {

    boolean ok = true;

    if (null != e) {

      if (ValidationTools.isEmpty(e.getStartDate())) {
        context.addError(ValidationError.create(ErrorMessages.EVENT_START_DATE_REQUIRED).setErrorCode(ErrorMessages.WARNING));

        ok = false;

      } // if

      if (ValidationTools.isEmpty(e.getEndDate())) {
        context.addError(ValidationError.create(ErrorMessages.EVENT_END_DATE_REQUIRED).setErrorCode(ErrorMessages.WARNING));

        ok = false;

      } // if

    } else {
      context.addError(ValidationError.create(ErrorMessages.EVENT_START_DATE_REQUIRED).setErrorCode(ErrorMessages.WARNING));
      context.addError(ValidationError.create(ErrorMessages.EVENT_END_DATE_REQUIRED).setErrorCode(ErrorMessages.WARNING));

      ok = false;

    } // if

    return ok;
  }
}
