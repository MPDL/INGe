package de.mpg.mpdl.inge.inge_validation;

import de.mpg.mpdl.inge.inge_validation.util.ValidationReportVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;

public interface ItemValidating {

  public static final String SERVICE_NAME =
      "ejb/de/mpg/escidoc/services/inge_validation/ItemValidating";

  ValidationReportVO validateItemObject(final ItemVO itemVO) throws ValidationException;

  ValidationReportVO validateItemObject(final ItemVO itemVO, final String validationPoint)
      throws ValidationException;

  void refreshValidationSchemaCache() throws ValidationConeCacheConfigException;

}
