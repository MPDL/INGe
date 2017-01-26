package de.mpg.mpdl.inge.inge_validation.validator.cone;

import java.util.List;
import java.util.Set;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;

/*
 * <!-- The language codes of the publication must be in ISO639-3 --> <iso:pattern
 * name="language_code" id="language_code"> <iso:rule context="publication:publication/dc:language">
 * <iso:assert test="not(exists($iso639-3/var/rdf:RDF)) or . = '' or
 * escidoc:contains($iso639-3/var/rdf:RDF/rdf:Description/@rdf:about, concat('iso639-3/resource/',
 * .))"> UnknownLanguageCode</iso:assert> </iso:rule> </iso:pattern>
 */

public class LanguageCodeValidator extends ValidatorHandler<List<String>> implements
    Validator<List<String>> {

  @Override
  public boolean validate(ValidatorContext context, List<String> languages) {

    boolean ok = true;

    if (languages != null) {

      Set<String> iso639_3_IdentifierSet = ConeCache.getInstance().getIso639_3_IdentifierSet();

      int i = 1;
      for (String language : languages) {

        if (language != null && !iso639_3_IdentifierSet.isEmpty()
            && !iso639_3_IdentifierSet.contains(language)) {
          context.addError(ValidationError.create(ErrorMessages.UNKNOWN_LANGUAGE_CODE).setField(
              "language[" + i + "]"));
          ok = false;
        }

        i++;
      } // for

    } // if

    return ok;
  }

}