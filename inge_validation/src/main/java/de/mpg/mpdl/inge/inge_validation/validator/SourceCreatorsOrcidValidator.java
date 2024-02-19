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
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;

public class SourceCreatorsOrcidValidator extends ValidatorHandler<List<SourceVO>> implements Validator<List<SourceVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<SourceVO> sources) {

    boolean ok = true;

    if (ValidationTools.isNotEmpty(sources)) {

      int i = 1;
      for (final SourceVO sourceVO : sources) {

        if (null != sourceVO) {

          int j = 1;
          for (final CreatorVO creatorVO : sourceVO.getCreators()) {

            if (null != creatorVO && CreatorVO.CreatorType.PERSON.equals(creatorVO.getType())) {

              final PersonVO p = creatorVO.getPerson();

              if (null != p) {

                String orcid = p.getOrcid();

                if (ValidationTools.isNotEmpty(orcid)) {

                  if (!orcid.startsWith(ValidationTools.ORCID_HTTPS)) {
                    context.addError(ValidationError.create(ErrorMessages.SOURCE_CREATOR_ORCID_INVALID)
                        .setField("source[" + i + "].creator[" + j + "]: " + p.getFamilyName() + ": " + p.getOrcid()));
                    ok = false;
                  } else if ((!orcid.substring(ValidationTools.ORCID_HTTPS.length()).matches(ValidationTools.ORCID_REGEX))) {
                    context.addError(ValidationError.create(ErrorMessages.SOURCE_CREATOR_ORCID_INVALID)
                        .setField("source[" + i + "].creator[" + j + "]: " + p.getFamilyName() + ": " + p.getOrcid()));
                    ok = false;
                  }

                } // if

              } // if

            } // if

            j++;
          } // for

        } // if

        i++;
      } // for

    } // if

    return ok;
  }

}

