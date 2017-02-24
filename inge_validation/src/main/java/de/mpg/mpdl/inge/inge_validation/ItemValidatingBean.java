package de.mpg.mpdl.inge.inge_validation;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationConeCacheConfigException;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.util.ValidationPoint;
import de.mpg.mpdl.inge.inge_validation.validator.cone.ConeCache;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;

@Stateless
@Remote(ItemValidating.class)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ItemValidatingBean implements ItemValidating {

  private static final Logger LOG = Logger.getLogger(ItemValidatingBean.class);

  // public void validateItemObject(final ItemVO itemVO) throws ValidationException,
  // ItemInvalidException {
  //
  // ValidationService s = new ValidationService();
  //
  // try {
  // s.doValidation(itemVO);
  // } catch (ValidationException e) {
  // LOG.error("validateItemObject:", e);
  // throw e;
  // } catch (ItemInvalidException e) {
  // throw e;
  // } catch (Exception e) {
  // LOG.error("validateItemObject: " + itemVO, e);
  // throw new ValidationException("validateItemObject:", e);
  // }
  //
  // }

  public void validateItemObject(final ItemVO itemVO, final ValidationPoint validationPoint)
      throws ValidationException, ItemInvalidException {

    ValidationService s = new ValidationService();

    try {
      s.doValidation(itemVO, validationPoint);
    } catch (ValidationException e) {
      LOG.error("validateItemObject:", e);
      throw e;
    } catch (ItemInvalidException e) {
      throw e;
    } catch (Exception e) {
      LOG.error("validateItemObject: " + itemVO + validationPoint, e);
      throw new ValidationException("validateItemObject:", e);
    }

  }

  public void refreshValidationSchemaCache() throws ValidationConeCacheConfigException {

    try {
      ConeCache.getInstance().refreshCache();
    } catch (ValidationConeCacheConfigException e) {
      LOG.error("refreshValidationSchemaCache:", e);
      throw e;
    }

  }

}
