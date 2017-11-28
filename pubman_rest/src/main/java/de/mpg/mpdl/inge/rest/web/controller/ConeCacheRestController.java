package de.mpg.mpdl.inge.rest.web.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.mpg.mpdl.inge.cone_cache.ConeCache;
import de.mpg.mpdl.inge.es.connector.ElasticSearchClientProvider;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;

// TODO: Authorization
@RestController
@RequestMapping("/coneCache")
public class ConeCacheRestController {

  private static Logger logger = Logger.getLogger(ConeCacheRestController.class);

  @Autowired
  ElasticSearchClientProvider client;

  @RequestMapping(value = "refresh", method = RequestMethod.POST)
  public ResponseEntity<String> refresh() throws AuthenticationException, AuthorizationException,
      IngeTechnicalException, IngeApplicationException {

    logger.info("REST: CONE-Cache refresh task starts...");

    String srResponse;

    try {
      ConeCache.refreshCache();
      srResponse = "REST: CONE-Cache refresh task finished.";
      logger.info(srResponse);
    } catch (Exception e) {
      srResponse = "Error in CONE Cache Refresh: " + e;
      logger.error(srResponse);
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.TEXT_PLAIN);

    return new ResponseEntity<String>(srResponse, headers, HttpStatus.OK);
  }

}
