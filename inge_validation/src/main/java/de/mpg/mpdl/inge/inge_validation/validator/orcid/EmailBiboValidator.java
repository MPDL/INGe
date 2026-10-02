package de.mpg.mpdl.inge.inge_validation.validator.orcid;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;
import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;

public class EmailBiboValidator extends ValidatorHandler<String> {

  @Override
  public boolean validate(ValidatorContext context, String email) {

    if (ValidationTools.isEmpty(email)) {
      context.addError(ValidationError.create(ErrorMessages.ORCID_EMAIL_BIBO_NOT_PROVIDED).setErrorCode(ErrorMessages.ERROR));

      return false;

    } else if (!email.matches(ValidationTools.EMAIL_REGEX)) {
      context.addError(ValidationError.create(ErrorMessages.ORCID_EMAIL_BIBO_INVALID).setErrorCode(ErrorMessages.ERROR));

      return false;
    }

    return true;
  }
}
