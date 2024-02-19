package de.mpg.mpdl.inge.inge_validation.validator;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;

/*
 * <!-- if an id is filled in for publication or source, also an id type has to be provided -->
 * <iso:pattern name="id_type_required" id="id_type_required"> <iso:rule
 * context="publication:publication/dc:identifier"> <iso:assert test=". = '' or not(.) or @xsi:type
 * != ''"> IdTypeNotProvided</iso:assert> </iso:rule> </iso:pattern>
 *
 * Additionally checking the format of specific IDs now
 */

public class IdTypeRequiredAndFormatValidator extends ValidatorHandler<List<IdentifierVO>> implements Validator<List<IdentifierVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<IdentifierVO> identifiers) {

    boolean ok = true;

    if (ValidationTools.isNotEmpty(identifiers)) {

      int i = 1;
      for (final IdentifierVO identifierVO : identifiers) {

        if (null != identifierVO) {
          if (ValidationTools.isNotEmpty(identifierVO.getId())) //
            if (null == identifierVO.getType()) {
              context.addError(ValidationError.create(ErrorMessages.ID_TYPE_NOT_PROVIDED).setField("identifier[" + i + "]"));
              ok = false;
            } else { // Check format of the IDs
              if ((IdentifierVO.IdType.BIORXIV.equals(identifierVO.getType()) //
                  || IdentifierVO.IdType.CHEMRXIV.equals(identifierVO.getType()) //
                  || IdentifierVO.IdType.DOI.equals(identifierVO.getType()) //
                  || IdentifierVO.IdType.EARTHARXIV.equals(identifierVO.getType()) //
                  || IdentifierVO.IdType.EDARXIV.equals(identifierVO.getType()) //
                  || IdentifierVO.IdType.ESS_OPEN_ARCHIVE.equals(identifierVO.getType()) //
                  || IdentifierVO.IdType.MEDRXIV.equals(identifierVO.getType()) //
                  || IdentifierVO.IdType.PSYARXIV.equals(identifierVO.getType()) //
                  || IdentifierVO.IdType.RESEARCH_SQUARE.equals(identifierVO.getType()) //
                  || IdentifierVO.IdType.SOCARXIV.equals(identifierVO.getType()))
                  && (identifierVO.getId().startsWith("https://doi.org") || identifierVO.getId().startsWith("http://doi.org"))) {
                context.addError(ValidationError.create(ErrorMessages.INCORRECT_ID_DOI_FORMAT).setField("identifier[" + i + "]"));
                ok = false;
              }
            }
        }

        i++;
      } // for

    } // if

    return ok;
  }

}
