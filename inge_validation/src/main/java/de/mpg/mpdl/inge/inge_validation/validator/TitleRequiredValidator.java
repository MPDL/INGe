package de.mpg.mpdl.inge.inge_validation.validator;

import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;

/*
 * <!-- Title is required --> <iso:pattern name="title_required" id="title_required"> <iso:rule
 * context="publication:publication"> <iso:assert test="dc:title != ''">
 * TitleNotProvided</iso:assert> </iso:rule> </iso:pattern>
 */

public class TitleRequiredValidator extends ValidatorHandler<String> {

  @Override
  public boolean validate(ValidatorContext context, String title) {

    if (ValidationTools.isEmpty(title)) {
      context.addErrorMsg(ErrorMessages.TITLE_NOT_PROVIDED);
      return false;
    }

    return true;
  }

}
