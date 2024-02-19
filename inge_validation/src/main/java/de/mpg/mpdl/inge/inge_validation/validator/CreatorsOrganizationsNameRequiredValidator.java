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

/*
 * <!-- if the field "Address of an Organization" within a creator of type "Person" is filled,
 * "Name of the Organization" has to be filled also. --> <iso:pattern
 * name="organization_name_required" id="organization_name_required"> <iso:rule
 * context="organization:organization"> <iso:assert test="dc:title != '' or not(escidoc:address) or
 * escidoc:address = ''"> OrganizationNameNotProvided</iso:assert> </iso:rule> </iso:pattern>
 */

public class CreatorsOrganizationsNameRequiredValidator extends ValidatorHandler<List<CreatorVO>> implements Validator<List<CreatorVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<CreatorVO> creators) {

    boolean ok = true;

    if (ValidationTools.isNotEmpty(creators)) {

      int i = 1;
      for (final CreatorVO creatorVO : creators) {

        if (null != creatorVO && CreatorVO.CreatorType.PERSON.equals(creatorVO.getType())) {

          final PersonVO p = creatorVO.getPerson();

          if (null != p) {
            final List<OrganizationVO> orgs = p.getOrganizations();

            if (ValidationTools.isNotEmpty(orgs)) {

              int j = 1;
              for (final OrganizationVO organizationVO : orgs) {

                if (null != organizationVO) {

                  if (ValidationTools.isEmpty(organizationVO.getName()) && ValidationTools.isNotEmpty(organizationVO.getAddress())) {
                    context.addError(ValidationError.create(ErrorMessages.ORGANIZATION_NAME_NOT_PROVIDED) //
                        .setField("creator[" + i + "].organization[" + j + "]"));
                    ok = false;
                  }

                } // if

                j++;
              } // for

            } // if

          } // if

        } // if

        i++;
      } // for

    } // if

    return ok;
  }

}
