package de.mpg.mpdl.inge.inge_validation.validator.yearbook;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorType;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;

public class CreatorsMaxPlanckAffiliationValidator extends ValidatorHandler<List<CreatorVO>> implements Validator<List<CreatorVO>> {

  private final List<String> childsOfMPG;

  public CreatorsMaxPlanckAffiliationValidator(List<String> childsOfMPG) {
    this.childsOfMPG = childsOfMPG;
  }

  @Override
  public boolean validate(ValidatorContext context, List<CreatorVO> creators) {

    boolean ok = true;

    if (ValidationTools.isNotEmpty(creators)) {

      int i = 1;
      for (final CreatorVO creatorVO : creators) {

        if (creatorVO != null) {

          final CreatorType type = creatorVO.getType();
          switch (type) {

            case ORGANIZATION:

              final OrganizationVO o = creatorVO.getOrganization();

              if (o != null //
                  && ValidationTools.isNotEmpty(o.getIdentifier()) //
                  && !this.childsOfMPG.contains(o.getIdentifier())) {

                context.addError(ValidationError.create(ErrorMessages.NO_MAX_PLANCK_AFFILIATION).setField("creator[" + i + "]"));

                ok = false;

              } // if

              break;

            case PERSON:

              final PersonVO p = creatorVO.getPerson();

              if (p != null) {

                int j = 1;
                for (final OrganizationVO op : p.getOrganizations()) {

                  if (op != null //
                      && ValidationTools.isNotEmpty(op.getIdentifier()) //
                      && !this.childsOfMPG.contains(op.getIdentifier())) {

                    context.addError(
                        ValidationError.create(ErrorMessages.NO_MAX_PLANCK_AFFILIATION).setField("creator[" + i + "].person[" + j + "]"));

                    ok = false;

                  } // if

                  j++;
                } // for

              } // if

              break;

          } // switch

        } // if

        i++;
      } // for

    } // if

    return ok;
  }
}
