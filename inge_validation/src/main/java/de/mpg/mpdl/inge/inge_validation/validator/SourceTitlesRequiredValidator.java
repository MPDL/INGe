<<<<<<< HEAD
package de.mpg.mpdl.inge.inge_validation.validator;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;

/*
 * <!-- if any fields at "Source" are filled, "Title" of the source has to be filled also. -->
 * <?rule file: rule1: none escidoc:file-name (matches '.pdf$') else report PdfIsNotAllowed?>
 * <iso:pattern name="source_title_required" id="source_title_required"> <iso:rule
 * context="source:source"> <iso:assert test="dc:title != '' or not(normalize-space(.) != '')">
 * SourceTitleNotProvided</iso:assert> </iso:rule> </iso:pattern>
 */

public class SourceTitlesRequiredValidator extends ValidatorHandler<List<SourceVO>> implements
    Validator<List<SourceVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<SourceVO> sources) {

    boolean ok = true;

    if (sources != null && !sources.isEmpty()) {

      int i = 1;
      for (SourceVO sourceVO : sources) {

        if (sourceVO.getTitle() == null && //
            (sourceVO.getAlternativeTitles() != null && !sourceVO.getAlternativeTitles().isEmpty() //
                || sourceVO.getCreators() != null && !sourceVO.getCreators().isEmpty() //
                || sourceVO.getDatePublishedInPrint() != null //
                || sourceVO.getEndPage() != null //
                || sourceVO.getGenre() != null //
                || sourceVO.getIdentifiers() != null && !sourceVO.getIdentifiers().isEmpty() //
                || sourceVO.getIssue() != null //
                || sourceVO.getPublishingInfo() != null //
                || sourceVO.getSequenceNumber() != null //
                || sourceVO.getSources() != null && !sourceVO.getSources().isEmpty() //
                || sourceVO.getStartPage() != null //
                || sourceVO.getTotalNumberOfPages() != null //
            || sourceVO.getVolume() != null)) {
          context.addError(ValidationError.create(ErrorMessages.SOURCE_TITLE_NOT_PROVIDED)
              .setField("source[" + i + "]"));
          ok = false;
        }

        i++;
      } // for

    } // if

    return ok;
  }

}
=======
package de.mpg.mpdl.inge.inge_validation.validator;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;

/*
 * <!-- if any fields at "Source" are filled, "Title" of the source has to be filled also. -->
 * <?rule file: rule1: none escidoc:file-name (matches '.pdf$') else report PdfIsNotAllowed?>
 * <iso:pattern name="source_title_required" id="source_title_required"> <iso:rule
 * context="source:source"> <iso:assert test="dc:title != '' or not(normalize-space(.) != '')">
 * SourceTitleNotProvided</iso:assert> </iso:rule> </iso:pattern>
 */

public class SourceTitlesRequiredValidator extends ValidatorHandler<List<SourceVO>>
    implements Validator<List<SourceVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<SourceVO> sources) {

    boolean ok = true;

    if (sources != null && !sources.isEmpty()) {

      int i = 1;
      for (SourceVO sourceVO : sources) {

        if (sourceVO.getTitle() == null && //
            (sourceVO.getAlternativeTitles() != null && !sourceVO.getAlternativeTitles().isEmpty() //
                || sourceVO.getCreators() != null && !sourceVO.getCreators().isEmpty() //
                || sourceVO.getDatePublishedInPrint() != null //
                || sourceVO.getEndPage() != null //
                || sourceVO.getGenre() != null //
                || sourceVO.getIdentifiers() != null && !sourceVO.getIdentifiers().isEmpty() //
                || sourceVO.getIssue() != null //
                || sourceVO.getPublishingInfo() != null //
                || sourceVO.getSequenceNumber() != null //
                || sourceVO.getSources() != null && !sourceVO.getSources().isEmpty() //
                || sourceVO.getStartPage() != null //
                || sourceVO.getTotalNumberOfPages() != null //
                || sourceVO.getVolume() != null)) {
          context.addError(ValidationError.create(ErrorMessages.SOURCE_TITLE_NOT_PROVIDED)
              .setField("source[" + i + "]"));
          ok = false;
        }

        i++;
      } // for

    } // if

    return ok;
  }

}
>>>>>>> branch 'master' of https://github.com/MPDL/INGe.git
