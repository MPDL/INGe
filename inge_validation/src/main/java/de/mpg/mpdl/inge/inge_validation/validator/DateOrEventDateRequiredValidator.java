package de.mpg.mpdl.inge.inge_validation.validator;

import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;

public class DateOrEventDateRequiredValidator extends ValidatorHandler<MdsPublicationVO> implements Validator<MdsPublicationVO> {

  @Override
  public boolean validate(ValidatorContext context, MdsPublicationVO m) {

    if (ValidationTools.isEmpty(m.getDateAccepted()) //
        && ValidationTools.isEmpty(m.getDateCreated()) //
        && ValidationTools.isEmpty(m.getDateModified()) //
        && ValidationTools.isEmpty(m.getDatePublishedInPrint()) //
        && ValidationTools.isEmpty(m.getDatePublishedOnline()) //
        && ValidationTools.isEmpty(m.getDateSubmitted()) //
        && (m.getEvent() == null || ValidationTools.isEmpty(m.getEvent().getStartDate()))) {
      context.addErrorMsg(ErrorMessages.DATE_NOT_PROVIDED);

      return false;
    }

    return true;
  }

}
