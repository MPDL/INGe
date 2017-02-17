package de.mpg.mpdl.inge.inge_validation.validator;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;

/*
 * <!-- If genre is equal to "Article", "Book Chapter" or "Conference Paper" at least one source has
 * to be provided --> <iso:pattern name="source_required" id="source_required" flag="restrictive">
 * <iso:rule context="publication:publication"> <iso:assert test="not(@type =
 * 'http://purl.org/escidoc/metadata/ves/publication-types/article' or @type =
 * 'http://purl.org/eprint/type/BookItem' or @type = 'http://purl.org/eprint/type/ConferencePaper'
 * or @type = 'http://purl.org/escidoc/metadata/ves/publication-types/meeting-abstract') or
 * (source:source/dc:title != '')"> SourceNotProvided</iso:assert> </iso:rule> </iso:pattern>
 */

public class SourceRequiredValidator extends ValidatorHandler<List<SourceVO>> implements
    Validator<List<SourceVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<SourceVO> sources) {

    if (sources == null || sources.isEmpty()) {
      context.addErrorMsg(ErrorMessages.SOURCE_NOT_PROVIDED);
      return false;
    }

    return true;
  }

}
