package de.mpg.mpdl.inge.inge_validation.validator.OLDyearbook;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;

public class SourcesGenreProceedingsOrJournalValidator extends ValidatorHandler<List<SourceVO>> implements Validator<List<SourceVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<SourceVO> sources) {

    boolean ok = true;

    int countNotOk = 0;

    if (ValidationTools.isNotEmpty(sources)) {

      for (SourceVO sourceVO : sources) {

        if (null != sourceVO) {

          if (!SourceVO.Genre.PROCEEDINGS.equals(sourceVO.getGenre()) //
              && !SourceVO.Genre.JOURNAL.equals(sourceVO.getGenre())) {

            ok = false;
            countNotOk++;

          } // if

        } // if

      } // for

      if (sources.size() == countNotOk) {
        context.addError(
            ValidationError.create(ErrorMessages.SOURCE_GENRE_MUST_BE_PROCCEDINGS_OR_JOURNAL).setErrorCode(ErrorMessages.WARNING));
        ok = false;
      } else {
        ok = true;
      }

    } else {
      context
          .addError(ValidationError.create(ErrorMessages.SOURCE_GENRE_MUST_BE_PROCCEDINGS_OR_JOURNAL).setErrorCode(ErrorMessages.WARNING));
      ok = false;

    } // if

    return ok;
  }

}
