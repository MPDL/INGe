package de.mpg.mpdl.inge.inge_validation.validator.yearbook;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;

/*
 * <iso:report test="not((source:source[1]/escidoc:sequence-number != '' or
 * source:source[2]/escidoc:sequence-number != '') or ((source:source[1]/escidoc:start-page != '' or
 * source:source[2]/escidoc:start-page != '') and (source:source[1]/escidoc:end-page != '' or
 * source:source[2]/escidoc:end-page != '')))">NoSequenceInformationGiven</iso:report>
 */

public class SequenceInfomationValidator extends ValidatorHandler<List<SourceVO>> implements
    Validator<List<SourceVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<SourceVO> sources) {

    boolean ok = true;

    if (ValidationTools.isNotEmpty(sources)) {

      int i = 1;
      for (final SourceVO sourceVO : sources) {

        if (sourceVO != null) {
          if (ValidationTools.isEmpty(sourceVO.getSequenceNumber())
              || (ValidationTools.isEmpty(sourceVO.getStartPage()) && ValidationTools
                  .isEmpty(sourceVO.getEndPage()))) {
            context.addError(ValidationError.create(ErrorMessages.NO_SEQUENCE_INFORMATION_GIVEN)
                .setField("source[" + i + "]"));
            ok = false;
          }
        }

        i++;
      } // for

    } // if

    return ok;
  }

}
