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

/*
 * <!-- if any fields at "Source creator" are filled, "role" has to be filled also. --> <iso:pattern
 * name="source_creator_role_required" id="source_creator_role_required"> <iso:rule
 * context="source:source/escidoc:creator"> <iso:assert test="@role != '' or
 * not(person:person/escidoc:family-name != '' or person:person/escidoc:given-name != '' or
 * organization:organization/dc:title != '' or person:person/organization:organization/dc:title !=
 * '' or organization:organization/escidoc:address != '' or
 * person:person/organization:organization/escidoc:address != '')">
 * SourceCreatorRoleNotProvided</iso:assert> </iso:rule> </iso:pattern>
 */

public class SourceCreatorsRoleRequiredValidator extends ValidatorHandler<List<SourceVO>> implements Validator<List<SourceVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<SourceVO> sources) {

    boolean ok = true;

    if (ValidationTools.isNotEmpty(sources)) {

      int i = 1;
      for (final SourceVO sourceVO : sources) {

        if (sourceVO != null) {

          int j = 1;
          for (final CreatorVO creatorVO : sourceVO.getCreators()) {

            if (creatorVO != null && creatorVO.getRole() == null) {

              switch (creatorVO.getType()) {

                case ORGANIZATION:

                  final OrganizationVO o = creatorVO.getOrganization();
                  if (o != null) {
                    if (ValidationTools.isNotEmpty(o.getName()) //
                        || ValidationTools.isNotEmpty(o.getAddress())) {
                      context.addError(ValidationError.create(ErrorMessages.SOURCE_CREATOR_ROLE_NOT_PROVIDED)
                          .setField("source[" + i + "].creator[" + j + "]"));
                      ok = false;
                    }
                  }

                  break;

                case PERSON:

                  final PersonVO p = creatorVO.getPerson();
                  if (p != null) {
                    if (ValidationTools.isNotEmpty(p.getFamilyName()) //
                        || ValidationTools.isNotEmpty(p.getGivenName())) {
                      context.addError(ValidationError.create(ErrorMessages.SOURCE_CREATOR_ROLE_NOT_PROVIDED)
                          .setField("source[" + i + "].creator[" + j + "]"));
                      ok = false;

                      break;
                    }
                  }

                  final List<OrganizationVO> orgs = p.getOrganizations();

                  if (ValidationTools.isNotEmpty(orgs)) {

                    int z = 1;
                    for (final OrganizationVO organizationVO : orgs) {

                      if (organizationVO != null) {
                        if (ValidationTools.isNotEmpty(organizationVO.getName()) //
                            || ValidationTools.isNotEmpty(organizationVO.getAddress())) {
                          context.addError(ValidationError.create(ErrorMessages.SOURCE_CREATOR_ROLE_NOT_PROVIDED)
                              .setField("source[" + i + "].creator[" + j + "].organization[" + z + "]"));
                          ok = false;

                          break;
                        }
                      }

                      z++;
                    } // for

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
