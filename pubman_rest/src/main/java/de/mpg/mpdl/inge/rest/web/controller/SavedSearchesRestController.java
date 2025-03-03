package de.mpg.mpdl.inge.rest.web.controller;

import de.mpg.mpdl.inge.model.db.valueobjects.SavedSearchDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.rest.web.exceptions.NotFoundException;
import de.mpg.mpdl.inge.rest.web.spring.AuthCookieToHeaderFilter;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.SavedSearchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/savedSearches")
@Tag(name = "Saved Searches")
public class SavedSearchesRestController {

  private static final Logger logger = LogManager.getLogger(SavedSearchesRestController.class);

  private static final String SAVED_SEARCH_PATH = "/{savedSearchId}";
  private static final String SAVED_SEARCH_VAR = "savedSearchId";


  @Autowired
  private SavedSearchService savedSearchService;


  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<SavedSearchDbVO> create(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @RequestBody SavedSearchDbVO ctx)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    SavedSearchDbVO created = null;
    created = this.savedSearchService.create(ctx, token);
    return new ResponseEntity<>(created, HttpStatus.CREATED);
  }

  @RequestMapping(value = SAVED_SEARCH_PATH, method = RequestMethod.PUT)
  public ResponseEntity<SavedSearchDbVO> update(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = SAVED_SEARCH_VAR) String ctxId, @RequestBody SavedSearchDbVO ctx)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    SavedSearchDbVO updated = null;
    updated = this.savedSearchService.update(ctx, token);
    return new ResponseEntity<>(updated, HttpStatus.OK);
  }

  @RequestMapping(value = SAVED_SEARCH_PATH, method = RequestMethod.GET)
  public ResponseEntity<SavedSearchDbVO> get(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token,
      @PathVariable(value = SAVED_SEARCH_VAR) String itemId)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, NotFoundException {
    SavedSearchDbVO item = null;
    item = this.savedSearchService.get(itemId, null);
    if (null == item) {
      throw new NotFoundException();
    }

    return new ResponseEntity<>(item, HttpStatus.OK);
  }

  @RequestMapping(value = "", method = RequestMethod.GET)
  public ResponseEntity<List<SavedSearchDbVO>> getAll(
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = true) String token)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    List<SavedSearchDbVO> returnLsit = savedSearchService.getAll(token);
    return new ResponseEntity<>(returnLsit, HttpStatus.OK);
  }

  @RequestMapping(value = SAVED_SEARCH_PATH, method = RequestMethod.DELETE)
  public ResponseEntity<?> delete(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = SAVED_SEARCH_VAR) String ctxId)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    this.savedSearchService.delete(ctxId, token);
    return new ResponseEntity<>(HttpStatus.OK);
  }


}
