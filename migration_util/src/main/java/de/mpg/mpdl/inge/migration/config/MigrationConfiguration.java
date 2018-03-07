package de.mpg.mpdl.inge.migration.config;

import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.concurrent.Executor;

import org.apache.http.client.HttpClient;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import de.mpg.mpdl.inge.db.spring.JPAConfiguration;
import de.mpg.mpdl.inge.migration.beans.Migration;
import de.mpg.mpdl.inge.migration.beans.MigrationUtilBean;
import de.mpg.mpdl.inge.service.spring.AppConfigPubmanLogic;

@Configuration
@ComponentScan("de.mpg.mpdl.inge.migration.beans")
@Import(value = {JPAConfiguration.class, AppConfigPubmanLogic.class})
@PropertySource(value = "file:migration.properties", ignoreResourceNotFound = false)
@EnableAsync
public class MigrationConfiguration implements AsyncConfigurer {

  @Override
  public Executor getAsyncExecutor() {
    // TODO Auto-generated method stub
    return new ThreadPoolTaskExecutor();
  }

  @Override
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    // TODO Auto-generated method stub
    return new AsyncUncaughtExceptionHandler() {

      @Override
      public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        // TODO Auto-generated method stub
        System.out.println(ex.getMessage());
        System.err.println(method.getName());
        for (Object obj : params) {
          System.out.println(obj);
        }
      }
    };
  }

}
