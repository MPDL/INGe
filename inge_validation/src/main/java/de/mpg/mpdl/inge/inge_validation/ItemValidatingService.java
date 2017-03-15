package de.mpg.mpdl.inge.inge_validation;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationConeCacheConfigException;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.util.ValidationPoint;
import de.mpg.mpdl.inge.inge_validation.validator.cone.ConeCache;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;

public class ItemValidatingService {
  private static final Logger LOG = Logger.getLogger(ItemValidatingService.class);

  public static void validateItemObject(final ItemVO itemVO, final ValidationPoint validationPoint)
      throws ValidationException, ItemInvalidException {

    try {
      Validation.doValidation(itemVO, validationPoint);
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

  public static void refreshValidationSchemaCache() throws ValidationConeCacheConfigException {
    try {
      ConeCache.getInstance().refreshCache();
    } catch (ValidationConeCacheConfigException e) {
      LOG.error("refreshValidationSchemaCache:", e);
      throw e;
    }
  }
}
