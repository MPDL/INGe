package de.mpg.mpdl.inge.inge_validation.validator;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;

/*
 * <!-- if any fields at "Source" are filled, "Title" of the source has to be filled also. -->
 * <?rule file: rule1: none escidoc:file-name (matches '.pdf$') else report PdfIsNotAllowed?>
 * <iso:pattern name="source_title_required" id="source_title_required"> <iso:rule
 * context="source:source"> <iso:assert test="dc:title != '' or not(normalize-space(.) != '')">
 * SourceTitleNotProvided</iso:assert> </iso:rule> </iso:pattern>
 */

public class SourceTitleRequiredValidator extends ValidatorHandler<List<SourceVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<SourceVO> sources) {

    boolean ok = true;

    if (ValidationTools.isNotEmpty(sources)) {

      int i = 1;
      for (SourceVO sourceVO : sources) {

        if (null != sourceVO) {
          if (ValidationTools.isEmpty(sourceVO.getTitle()) && //
              (ValidationTools.isNotEmpty(sourceVO.getAlternativeTitles()) //
                  || ValidationTools.isNotEmpty(sourceVO.getCreators()) //
                  || null != sourceVO.getDatePublishedInPrint() //
                  || ValidationTools.isNotEmpty(sourceVO.getEndPage()) //
                  || null != sourceVO.getGenre() //
                  || ValidationTools.isNotEmpty(sourceVO.getIdentifiers()) //
                  || ValidationTools.isNotEmpty(sourceVO.getIssue()) //
                  || null != sourceVO.getPublishingInfo() //
                  || ValidationTools.isNotEmpty(sourceVO.getSequenceNumber()) //
                  || ValidationTools.isNotEmpty(sourceVO.getSources()) //
                  || ValidationTools.isNotEmpty(sourceVO.getStartPage()) //
                  || ValidationTools.isNotEmpty(sourceVO.getTotalNumberOfPages()) //
                  || ValidationTools.isNotEmpty(sourceVO.getVolume()))) {
            context.addError(ValidationError.create(ErrorMessages.SOURCE_TITLE_NOT_PROVIDED).setField("source[" + i + "]"));
            ok = false;
          }
        }

        i++;
      } // for

    } // if

    return ok;
  }

}
