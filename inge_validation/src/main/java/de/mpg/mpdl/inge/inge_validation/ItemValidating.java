package de.mpg.mpdl.inge.inge_validation;

import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationConeCacheConfigException;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.util.ValidationPoint;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;

public interface ItemValidating {

  public static final String SERVICE_NAME =
      "ejb/de/mpg/escidoc/services/inge_validation/ItemValidating";

  void validateItemObject(final ItemVO itemVO) throws ValidationException, ItemInvalidException;

  void validateItemObject(final ItemVO itemVO, final ValidationPoint validationPoint)
      throws ValidationException, ItemInvalidException;

  void refreshValidationSchemaCache() throws ValidationConeCacheConfigException;

}
