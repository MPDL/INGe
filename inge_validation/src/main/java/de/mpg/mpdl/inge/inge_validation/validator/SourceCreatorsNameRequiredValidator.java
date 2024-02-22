package de.mpg.mpdl.inge.inge_validation.validator;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;

public class SourceCreatorsNameRequiredValidator extends ValidatorHandler<List<SourceVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<SourceVO> sources) {

    boolean ok = true;

    if (ValidationTools.isNotEmpty(sources)) {

      int i = 1;
      for (SourceVO sourceVO : sources) {

        if (null != sourceVO) {

          int j = 1;
          for (CreatorVO creatorVO : sourceVO.getCreators()) {

            if (null != creatorVO) {

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

                  PersonVO p = creatorVO.getPerson();
                  if (null != p) {
                    if (ValidationTools.isEmpty(p.getFamilyName())) { //
                      context.addError(ValidationError.create(ErrorMessages.NO_SOURCE_CREATOR_FAMILY_NAME)
                          .setField("source[" + i + "].creator[" + j + "]"));
                      ok = false;
                    }
                  }

                  if (null != p) {
                    List<OrganizationVO> orgs = p.getOrganizations();

                    if (ValidationTools.isNotEmpty(orgs)) {

                      int z = 1;
                      for (OrganizationVO organizationVO : orgs) {

                        if (null != organizationVO) {
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
