package de.mpg.mpdl.inge.inge_validation.validator;

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

public class SourceCreatorsNameRequiredValidator extends ValidatorHandler<List<SourceVO>> implements Validator<List<SourceVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<SourceVO> sources) {

    boolean ok = true;

    if (ValidationTools.isNotEmpty(sources)) {

      int i = 1;
      for (final SourceVO sourceVO : sources) {

        if (sourceVO != null) {

          int j = 1;
          for (final CreatorVO creatorVO : sourceVO.getCreators()) {

            if (creatorVO != null) {

              switch (creatorVO.getType()) {

                case ORGANIZATION:

                  //                  final OrganizationVO o = creatorVO.getOrganization();
                  //                  if (o != null) {
                  //                    if (ValidationTools.isEmpty(o.getName()) //
                  //                        && ValidationTools.isNotEmpty(o.getAddress())) {
                  //                      context.addError(ValidationError.create(ErrorMessages.SOURCE_CREATOR_ORGANIZATION_NAME_NOT_PROVIDED)
                  //                          .setField("source[" + i + "].creator[" + j + "]"));
                  //                      ok = false;
                  //                    }
                  //                  }

                  break;

                case PERSON:

                  final PersonVO p = creatorVO.getPerson();
                  if (p != null) {
                    if (ValidationTools.isEmpty(p.getFamilyName())) { //
                      context.addError(ValidationError.create(ErrorMessages.NO_SOURCE_CREATOR_FAMILY_NAME)
                          .setField("source[" + i + "].creator[" + j + "]"));
                      ok = false;
                    }
                  }

                  if (p != null) {
                    final List<OrganizationVO> orgs = p.getOrganizations();

                    if (ValidationTools.isNotEmpty(orgs)) {

                      int z = 1;
                      for (final OrganizationVO organizationVO : orgs) {

                        if (organizationVO != null) {
                          if (ValidationTools.isEmpty(organizationVO.getName()) //
                              && ValidationTools.isNotEmpty(organizationVO.getAddress())) {
                            context.addError(ValidationError.create(ErrorMessages.NO_SOURCE_CREATOR_ORGANIZATION_NAME)
                                .setField("source[" + i + "].creator[" + j + "].organization[" + z + "]"));
                            ok = false;
                          }
                        }

                        z++;
                      } // for

                    } // if
                  }

                  break;
              } // switch

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
