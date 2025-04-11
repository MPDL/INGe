package de.mpg.mpdl.inge.inge_validation;

import com.baidu.unbiz.fluentvalidator.ComplexResult;
import com.baidu.unbiz.fluentvalidator.FluentValidator;
import com.baidu.unbiz.fluentvalidator.ResultCollectors;
import com.baidu.unbiz.fluentvalidator.ValidationError;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportItemVO;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportVO;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationServiceException;
import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationPoint;
import de.mpg.mpdl.inge.inge_validation.validator.EventTitleRequiredValidator;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.EventVO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

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

  public ValidationReportVO validateEventTitleRequired(EventVO eventVO) {
    FluentValidator validator = FluentValidator.checkAll().failOver().on(eventVO, new EventTitleRequiredValidator());

    ComplexResult complexResult = validator.doValidate().result(ResultCollectors.toComplex());
    ValidationReportVO validationReportVO = parseResult(complexResult);

    return validationReportVO;
  }

  private ValidationReportVO parseResult(ComplexResult complexResult) {
    ValidationReportVO v = new ValidationReportVO();

    if (!complexResult.isSuccess()) {
      for (ValidationError error : complexResult.getErrors()) {
        ValidationReportItemVO item = new ValidationReportItemVO(error.getErrorMsg(),
            (ErrorMessages.WARNING == error.getErrorCode() ? ValidationReportItemVO.Severity.WARNING
                : ValidationReportItemVO.Severity.ERROR));
        item.setElement(error.getField());
        v.addItem(item);
      }
    }

    return v;
  }
}
