package de.mpg.mpdl.inge.inge_validation.validator;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;

/*
 * <!-- The file name of a component must not contain slashes "/" --> <iso:pattern
 * name="no_slashes_in_filename" id="no_slashes_in_filename"> <iso:rule
 * context="escidocComponents:component"> <iso:assert test="not(escidocComponents:content/@storage =
 * 'internal-managed') or (not(contains(escidocComponents:properties/prop:file-name, '/')) and
 * not(contains(escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/file:file/dc:
 * title, '/')))"> SlashInFilename </iso:assert> </iso:rule> </iso:pattern>
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

public class ComponentsNoSlashesInNameValidator extends ValidatorHandler<List<FileDbVO>> implements Validator<List<FileDbVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<FileDbVO> files) {

    boolean ok = true;

    if (ValidationTools.isNotEmpty(files)) {

      int i = 1;
      for (FileDbVO fileDbVO : files) {

        if (null != fileDbVO && fileDbVO.getStorage().equals(FileDbVO.Storage.INTERNAL_MANAGED)) {
          if (ValidationTools.isNotEmpty(fileDbVO.getName()) //
              && fileDbVO.getName().contains("/") //
              || null != fileDbVO.getMetadata() //
                  && ValidationTools.isNotEmpty(fileDbVO.getMetadata().getTitle()) && fileDbVO.getMetadata().getTitle().contains("/")) {
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
