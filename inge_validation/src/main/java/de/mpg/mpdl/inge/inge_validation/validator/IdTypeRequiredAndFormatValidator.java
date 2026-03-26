package de.mpg.mpdl.inge.inge_validation.validator;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO.IdType;

/*
 * <!-- if an id is filled in for publication or source, also an id type has to be provided -->
 * <iso:pattern name="id_type_required" id="id_type_required"> <iso:rule
 * context="publication:publication/dc:identifier"> <iso:assert test=". = '' or not(.) or @xsi:type
 * != ''"> IdTypeNotProvided</iso:assert> </iso:rule> </iso:pattern>
 *
 * Additionally checking the format of specific IDs now
 */


public class IdTypeRequiredAndFormatValidator extends ValidatorHandler<List<IdentifierVO>> {

  private static final Set<IdType> DOI_BASED_TYPES = EnumSet.of(IdType.BIORXIV, IdType.CHEMRXIV, IdType.DOI, IdType.EARTHARXIV,
      IdType.EDARXIV, IdType.ESS_OPEN_ARCHIVE, IdType.MEDRXIV, IdType.PSYARXIV, IdType.RESEARCH_SQUARE, IdType.SOCARXIV);

  @Override
  public boolean validate(ValidatorContext context, List<IdentifierVO> identifiers) {
    if (ValidationTools.isEmpty(identifiers)) {
      return true;
    }

    return IntStream.range(0, identifiers.size()).mapToObj(i -> validateIdentifier(context, identifiers.get(i), i + 1)).reduce(true,
        (a, b) -> a && b);
  }

  private boolean validateIdentifier(ValidatorContext context, IdentifierVO identifierVO, int index) {
    if (identifierVO == null) {
      return true;
    }

    String id = identifierVO.getId();
    IdType type = identifierVO.getType();
    String fieldName = "identifier[" + index + "]";

    if (ValidationTools.isEmpty(id)) {
      if (type != null) {
        context.addError(ValidationError.create(ErrorMessages.IDENTIFIER_ID_NOT_PROVIDED).setField(fieldName));
        return false;
      }
      return true;
    }

    if (type == null) {
      context.addError(ValidationError.create(ErrorMessages.IDENTIFIER_TYPE_NOT_PROVIDED).setField(fieldName));
      return false;
    }

    if (isDoiBasedTypeWithUrlFormat(id, type)) {
      context.addError(ValidationError.create(ErrorMessages.INCORRECT_ID_DOI_FORMAT).setField(fieldName));
      return false;
    }

    return true;
  }

  private boolean isDoiBasedTypeWithUrlFormat(String id, IdType type) {
    return DOI_BASED_TYPES.contains(type) && (id.startsWith("https://doi.org") || id.startsWith("http://doi.org"));
  }
}
