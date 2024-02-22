package de.mpg.mpdl.inge.inge_validation.validator.OLDyearbook;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PublishingInfoVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;

public class SourcesPublisherEditionRequiredValidator extends ValidatorHandler<List<SourceVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<SourceVO> sources) {

    boolean ok = true;

    if (ValidationTools.isNotEmpty(sources)) {

      int i = 1;
      for (SourceVO sourceVO : sources) {

        if (null != sourceVO) {

          PublishingInfoVO p = sourceVO.getPublishingInfo();

          if (null == p || ValidationTools.isEmpty(p.getEdition())) {
            context.addError(ValidationError.create(ErrorMessages.SOURCE_EDITION_NOT_PROVIDED).setField("source[" + i + "]")
                .setErrorCode(ErrorMessages.WARNING));

            ok = false;

          } // if

        } // if

        i++;

      } // for

    } else {
      context.addError(ValidationError.create(ErrorMessages.SOURCE_EDITION_NOT_PROVIDED).setErrorCode(ErrorMessages.WARNING));

      ok = false;

    } // if

    return ok;
  }

}
