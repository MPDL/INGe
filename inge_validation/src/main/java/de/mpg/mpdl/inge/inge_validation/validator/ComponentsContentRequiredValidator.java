package de.mpg.mpdl.inge.inge_validation.validator;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;

/*
 * <!-- File: If a filename, a content category, a mime-type or a description is given, there has to
 * be a content. --> <iso:pattern name="component_content_required" id="component_content_required">
 * <iso:rule context="escidocComponents:component"> <iso:assert
 * test="(escidocComponents:content/@xlink:href != '') or
 * not(escidocMetadataRecords:md-records/escidocMetadataRecords:md-record[@name='escidoc']/file:file
 * /dc:title != '' or escidocComponents:properties/prop:mime-type != '' or
 * escidocComponents:properties/prop:description != '')"> ComponentContentNotProvided </iso:assert>
 * </iso:rule> </iso:pattern>
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
 * escidocComponents:properties/prop:description -> FileVO.description
 * escidocComponents:content/@xlink:href -> FileVO.content
 */

public class ComponentsContentRequiredValidator extends ValidatorHandler<List<FileDbVO>> implements Validator<List<FileDbVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<FileDbVO> files) {

    boolean ok = true;

    if (ValidationTools.isNotEmpty(files)) {

      int i = 1;
      for (final FileDbVO fileVO : files) {

        if (fileVO != null //
            && ValidationTools.isEmpty(fileVO.getContent())) {

          if (fileVO.getMetadata() != null && ValidationTools.isNotEmpty(fileVO.getMetadata().getTitle()) //
              || ValidationTools.isNotEmpty(fileVO.getMimeType()) //
              || ValidationTools.isNotEmpty(fileVO.getMetadata().getDescription()) //
              || ValidationTools.isNotEmpty(fileVO.getMetadata().getContentCategory())) {
            context.addError(ValidationError.create(ErrorMessages.COMPONENT_CONTENT_NOT_PROVIDED).setField("file[" + i + "]"));
            ok = false;
          }

        } // if

        i++;
      } // for

    } // if

    return ok;
  }

}
