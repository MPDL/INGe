package de.mpg.mpdl.inge.inge_validation.validator;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AbstractVO;

public class Utf8AbstractValidator extends ValidatorHandler<List<AbstractVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<AbstractVO> abstractVOs) {
    boolean ok = true;

    if (!ValidationTools.isEmpty(abstractVOs)) {
      for (AbstractVO abstractVO : abstractVOs) {
        if (!ValidationTools.isEmpty(abstractVO.getValue())
            && !ValidationTools.checkUtf8(context, abstractVO.getValue(), ErrorMessages.NO_UTF8_CHAR_IN_ABSTRACT)) {
          ok = false;
        }
      }
    }

    return ok;
  }
}
