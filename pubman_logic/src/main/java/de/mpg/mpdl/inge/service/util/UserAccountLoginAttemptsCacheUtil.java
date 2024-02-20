package de.mpg.mpdl.inge.service.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Service
public class UserAccountLoginAttemptsCacheUtil {

  private static final Logger logger = LogManager.getLogger(UserAccountLoginAttemptsCacheUtil.class);

  public static final int ATTEMPT_TIMER = 30; // in Minutes
  private final LoadingCache<String, Integer> attemptsCache;

  public UserAccountLoginAttemptsCacheUtil() {
    this.attemptsCache =
        CacheBuilder.newBuilder().expireAfterWrite(this.ATTEMPT_TIMER, TimeUnit.MINUTES).build(new StringIntegerCacheLoader());
  }

  public void loginSucceeded(String key) {
    this.attemptsCache.invalidate(key);
  }

  public void loginFailed(String key) {
    int attempts = 0;
    try {
      attempts = this.attemptsCache.get(key);
    } catch (ExecutionException e) {
      attempts = 0;
    }
    attempts++;
    this.attemptsCache.put(key, attempts);
  }

  public boolean isBlocked(String key) {
    try {
      int MAX_ATTEMPT = 10;
      if (this.attemptsCache.get(key) >= MAX_ATTEMPT) {
        logger.info(key + " is blocked for the moment");
        return true;
      }
      return this.attemptsCache.get(key) >= MAX_ATTEMPT;
    } catch (ExecutionException e) {
      return false;
    }
  }

  private static class StringIntegerCacheLoader extends CacheLoader<String, Integer> {
    public Integer load(String key) {
      return 0;
    }
  }
}
