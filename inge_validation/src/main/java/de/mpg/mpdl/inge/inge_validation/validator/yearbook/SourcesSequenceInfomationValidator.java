package de.mpg.mpdl.inge.inge_validation.validator.yearbook;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.Validation;
import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;

public class SourcesSequenceInfomationValidator extends ValidatorHandler<List<SourceVO>> implements Validator<List<SourceVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<SourceVO> sources) {

    boolean ok = true;

    if (ValidationTools.isNotEmpty(sources)) {

      int i = 1;
      for (final SourceVO sourceVO : sources) {

        if (sourceVO != null) {

          if (ValidationTools.isEmpty(sourceVO.getSequenceNumber()) && ValidationTools.isEmpty(sourceVO.getStartPage())
              && ValidationTools.isEmpty(sourceVO.getEndPage())
              || ValidationTools.isNotEmpty(sourceVO.getSequenceNumber()) && ValidationTools.isNotEmpty(sourceVO.getStartPage())
                  && ValidationTools.isNotEmpty(sourceVO.getEndPage())) {

            context.addError(ValidationError.create(ErrorMessages.NO_SEQUENCE_INFORMATION_GIVEN).setField("source[" + i + "]")
                .setErrorCode(Validation.WARNING));

            ok = false;

          } // if

        } // if

        i++;
      } // for

    } // if

    return ok;
  }

}
