package de.mpg.mpdl.inge.service.pubman.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.inge.citationmanager.CitationStyleExecuterService;
import de.mpg.mpdl.inge.citationmanager.CitationStyleManagerException;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO.FormatType;
import de.mpg.mpdl.inge.model.valueobjects.SearchAndExportResultVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchAndExportRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
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
    List<PubItemVO> searchResult = getSearchResult(saerrVO, token);

    String itemList = getItemList(searchResult);

    ExportFormatVO exportFormatVO = getExportFormatVO(saerrVO.getExportFormat(), saerrVO.getOutputFormat(), saerrVO.getCslConeId());

    byte[] result = this.itemTransformingService.getOutputForExport(exportFormatVO, itemList);

    return new SearchAndExportResultVO(result, exportFormatVO.getOutputFormat().getMimeType());
  }

  private List<PubItemVO> getSearchResult(SearchAndExportRetrieveRequestVO saerrVO, String token)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    SearchRetrieveResponseVO<PubItemVO> srrVO = this.pubItemService.search(saerrVO.getSearchRetrieveRequestVO(), token);

    List<PubItemVO> searchResult = new ArrayList<PubItemVO>();
    for (SearchRetrieveRecordVO<PubItemVO> record : srrVO.getRecords()) {
      searchResult.add(record.getData());
    }

    return searchResult;
  }

  private String getItemList(List<PubItemVO> list) throws IngeTechnicalException {
    String itemList;
    try {
      itemList = XmlTransformingService.transformToItemList(list);
    } catch (TechnicalException e) {
      throw new IngeTechnicalException(e);
    }

    return itemList;
  }

  private ExportFormatVO getExportFormatVO(String exportFormat, String outputFormat, String cslConeId) throws IngeTechnicalException {
    ExportFormatVO exportFormatVO;

    if (isStructured(exportFormat)) {
      exportFormatVO = new ExportFormatVO(FormatType.STRUCTURED, exportFormat, outputFormat == null ? "pdf" : outputFormat);
    } else if (isCitationStyle(exportFormat)) {
      exportFormatVO = new ExportFormatVO(FormatType.LAYOUT, exportFormat, outputFormat, cslConeId);
    } else {
      throw new IngeTechnicalException("Undefined export format: " + exportFormat);
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
