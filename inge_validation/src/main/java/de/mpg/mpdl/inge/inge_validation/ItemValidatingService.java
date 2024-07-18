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

  public void validate(ItemVersionVO itemVO, ValidationPoint validationPoint) throws ValidationServiceException, ValidationException {

    try {
      Validation validation = new Validation();
      validation.validate(itemVO, validationPoint);
    } catch (ValidationServiceException e) {
      logger.error("validate:", e);
      throw e;
    } catch (ValidationException e) {
      throw e;
    } catch (Exception e) {
      logger.error("validate: " + itemVO + " " + validationPoint, e);
      throw new ValidationServiceException("validate:", e);
    }
  }
}
