package de.mpg.mpdl.inge.inge_validation.validator;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;

/*
 * <!-- if an id is filled in for publication or source, also an id type has to be provided -->
 * <iso:pattern name="id_type_required" id="id_type_required"> <iso:rule
 * context="publication:publication/dc:identifier"> <iso:assert test=". = '' or not(.) or @xsi:type
 * != ''"> IdTypeNotProvided</iso:assert> </iso:rule> </iso:pattern>
 */

public class IdTypeRequiredValidator extends ValidatorHandler<List<IdentifierVO>> implements
    Validator<List<IdentifierVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<IdentifierVO> identifiers) {

    boolean ok = true;

    if (identifiers != null && identifiers.isEmpty() == false) {

      int i = 1;
      for (IdentifierVO identifierVO : identifiers) {

        if (identifierVO.getId() != null && identifierVO.getId().trim().length() > 0
            && identifierVO.getType() == null) {
          context.addError(ValidationError.create(ErrorMessages.ID_TYPE_NOT_PROVIDED).setField(
              "identifier[" + i + "]"));
          ok = false;
        }

        i++;
      } // for

    } // if

    return ok;
  }

}
