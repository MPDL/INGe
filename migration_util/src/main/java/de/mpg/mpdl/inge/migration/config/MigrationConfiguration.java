package de.mpg.mpdl.inge.migration.config;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

import org.apache.log4j.Logger;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import de.mpg.mpdl.inge.db.spring.JPAConfiguration;
import de.mpg.mpdl.inge.migration.beans.Migration;
import de.mpg.mpdl.inge.service.spring.AppConfigPubmanLogic;

@Configuration
@ComponentScan("de.mpg.mpdl.inge.migration.beans")
@Import(value = {JPAConfiguration.class, AppConfigPubmanLogic.class})
@PropertySource(value = "file:migration.properties", ignoreResourceNotFound = false)
@EnableAsync
public class MigrationConfiguration implements AsyncConfigurer {
  static Logger log = Logger.getLogger(MigrationConfiguration.class.getName());

  @Override
  public Executor getAsyncExecutor() {
    // TODO Auto-generated method stub
    // ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    // executor.setMaxPoolSize(1);
    return new ThreadPoolTaskExecutor();
  }

  @Override
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    // TODO Auto-generated method stub
    return new AsyncUncaughtExceptionHandler() {

      @Override
      public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        // TODO Auto-generated method stub
        log.error("uncaught async exception", ex);
        log.info("method name: " + method.getName());
        for (Object obj : params) {
          log.info("object param " + obj);
        }
      }
    };
  }

}
