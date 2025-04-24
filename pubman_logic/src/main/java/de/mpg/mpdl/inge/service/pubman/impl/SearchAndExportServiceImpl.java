package de.mpg.mpdl.inge.service.pubman.impl;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.model.valueobjects.*;
import de.mpg.mpdl.inge.transformation.results.TransformerWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
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
  public SearchAndExportResultVO exportItems(ExportFormatVO exportFormat, List<ItemVersionVO> itemList, String token)
      throws IngeTechnicalException {

    byte[] result = this.itemTransformingService.getOutputForExport(exportFormat, itemList);
    return getSearchAndExportResult(result, exportFormat, null, itemList, null);
  }

  @Override
  public SearchAndExportResultVO exportItemsWrapped(ExportFormatVO exportFormat, List<ItemVersionVO> itemList, String token)
      throws IngeTechnicalException {

    TransformerWrapper tw = this.itemTransformingService.getTransformationForExport(exportFormat, itemList);
    return getSearchAndExportResult(null, exportFormat, null, itemList, tw);
  }

  @Override
  public SearchAndExportResultVO searchAndExportItems(SearchAndExportRetrieveRequestVO saerrVO, String token)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    SearchRetrieveResponseVO<ItemVersionVO> srrVO = this.pubItemService.search(saerrVO.getSearchRetrieveRequestVO(), token);
    //saerrVO.setSearchRetrieveReponseVO(srrVO);

    byte[] result = this.itemTransformingService.getOutputForExport(saerrVO.getExportFormat(), srrVO);

    return getSearchAndExportResult(result, saerrVO.getExportFormat(), srrVO, null, null);
  }

  @Override
  public SearchAndExportResultVO searchAndExportItemsWrapped(SearchAndExportRetrieveRequestVO saerrVO, String token)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    SearchRetrieveResponseVO<ItemVersionVO> srrVO = this.pubItemService.search(saerrVO.getSearchRetrieveRequestVO(), token);
    //saerrVO.setSearchRetrieveReponseVO(srrVO);

    TransformerWrapper tw = this.itemTransformingService.getTransformationForExport(saerrVO.getExportFormat(), srrVO);

    return getSearchAndExportResult(null, saerrVO.getExportFormat(), srrVO, null, tw);
  }

  private SearchAndExportResultVO getSearchAndExportResult(byte[] result, ExportFormatVO exportFormat, SearchRetrieveResponseVO srrVO,
      List<ItemVersionVO> itemList, TransformerWrapper tw) {
    String fileName;
    String targetMimeType;
    int totalNumberOfRecords = srrVO != null ? srrVO.getNumberOfRecords() : itemList.size();

    TransformerFactory.FORMAT format = TransformerFactory.getFormat(exportFormat.getFormat());

    fileName = exportFormat.getFormat() + "." + format.getFileFormat().getExtension();
    targetMimeType = format.getFileFormat().getMimeType();

    if (tw != null) {
      ExtendedSearchAndExportResultVO saervo = new ExtendedSearchAndExportResultVO(tw, fileName, targetMimeType, totalNumberOfRecords);
      saervo.setSearchRetrieveResponseVO(srrVO);
      return saervo;
    } else {
      SearchAndExportResultVO saervo = new SearchAndExportResultVO(result, fileName, targetMimeType, totalNumberOfRecords);
      saervo.setSearchRetrieveResponseVO(srrVO);
      return saervo;
    }


  }



  private List<ItemVersionVO> getSearchResult(SearchRetrieveResponseVO<ItemVersionVO> srrVO) {
    List<ItemVersionVO> searchResult = new ArrayList<>();
    for (SearchRetrieveRecordVO<ItemVersionVO> record : srrVO.getRecords()) {
      searchResult.add(record.getData());
    }

    return searchResult;
  }

  public class ExtendedSearchAndExportResultVO extends SearchAndExportResultVO {

    TransformerWrapper transformerWrapper;

    public ExtendedSearchAndExportResultVO(TransformerWrapper tw, String fileName, String targetMimeType, int totalNumberOfRecords) {
      super(null, fileName, targetMimeType, totalNumberOfRecords);
      this.transformerWrapper = tw;
    }

    public TransformerWrapper getTransformerWrapper() {
      return transformerWrapper;
    }

    public void setTransformerWrapper(TransformerWrapper transformerWrapper) {
      this.transformerWrapper = transformerWrapper;
    }


  }


}
