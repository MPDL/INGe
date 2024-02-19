package de.mpg.mpdl.inge.inge_validation.validator;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;

/*
 * <!-- If source name is given, the source genre has to be given, too --> <iso:pattern
 * name="source_genre_required" id="source_genre_required" flag="restrictive"> <iso:rule
 * context="publication:publication/source:source"> <iso:assert test="not(dc:title != '') or (@type
 * and @type != '')"> SourceGenreNotProvided</iso:assert> </iso:rule> </iso:pattern>
 */

public class SourcesGenreRequiredValidator extends ValidatorHandler<List<SourceVO>> implements Validator<List<SourceVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<SourceVO> sources) {

    boolean ok = true;

    if (ValidationTools.isNotEmpty(sources)) {

      int i = 1;
      for (final SourceVO sourceVO : sources) {

        if (null != sourceVO) {
          if (ValidationTools.isNotEmpty(sourceVO.getTitle()) //
              && null == sourceVO.getGenre()) {
            context.addError(ValidationError.create(ErrorMessages.SOURCE_GENRE_NOT_PROVIDED).setField("source[" + i + "]"));
            ok = false;
          }
        }

        i++;
      } // for

    } // if

    return ok;
  }

}
