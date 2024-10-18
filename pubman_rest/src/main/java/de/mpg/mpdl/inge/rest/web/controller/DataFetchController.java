
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
import de.mpg.mpdl.inge.rest.web.exceptions.NotFoundException;
import de.mpg.mpdl.inge.rest.web.spring.AuthCookieToHeaderFilter;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
  private static final Logger logger = LogManager.getLogger(DataFetchController.class);

  private static final String CROSSREF = "crossref";

  private static final String CONTEXT_ID = "contextId";
  private static final String IDENTIFIER = "identifier";

  private final AuthorizationService authorizationService;
  private final ContextService contextService;
  private final DataHandlerService dataHandlerService;
  private final DataSourceHandlerService dataSourceHandlerService;

  public DataFetchController(AuthorizationService authorizationService, ContextService contextService,
      DataHandlerService dataHandlerService, DataSourceHandlerService dataSourceHandlerService) {
    this.authorizationService = authorizationService;
    this.contextService = contextService;
    this.dataHandlerService = dataHandlerService;
    this.dataSourceHandlerService = dataSourceHandlerService;
  }

  @RequestMapping(value = "/getCrossref", method = RequestMethod.GET)
  public ResponseEntity<ItemVersionVO> getCrossref( //
      @RequestHeader(AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestParam(CONTEXT_ID) String contextId, //
      @RequestParam(IDENTIFIER) String identifier //
  ) throws AuthenticationException, IngeApplicationException, AuthorizationException, IngeTechnicalException, NotFoundException {

    this.authorizationService.getUserAccountFromToken(token);

    DataSourceVO dataSourceVO = this.dataSourceHandlerService.getSourceByName(CROSSREF);

    if (null == dataSourceVO) {
      throw new IngeApplicationException("invalid source definition " + CROSSREF);
    }

    ContextDbVO contextDbVO = this.contextService.get(contextId, token);
    if (null == contextDbVO) {
      throw new IngeApplicationException("given context not found");
    }

    byte[] fetchedItemByte = null;
    try {
      fetchedItemByte = this.dataHandlerService.doFetchMetaData(CROSSREF, dataSourceVO, identifier, TransformerFactory.getInternalFormat());
    } catch (DataacquisitionException e) {
      throw new IngeTechnicalException(e);
    }
    String fetchedItem = new String(fetchedItemByte);

    logger.info("fetchedItem: *" + fetchedItem + "*");
    if (null == fetchedItem || fetchedItem.trim().isEmpty() || -1 != fetchedItem.indexOf("<mdp:publication/>")) {
      throw new NotFoundException();
    }

    ItemVersionVO itemVersionVO = null;
    try {
      itemVersionVO = EntityTransformer.transformToNew(XmlTransformingService.transformToPubItem(fetchedItem));
      itemVersionVO.getObject().setContext(contextDbVO);
      itemVersionVO.getFiles().clear();
    } catch (TechnicalException e) {
      throw new IngeTechnicalException(e);
    }

    return new ResponseEntity<>(itemVersionVO, HttpStatus.OK);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


}
