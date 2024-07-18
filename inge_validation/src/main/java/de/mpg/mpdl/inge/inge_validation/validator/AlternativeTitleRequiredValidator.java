package de.mpg.mpdl.inge.inge_validation.validator;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;
import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AlternativeTitleVO;
import java.util.List;

public class AlternativeTitleRequiredValidator extends ValidatorHandler<List<AlternativeTitleVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<AlternativeTitleVO> alternativeTitleVOs) {
    boolean ok = true;

    if (ValidationTools.isNotEmpty(alternativeTitleVOs)) {

      int i = 1;
      for (AlternativeTitleVO alternativeTitleVO : alternativeTitleVOs) {

        if (null != alternativeTitleVO) {
          if (ValidationTools.isEmpty(alternativeTitleVO.getValue())) {
            context.addError(ValidationError.create(ErrorMessages.ALTERNATIVE_TITLE_NOT_PROVIDED).setField("alternativeTitle[" + i + "]"));
            ok = false;
          }
        }

        i++;
      } // for

    } // if

    return ok;
  }
}
