package de.mpg.mpdl.inge.rest.web.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.apache.log4j.Logger;

import org.ehcache.core.statistics.CacheStatistics;
import org.ehcache.sizeof.SizeOf;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.util.Arrays;
import java.util.Iterator;

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
      CachingProvider provider = Caching.getCachingProvider();
      CacheManager cacheManager = provider.getCacheManager();

      for (String cacheName : cacheManager.getCacheNames()) {
        Cache cache = cacheManager.getCache(cacheName);

        if (name == null || cacheName.equals(name)) {
          srResponse.append(cacheName + " : " + getSize(cache) + "\n");
        }

        if (cacheName.equals(name)) {
          for (Object key : cache) {
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
      CachingProvider provider = Caching.getCachingProvider();
      CacheManager cacheManager = provider.getCacheManager();



      for (String cacheName : cacheManager.getCacheNames()) {
        Cache cache = cacheManager.getCache(cacheName);

        if (name == null || cacheName.equals(name)) {
          srResponse.append(cacheName + " : " + getSize(cache));
          cache.clear();
          //cacheManager.clearAllStartingWith(cacheName);
          long newSize = getSize(cache);
          srResponse.append(" -> " + (newSize == 0 ? "cleared" : newSize) + "\n");
        }
      }
    } catch (Exception e) {
      srResponse.append("Error: " + e);
      logger.error(srResponse);
    }

    return srResponse.toString();
  }

  private static <K extends Object, V extends Object> long getSize(Cache<K, V> cache) {
    Iterator<Cache.Entry<K, V>> itr = cache.iterator();
    long count = 0;
    while (itr.hasNext()) {
      itr.next();
      count++;
    }
    return count;
  }
}
