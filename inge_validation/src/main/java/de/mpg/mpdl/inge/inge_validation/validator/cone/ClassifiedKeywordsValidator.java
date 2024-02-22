package de.mpg.mpdl.inge.inge_validation.validator.cone;

import java.util.List;
import java.util.Set;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.cone_cache.ConeCache;
import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SubjectVO;
import de.mpg.mpdl.inge.util.PropertyReader;

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

public class ClassifiedKeywordsValidator extends ValidatorHandler<List<SubjectVO>> {
  public static final String DDC = "DDC";
  public static final String ISO639_3 = "ISO639_3";
  public static final String JEL = "JEL";
  public static final String MPICC_PROJECTS = "MPICC_PROJECTS";
  public static final String MPINP = "MPINP";
  public static final String MPIPKS = "MPIPKS";
  public static final String MPIRG = "MPIRG";
  public static final String MPIS_GROUPS = "MPIS_GROUPS";
  public static final String MPIS_PROJECTS = "MPIS_PROJECTS";
  public static final String MPIWG_PROJECTS = "MPIWG_PROJECTS";

  @Override
  public boolean validate(ValidatorContext context, List<SubjectVO> subjects) {

    boolean ok = true;

    if ("true".equalsIgnoreCase(PropertyReader.getProperty(PropertyReader.INGE_CONE_CACHE_USE))) {

      if (null != subjects && !subjects.isEmpty()) {

        ConeCache coneCache = ConeCache.getInstance();

        Set<String> ddcTitleSet = coneCache.getDdcTitleSet();
        Set<String> iso639_3_TitleSet = coneCache.getIso639_3_TitleSet();
        Set<String> jelTitleSet = coneCache.getJelTitleSet();
        Set<String> mpiccProjectsTitleSet = coneCache.getMpiccProjectsTitleSet();
        Set<String> mpinpTitleSet = coneCache.getMpinpTitleSet();
        Set<String> mpipksTitleSet = coneCache.getMpipksTitleSet();
        Set<String> mpirgTitleSet = coneCache.getMpirgTitleSet();
        Set<String> mpisGroupsTitleSet = coneCache.getMpisGroupsTitleSet();
        Set<String> mpisProjectsTitleSet = coneCache.getMpisProjectsTitleSet();
        Set<String> mpiwgProjectsTitleSet = coneCache.getMpiwgProjectsTitleSet();

        int i = 1;
        for (SubjectVO subjectVO : subjects) {

          if (ValidationTools.isNotEmpty(subjectVO.getType()) && ValidationTools.isNotEmpty(subjectVO.getValue())) {

            if (ClassifiedKeywordsValidator.DDC.equals(subjectVO.getType())) { //
              if (ValidationTools.isEmpty(ddcTitleSet)) {
                context.addErrorMsg(ErrorMessages.CONE_EMPTY_DDC_TITLE);
                ok = false;
              } else if (!ddcTitleSet.contains(subjectVO.getValue())) {
                context.addError(ValidationError.create(ErrorMessages.INCORRECT_DDC_CLASSIFICATION).setField("subject[" + i + "]"));
                ok = false;
              }

            } else if (ClassifiedKeywordsValidator.ISO639_3.equals(subjectVO.getType())) { //
              if (ValidationTools.isEmpty(iso639_3_TitleSet)) {
                context.addErrorMsg(ErrorMessages.CONE_EMPTY_ISO639_3_TITLE);
                ok = false;
              } else if (!iso639_3_TitleSet.contains(subjectVO.getValue())) {
                context.addError(ValidationError.create(ErrorMessages.INCORRECT_ISO639_3_CLASSIFICATION).setField("subject[" + i + "]"));
                ok = false;
              }

            } else if (ClassifiedKeywordsValidator.JEL.equals(subjectVO.getType())) { //
              if (ValidationTools.isEmpty(jelTitleSet)) {
                context.addErrorMsg(ErrorMessages.CONE_EMPTY_JEL_TITLE);
                ok = false;
              } else if (!jelTitleSet.contains(subjectVO.getValue())) {
                context.addError(ValidationError.create(ErrorMessages.INCORRECT_JEL_CLASSIFICATION).setField("subject[" + i + "]"));
                ok = false;
              }

            } else if (ClassifiedKeywordsValidator.MPICC_PROJECTS.equals(subjectVO.getType())) { //
              if (ValidationTools.isEmpty(mpiccProjectsTitleSet)) { //
                context.addErrorMsg(ErrorMessages.CONE_EMPTY_MPICC_PROJECTS_TITLE);
                ok = false;
              } else if (!mpiccProjectsTitleSet.contains(subjectVO.getValue())) {
                context
                    .addError(ValidationError.create(ErrorMessages.INCORRECT_MPICC_PROJECTS_CLASSIFICATION).setField("subject[" + i + "]"));
                ok = false;
              }

            } else if (ClassifiedKeywordsValidator.MPIPKS.equals(subjectVO.getType())) { //
              if (ValidationTools.isEmpty(mpipksTitleSet)) { //
                context.addErrorMsg(ErrorMessages.CONE_EMPTY_MPIPKS_TITLE);
                ok = false;
              } else if (!mpipksTitleSet.contains(subjectVO.getValue())) {
                context.addError(ValidationError.create(ErrorMessages.INCORRECT_MPIPKS_CLASSIFICATION).setField("subject[" + i + "]"));
                ok = false;
              }

            } else if (ClassifiedKeywordsValidator.MPINP.equals(subjectVO.getType())) { //
              if (ValidationTools.isEmpty(mpinpTitleSet)) {//
                context.addErrorMsg(ErrorMessages.CONE_EMPTY_MPINP_TITLE);
                ok = false;
              } else if (!mpinpTitleSet.contains(subjectVO.getValue())) {
                context.addError(ValidationError.create(ErrorMessages.INCORRECT_MPINP_CLASSIFICATION).setField("subject[" + i + "]"));
                ok = false;
              }

            } else if (ClassifiedKeywordsValidator.MPIRG.equals(subjectVO.getType())) { //
              if (ValidationTools.isEmpty(mpirgTitleSet)) {//
                context.addErrorMsg(ErrorMessages.CONE_EMPTY_MPIRG_TITLE);
                ok = false;
              } else if (!mpirgTitleSet.contains(subjectVO.getValue())) {
                context.addError(ValidationError.create(ErrorMessages.INCORRECT_MPIRG_CLASSIFICATION).setField("subject[" + i + "]"));
                ok = false;
              }

            } else if (ClassifiedKeywordsValidator.MPIS_GROUPS.equals(subjectVO.getType())) { //
              if (ValidationTools.isEmpty(mpisGroupsTitleSet)) { //
                context.addErrorMsg(ErrorMessages.CONE_EMPTY_MPIS_GROUPS_TITLE);
                ok = false;
              } else if (!mpisGroupsTitleSet.contains(subjectVO.getValue())) {
                context.addError(ValidationError.create(ErrorMessages.INCORRECT_MPIS_GROUPS_CLASSIFICATION).setField("subject[" + i + "]"));
                ok = false;
              }

            } else if (ClassifiedKeywordsValidator.MPIS_PROJECTS.equals(subjectVO.getType())) { //
              if (ValidationTools.isEmpty(mpisProjectsTitleSet)) { //
                context.addErrorMsg(ErrorMessages.CONE_EMPTY_MPIS_PROJECTS_TITLE);
                ok = false;
              } else if (!mpisProjectsTitleSet.contains(subjectVO.getValue())) {
                context
                    .addError(ValidationError.create(ErrorMessages.INCORRECT_MPIS_PROJECTS_CLASSIFICATION).setField("subject[" + i + "]"));
                ok = false;
              }

            } else if (ClassifiedKeywordsValidator.MPIWG_PROJECTS.equals(subjectVO.getType())) { //
              if (ValidationTools.isEmpty(mpiwgProjectsTitleSet)) { //
                context.addErrorMsg(ErrorMessages.CONE_EMPTY_MPIWG_PROJECTS_TITLE);
                ok = false;
              } else if (!mpiwgProjectsTitleSet.contains(subjectVO.getValue())) {
                context
                    .addError(ValidationError.create(ErrorMessages.INCORRECT_MPIWG_PROJECTS_CLASSIFICATION).setField("subject[" + i + "]"));
                ok = false;
              }
            }

          } // if

          i++;
        } // for

      } // if

    } // if

    return ok;
  }

}
