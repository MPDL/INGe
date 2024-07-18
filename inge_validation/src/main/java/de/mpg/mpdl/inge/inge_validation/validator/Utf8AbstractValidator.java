package de.mpg.mpdl.inge.inge_validation.validator;

import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;
import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AbstractVO;
import java.util.List;

public class Utf8AbstractValidator extends ValidatorHandler<List<AbstractVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<AbstractVO> abstractVOs) {
    boolean ok = true;

    if (ValidationTools.isNotEmpty(abstractVOs)) {

      int i = 1;
      for (AbstractVO abstractVO : abstractVOs) {

        if (null != abstractVO) {
          if (ValidationTools.isNotEmpty(abstractVO.getValue())
              && !ValidationTools.checkUtf8(context, abstractVO.getValue(), i, ErrorMessages.NO_UTF8_CHAR_IN_ABSTRACT)) {
            ok = false;
          }
        }

        i++;
      } // for

    } // if

    return ok;
  }
}
