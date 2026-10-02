package de.mpg.mpdl.inge.inge_validation;

import com.baidu.unbiz.fluentvalidator.ComplexResult;
import com.baidu.unbiz.fluentvalidator.FluentValidator;
import com.baidu.unbiz.fluentvalidator.ResultCollectors;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportVO;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.validator.orcid.ConeCheckValidator;
import de.mpg.mpdl.inge.inge_validation.validator.orcid.ConeIdAuthorValidator;
import de.mpg.mpdl.inge.inge_validation.validator.orcid.EmailAuthorValidator;
import de.mpg.mpdl.inge.inge_validation.validator.orcid.EmailBiboValidator;
import de.mpg.mpdl.inge.inge_validation.validator.orcid.NameAuthorValidator;
import de.mpg.mpdl.inge.inge_validation.validator.orcid.OrcidValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class OrcidValidatingService {
  private static final Logger logger = LogManager.getLogger(OrcidValidatingService.class);

  public void validate(String coneIdAuthor, String orcidAuthor, String nameAuthor, String emailBibo, String emailAuthor)
      throws ValidationException {
    validateOrcid(coneIdAuthor, orcidAuthor, nameAuthor, emailBibo, emailAuthor);
  }

  private void validateOrcid(String coneIdAuthor, String orcidAuthor, String nameAuthor, String emailBibo, String emailAuthor)
      throws ValidationException {

    FluentValidator validator = FluentValidator.checkAll().failOver() //
        .on(coneIdAuthor, new ConeIdAuthorValidator()) //
        .on(orcidAuthor, new OrcidValidator()) //
        .on(nameAuthor, new NameAuthorValidator()) //
        .on(emailBibo, new EmailBiboValidator()) //
        .on(emailAuthor, new EmailAuthorValidator()) //
        .on(coneIdAuthor, new ConeCheckValidator(nameAuthor, orcidAuthor)); //

    ComplexResult complexResult = validator.doValidate().result(ResultCollectors.toComplex());

    if (!complexResult.isSuccess()) {
      ValidationReportVO v = Validation.getValidationReportVO(complexResult);

      logger.warn(complexResult);

      throw new ValidationException(v);
    }
  }

}
