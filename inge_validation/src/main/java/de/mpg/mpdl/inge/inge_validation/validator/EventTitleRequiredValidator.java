package de.mpg.mpdl.inge.inge_validation.validator;

import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.metadata.EventVO;

/*
 * <!-- if any fields at "Event" are filled, "Title" of the event has to be filled also. -->
 * <iso:pattern name="event_title_required" id="event_title_required"> <iso:rule
 * context="event:event"> <iso:assert test="dc:title != '' or not(* != '' or @xml:lang != '')">
 * EventTitleNotProvided</iso:assert> </iso:rule> </iso:pattern>
 */

public class EventTitleRequiredValidator extends ValidatorHandler<EventVO> {

  @Override
  public boolean validate(ValidatorContext context, EventVO e) {

    if (null != e) {

      if (ValidationTools.isEmpty(e.getTitle()) //
          && (ValidationTools.isNotEmpty(e.getEndDate()) //
              || null != e.getInvitationStatus() //
              || ValidationTools.isNotEmpty(e.getPlace()) //
              || ValidationTools.isNotEmpty(e.getStartDate()))) {
        context.addErrorMsg(ErrorMessages.EVENT_TITLE_NOT_PROVIDED);
        return false;
      }

    }

    return true;
  }
}
