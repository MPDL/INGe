package de.mpg.mpdl.inge.inge_validation.validator;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorType;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;

/*
 * <!-- At least one creator with an organizational unit provided is required --> <iso:pattern
 * name="creator_required" id="creator_required"> <iso:rule context="publication:publication">
 * <iso:assert test="escidoc:creator/person:person/escidoc:family-name != '' or
 * escidoc:creator/organization:organization/dc:title != ''"> CreatorNotProvided</iso:assert>
 * <iso:assert test="escidoc:creator/person:person/organization:organization/dc:title != '' or
 * escidoc:creator/organization:organization/dc:title != ''">
 * OrganizationalMetadataNotProvided</iso:assert> </iso:rule>
 * 
 * <iso:rule context="publication:publication/escidoc:creator/person:person"> <iso:assert
 * test="escidoc:family-name != ''"> CreatorFamilyNameNotProvided</iso:assert> </iso:rule>
 * 
 * <iso:rule context="publication:publication/escidoc:creator/organization:organization">
 * <iso:assert test="dc:title != ''"> CreatorOrganizationNameNotProvided</iso:assert> </iso:rule>
 * </iso:pattern>
 */

public class CreatorRequiredValidator extends ValidatorHandler<List<CreatorVO>> implements
    Validator<List<CreatorVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<CreatorVO> creators) {

    if (creators == null || creators.isEmpty()) {
      context.addErrorMsg(ErrorMessages.CREATOR_NOT_PROVIDED);
      return false;
    }

    int ok = 0;
    boolean errorOrg = false;
    boolean errorPers = false;
    boolean errorPersOrg = false;

    for (CreatorVO creatorVO : creators) {

      if (ok > 0) {
        break;
      }

      CreatorType type = creatorVO.getType();
      switch (type) {

        case ORGANIZATION:

          OrganizationVO o = creatorVO.getOrganization();
          if (o != null && o.getName() != null) {
            ok++;
          } else {
            errorOrg = true;
            continue;
          }

          break;

        case PERSON:

          PersonVO p = creatorVO.getPerson();
          if (p == null || p.getFamilyName() == null) {
            errorPers = true;
            continue;
          }

          int orgsOk = 0;
          List<OrganizationVO> orgs = p.getOrganizations();
          if (orgs != null) {
            for (OrganizationVO organizationVO : orgs) {
              if (organizationVO.getName() != null) {
                orgsOk++;
              }
            }
          }

          if (orgsOk > 0) {
            ok++;
          } else {
            errorPersOrg = true;
          }

          break;
      }

    } // for

    if (ok == 0) {

      if (errorOrg) {
        context.addErrorMsg(ErrorMessages.CREATOR_ORGANIZATION_NAME_NOT_PROVIDED);
      }

      if (errorPers) {
        context.addErrorMsg(ErrorMessages.CREATOR_FAMILY_NAME_NOT_PROVIDED);
      }

      if (errorPersOrg) {
        context.addErrorMsg(ErrorMessages.ORGANIZATIONAL_METADATA_NOT_PROVIDED);
      }

      return false;
    }


    return true;
  }

}
