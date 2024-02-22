package de.mpg.mpdl.inge.inge_validation.validator;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;

/*
 * If genre is equal to "Article" "Book Chapter" "Conference Paper" "Magazine Article"
 * "Meeting Abstract" "Review Article" at least one source has to be provided
 */

public class SourceRequiredValidator extends ValidatorHandler<List<SourceVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<SourceVO> sources) {

    if (ValidationTools.isEmpty(sources)) {
      context.addErrorMsg(ErrorMessages.SOURCE_NOT_PROVIDED);
      return false;
    }

    return true;
  }

}
