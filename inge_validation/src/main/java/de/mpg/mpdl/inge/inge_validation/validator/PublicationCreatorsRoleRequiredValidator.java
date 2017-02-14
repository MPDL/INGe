package de.mpg.mpdl.inge.inge_validation.validator;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
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

public class PublicationCreatorsRoleRequiredValidator extends ValidatorHandler<List<CreatorVO>>
    implements Validator<List<CreatorVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<CreatorVO> creators) {

    boolean ok = true;

    if (creators != null) {

      int i = 1;
      for (CreatorVO creatorVO : creators) {

        if (creatorVO.getRole() == null) {

          switch (creatorVO.getType()) {

            case ORGANIZATION:

              OrganizationVO o = creatorVO.getOrganization();
              if (o.getName() != null //
                  && o.getName().trim().length() > 0 //
                  || o.getAddress() != null //
                  && o.getAddress().trim().length() > 0) {
                context.addError(ValidationError.create(ErrorMessages.CREATOR_ROLE_NOT_PROVIDED)
                    .setField("creator[" + i + "]"));
                ok = false;
              }

              break;

            case PERSON:

              PersonVO p = creatorVO.getPerson();
              if (p.getFamilyName() != null //
                  && p.getFamilyName().trim().length() > 0 //
                  || p.getGivenName() != null //
                  && p.getGivenName().trim().length() > 0) {
                context.addError(ValidationError.create(ErrorMessages.CREATOR_ROLE_NOT_PROVIDED)
                    .setField("creator[" + i + "]"));
                ok = false;
              }

              List<OrganizationVO> orgs = p.getOrganizations();

              int j = 1;
              for (OrganizationVO organizationVO : orgs) {

                if (organizationVO.getName() != null //
                    && organizationVO.getName().trim().length() > 0 //
                    || organizationVO.getAddress() != null //
                    && organizationVO.getAddress().trim().length() > 0)
                  context.addError(ValidationError.create(ErrorMessages.CREATOR_ROLE_NOT_PROVIDED) //
                      .setField("creator[" + i + "].organization[" + j + "]"));
                ok = false;

                j++;
              } // for

              break;

          } // switch

        } // if

        i++;
      } // for

    } // if

    return ok;
  }

}
