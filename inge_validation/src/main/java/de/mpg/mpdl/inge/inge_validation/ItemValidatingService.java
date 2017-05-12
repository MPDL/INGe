package de.mpg.mpdl.inge.inge_validation;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationConeCacheConfigException;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.util.ValidationPoint;
import de.mpg.mpdl.inge.inge_validation.validator.cone.ConeCache;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;

public class ItemValidatingService {
  private static final Logger logger = Logger.getLogger(ItemValidatingService.class);

  public static void validate(final ItemVO itemVO, final ValidationPoint validationPoint)
      throws ValidationException, ItemInvalidException {

    try {
      Validation.validate(itemVO, validationPoint);
    } catch (final ValidationException e) {
      logger.error("validateItemObject:", e);
      throw e;
    } catch (final ItemInvalidException e) {
      throw e;
    } catch (final Exception e) {
      logger.error("validateItemObject: " + itemVO + validationPoint, e);
      throw new ValidationException("validateItemObject:", e);
    }
  }

  public static void refreshValidationSchemaCache() throws ValidationConeCacheConfigException {
    try {
      ConeCache.getInstance().refreshCache();
    } catch (final ValidationConeCacheConfigException e) {
      logger.error("refreshValidationSchemaCache:", e);
      throw e;
    }
  }
}
