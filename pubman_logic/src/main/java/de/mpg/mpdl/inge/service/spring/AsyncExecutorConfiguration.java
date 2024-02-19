package de.mpg.mpdl.inge.service.spring;

import java.util.concurrent.Executor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncExecutorConfiguration {

  private static final Logger logger = LogManager.getLogger(AsyncExecutorConfiguration.class);

  @Bean
  @Primary
  public Executor defaultAsyncExecutor() {

    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2); //default: 1
    executor.setMaxPoolSize(10); //default: Integer.MAX_VALUE
    executor.setQueueCapacity(20); // default: Integer.MAX_VALUE
    executor.setKeepAliveSeconds(120); // default: 60 seconds
    executor.initialize();

    logger.info("Using AsyncExecutorCorePoolSize " + executor.getCorePoolSize());
    logger.info("Using AsyncExecutorMaxPoolSize " + executor.getMaxPoolSize());
    logger.info("Using AsyncExecutorQueueCapacity " + executor.getQueueCapacity());
    logger.info("Using AsyncExecutorKeepAliveSeconds " + executor.getKeepAliveSeconds());

    return executor;
  }
}
