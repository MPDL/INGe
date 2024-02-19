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
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;

public class CreatorsMaxPlanckAffiliationValidator extends ValidatorHandler<List<CreatorVO>> implements Validator<List<CreatorVO>> {

  private final List<String> childsOfMPG;

  public CreatorsMaxPlanckAffiliationValidator(List<String> childsOfMPG) {
    this.childsOfMPG = childsOfMPG;
  }

  @Override
  public boolean validate(ValidatorContext context, List<CreatorVO> creators) {

    if (ValidationTools.isNotEmpty(creators)) {

      if (this.childsOfMPG.isEmpty()) {
        context.addError(ValidationError.create(ErrorMessages.EMPTY_CHILDS_OF_MPG).setErrorCode(ErrorMessages.ERROR));
        return false;
      }

      for (final CreatorVO creatorVO : creators) {

        if (null != creatorVO) {

          final CreatorVO.CreatorType type = creatorVO.getType();
          switch (type) {

            case ORGANIZATION:

              final OrganizationVO o = creatorVO.getOrganization();

              if (null != o //
                  && ValidationTools.isNotEmpty(o.getIdentifier()) //
                  && !this.childsOfMPG.contains(o.getIdentifier())) {
              } else {
                return true;
              } // if

              break;

            case PERSON:

              final PersonVO p = creatorVO.getPerson();

              if (null != p) {

                for (final OrganizationVO op : p.getOrganizations()) {

                  if (null != op //
                      && ValidationTools.isNotEmpty(op.getIdentifier()) //
                      && !this.childsOfMPG.contains(op.getIdentifier())) {
                  } else {
                    return true;
                  } // if

                } // for

              } // if

              break;

          } // switch

        } // if

      } // for

      context.addError(ValidationError.create(ErrorMessages.NO_MAX_PLANCK_AFFILIATION).setErrorCode(ErrorMessages.WARNING));
      return false;

    } // if

    return true;
  }

}
