package de.mpg.mpdl.inge.inge_validation.validator.cone;

import java.util.List;
import java.util.Set;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.cone_cache.ConeCache;
import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
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

  public static final String ISO639_3 = "eterms:ISO639_3";
  public static final String DDC = "eterms:DDC";
  public static final String MPIPKS = "eterms:MPIPKS";
  public static final String MPIRG = "eterms:MPIRG";
  public static final String MPIS_GROUPS = "eterms:MPIS_GROUPS";
  public static final String MPIS_PROJECTS = "eterms:MPIS_PROJECTS";

  @Override
  public boolean validate(ValidatorContext context, List<SubjectVO> subjects) {

    boolean ok = true;

    if (subjects != null && subjects.isEmpty() == false) {

      final ConeCache coneCache = ConeCache.getInstance();

      final Set<String> iso639_3_TitleSet = coneCache.getIso639_3_TitleSet();
      final Set<String> ddcTitleSet = coneCache.getDdcTitleSet();
      final Set<String> mpipksTitleSet = coneCache.getMpipksTitleSet();
      final Set<String> mpirgTitleSet = coneCache.getMpirgTitleSet();
      final Set<String> mpisGroupsTitleSet = coneCache.getMpisGroupsTitleSet();
      final Set<String> mpisProjectsTitleSet = coneCache.getMpisProjectsTitleSet();

      int i = 1;
      for (final SubjectVO subjectVO : subjects) {

        if (ValidationTools.isNotEmpty(subjectVO.getType())
            && ValidationTools.isNotEmpty(subjectVO.getValue())) {

          if (ClassifiedKeywordsValidator.ISO639_3.equals(subjectVO.getType())) { //
            if (ValidationTools.isEmpty(iso639_3_TitleSet)) {
              context.addError(ValidationError.create(ErrorMessages.CONE_EMPTY_ISO639_3_TITLE));
              return false;
            }
            if (!iso639_3_TitleSet.contains(subjectVO.getValue())) {
              context.addError(ValidationError.create(ErrorMessages.INCORRECT_CLASSIFICATION)
                  .setField("subject[" + i + "]"));
              ok = false;
            }

          } else if (ClassifiedKeywordsValidator.DDC.equals(subjectVO.getType())) { //
            if (ValidationTools.isEmpty(ddcTitleSet)) {
              context.addError(ValidationError.create(ErrorMessages.CONE_EMPTY_DDC_TITLE));
              return false;
            }
            if (!ddcTitleSet.contains(subjectVO.getValue())) {
              context.addError(ValidationError.create(ErrorMessages.INCORRECT_DDC_CLASSIFICATION)
                  .setField("subject[" + i + "]"));
              ok = false;
            }

          } else if (ClassifiedKeywordsValidator.MPIPKS.equals(subjectVO.getType())) { //
            if (ValidationTools.isEmpty(mpipksTitleSet)) { //
              context.addError(ValidationError.create(ErrorMessages.CONE_EMPTY_MPIPKS_TITLE));
              return false;
            }
            if (!mpipksTitleSet.contains(subjectVO.getValue())) {
              context.addError(ValidationError
                  .create(ErrorMessages.INCORRECT_MPIPKS_CLASSIFICATION).setField(
                      "subject[" + i + "]"));
              ok = false;
            }

          } else if (ClassifiedKeywordsValidator.MPIRG.equals(subjectVO.getType())) { //
            if (ValidationTools.isEmpty(mpirgTitleSet)) {//
              context.addError(ValidationError.create(ErrorMessages.CONE_EMPTY_MPIRG_TITLE));
              return false;
            }
            if (!mpirgTitleSet.contains(subjectVO.getValue())) {
              context.addError(ValidationError.create(ErrorMessages.INCORRECT_CLASSIFICATION)
                  .setField("subject[" + i + "]"));
              ok = false;
            }

          } else if (ClassifiedKeywordsValidator.MPIS_GROUPS.equals(subjectVO.getType())) { //
            if (ValidationTools.isEmpty(mpisGroupsTitleSet)) { //
              context.addError(ValidationError.create(ErrorMessages.CONE_EMPTY_MPIS_GROUPS_TITLE));
              return false;
            }
            if (!mpisGroupsTitleSet.contains(subjectVO.getValue())) {
              context.addError(ValidationError.create(
                  ErrorMessages.INCORRECT_MPIS_GROUPS_CLASSIFICATION)
                  .setField("subject[" + i + "]"));
              ok = false;
            }

          } else if (ClassifiedKeywordsValidator.MPIS_PROJECTS.equals(subjectVO.getType())) { //
            if (ValidationTools.isEmpty(mpisProjectsTitleSet)) { //
              context
                  .addError(ValidationError.create(ErrorMessages.CONE_EMPTY_MPIS_PROJECTS_TITLE));
              return false;
            }
            if (!mpisProjectsTitleSet.contains(subjectVO.getValue())) {
              context.addError(ValidationError.create(
                  ErrorMessages.INCORRECT_MPIS_PROJECTS_CLASSIFICATION).setField(
                  "subject[" + i + "]"));
              ok = false;
            }
          }

        } // if

        i++;
      } // for

    } // if

    return ok;
  }

}
