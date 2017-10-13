package de.mpg.mpdl.inge.inge_validation.validator.yearbook;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorType;

public class SourceCreatorsRequiredValidator extends ValidatorHandler<List<SourceVO>>
    implements Validator<List<SourceVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<SourceVO> sources) {

    boolean ok = true;

    if (ValidationTools.isNotEmpty(sources)) {

      for (SourceVO sourceVO : sources) {
        if (ValidationTools.isEmpty(sourceVO.getCreators())) {
          context.addErrorMsg(ErrorMessages.SOURCE_CREATOR_NOT_PROVIDED);
          return false;
        }
      }

      int i = 1;
      for (SourceVO sourceVO : sources) {

        int j = 1;
        for (final CreatorVO creatorVO : sourceVO.getCreators()) {
          
          boolean errorOrgName = false;
          boolean errorPersFamName = false;
          boolean errorPersGivName = false;

          if (creatorVO != null) {

            final CreatorType type = creatorVO.getType();
            switch (type) {

              case ORGANIZATION:

                final OrganizationVO o = creatorVO.getOrganization();
                if (o != null && ValidationTools.isEmpty(o.getName())) {
                  errorOrgName = true;
                }

                break;

              case PERSON:

                final PersonVO p = creatorVO.getPerson();
                if (p == null || p.getFamilyName() == null) {
                  errorPersFamName = true;
                } else if (p != null && p.getGivenName() == null) {
                  errorPersGivName = true;
                }

                break;
            }

          } // if

          if (errorOrgName) {
            context.addError(
                ValidationError.create(ErrorMessages.SOURCE_CREATOR_ORGANIZATION_NAME_NOT_PROVIDED)
                    .setField("source[" + i + "].creator[" + j + "]"));
            ok = false;
          }

          if (errorPersFamName) {
            context.addError(
                ValidationError.create(ErrorMessages.SOURCE_CREATOR_FAMILY_NAME_NOT_PROVIDED)
                    .setField("source[" + i + "].creator[" + j + "]"));
            ok = false;
          }

          if (errorPersGivName) {
            context.addError(
                ValidationError.create(ErrorMessages.SOURCE_CREATOR_GIVEN_NAME_NOT_PROVIDED)
                    .setField("source[" + i + "].creator[" + j + "]"));
            ok = false;
          }

          j++;
        } // for
        
        i++;
      } // for

    } // if

    return ok;
  }

}
