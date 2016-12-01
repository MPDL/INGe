package de.mpg.mpdl.inge.inge_validation;

import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.ValidationReportVO;

public interface ItemValidating {

  public static final String SERVICE_NAME = "ejb/de/mpg/escidoc/services/inge_validation/ItemValidating";

  ValidationReportVO validateItemObject(final ItemVO itemVO);

  ValidationReportVO validateItemObject(final ItemVO itemVO, final String validationPoint);

  void refreshValidationSchemaCache();
  
}
