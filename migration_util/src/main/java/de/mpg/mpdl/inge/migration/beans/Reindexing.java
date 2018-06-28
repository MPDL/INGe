package de.mpg.mpdl.inge.migration.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;
import de.mpg.mpdl.inge.service.pubman.YearbookService;

@Component
public class Reindexing {

  @Autowired
  OrganizationService ouService;

  @Autowired
  ContextService ctxService;

  @Autowired
  UserAccountService userService;

  @Autowired
  PubItemService itemService;

  @Autowired
  YearbookService ybService;

  public void reindexOus() throws Exception {
    ouService.reindexAll(null);
    Thread.sleep(300000);
  }

  public void reindexContexts() throws Exception {
    ctxService.reindexAll(null);
    Thread.sleep(300000);
  }

  public void reindexUser(String id) throws Exception {
    userService.reindex(id, null);
  }

  public void reindexUsers() throws Exception {
    userService.reindexAll(null);
    Thread.sleep(300000);
  }

  // @Async
  public void reindexItems() throws Exception {
    itemService.reindexAll(null);
    Thread.sleep(28800000);
  }

  public void reindexItem(String id) throws Exception {
    itemService.reindex(id, null);
  }

  public void reindexYBs() throws Exception {
    ybService.reindexAll(null);
    Thread.sleep(300000);
  }

  public void reindexYB(String id) throws Exception {
    ybService.reindex(id, null);
  }

}
