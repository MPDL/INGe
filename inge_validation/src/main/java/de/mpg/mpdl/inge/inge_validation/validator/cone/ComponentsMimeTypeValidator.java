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
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO.Storage;
import de.mpg.mpdl.inge.model.valueobjects.metadata.FormatVO;

/*
 * <!-- the mime type of a file has to be one of the list given in "mimeTypes" above -->
 * <iso:pattern name="component_mimeType" id="component_mimeType"> <iso:rule
 * context="escidocComponents:component"> <iso:assert test="not(exists($mimeTypes/var/rdf:RDF)) or
 * not(escidocComponents:content/@xlink:href != '') or not(escidocComponents:content/@storage =
 * 'internal-managed') or
 * escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/file:file
 * /dc:format[@xsi:type='dcterms:IMT']=$mimeTypes/var/rdf:RDF/rdf:Description/dc:title">
 * MimeTypeNotValid</iso:assert> </iso:rule> </iso:pattern>
 */

/*
 * $mimeTypes/var/rdf:RDF -> ConeSetsCache.getInstance().getMimeTypesTitleSet()
 * escidocComponents:component -> de.mpg.mpdl.inge.model.valueobjects.FileVO
 * escidocMetadataRecords:md-records -> de.mpg.mpdl.inge.model.valueobjects.MetadataSetVO ->
 * FileVO.metadataSets escidocMetadataRecords:md-records/escidocMetadataRecords:md-record ->
 * de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO -> FileVO.getDefaultMetaData()
 * escidocMetadataRecords:md-record.file:file/dc:format ->
 * de.mpg.mpdl.inge.model.valueobjects.metadata.FormatVO -> MdsFileVO.formats
 * escidocComponents:content/@storage -> FileVO.storage escidocComponents:content/@xlink:href ->
 * FileVO.content
 */

public class ComponentsMimeTypeValidator extends ValidatorHandler<List<FileDbVO>> implements Validator<List<FileDbVO>> {

  public static final String IMT = "dcterms:IMT";

  @Override
  public boolean validate(ValidatorContext context, List<FileDbVO> files) {

    boolean ok = true;

    if (ValidationTools.isNotEmpty(files)) {

      final Set<String> mimeTypesTitleSet = ConeCache.getInstance().getMimeTypesTitleSet();

      if (ValidationTools.isEmpty(mimeTypesTitleSet)) {
        context.addErrorMsg(ErrorMessages.CONE_EMPTY_MIME_TYPE);
        return false;
      }

      int i = 1;
      for (final FileDbVO fileVO : files) {

        if (ValidationTools.isNotEmpty(fileVO.getContent()) && fileVO.getStorage().equals(Storage.INTERNAL_MANAGED)) {

          int j = 1;
          for (final FormatVO formatVO : fileVO.getMetadata().getFormats()) {

            if (ComponentsMimeTypeValidator.IMT.equals(formatVO.getType()) //
                && !mimeTypesTitleSet.contains(formatVO.getValue())) {
              context.addError(ValidationError.create(ErrorMessages.MIME_TYPE_NOT_VALID).setField("file[" + i + "].format[" + j + "]"));
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
