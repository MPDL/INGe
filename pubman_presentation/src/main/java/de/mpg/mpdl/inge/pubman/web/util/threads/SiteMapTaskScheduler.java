package de.mpg.mpdl.inge.pubman.web.util.threads;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SiteMapTaskScheduler {


  @Autowired
  private ApplicationContext applicationContext;

  @Scheduled(cron = "${inge.cron.pubman.sitemap}")
  public void createSiteMap() {
    //As SiteMapTask is in scope "prototype", it creates a new instance
    this.applicationContext.getBean(SiteMapTask.class).run();
  }

}
