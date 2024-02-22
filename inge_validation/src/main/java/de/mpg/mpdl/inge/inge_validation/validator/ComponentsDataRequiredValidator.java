package de.mpg.mpdl.inge.inge_validation.validator;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;

/*
 * <iso:pattern name="component_data_required" id="component_data_required"> <iso:rule
 * context="escidocComponents:component"> <!-- If a file is given, the filename is mandatory. -->
 * <iso:assert test="not(escidocComponents:content/@xlink:href != '') or
 * escidocMetadataRecords:md-records/escidocMetadataRecords:md-record[@name='escidoc']/file:file/dc:
 * title != ''"> ComponentFileNameNotProvided </iso:assert> <!-- If a file is given, the content
 * category is mandatory. --> <iso:assert test="not(escidocComponents:content/@xlink:href != '') or
 * escidocComponents:properties/prop:content-category != ''"> ComponentContentCategoryNotProvided
 * </iso:assert> <!-- If a file is given, the mime type is mandatory. --> <iso:assert
 * test="not(escidocComponents:content/@xlink:href != '') or
 * escidocComponents:properties/prop:mime-type != '' or escidocComponents:content/@storage =
 * 'external-url'"> ComponentMimeTypeNotProvided </iso:assert> <!-- If a file is given, the
 * visibility is mandatory. --> <iso:assert test="not(escidocComponents:content/@xlink:href != '')
 * or escidocComponents:properties/prop:visibility != ''"> ComponentVisibilityNotProvided
 * </iso:assert> </iso:rule> </iso:pattern>
 */

/*
 * escidocComponents:component -> de.mpg.mpdl.inge.model.valueobjects.FileVO
 * escidocMetadataRecords:md-records -> de.mpg.mpdl.inge.model.valueobjects.MetadataSetVO ->
 * FileVO.metadataSets escidocMetadataRecords:md-records ->
 * de.mpg.mpdl.inge.model.valueobjects.MetadataSetVO ->
 * escidocMetadataRecords:md-records/escidocMetadataRecords:md-record[@name='escidoc'] ->
 * FileVO.getDefaultMetaData() -> de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO
 * escidocMetadataRecords:md-record.file:file/dc:title -> MetadataSetVO.titel
 * escidocComponents:properties/prop:mime-type -> FileVO.mimeType
 * escidocComponents:properties/prop:category -> FileVO.contentCategorie
 * escidocComponents:properties/prop:visibility -> FileVO.visibility
 * escidocComponents:content/@xlink:href -> FileVO.content
 */

public class ComponentsDataRequiredValidator extends ValidatorHandler<List<FileDbVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<FileDbVO> files) {

    boolean ok = true;

    if (ValidationTools.isNotEmpty(files)) {

      int i = 1;
      for (FileDbVO fileDbVO : files) {

        if (null != fileDbVO) {

          //External reference with missing content
          if ((FileDbVO.Storage.EXTERNAL_URL.equals(fileDbVO.getStorage()) && ValidationTools.isEmpty(fileDbVO.getContent()))) {
            context.addError(ValidationError.create(ErrorMessages.COMPONENT_CONTENT_NOT_PROVIDED).setField("file[" + i + "]"));
            ok = false;
          }

          //File with missing content or missing id
          if ((FileDbVO.Storage.INTERNAL_MANAGED.equals(fileDbVO.getStorage()) && ValidationTools.isEmpty(fileDbVO.getContent())
              && ValidationTools.isEmpty(fileDbVO.getObjectId()))) {
            context.addError(ValidationError.create(ErrorMessages.COMPONENT_CONTENT_NOT_PROVIDED).setField("file[" + i + "]"));
            ok = false;
          }

          if (null != fileDbVO.getMetadata() //
              && ValidationTools.isEmpty(fileDbVO.getMetadata().getTitle())) {
            context.addError(ValidationError.create(ErrorMessages.COMPONENT_FILE_NAME_NOT_PROVIDED).setField("file[" + i + "]"));
            ok = false;
          }

          if (ValidationTools.isEmpty(fileDbVO.getMetadata().getContentCategory())) {
            context.addError(ValidationError.create(ErrorMessages.COMPONENT_CONTENT_CATEGORY_NOT_PROVIDED).setField("file[" + i + "]"));
            ok = false;
          }

          if (!FileDbVO.Storage.EXTERNAL_URL.equals(fileDbVO.getStorage()) && null == fileDbVO.getVisibility()) {
            context.addError(ValidationError.create(ErrorMessages.COMPONENT_VISIBILITY_NOT_PROVIDED).setField("file[" + i + "]"));
            ok = false;
          }

        } // if

        i++;
      } // for

    } // if

    return ok;
  }

}
