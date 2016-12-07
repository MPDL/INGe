package de.mpg.mpdl.inge.inge_validation;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.inge_validation.util.ConeCache;
import de.mpg.mpdl.inge.inge_validation.util.ValidationException;
import de.mpg.mpdl.inge.inge_validation.util.ValidationReportVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;

@Stateless
@Remote(ItemValidating.class)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ItemValidatingBean implements ItemValidating {

  private static final Logger LOG = Logger.getLogger(ItemValidatingBean.class);

  public ValidationReportVO validateItemObject(final ItemVO itemVO) throws ValidationException {
    ValidationService s = new ValidationService();

    try {
      return s.doValidation(itemVO);
    } catch (ValidationException e) {
      LOG.error("validateItemObject:", e);
      throw e;
    }
  }

  public ValidationReportVO validateItemObject(final ItemVO itemVO, final String validationPoint)
      throws ValidationException {
    ValidationService s = new ValidationService();

    try {
      return s.doValidation(itemVO, validationPoint);
    } catch (ValidationException e) {
      LOG.error("validateItemObject:", e);
      throw e;
    }
  }

  public void refreshValidationSchemaCache() throws ValidationException {
    try {
      ConeCache.getInstance().refreshCache();
    } catch (ValidationException e) {
      LOG.error("refreshValidationSchemaCache:", e);
      throw e;
    }
  }

}
