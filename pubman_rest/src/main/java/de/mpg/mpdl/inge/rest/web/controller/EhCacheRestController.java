package de.mpg.mpdl.inge.rest.web.controller;

import io.swagger.v3.oas.annotations.Hidden;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

// TODO: Authorization
@RestController
@RequestMapping("/ehCache")
@Hidden
public class EhCacheRestController {

  private static final Logger logger = Logger.getLogger(EhCacheRestController.class);

  @RequestMapping(value = "info", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
  public String info(@RequestParam(value = "name", required = false) String name) {
    StringBuilder srResponse = new StringBuilder();
    try {
      CacheManager cacheManager = CacheManager.getInstance();

      for (String cacheName : Arrays.asList(cacheManager.getCacheNames())) {
        Cache cache = cacheManager.getCache(cacheName);

        if (name == null || cacheName.equals(name)) {
          srResponse.append(cacheName + " : " + cache.getSize() + "\n");
        }

        if (cacheName.equals(name)) {
          for (Object key : cache.getKeys()) {
            srResponse.append(key + ":" + cache.get(key) + "\n");
          }
        }
      }
    } catch (Exception e) {
      srResponse.append("Error: " + e);
      logger.error(srResponse);
    }

    return srResponse.toString();
  }

  @RequestMapping(value = "clear", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
  public String clear(@RequestParam(value = "name", required = false) String name) {
    StringBuilder srResponse = new StringBuilder();

    try {
      CacheManager cacheManager = CacheManager.getInstance();

      for (String cacheName : Arrays.asList(cacheManager.getCacheNames())) {
        Cache cache = cacheManager.getCache(cacheName);

        if (name == null || cacheName.equals(name)) {
          srResponse.append(cacheName + " : " + cache.getSize());
          cacheManager.clearAllStartingWith(cacheName);
          srResponse.append(" -> " + (cache.getSize() == 0 ? "cleared" : cache.getSize()) + "\n");
        }
      }
    } catch (Exception e) {
      srResponse.append("Error: " + e);
      logger.error(srResponse);
    }

    return srResponse.toString();
  }
}
