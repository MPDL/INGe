package de.mpg.mpdl.inge.inge_validation;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.inge_validation.util.ConeCache;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.ValidationReportVO;

@Stateless
@Remote(ItemValidating.class)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ItemValidatingBean implements ItemValidating {

  private static final Logger LOG = Logger.getLogger(ItemValidatingBean.class);

  public ValidationReportVO validateItemObject(final ItemVO itemVO) {
    try {
      ValidationService s = new ValidationService();
      return s.doValidation(itemVO);
    } catch (Exception e) {
      LOG.error("validateItemObject:", e);
      // TODO
    }

    return null;
  }

  public ValidationReportVO validateItemObject(final ItemVO itemVO, final String validationPoint) {
    try {
      ValidationService s = new ValidationService();
      return s.doValidation(itemVO, validationPoint);
    } catch (Exception e) {
      LOG.error("validateItemObject:", e);
      // TODO
    }

    return null;
  }

  public void refreshValidationSchemaCache() {
    try {
      ConeCache.getInstance().refreshCache();
    } catch (Exception e) {
      LOG.error("refreshValidationSchemaCache:", e);
      // TODO
    }
  }

}
