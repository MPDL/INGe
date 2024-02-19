package de.mpg.mpdl.inge.inge_validation.validator.OLDyearbook;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;

public class CreatorsPersonNamesRequiredValidator extends ValidatorHandler<List<CreatorVO>> implements Validator<List<CreatorVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<CreatorVO> creators) {

    boolean ok = true;

    if (ValidationTools.isNotEmpty(creators)) {

      int i = 1;
      for (final CreatorVO creatorVO : creators) {

        if (null != creatorVO && CreatorVO.CreatorType.PERSON.equals(creatorVO.getType())) {

          final PersonVO p = creatorVO.getPerson();

          if (null != p) {

            if (ValidationTools.isEmpty(p.getFamilyName())) {

              context.addError(ValidationError.create(ErrorMessages.CREATOR_FAMILY_NAME_NOT_PROVIDED).setField("creator[" + i + "]")
                  .setErrorCode(ErrorMessages.WARNING));

              ok = false;

            }

            if (ValidationTools.isEmpty(p.getGivenName())) {

              context.addError(ValidationError.create(ErrorMessages.CREATOR_GIVEN_NAME_NOT_PROVIDED).setField("creator[" + i + "]")
                  .setErrorCode(ErrorMessages.WARNING));

              ok = false;

            }

          } // if

        } // if

        i++;
      } // for

    } // if

    return ok;
  }

}
