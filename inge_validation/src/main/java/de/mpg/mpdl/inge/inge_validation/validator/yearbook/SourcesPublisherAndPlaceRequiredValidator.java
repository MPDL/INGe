package de.mpg.mpdl.inge.inge_validation.validator.yearbook;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PublishingInfoVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;

public class SourcesPublisherAndPlaceRequiredValidator extends ValidatorHandler<List<SourceVO>> implements Validator<List<SourceVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<SourceVO> sources) {

    boolean ok = true;

    if (ValidationTools.isNotEmpty(sources)) {

      int i = 1;
      for (final SourceVO sourceVO : sources) {

        if (sourceVO != null) {

          PublishingInfoVO p = sourceVO.getPublishingInfo();

          if (p == null || ValidationTools.isEmpty(p.getPublisher())) {

            context.addError(ValidationError.create(ErrorMessages.PUBLISHER_NOT_PROVIDED).setField("source[" + i + "]"));

            ok = false;

          } // if

          if (p != null && ValidationTools.isEmpty(p.getPlace())) {

            context.addError(ValidationError.create(ErrorMessages.PUBLISHER_PLACE_NOT_PROVIDED).setField("source[" + i + "]"));

            ok = false;

          } // if

        } // if

        i++;
      } // for

    } // if

    return ok;
  }

}
