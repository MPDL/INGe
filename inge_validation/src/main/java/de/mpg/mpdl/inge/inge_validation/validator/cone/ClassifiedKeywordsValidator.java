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

public class ClassifiedKeywordsValidator extends ValidatorHandler<List<SubjectVO>> implements Validator<List<SubjectVO>> {
  public static final String DDC = "DDC";
  public static final String ISO639_3 = "ISO639_3";

  @Override
  public boolean validate(ValidatorContext context, List<SubjectVO> subjects) {

    boolean ok = true;

    if (PropertyReader.getProperty(PropertyReader.INGE_CONE_CACHE_USE).equalsIgnoreCase("true")) {

      if (subjects != null && subjects.isEmpty() == false) {

        final ConeCache coneCache = ConeCache.getInstance();

        final Set<String> ddcTitleSet = coneCache.getDdcTitleSet();
        final Set<String> iso639_3_TitleSet = coneCache.getIso639_3_TitleSet();

        int i = 1;
        for (final SubjectVO subjectVO : subjects) {

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

            }

          } // if

          i++;
        } // for

      } // if

    } // if

    return ok;
  }

}

