package de.mpg.mpdl.inge.inge_validation.validator.orcid;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;
import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;

public class NameAuthorValidator extends ValidatorHandler<String> {

  @Override
  public boolean validate(ValidatorContext context, String nameAuthor) {

    if (ValidationTools.isEmpty(nameAuthor)) {
      context.addError(ValidationError.create(ErrorMessages.ORCID_NAME_AUTHOR_NOT_PROVIDED).setErrorCode(ErrorMessages.ERROR));

      return false;

    } // if

    return true;
  }

}
