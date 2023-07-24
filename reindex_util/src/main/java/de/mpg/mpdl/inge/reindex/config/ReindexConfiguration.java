package de.mpg.mpdl.inge.reindex.config;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

import org.apache.log4j.Logger;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import de.mpg.mpdl.inge.db.spring.JPAConfiguration;
import de.mpg.mpdl.inge.service.spring.AppConfigPubmanLogic;

@Configuration
@ComponentScan("de.mpg.mpdl.inge.reindex.beans")
@Import(value = {JPAConfiguration.class, AppConfigPubmanLogic.class})
@EnableAsync
public class ReindexConfiguration implements AsyncConfigurer {
  private static final Logger logger = Logger.getLogger(ReindexConfiguration.class);

  @Override
  public Executor getAsyncExecutor() {
    return new ThreadPoolTaskExecutor();
  }

  @Override
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return new AsyncUncaughtExceptionHandler() {

      @Override
      public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        logger.error("uncaught async exception", ex);
        logger.info("method name: " + method.getName());
        for (Object obj : params) {
          logger.info("object param " + obj);
        }
      }
    };
  }

}
