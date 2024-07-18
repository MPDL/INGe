package de.mpg.mpdl.inge.inge_validation.validator;

import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;

public class Utf8TitleValidator extends ValidatorHandler<String> {

  @Override
  public boolean validate(ValidatorContext context, String text) {
    boolean ok = true;

    if (!ValidationTools.isEmpty(text)) {
      ok = ValidationTools.checkUtf8(context, text, null, ErrorMessages.NO_UTF8_CHAR_IN_TITLE);
    }

    return ok;
  }

}
