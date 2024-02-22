package de.mpg.mpdl.inge.inge_validation.validator.OLDyearbook;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;

public class SourcesCreatorsRoleValidator extends ValidatorHandler<List<SourceVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<SourceVO> sources) {

    boolean ok = true;

    if (ValidationTools.isNotEmpty(sources)) {

      int i = 1;
      for (SourceVO sourceVO : sources) {

        if (null != sourceVO) {

          int j = 1;
          for (CreatorVO creatorVO : sourceVO.getCreators()) {

            if (null == creatorVO || null == creatorVO.getRole() //
                || !CreatorVO.CreatorRole.AUTHOR.equals(creatorVO.getRole()) //
                    && !CreatorVO.CreatorRole.EDITOR.equals(creatorVO.getRole())) {
              context.addError(ValidationError.create(ErrorMessages.SOURCE_CREATOR_ROLE_INVALID)
                  .setField("source[" + i + "].creator[" + j + "]").setErrorCode(ErrorMessages.WARNING));

              ok = false;

            } // if

            j++;
          } // for

        } // if

        i++;
      } // for

    } // if

    return ok;
  }

}
