package de.mpg.mpdl.inge.inge_validation.validator;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AlternativeTitleVO;

public class Utf8AlternativeTitleValidator extends ValidatorHandler<List<AlternativeTitleVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<AlternativeTitleVO> alternativeTitleVOs) {
    boolean ok = true;

    if (!ValidationTools.isEmpty(alternativeTitleVOs)) {
      for (AlternativeTitleVO alternativTitleVO : alternativeTitleVOs) {
        if (!ValidationTools.isEmpty(alternativTitleVO.getValue())
            && !ValidationTools.checkUtf8(context, alternativTitleVO.getValue(), ErrorMessages.NO_UTF8_CHAR_IN_ALTERNATIVE_TITLE)) {
          ok = false;
        }
      }
    }

    return ok;
  }
}
