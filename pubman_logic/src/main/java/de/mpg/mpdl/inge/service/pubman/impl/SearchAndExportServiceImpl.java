package de.mpg.mpdl.inge.service.pubman.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.inge.citationmanager.CitationStyleExecuterService;
import de.mpg.mpdl.inge.citationmanager.CitationStyleManagerException;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.FileFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchAndExportResultVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchAndExportRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ItemTransformingService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.SearchAndExportService;
import de.mpg.mpdl.inge.transformation.TransformerFactory;

@Service
@Primary
public class SearchAndExportServiceImpl implements SearchAndExportService {

  @Autowired
  private PubItemService pubItemService;

  @Autowired
  private ItemTransformingService itemTransformingService;

  public SearchAndExportServiceImpl() {}

  @Override
  public SearchAndExportResultVO searchAndExportItems(SearchAndExportRetrieveRequestVO saerrVO, String token)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    byte[] result;
    String fileName;
    String targetMimeType;

    SearchRetrieveResponseVO<ItemVersionVO> srrVO = this.pubItemService.search(saerrVO.getSearchRetrieveRequestVO(), token);

    if (saerrVO.getExportFormatName() == null || saerrVO.getExportFormatName().equals(TransformerFactory.JSON)) {
      result = srrVO.getOriginalResponse().toString().getBytes();
      fileName = FileFormatVO.FILE_FORMAT.JSON.getName() + "." + FileFormatVO.FILE_FORMAT.JSON.getExtension();
      targetMimeType = FileFormatVO.JSON_MIMETYPE;
    } else {
      List<ItemVersionVO> searchResult = getSearchResult(srrVO);
      ExportFormatVO exportFormatVO = getExportFormatVO(saerrVO.getExportFormatName(), saerrVO.getOutputFormat(), saerrVO.getCslConeId());
      result = this.itemTransformingService.getOutputForExport(exportFormatVO, searchResult);
      fileName = exportFormatVO.getName() + "." + exportFormatVO.getFileFormat().getExtension();
      targetMimeType = exportFormatVO.getFileFormat().getMimeType();
    }

    return new SearchAndExportResultVO(result, fileName, targetMimeType);
  }

  private List<ItemVersionVO> getSearchResult(SearchRetrieveResponseVO<ItemVersionVO> srrVO) {
    List<ItemVersionVO> searchResult = new ArrayList<ItemVersionVO>();
    for (SearchRetrieveRecordVO<ItemVersionVO> record : srrVO.getRecords()) {
      searchResult.add(record.getData());
    }

    return searchResult;
  }

  private ExportFormatVO getExportFormatVO(String exportFormatName, String outputFormatName, String cslConeId)
      throws IngeTechnicalException {
    ExportFormatVO exportFormatVO;
    if (isStructured(exportFormatName)) {
      exportFormatVO = new ExportFormatVO(ExportFormatVO.FormatType.STRUCTURED, exportFormatName,
          TransformerFactory.getFormat(exportFormatName).getFileFormat().getName());
    } else if (isCitationStyle(exportFormatName)) {
      exportFormatVO = new ExportFormatVO(ExportFormatVO.FormatType.LAYOUT, exportFormatName,
          outputFormatName == null ? FileFormatVO.PDF_NAME : outputFormatName, cslConeId);
    } else {
      throw new IngeTechnicalException("Undefined export format: " + exportFormatName);
    }

    return exportFormatVO;
  }

  private boolean isStructured(String name) {
    try {
      TransformerFactory.getFormat(name);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  private boolean isCitationStyle(String name) {
    try {
      return CitationStyleExecuterService.isCitationStyle(name);
    } catch (CitationStyleManagerException e) {
      return false;
    }
  }
}
