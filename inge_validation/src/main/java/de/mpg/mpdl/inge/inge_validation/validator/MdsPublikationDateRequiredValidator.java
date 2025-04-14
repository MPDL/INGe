package de.mpg.mpdl.inge.inge_validation.validator;

import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;

/*
 * <!-- If genre is not equal to "Series" or "Journal" or "Other" or "Manuscript" at least one date
 * has to be provided --> <iso:pattern name="date_required" id="date_required"> <iso:rule
 * context="publication:publication"> <iso:assert test="(@type =
 * 'http://purl.org/escidoc/metadata/ves/publication-types/series' or @type =
 * 'http://purl.org/escidoc/metadata/ves/publication-types/journal' or @type =
 * 'http://purl.org/escidoc/metadata/ves/publication-types/manuscript' or @type =
 * 'http://purl.org/escidoc/metadata/ves/publication-types/other') or ((dcterms:created != '') or
 * (dcterms:modified != '') or (dcterms:dateSubmitted != '') or (dcterms:dateAccepted != '') or
 * (dcterms:issued != '') or (escidoc:published-online != '') or (escidoc:published-online != ''))">
 * DateNotProvided</iso:assert> </iso:rule> </iso:pattern>
 */

public class MdsPublikationDateRequiredValidator extends ValidatorHandler<MdsPublicationVO> {

  @Override
  public boolean validate(ValidatorContext context, MdsPublicationVO m) {

    if (MdsPublicationVO.Genre.SERIES.equals(m.getGenre()) //
        || MdsPublicationVO.Genre.JOURNAL.equals(m.getGenre()) //
        || MdsPublicationVO.Genre.MANUSCRIPT.equals(m.getGenre()) //
        || MdsPublicationVO.Genre.OTHER.equals(m.getGenre())) {
      return true;
    }

    if (ValidationTools.isEmpty(m.getDateAccepted()) //
        && ValidationTools.isEmpty(m.getDateCreated()) //
        && ValidationTools.isEmpty(m.getDateModified()) //
        && ValidationTools.isEmpty(m.getDatePublishedInPrint()) //
        && ValidationTools.isEmpty(m.getDatePublishedOnline()) //
        && ValidationTools.isEmpty(m.getDateSubmitted())) {

      if ((MdsPublicationVO.Genre.COURSEWARE_LECTURE.equals(m.getGenre()) //
          || MdsPublicationVO.Genre.TALK_AT_EVENT.equals(m.getGenre()) //
          || MdsPublicationVO.Genre.POSTER.equals(m.getGenre()))
          && (null != m.getEvent() && ValidationTools.isNotEmpty(m.getEvent().getStartDate()))) {
        return true;
      }

      context.addErrorMsg(ErrorMessages.DATE_NOT_PROVIDED);
      return false;
    }

    return true;
  }

}
