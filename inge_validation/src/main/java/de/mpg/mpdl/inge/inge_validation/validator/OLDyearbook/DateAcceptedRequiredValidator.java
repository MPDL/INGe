package de.mpg.mpdl.inge.inge_validation.validator.OLDyearbook;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;

public class DateAcceptedRequiredValidator extends ValidatorHandler<MdsPublicationVO> {

  @Override
  public boolean validate(ValidatorContext context, MdsPublicationVO m) {

    if (ValidationTools.isEmpty(m.getDateAccepted())) {
      context.addError(ValidationError.create(ErrorMessages.DATE_ACCEPTED_NOT_PROVIDED).setErrorCode(ErrorMessages.ERROR));

      return false;

    } // if

    return true;
  }

}
