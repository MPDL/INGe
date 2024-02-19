package de.mpg.mpdl.inge.inge_validation.validator.OLDyearbook;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PublishingInfoVO;

public class PublisherAndPlaceRequiredValidator extends ValidatorHandler<PublishingInfoVO> implements Validator<PublishingInfoVO> {

  @Override
  public boolean validate(ValidatorContext context, PublishingInfoVO publishingInfoVO) {

    boolean ok = true;

    if (null != publishingInfoVO) {

      if (ValidationTools.isEmpty(publishingInfoVO.getPublisher())) {
        context.addError(ValidationError.create(ErrorMessages.PUBLISHER_NOT_PROVIDED).setErrorCode(ErrorMessages.WARNING));

        ok = false;

      } // if

      if (ValidationTools.isEmpty(publishingInfoVO.getPlace())) {
        context.addError(ValidationError.create(ErrorMessages.PUBLISHER_PLACE_NOT_PROVIDED).setErrorCode(ErrorMessages.WARNING));

        ok = false;

      } // if

    } else {
      context.addError(ValidationError.create(ErrorMessages.PUBLISHER_NOT_PROVIDED).setErrorCode(ErrorMessages.WARNING));
      context.addError(ValidationError.create(ErrorMessages.PUBLISHER_PLACE_NOT_PROVIDED).setErrorCode(ErrorMessages.WARNING));

      ok = false;

    } // if

    return ok;
  }

}
