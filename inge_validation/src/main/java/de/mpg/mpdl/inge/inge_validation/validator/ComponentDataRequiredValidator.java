package de.mpg.mpdl.inge.inge_validation.validator;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;

/*
 * <iso:pattern name="component_data_required" id="component_data_required"> <iso:rule
 * context="escidocComponents:component"> <!-- If a file is given, the filename is mandatory. -->
 * <iso:assert test="not(escidocComponents:content/@xlink:href != '') or
 * escidocMetadataRecords:md-records
 * /escidocMetadataRecords:md-record[@name='escidoc']/file:file/dc:title != ''">
 * ComponentFileNameNotProvided</iso:assert> <!-- If a file is given, the content category is
 * mandatory. --> <iso:assert test="not(escidocComponents:content/@xlink:href != '') or
 * escidocComponents:properties/prop:content-category != ''">
 * ComponentContentCategoryNotProvided</iso:assert> <!-- If a file is given, the mime type is
 * mandatory. --> <iso:assert test="not(escidocComponents:content/@xlink:href != '') or
 * escidocComponents:properties/prop:mime-type != '' or escidocComponents:content/@storage =
 * 'external-url'"> ComponentMimeTypeNotProvided</iso:assert> <!-- If a file is given, the
 * visibility is mandatory. --> <iso:assert test="not(escidocComponents:content/@xlink:href != '')
 * or escidocComponents:properties/prop:visibility != ''">
 * ComponentVisibilityNotProvided</iso:assert> </iso:rule> </iso:pattern>
 */

/*
 * escidocComponents:component -> de.mpg.mpdl.inge.model.valueobjects.FileVO
 * escidocMetadataRecords:md-records -> de.mpg.mpdl.inge.model.valueobjects.MetadataSetVO ->
 * FileVO.metadataSets
 * escidocMetadataRecords:md-records/escidocMetadataRecords:md-record[@name='escidoc'] ->
 * de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO -> FileVO.getDefaultMetaData()
 * escidocMetadataRecords:md-record.file:file/dc:title -> MetadataSetVO.titel
 * escidocComponents:properties/prop:mime-type -> FileVO.mimeType
 * escidocComponents:properties/prop:category -> FileVO.contentCategorie
 * escidocComponents:properties/prop:visibility -> FileVO.visibility
 * escidocComponents:content/@xlink:href -> FileVO.content
 */

public class ComponentDataRequiredValidator extends ValidatorHandler<List<FileVO>> implements
    Validator<List<FileVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<FileVO> files) {

    boolean ok = true;

    if (files != null && files.isEmpty() == false) {

      int i = 1;
      for (final FileVO fileVO : files) {

        if (fileVO.getContent() == null) {

          if (fileVO.getDefaultMetadata() != null //
              && fileVO.getDefaultMetadata().getTitle() != null //
              && fileVO.getDefaultMetadata().getTitle().trim().length() > 0) {
            context.addError(ValidationError.create(ErrorMessages.COMPONENT_FILE_NAME_NOT_PROVIDED)
                .setField("file[" + i + "]"));
            ok = false;
          }

          if (fileVO.getContentCategory() != null //
              && fileVO.getContentCategory().trim().length() > 0) {
            context.addError(ValidationError.create(
                ErrorMessages.COMPONENT_CONTENT_CATEGORY_NOT_PROVIDED).setField("file[" + i + "]"));
            ok = false;
          }

          if (fileVO.getMimeType() != null //
              && fileVO.getMimeType().trim().length() > 0) {
            context.addError(ValidationError.create(ErrorMessages.COMPONENT_MIME_TYPE_NOT_PROVIDED)
                .setField("file[" + i + "]"));
            ok = false;
          }

          if (fileVO.getVisibility() != null) {
            context.addError(ValidationError
                .create(ErrorMessages.COMPONENT_VISIBILITY_NOT_PROVIDED)
                .setField("file[" + i + "]"));
            ok = false;
          }

        } // if

        i++;
      } // for

    } // if

    return ok;
  }

}
