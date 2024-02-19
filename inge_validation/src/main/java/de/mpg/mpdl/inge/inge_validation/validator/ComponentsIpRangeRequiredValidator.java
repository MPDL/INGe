package de.mpg.mpdl.inge.inge_validation.validator;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;

public class ComponentsIpRangeRequiredValidator extends ValidatorHandler<List<FileDbVO>> implements Validator<List<FileDbVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<FileDbVO> files) {

    boolean ok = true;

    if (ValidationTools.isNotEmpty(files)) {

      int i = 1;
      for (final FileDbVO fileDbVO : files) {

        if (null != fileDbVO) {

          //File with missing IP range
          if (FileDbVO.Storage.INTERNAL_MANAGED.equals(fileDbVO.getStorage())
              && FileDbVO.Visibility.AUDIENCE.equals(fileDbVO.getVisibility())) {
            boolean isEmpty = ValidationTools.isEmpty(fileDbVO.getAllowedAudienceIds());
            if (!isEmpty) {
              int countEmpty = 0;
              for (String audienceId : fileDbVO.getAllowedAudienceIds()) {
                if (null == audienceId) {
                  countEmpty++;
                }
              }
              if (countEmpty == fileDbVO.getAllowedAudienceIds().size()) {
                context.addError(ValidationError.create(ErrorMessages.COMPONENT_IP_RANGE_NOT_PROVIDED).setField("file[" + i + "]"));
                ok = false;
              }
            }
          }

        } // if

        i++;
      } // for

    } // if

    return ok;
  }

}
