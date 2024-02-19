package de.mpg.mpdl.inge.inge_validation.validator;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;

public class CreatorsOrcidValidator extends ValidatorHandler<List<CreatorVO>> implements Validator<List<CreatorVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<CreatorVO> creators) {

    boolean ok = true;

    if (ValidationTools.isNotEmpty(creators)) {

      int i = 1;
      for (final CreatorVO creatorVO : creators) {

        if (null != creatorVO && CreatorVO.CreatorType.PERSON.equals(creatorVO.getType())) {

          final PersonVO p = creatorVO.getPerson();

          if (null != p) {

            String orcid = p.getOrcid();

            if (ValidationTools.isNotEmpty(orcid)) {

              if (!orcid.startsWith(ValidationTools.ORCID_HTTPS)) {
                context.addError(ValidationError.create(ErrorMessages.CREATOR_ORCID_INVALID)
                    .setField("creator[" + i + "]: " + p.getFamilyName() + ": " + p.getOrcid()));
                ok = false;
              } else if ((!orcid.substring(ValidationTools.ORCID_HTTPS.length()).matches(ValidationTools.ORCID_REGEX))) {
                context.addError(ValidationError.create(ErrorMessages.CREATOR_ORCID_INVALID)
                    .setField("creator[" + i + "]: " + p.getFamilyName() + ": " + p.getOrcid()));
                ok = false;
              }

            } // if

          } // if

        } // if

        i++;
      } // for

    } // if

    return ok;
  }

}
