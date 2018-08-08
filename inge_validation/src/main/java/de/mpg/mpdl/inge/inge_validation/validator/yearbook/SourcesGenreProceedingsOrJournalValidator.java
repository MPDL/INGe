package de.mpg.mpdl.inge.inge_validation.validator.yearbook;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.Validation;
import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;

public class SourcesGenreProceedingsOrJournalValidator extends ValidatorHandler<List<SourceVO>> implements Validator<List<SourceVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<SourceVO> sources) {

    boolean ok = true;

    if (ValidationTools.isNotEmpty(sources)) {

      int i = 1;
      for (final SourceVO sourceVO : sources) {

        if (sourceVO != null) {

          if (!SourceVO.Genre.PROCEEDINGS.equals(sourceVO.getGenre()) //
              && !SourceVO.Genre.JOURNAL.equals(sourceVO.getGenre())) {

            context.addError(ValidationError.create(ErrorMessages.SOURCE_GENRE_MUST_BE_PROCCEDINGS_OR_JOURNAL).setField("source[" + i + "]")
                .setErrorCode(Validation.WARNING));

            ok = false;

          } // if

        } // if

        i++;
      } // for

    } // if

    return ok;
  }

}
