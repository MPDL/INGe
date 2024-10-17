
package de.mpg.mpdl.inge.rest.web.controller;

import de.mpg.mpdl.inge.dataacquisition.DataHandlerService;
import de.mpg.mpdl.inge.dataacquisition.DataSourceHandlerService;
import de.mpg.mpdl.inge.dataacquisition.DataacquisitionException;
import de.mpg.mpdl.inge.dataacquisition.valueobjects.DataSourceVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.util.EntityTransformer;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.rest.web.spring.AuthCookieToHeaderFilter;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.faces.bean.ManagedProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dataFetch")
@Tag(name = "DataFetch")
public class DataFetchController {

  private static final String CROSSREF = "crossref";

  private static final String CONTEXT_ID = "contextId";
  private static final String IDENTIFIER = "identifier";

  private final AuthorizationService authorizationService;
  private final DataHandlerService dataHandlerService;
  private final DataSourceHandlerService dataSourceHandlerService;

  @ManagedProperty("#{contextServiceDbImpl}")
  private de.mpg.mpdl.inge.service.pubman.ContextService contextService;

  public DataFetchController(AuthorizationService authorizationService, DataHandlerService dataHandlerService,
      DataSourceHandlerService dataSourceHandlerService) {
    this.authorizationService = authorizationService;
    this.dataHandlerService = dataHandlerService;
    this.dataSourceHandlerService = dataSourceHandlerService;
  }

  @RequestMapping(value = "/getCrossref", method = RequestMethod.GET)
  public ResponseEntity<ItemVersionVO> getCrossref( //
      @RequestHeader(AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestParam(CONTEXT_ID) String contextId, //
      @RequestParam(IDENTIFIER) String identifier //
  ) throws AuthenticationException, IngeApplicationException, DataacquisitionException, TechnicalException, AuthorizationException,
      IngeTechnicalException {

    this.authorizationService.getUserAccountFromToken(token);

    DataSourceVO dataSourceVO = this.dataSourceHandlerService.getSourceByName(CROSSREF);

    if (null == dataSourceVO) {
      throw new IngeApplicationException("invalid source definition " + CROSSREF);
    }

    ContextDbVO contextDbVO = this.contextService.get(contextId, token);
    if (null == contextDbVO) {
      throw new IngeApplicationException("given context not found");
    }

    byte[] fetchedItemByte =
        this.dataHandlerService.doFetchMetaData(CROSSREF, dataSourceVO, identifier, TransformerFactory.getInternalFormat());
    String fetchedItem = new String(fetchedItemByte);

    if (null == fetchedItem || fetchedItem.trim().isEmpty()) {
      throw new IngeApplicationException("nothing found");
    }

    ItemVersionVO itemVersionVO = EntityTransformer.transformToNew(XmlTransformingService.transformToPubItem(fetchedItem));
    itemVersionVO.getObject().setContext(contextDbVO);
    itemVersionVO.getFiles().clear();

    return new ResponseEntity<>(itemVersionVO, HttpStatus.OK);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


}
