package de.mpg.mpdl.inge.inge_validation.validator.yearbook;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;

/*
 * <iso:report
 * test="not(source:source[1]/escidoc:creator/@role = 'http://www.loc.gov/loc.terms/relators/AUT' or source:source[1]/escidoc:creator/@role = 'http://www.loc.gov/loc.terms/relators/EDT' or source:source[2]/escidoc:creator/@role = 'http://www.loc.gov/loc.terms/relators/AUT' or source:source[2]/escidoc:creator/@role = 'http://www.loc.gov/loc.terms/relators/EDT')"
 * >SourceCreatorRoleInvalid</iso:report>
 */
public class SourceCreatorsRoleValidator extends ValidatorHandler<List<SourceVO>>
    implements Validator<List<SourceVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<SourceVO> sources) {

    boolean ok = true;

    if (ValidationTools.isNotEmpty(sources)) {

      int i = 1;
      for (final SourceVO sourceVO : sources) {

        if (sourceVO != null) {

          int j = 1;
          for (final CreatorVO creatorVO : sourceVO.getCreators()) {

            if (creatorVO != null && (creatorVO.getRole() == null
                || !CreatorVO.CreatorRole.AUTHOR.equals(creatorVO.getRole())
                    && !CreatorVO.CreatorRole.EDITOR.equals(creatorVO.getRole()))) {
              context.addError(ValidationError.create(ErrorMessages.SOURCE_CREATOR_ROLE_INVALID)
                  .setField("source[" + i + "].creator[" + j + "]"));
              ok = false;
            }

            j++;
          } // for

        } // if

        i++;
      } // for

    } // if

    return ok;
  }

}
