package de.mpg.mpdl.inge.inge_validation.validator;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO.Storage;

/*
 * <!-- The file name of a component must not contain slashes "/" --> <iso:pattern
 * name="no_slashes_in_filename" id="no_slashes_in_filename"> <iso:rule
 * context="escidocComponents:component"> <iso:assert test="not(escidocComponents:content/@storage =
 * 'internal-managed') or (not(contains(escidocComponents:properties/prop:file-name, '/')) and
 * not(contains
 * (escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/file:file/dc:title, '/')))">
 * SlashInFilename</iso:assert> </iso:rule> </iso:pattern>
 */

/*
 * escidocComponents:component -> de.mpg.mpdl.inge.model.valueobjects.FileVO
 * escidocMetadataRecords:md-records -> de.mpg.mpdl.inge.model.valueobjects.MetadataSetVO ->
 * FileVO.metadataSets escidocMetadataRecords:md-records/escidocMetadataRecords:md-record ->
 * de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO -> FileVO.getDefaultMetaData()
 * escidocMetadataRecords:md-record.file:file/dc:title -> MetadataSetVO.titel
 * escidocComponents:content/@storage -> FileVO.storage escidocComponents:properties/prop:file-name
 * -> FileVO.name
 */

public class ComponentsNoSlashesInNameValidator extends ValidatorHandler<List<FileVO>> implements Validator<List<FileVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<FileVO> files) {

    boolean ok = true;

    if (ValidationTools.isNotEmpty(files)) {

      int i = 1;
      for (final FileVO fileVO : files) {

        if (fileVO != null && fileVO.getStorage().equals(Storage.INTERNAL_MANAGED)) {
          if (ValidationTools.isNotEmpty(fileVO.getName()) //
              && fileVO.getName().contains("/") //
              || fileVO.getDefaultMetadata() != null //
                  && ValidationTools.isNotEmpty(fileVO.getDefaultMetadata().getTitle())
                  && fileVO.getDefaultMetadata().getTitle().contains("/")) {
            context.addError(ValidationError.create(ErrorMessages.SLASH_IN_FILENAME).setField("file[" + i + "]"));
            ok = false;
          }
        }

        i++;
      } // for

    } // if

    return ok;
  }

}
