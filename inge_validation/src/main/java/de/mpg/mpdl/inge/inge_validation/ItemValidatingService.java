package de.mpg.mpdl.inge.inge_validation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationServiceException;
import de.mpg.mpdl.inge.inge_validation.util.ValidationPoint;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;

@Service
public class ItemValidatingService {
  private static final Logger logger = LogManager.getLogger(ItemValidatingService.class);

  public void validate(final ItemVersionVO itemVO, final ValidationPoint validationPoint)
      throws ValidationServiceException, ValidationException {

    try {
      Validation validation = new Validation();
      validation.validate(itemVO, validationPoint);
    } catch (final ValidationServiceException e) {
      logger.error("validate:", e);
      throw e;
    } catch (final ValidationException e) {
      throw e;
    } catch (final Exception e) {
      logger.error("validate: " + itemVO + validationPoint, e);
      throw new ValidationServiceException("validate:", e);
    }
  }

  //  public void validateYearbook(final ItemVersionVO itemVO, List<String> childsOfMPG)
  //      throws ValidationServiceException, ValidationException {
  //
  //    try {
  //      Validation validation = new Validation();
  //      validation.validateYearbook(itemVO, childsOfMPG);
  //    } catch (final ValidationServiceException e) {
  //      logger.error("validateYearbook:", e);
  //      throw e;
  //    } catch (final ValidationException e) {
  //      throw e;
  //    } catch (final Exception e) {
  //      logger.error("validateYearbook: " + itemVO, e);
  //      throw new ValidationServiceException("validateYearbook:", e);
  //    }
  //  }
}
