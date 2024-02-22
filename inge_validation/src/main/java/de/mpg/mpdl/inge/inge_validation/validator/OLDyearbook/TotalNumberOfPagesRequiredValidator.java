package de.mpg.mpdl.inge.inge_validation.validator.OLDyearbook;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;

public class TotalNumberOfPagesRequiredValidator extends ValidatorHandler<String> {

  @Override
  public boolean validate(ValidatorContext context, String totalNumberOfPages) {

    boolean ok = true;

    if (ValidationTools.isEmpty(totalNumberOfPages)) {

      context.addError(ValidationError.create(ErrorMessages.TOTAL_NUMBER_OF_PAGES_NOT_PROVIDED).setErrorCode(ErrorMessages.WARNING));

      ok = false;

    } // if

    return ok;
  }

}
