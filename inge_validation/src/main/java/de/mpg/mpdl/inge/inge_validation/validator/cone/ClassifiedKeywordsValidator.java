package de.mpg.mpdl.inge.inge_validation.validator.cone;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

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
    if (!"true".equalsIgnoreCase(PropertyReader.getProperty(PropertyReader.INGE_CONE_CACHE_USE))) {
      return true;
    }

    if (ValidationTools.isEmpty(subjects)) {
      return true;
    }

    Vocabularies vocabularies = new Vocabularies(ConeCache.getInstance());

    return IntStream.range(0, subjects.size()).mapToObj(i -> validateSubject(context, subjects.get(i), i + 1, vocabularies)).reduce(true,
        (a, b) -> a && b);
  }

  private boolean validateSubject(ValidatorContext context, SubjectVO subjectVO, int index, Vocabularies vocabularies) {
    String type = subjectVO.getType();
    String value = subjectVO.getValue();
    String fieldName = "subject[" + index + "]";

    if (ValidationTools.isNotEmpty(type) && ValidationTools.isEmpty(value)) {
      context.addError(ValidationError.create(ErrorMessages.CLASSIFICATION_VALUE_NOT_PROVIDED).setField(fieldName));
      return false;
    }

    if (ValidationTools.isEmpty(type) && ValidationTools.isNotEmpty(value)) {
      context.addError(ValidationError.create(ErrorMessages.CLASSIFICATION_TYPE_NOT_PROVIDED).setField(fieldName));
      return false;
    }

    if (ValidationTools.isNotEmpty(type) && ValidationTools.isNotEmpty(value)) {
      Set<String> vocabulary = vocabularies.get(type);

      if (ValidationTools.isEmpty(vocabulary)) {
        context.addErrorMsg(vocabularies.getEmptyErrorMessage(type));
        return false;
      }

      if (!vocabulary.contains(value)) {
        context.addError(ValidationError.create(vocabularies.getIncorrectErrorMessage(type)).setField(fieldName));
        return false;
      }
    }

    return true;
  }

  private static class Vocabularies {
    private final Map<String, Set<String>> titleSets;

    private static final Map<String, String> EMPTY_ERRORS =
        Map.of(DDC, ErrorMessages.CONE_EMPTY_DDC_TITLE, ISO639_3, ErrorMessages.CONE_EMPTY_ISO639_3_TITLE, JEL,
            ErrorMessages.CONE_EMPTY_JEL_TITLE, MPICC_PROJECTS, ErrorMessages.CONE_EMPTY_MPICC_PROJECTS_TITLE, MPINP,
            ErrorMessages.CONE_EMPTY_MPINP_TITLE, MPIPKS, ErrorMessages.CONE_EMPTY_MPIPKS_TITLE, MPIRG,
            ErrorMessages.CONE_EMPTY_MPIRG_TITLE, MPIS_GROUPS, ErrorMessages.CONE_EMPTY_MPIS_GROUPS_TITLE, MPIS_PROJECTS,
            ErrorMessages.CONE_EMPTY_MPIS_PROJECTS_TITLE, MPIWG_PROJECTS, ErrorMessages.CONE_EMPTY_MPIWG_PROJECTS_TITLE);

    private static final Map<String, String> INCORRECT_ERRORS =
        Map.of(DDC, ErrorMessages.INCORRECT_DDC_CLASSIFICATION, ISO639_3, ErrorMessages.INCORRECT_ISO639_3_CLASSIFICATION, JEL,
            ErrorMessages.INCORRECT_JEL_CLASSIFICATION, MPICC_PROJECTS, ErrorMessages.INCORRECT_MPICC_PROJECTS_CLASSIFICATION, MPINP,
            ErrorMessages.INCORRECT_MPINP_CLASSIFICATION, MPIPKS, ErrorMessages.INCORRECT_MPIPKS_CLASSIFICATION, MPIRG,
            ErrorMessages.INCORRECT_MPIRG_CLASSIFICATION, MPIS_GROUPS, ErrorMessages.INCORRECT_MPIS_GROUPS_CLASSIFICATION, MPIS_PROJECTS,
            ErrorMessages.INCORRECT_MPIS_PROJECTS_CLASSIFICATION, MPIWG_PROJECTS, ErrorMessages.INCORRECT_MPIWG_PROJECTS_CLASSIFICATION);

    Vocabularies(ConeCache coneCache) {
      titleSets = Map.ofEntries(Map.entry(DDC, coneCache.getDdcTitleSet()), Map.entry(ISO639_3, coneCache.getIso639_3_TitleSet()),
          Map.entry(JEL, coneCache.getJelTitleSet()), Map.entry(MPICC_PROJECTS, coneCache.getMpiccProjectsTitleSet()),
          Map.entry(MPINP, coneCache.getMpinpTitleSet()), Map.entry(MPIPKS, coneCache.getMpipksTitleSet()),
          Map.entry(MPIRG, coneCache.getMpirgTitleSet()), Map.entry(MPIS_GROUPS, coneCache.getMpisGroupsTitleSet()),
          Map.entry(MPIS_PROJECTS, coneCache.getMpisProjectsTitleSet()), Map.entry(MPIWG_PROJECTS, coneCache.getMpiwgProjectsTitleSet()));
    }

    Set<String> get(String type) {
      return titleSets.get(type);
    }

    String getEmptyErrorMessage(String type) {
      return EMPTY_ERRORS.get(type);
    }

    String getIncorrectErrorMessage(String type) {
      return INCORRECT_ERRORS.get(type);
    }
  }

}
