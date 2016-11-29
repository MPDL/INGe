package de.mpg.mpdl.inge.inge_validation.validator;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ConeSetsCache;
import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SubjectVO;

/*
 * <!-- subjects should be from the matching vocabulary --> <iso:pattern name="classified_keywords"
 * id="classified_keywords"> <iso:rule context="publication:publication/dc:subject"> <iso:assert
 * test=
 * "not(exists($ddcSubjects/var/rdf:RDF)) or . = '' or @xsi:type != 'eterms:DDC' or . = $ddcSubjects/var/rdf:RDF/rdf:Description/dc:title"
 * > IncorrectDDCClassification</iso:assert> <iso:assert test=
 * "not(exists($mpipksSubjects/var/rdf:RDF)) or . = '' or @xsi:type != 'eterms:MPIPKS' or . = $mpipksSubjects/var/rdf:RDF/rdf:Description/dc:title"
 * > IncorrectMPIPKSClassification</iso:assert> <iso:assert test=
 * "not(exists($mpirgSubjects/var/rdf:RDF)) or . = '' or @xsi:type != 'eterms:MPIRG' or . = $mpirgSubjects/var/rdf:RDF/rdf:Description/dc:title"
 * > IncorrectClassification</iso:assert> <iso:assert test=
 * "not(exists($mpis-groups/var/rdf:RDF)) or . = '' or @xsi:type != 'eterms:MPIS_GROUPS' or . = $mpis-groups/var/rdf:RDF/rdf:Description/dc:title"
 * > IncorrectMPISGroupsClassification</iso:assert> <iso:assert test=
 * "not(exists($mpis-projects/var/rdf:RDF)) or . = '' or @xsi:type != 'eterms:MPIS_PROJECTS' or . = $mpis-projects/var/rdf:RDF/rdf:Description/dc:title"
 * > IncorrectMPISProjectsClassification</iso:assert> <iso:assert test=
 * "not(exists($iso639-3/var/rdf:RDF)) or . = '' or @xsi:type != 'eterms:ISO639_3' or . = $iso639-3/var/rdf:RDF/rdf:Description/dc:title"
 * > IncorrectClassification</iso:assert> </iso:rule> </iso:pattern>
 */

public class ClassifiedKeywordsValidator extends ValidatorHandler<List<SubjectVO>> implements
    Validator<List<SubjectVO>> {

  private final static String ISO639_3 = "eterms:ISO639_3";
  private final static String DDC = "eterms:DDC";
  private final static String MPIPKS = "eterms:MPIPKS";
  private final static String MPIRG = "eterms:MPIRG";
  private final static String MPIS_GROUPS = "eterms:MPIS_GROUPS";
  private final static String MPIS_PROJECTS = "eterms:MPIS_PROJECTS";

  @Override
  public boolean validate(ValidatorContext context, List<SubjectVO> subjects) {

    boolean ok = true;

    if (subjects != null) {

      ConeSetsCache coneSets = ConeSetsCache.getInstance();

      int i = 1;
      for (SubjectVO subjectVO : subjects) {

        if (subjectVO.getType() != null && subjectVO.getValue() != null) {

          if (ISO639_3.equals(subjectVO.getType()) //
              && !coneSets.getIso639_3_TitleSet().isEmpty()
              && coneSets.getIso639_3_TitleSet().contains(subjectVO.getValue())) {
            context.addError(ValidationError.create(ErrorMessages.INCORRECT_CLASSIFICATION)
                .setField("subject[" + i + "]"));
            ok = false;

          } else if (DDC.equals(subjectVO.getType()) //
              && !coneSets.getDdcTitleSet().isEmpty()
              && coneSets.getDdcTitleSet().contains(subjectVO.getValue())) {
            context.addError(ValidationError.create(ErrorMessages.INCORRECT_DDC_CLASSIFICATION)
                .setField("subject[" + i + "]"));
            ok = false;

          } else if (MPIPKS.equals(subjectVO.getType()) //
              && !coneSets.getMpipksTitleSet().isEmpty()
              && coneSets.getMpipksTitleSet().contains(subjectVO.getValue())) {
            context.addError(ValidationError.create(ErrorMessages.INCORRECT_MPIPKS_CLASSIFICATION)
                .setField("subject[" + i + "]"));
            ok = false;

          } else if (MPIRG.equals(subjectVO.getType()) //
              && !coneSets.getMpirgTitleSet().isEmpty()
              && coneSets.getMpirgTitleSet().contains(subjectVO.getValue())) {
            context.addError(ValidationError.create(ErrorMessages.INCORRECT_CLASSIFICATION)
                .setField("subject[" + i + "]"));
            ok = false;

          } else if (MPIS_GROUPS.equals(subjectVO.getType()) //
              && !coneSets.getMpisGroupsTitleSet().isEmpty()
              && coneSets.getMpisGroupsTitleSet().contains(subjectVO.getValue())) {
            context.addError(ValidationError.create(
                ErrorMessages.INCORRECT_MPIS_GROUPS_CLASSIFICATION).setField("subject[" + i + "]"));
            ok = false;

          } else if (MPIS_PROJECTS.equals(subjectVO.getType()) //
              && !coneSets.getMpisProjectTitleSet().isEmpty()
              && coneSets.getMpisProjectTitleSet().contains(subjectVO.getValue())) {
            context.addError(ValidationError.create(
                ErrorMessages.INCORRECT_MPIS_PROJECTS_CLASSIFICATION)
                .setField("subject[" + i + "]"));
            ok = false;
          }

        } // if

        i++;
      } // for

    } // if

    return ok;
  }

}
