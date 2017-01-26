package de.mpg.mpdl.inge.inge_validation;

import de.mpg.mpdl.inge.inge_validation.data.ValidationReportVO;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationConeCacheConfigException;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.util.ValidationPoint;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;

public interface ItemValidating {

  public static final String SERVICE_NAME =
      "ejb/de/mpg/escidoc/services/inge_validation/ItemValidating";

  ValidationReportVO validateItemObject(final ItemVO itemVO) throws ValidationException;

  ValidationReportVO validateItemObject(final ItemVO itemVO, final ValidationPoint validationPoint)
      throws ValidationException;

  void refreshValidationSchemaCache() throws ValidationConeCacheConfigException;

}