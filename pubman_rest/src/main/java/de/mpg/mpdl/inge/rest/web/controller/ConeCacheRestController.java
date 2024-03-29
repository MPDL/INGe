package de.mpg.mpdl.inge.rest.web.controller;

import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.mpg.mpdl.inge.cone_cache.ConeCache;
import springfox.documentation.annotations.ApiIgnore;

// TODO: Authorization
@RestController
@RequestMapping("/coneCache")
@ApiIgnore
public class ConeCacheRestController {

  private static final Logger logger = Logger.getLogger(ConeCacheRestController.class);

  @RequestMapping(value = "refresh", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
  public String refresh() {

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

    return srResponse;
  }

}
