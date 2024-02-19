package de.mpg.mpdl.inge.inge_validation.validator.OLDyearbook;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;

public class SourcesCreatorsOrganizationNamesRequiredValidator extends ValidatorHandler<List<SourceVO>>
    implements Validator<List<SourceVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<SourceVO> sources) {

    boolean ok = true;

    if (ValidationTools.isNotEmpty(sources)) {

      int i = 1;
      for (SourceVO sourceVO : sources) {

        if (ValidationTools.isNotEmpty(sourceVO.getCreators())) {

          int j = 1;
          for (final CreatorVO creatorVO : sourceVO.getCreators()) {

            if (null != creatorVO && CreatorVO.CreatorType.ORGANIZATION.equals(creatorVO.getType())) {

              final OrganizationVO o = creatorVO.getOrganization();

              if (null != o) {

                if (ValidationTools.isEmpty(o.getName())) {
                  context.addError(ValidationError.create(ErrorMessages.SOURCE_CREATOR_ORGANIZATION_NAME_NOT_PROVIDED)
                      .setField("source[" + i + "].creator[" + j + "]").setErrorCode(ErrorMessages.WARNING));

                  ok = false;

                } // if

              } // if

            } // if

            j++;
          } // for

        } // if

      } // for

      i++;

    } // if

    return ok;
  }

}
