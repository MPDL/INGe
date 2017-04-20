package de.mpg.mpdl.inge.inge_validation.validator;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorType;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;

/*
 * <!-- if the field "Address of an Organization" within a creator of type "Person" is filled,
 * "Name of the Organization" has to be filled also. --> <iso:pattern
 * name="organization_name_required" id="organization_name_required"> <iso:rule
 * context="organization:organization"> <iso:assert test="dc:title != '' or not(escidoc:address) or
 * escidoc:address = ''"> OrganizationNameNotProvided</iso:assert> </iso:rule> </iso:pattern>
 */

public class OrganizationNameRequiredValidator extends ValidatorHandler<List<CreatorVO>> implements
    Validator<List<CreatorVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<CreatorVO> creators) {

    boolean ok = true;

    if (creators != null && creators.isEmpty() == false) {

      int i = 1;
      for (final CreatorVO creatorVO : creators) {

        if (CreatorType.PERSON.equals(creatorVO.getType())) {

          final PersonVO p = creatorVO.getPerson();

          if (p != null) {
            final List<OrganizationVO> orgs = p.getOrganizations();

            int j = 1;
            for (final OrganizationVO organizationVO : orgs) {

              if ((organizationVO.getName() == null || organizationVO.getName().isEmpty()) && (organizationVO.getAddress() != null || !organizationVO.getAddress().isEmpty())) {
                context.addError(ValidationError.create(
                    ErrorMessages.ORGANIZATION_NAME_NOT_PROVIDED) //
                    .setField("creator[" + i + "].organization[" + j + "]"));
                ok = false;
              }

              j++;
            } // for

          } // if

        } // if

        i++;
      } // for

    } // if

    return ok;
  }

}
