package de.mpg.mpdl.inge.service.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Service
public class UserAccountLoginAttemptsCacheUtil {

  private static final Logger logger = Logger.getLogger(UserAccountLoginAttemptsCacheUtil.class);

  public final int ATTEMPT_TIMER = 30; // in Minutes
  private final int MAX_ATTEMPT = 10;
  private LoadingCache<String, Integer> attemptsCache;

  public UserAccountLoginAttemptsCacheUtil() {
    super();
    attemptsCache = CacheBuilder.newBuilder().expireAfterWrite(ATTEMPT_TIMER, TimeUnit.MINUTES).build(new CacheLoader<String, Integer>() {
      public Integer load(String key) {
        return 0;
      }
    });
  }

  public void loginSucceeded(String key) {
    attemptsCache.invalidate(key);
  }

  public void loginFailed(String key) {
    int attempts = 0;
    try {
      attempts = attemptsCache.get(key);
    } catch (ExecutionException e) {
      attempts = 0;
    }
    attempts++;
    attemptsCache.put(key, attempts);
  }

  public boolean isBlocked(String key) {
    try {
      if (attemptsCache.get(key) >= MAX_ATTEMPT) {
        logger.info(key + " is blocked for the moment");
        return true;
      }
      return attemptsCache.get(key) >= MAX_ATTEMPT;
    } catch (ExecutionException e) {
      return false;
    }
  }
}
