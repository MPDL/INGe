package de.mpg.mpdl.inge.rest.web.controller;

import de.mpg.mpdl.inge.model.db.valueobjects.ImportLog;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ImportLogItemDetailDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.rest.web.spring.AuthCookieToHeaderFilter;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ImportService;
import de.mpg.mpdl.inge.util.PropertyReader;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/import")
@Tag(name = "Import")
public class ImportController {

  private static final Logger logger = LogManager.getLogger(ImportController.class);

  private static final String TMP_FILE_ROOT_PATH = System.getProperty(PropertyReader.JBOSS_HOME_DIR)
      + PropertyReader.getProperty(PropertyReader.INGE_LOGIC_TEMPORARY_FILESYSTEM_ROOT_PATH);

  private static final String ImportLog_ID_PATH = "/importLog/{importLogId}";
  private static final String ImportLogItems_ID_PATH = "/importLogItems/{importLogId}";
  private static final String ImportLogItemDetails_ID_PATH = "/importLogItemDetails/{importLogItemId}";
  private static final String ImportLog_VAR = "importLogId";
  private static final String ImportLogItem_VAR = "importLogItemId";
  private static final String FILE = "file";

  private static final String FORMAT = "format";
  private static final String CONTEXT_ID = "contextId";
  private static final String IMPORT_NAME = "importName";
  private static final String IMPORT_LOG_ID = "importLogId";
  private static final String SUBMIT_MODUS = "submitModus";

  private final ImportService importService;

  public ImportController(ImportService importService) {
    this.importService = importService;
  }

  @RequestMapping(value = ImportLog_ID_PATH, method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteImportLog( //
      @RequestHeader(AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @PathVariable(ImportLog_VAR) Integer importLogId) //
      throws AuthenticationException, IngeApplicationException, AuthorizationException {

    this.importService.deleteImportLog(importLogId, token);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/deleteImportedItems", method = RequestMethod.PUT)
  public ResponseEntity<?> deleteImportedItems( //
      @RequestHeader(AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestParam(IMPORT_LOG_ID) Integer importLogId) //
      throws AuthenticationException, IngeApplicationException, AuthorizationException {

    this.importService.deleteImportedItems(importLogId, token);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = ImportLogItemDetails_ID_PATH, method = RequestMethod.GET)
  public ResponseEntity<List<ImportLogItemDetailDbVO>> getImportLogItemDetails( //
      @RequestHeader(AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @PathVariable(ImportLogItem_VAR) Integer importLogItemId) //
      throws AuthenticationException, IngeApplicationException, AuthorizationException {

    List<ImportLogItemDetailDbVO> importLogItemDetailDbVOs = this.importService.getImportLogItemDetails(importLogItemId, token);

    return new ResponseEntity<>(importLogItemDetailDbVOs, HttpStatus.OK);
  }

  @RequestMapping(value = ImportLogItems_ID_PATH, method = RequestMethod.GET)
  public ResponseEntity<List<ImportLogItemDbVO>> getImportLogItems( //
      @RequestHeader(AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @PathVariable(ImportLog_VAR) Integer importLogId) //
      throws AuthenticationException, IngeApplicationException, AuthorizationException {

    List<ImportLogItemDbVO> importLogItemDbVOs = this.importService.getImportLogItems(importLogId, token);

    return new ResponseEntity<>(importLogItemDbVOs, HttpStatus.OK);
  }

  @RequestMapping(value = "/getImportLogs", method = RequestMethod.GET)
  public ResponseEntity<List<ImportLogDbVO>> getImportLogs( //
      @RequestHeader(AuthCookieToHeaderFilter.AUTHZ_HEADER) String token) //
      throws AuthenticationException, IngeApplicationException {

    List<ImportLogDbVO> importLogDbVOs = this.importService.getImportLogs(token);

    return new ResponseEntity<>(importLogDbVOs, HttpStatus.OK);
  }

  @RequestMapping(value = "/getImportLogsForModerator", method = RequestMethod.GET)
  public ResponseEntity<List<ImportLogDbVO>> getImportLogsForModerator( //
      @RequestHeader(AuthCookieToHeaderFilter.AUTHZ_HEADER) String token) //
      throws AuthenticationException, IngeApplicationException, AuthorizationException, IngeTechnicalException {

    List<ImportLogDbVO> importLogDbVOs = this.importService.getImportLogsForModerator(token);

    return new ResponseEntity<>(importLogDbVOs, HttpStatus.OK);
  }

  @RequestMapping(value = "/import", method = RequestMethod.POST)
  public ResponseEntity<?> doImport( //
      @RequestHeader(AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestParam(IMPORT_NAME) String importName, //
      @RequestParam(CONTEXT_ID) String contextId, //
      @RequestParam(FORMAT) ImportLogDbVO.Format format, //
      HttpServletRequest request) //
      throws AuthenticationException, AuthorizationException, IngeApplicationException, IngeTechnicalException {

    InputStream fileStream = null;
    try {
      fileStream = request.getInputStream();
    } catch (IOException e) {
    }

    this.importService.doImport(importName, contextId, format, fileStream, token);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  // TODO: Multipart Konfiguration in Server einrichten
  @RequestMapping(value = "/import2", method = RequestMethod.POST, consumes = "multipart/form-data")
  public ResponseEntity<?> doImport2( //
      @RequestHeader(AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestParam(IMPORT_NAME) String importName, //
      @RequestParam(CONTEXT_ID) String contextId, //
      @RequestParam(FORMAT) ImportLogDbVO.Format format, //
      @RequestParam(FILE) MultipartFile multipartFile) //
      throws AuthenticationException, AuthorizationException, IngeApplicationException, IngeTechnicalException {

    InputStream fileStream = null;
    try {
      fileStream = multipartFile.getInputStream();
    } catch (IOException e) {
    }

    this.importService.doImport(importName, contextId, format, fileStream, token);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/submitImportedItems", method = RequestMethod.PUT)
  public ResponseEntity<?> submitImportedItems( //
      @RequestHeader(AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestParam(IMPORT_LOG_ID) Integer importLogId, //
      @RequestParam(SUBMIT_MODUS) ImportLog.SubmitModus submitModus) //
      throws AuthenticationException, IngeApplicationException, AuthorizationException, IngeTechnicalException {

    this.importService.submitImportedItems(importLogId, submitModus, token);

    return new ResponseEntity<>(HttpStatus.OK);
  }
}
