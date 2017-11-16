package de.mpg.mpdl.inge.inge_validation;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationServiceException;
import de.mpg.mpdl.inge.inge_validation.util.ValidationPoint;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;

@Service
public class ItemValidatingService {
  private static final Logger logger = Logger.getLogger(ItemValidatingService.class);

  public void validate(final ItemVO itemVO, final ValidationPoint validationPoint)
      throws ValidationServiceException, ValidationException {

    try {
      Validation.validate(itemVO, validationPoint);
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

  public void validateYearbook(final ItemVO itemVO) throws ValidationServiceException,
      ValidationException {

    try {
      Validation.validateYearbook(itemVO);
    } catch (final ValidationServiceException e) {
      logger.error("validateYearbook:", e);
      throw e;
    } catch (final ValidationException e) {
      throw e;
    } catch (final Exception e) {
      logger.error("validateYearbook: " + itemVO, e);
      throw new ValidationServiceException("validateYearbook:", e);
    }
  }
}
