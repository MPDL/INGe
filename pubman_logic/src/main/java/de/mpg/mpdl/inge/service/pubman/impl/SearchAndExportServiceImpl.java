package de.mpg.mpdl.inge.service.pubman.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
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
  public SearchAndExportResultVO exportItems(SearchAndExportRetrieveRequestVO saerrVO, String token) throws IngeTechnicalException {
    byte[] result;
    String fileName;
    String targetMimeType;
    int totalNumberOfRecords = saerrVO.getSearchRetrieveReponseVO().getNumberOfRecords();

    result = this.itemTransformingService.getOutputForExport(saerrVO.getExportFormat(), saerrVO.getSearchRetrieveReponseVO());
    TransformerFactory.FORMAT format = TransformerFactory.getFormat(saerrVO.getExportFormat().getFormat());

    fileName = saerrVO.getExportFormat().getFormat() + "." + format.getFileFormat().getExtension();
    targetMimeType = format.getFileFormat().getMimeType();


    return new SearchAndExportResultVO(result, fileName, targetMimeType, totalNumberOfRecords);
  }

  @Override
  public SearchAndExportResultVO searchAndExportItems(SearchAndExportRetrieveRequestVO saerrVO, String token)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    SearchRetrieveResponseVO<ItemVersionVO> srrVO = this.pubItemService.search(saerrVO.getSearchRetrieveRequestVO(), token);
    saerrVO.setSearchRetrieveReponseVO(srrVO);

    return exportItems(saerrVO, token);
  }

  private List<ItemVersionVO> getSearchResult(SearchRetrieveResponseVO<ItemVersionVO> srrVO) {
    List<ItemVersionVO> searchResult = new ArrayList<>();
    for (SearchRetrieveRecordVO<ItemVersionVO> record : srrVO.getRecords()) {
      searchResult.add(record.getData());
    }

    return searchResult;
  }


}
