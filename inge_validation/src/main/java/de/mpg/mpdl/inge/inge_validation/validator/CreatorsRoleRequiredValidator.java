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

/*
 * <!-- if any fields at "Source creator" are filled, "role" has to be filled also. --> <iso:pattern
 * name="source_creator_role_required" id="source_creator_role_required"> <iso:rule
 * context="source:source/escidoc:creator"> <iso:assert test="@role != '' or
 * not(person:person/escidoc:family-name != '' or person:person/escidoc:given-name != '' or
 * organization:organization/dc:title != '' or person:person/organization:organization/dc:title !=
 * '' or organization:organization/escidoc:address != '' or
 * person:person/organization:organization/escidoc:address != '')">
 * CreatorRoleNotProvided</iso:assert> </iso:rule> </iso:pattern>
 */

public class CreatorsRoleRequiredValidator extends ValidatorHandler<List<CreatorVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<CreatorVO> creators) {

    boolean ok = true;

    if (ValidationTools.isNotEmpty(creators)) {

      int i = 1;
      for (CreatorVO creatorVO : creators) {

        if (null != creatorVO && null == creatorVO.getRole()) {

          switch (creatorVO.getType()) {

            case ORGANIZATION:

              OrganizationVO o = creatorVO.getOrganization();
              if (null != o) {
                if (ValidationTools.isNotEmpty(o.getName()) //
                    || ValidationTools.isNotEmpty(o.getAddress())) {
                  context.addError(ValidationError.create(ErrorMessages.CREATOR_ROLE_NOT_PROVIDED).setField("creator[" + i + "]"));
                  ok = false;
                }
              }

              break;

            case PERSON:

              PersonVO p = creatorVO.getPerson();
              if (null != p) {
                if (ValidationTools.isNotEmpty(p.getFamilyName()) //
                    || ValidationTools.isNotEmpty(p.getGivenName())) {
                  context.addError(ValidationError.create(ErrorMessages.CREATOR_ROLE_NOT_PROVIDED).setField("creator[" + i + "]"));
                  ok = false;

                  break;
                }
              }

              List<OrganizationVO> orgs = p.getOrganizations();

              if (ValidationTools.isNotEmpty(orgs)) {

                int j = 1;
                for (OrganizationVO organizationVO : orgs) {

                  if (null != organizationVO) {
                    if (ValidationTools.isNotEmpty(organizationVO.getName()) //
                        || ValidationTools.isNotEmpty(organizationVO.getAddress())) {
                      context.addError(ValidationError.create(ErrorMessages.CREATOR_ROLE_NOT_PROVIDED) //
                          .setField("creator[" + i + "].organization[" + j + "]"));
                      ok = false;

                      break;
                    }

                  }

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
