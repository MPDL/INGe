package de.mpg.mpdl.inge.service.pubman;

import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchAndExportResultVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchAndExportRetrieveRequestVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;

import java.util.List;

public interface SearchAndExportService {

  SearchAndExportResultVO exportItems(ExportFormatVO exportFormat, List<ItemVersionVO> itemList, String token)
      throws IngeTechnicalException;

  SearchAndExportResultVO searchAndExportItems(SearchAndExportRetrieveRequestVO saerrVO, String token)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

}
