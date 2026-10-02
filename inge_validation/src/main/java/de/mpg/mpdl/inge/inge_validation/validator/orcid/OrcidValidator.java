package de.mpg.mpdl.inge.inge_validation.validator.orcid;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;
import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;

public class OrcidValidator extends ValidatorHandler<String> {

  @Override
  public boolean validate(ValidatorContext context, String orcid) {

    if (ValidationTools.isEmpty(orcid)) {
      context.addError(ValidationError.create(ErrorMessages.ORCID_AUTHOR_NOT_PROVIDED).setErrorCode(ErrorMessages.ERROR));

      return false;

    } else if (!orcid.matches(ValidationTools.ORCID_REGEX)) {
      context.addError(ValidationError.create(ErrorMessages.ORCID_INVALID).setErrorCode(ErrorMessages.ERROR));

      return false;
    }

    return true;
  }
}
