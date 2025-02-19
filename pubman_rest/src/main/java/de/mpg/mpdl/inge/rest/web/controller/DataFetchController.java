package de.mpg.mpdl.inge.rest.web.controller;

import de.mpg.mpdl.inge.dataacquisition.DataHandlerService;
import de.mpg.mpdl.inge.dataacquisition.DataSourceHandlerService;
import de.mpg.mpdl.inge.dataacquisition.DataacquisitionException;
import de.mpg.mpdl.inge.dataacquisition.valueobjects.DataSourceVO;
import de.mpg.mpdl.inge.dataacquisition.valueobjects.FullTextVO;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.db.valueobjects.StagedFileDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.util.EntityTransformer;
import de.mpg.mpdl.inge.model.valueobjects.FileFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.FormatVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.rest.web.exceptions.NotFoundException;
import de.mpg.mpdl.inge.rest.web.spring.AuthCookieToHeaderFilter;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.service.pubman.FileService;
import de.mpg.mpdl.inge.service.util.GrantUtil;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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

  private static final String ARXIV = "arXiv";
  private static final String CROSSREF = "crossref";

  private static final String CONTEXT_ID = "contextId";
  private static final String IDENTIFIER = "identifier";

  private final AuthorizationService authorizationService;
  private final ContextService contextService;
  private final DataHandlerService dataHandlerService;
  private final DataSourceHandlerService dataSourceHandlerService;
  private final FileService fileService;

  public DataFetchController(AuthorizationService authorizationService, ContextService contextService,
      DataHandlerService dataHandlerService, DataSourceHandlerService dataSourceHandlerService, FileService fileService) {
    this.authorizationService = authorizationService;
    this.contextService = contextService;
    this.dataHandlerService = dataHandlerService;
    this.dataSourceHandlerService = dataSourceHandlerService;
    this.fileService = fileService;
  }

  @RequestMapping(value = "/getCrossref", method = RequestMethod.GET)
  public ResponseEntity<ItemVersionVO> getCrossref( //
      @RequestHeader(AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestParam(CONTEXT_ID) String contextId, //
      @RequestParam(IDENTIFIER) String identifier //
  ) throws AuthenticationException, IngeApplicationException, AuthorizationException, IngeTechnicalException, NotFoundException {

    DataSourceVO dataSourceVO = getDataSource(CROSSREF);
    AccountUserDbVO accountUserDbVO = getUser(token);
    ContextDbVO contextDbVO = getContext(contextId, accountUserDbVO, token);
    String fetchedItem = fetchMetaData(CROSSREF, dataSourceVO, identifier);
    ItemVersionVO itemVersionVO = getItemVersion(fetchedItem, contextDbVO);

    return new ResponseEntity<>(itemVersionVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/getArxiv", method = RequestMethod.GET)
  public ResponseEntity<ItemVersionVO> getArxiv( //
      @RequestHeader(AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestParam(CONTEXT_ID) String contextId, //
      @RequestParam(IDENTIFIER) String identifier)
      throws AuthenticationException, IngeApplicationException, AuthorizationException, IngeTechnicalException, NotFoundException {

    DataSourceVO dataSourceVO = getDataSource(ARXIV);
    AccountUserDbVO accountUserDbVO = getUser(token);
    ContextDbVO contextDbVO = getContext(contextId, accountUserDbVO, token);
    String fetchedItem = fetchMetaData(ARXIV, dataSourceVO, identifier);
    ItemVersionVO itemVersionVO = getItemVersion(fetchedItem, contextDbVO);

    List<FileDbVO> fileVOs = getFiles(dataSourceVO, identifier, token, ARXIV);
    for (FileDbVO tmp : fileVOs) {
      itemVersionVO.getFiles().add(tmp);
    }

    return new ResponseEntity<>(itemVersionVO, HttpStatus.OK);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private DataSourceVO getDataSource(String source) throws IngeApplicationException {
    DataSourceVO dataSourceVO = this.dataSourceHandlerService.getSourceByName(source);

    if (null == dataSourceVO) {
      throw new IngeApplicationException("invalid source definition " + source);
    }

    return dataSourceVO;
  }

  private AccountUserDbVO getUser(String token) throws AuthenticationException, IngeApplicationException {

    AccountUserDbVO accountUserDbVO = this.authorizationService.getUserAccountFromToken(token);

    return accountUserDbVO;
  }

  private ContextDbVO getContext(String contextId, AccountUserDbVO accountUserDbVO, String token)
      throws AuthenticationException, AuthorizationException, IngeApplicationException, IngeTechnicalException {
    ContextDbVO contextDbVO = this.contextService.get(contextId, token);

    if (null == contextDbVO) {
      throw new IngeApplicationException("given context not found");
    }

    if (GrantUtil.hasRole(accountUserDbVO, GrantVO.PredefinedRoles.DEPOSITOR, contextDbVO.getObjectId()) //
        || GrantUtil.hasRole(accountUserDbVO, GrantVO.PredefinedRoles.MODERATOR, contextDbVO.getObjectId())) {
      return contextDbVO;
    }

    throw new AuthorizationException("given user is not allowed to access the given context.");
  }

  private String fetchMetaData(String source, DataSourceVO dataSourceVO, String identifier)
      throws IngeTechnicalException, NotFoundException {

    byte[] fetchedItemByte = null;
    try {
      fetchedItemByte = this.dataHandlerService.doFetchMetaData(source, dataSourceVO, identifier, TransformerFactory.getInternalFormat());
    } catch (DataacquisitionException e) {
      throw new IngeTechnicalException(e);
    }
    String fetchedItem = new String(fetchedItemByte);

    logger.info("fetchedItem: *" + fetchedItem + "*");
    if (null == fetchedItem || fetchedItem.trim().isEmpty() || source.equals(CROSSREF) && -1 != fetchedItem.indexOf("<mdp:publication/>")) {
      throw new NotFoundException();
    }

    return fetchedItem;
  }

  private ItemVersionVO getItemVersion(String fetchedItem, ContextDbVO contextDbVO) throws IngeTechnicalException {

    ItemVersionVO itemVersionVO = null;
    try {
      itemVersionVO = EntityTransformer.transformToNew(XmlTransformingService.transformToPubItem(fetchedItem));
      itemVersionVO.getObject().setContext(contextDbVO);
      itemVersionVO.getFiles().clear();
    } catch (TechnicalException e) {
      throw new IngeTechnicalException(e);
    }

    return itemVersionVO;
  }

  private List<FileDbVO> getFiles(DataSourceVO dataSourceVO, String identifier, String token, String source) throws IngeTechnicalException {

    List<FileDbVO> fileVOs = new ArrayList<>();
    List<FullTextVO> ftFormats = dataSourceVO.getFtFormats();
    List<String> fullTextFormats = new ArrayList<>();

    for (FullTextVO fulltextVO : ftFormats) {
      if (fulltextVO.isFtDefault()) {
        FileFormatVO.FILE_FORMAT fileFormat = FileFormatVO.getFileFormat(fulltextVO.getName());
        fullTextFormats.add(fileFormat.getExtension());
        break;
      }
    }

    byte[] ba = null;
    try {
      ba = this.dataHandlerService.doFetchFullText(dataSourceVO, identifier, fullTextFormats.toArray(new String[0]));
    } catch (DataacquisitionException e) {
      throw new IngeTechnicalException(e);
    }

    ByteArrayInputStream in = new ByteArrayInputStream(ba);
    String fileId = null;
    String fileName = source + ":" + identifier + this.dataHandlerService.getFileEnding();
    try {
      StagedFileDbVO stagedFile = this.fileService.createStageFile(in, fileName, token);
      fileId = String.valueOf(stagedFile.getId());
    } catch (Exception e) {
      logger.error("Could not upload staged file [" + fileId + "]", e);
      throw new IngeTechnicalException("Could not upload staged file [" + fileId + "]", e);
    }

    if (null != fileId && !fileId.trim().isEmpty()) {
      FileDbVO fileVO = this.dataHandlerService.getComponentVO(dataSourceVO);
      MdsFileVO fileMd = fileVO.getMetadata();
      fileVO.setStorage(FileDbVO.Storage.INTERNAL_MANAGED);
      fileVO.setVisibility(this.dataHandlerService.getVisibility());
      fileVO.setMetadata(fileMd);
      fileVO.getMetadata().setTitle(fileName);
      fileVO.setMimeType(this.dataHandlerService.getContentType());
      fileVO.setName(fileName);
      FormatVO formatVO = new FormatVO();
      formatVO.setType("dcterms:IMT");
      formatVO.setValue(this.dataHandlerService.getContentType());
      fileVO.getMetadata().getFormats().add(formatVO);
      fileVO.setContent(fileId);
      fileVO.setSize(ba.length);
      fileVO.getMetadata().setDescription("File downloaded from " + source + " at " + getCurrentDate());
      fileVO.getMetadata().setContentCategory(this.dataHandlerService.getContentCategory());
      fileVOs.add(fileVO);
    }

    return fileVOs;
  }

  private String getCurrentDate() {

    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    return sdf.format(cal.getTime());
  }
}
