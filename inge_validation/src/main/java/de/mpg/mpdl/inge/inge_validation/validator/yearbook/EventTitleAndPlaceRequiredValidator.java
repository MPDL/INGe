package de.mpg.mpdl.inge.inge_validation.validator.yearbook;

import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.metadata.EventVO;

public class EventTitleAndPlaceRequiredValidator extends ValidatorHandler<EventVO> implements Validator<EventVO> {

  @Override
  public boolean validate(ValidatorContext context, EventVO e) {

    boolean ok = true;

    if (e != null) {

      if (ValidationTools.isEmpty(e.getTitle())) {
        context.addErrorMsg(ErrorMessages.EVENT_TITLE_REQUIRED);

        ok = false;

      } // if

      if (ValidationTools.isEmpty(e.getPlace())) {
        context.addErrorMsg(ErrorMessages.EVENT_PLACE_REQUIRED);

        ok = false;

      } // if

    }

    return ok;
  }
}
