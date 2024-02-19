package de.mpg.mpdl.inge.reindex.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;

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

  public void reindexOus() throws Exception {
    this.ouService.reindexAll(null);
    Thread.sleep(300000);
  }

  public void reindexContexts() throws Exception {
    this.ctxService.reindexAll(null);
    Thread.sleep(300000);
  }

  public void reindexUsers() throws Exception {
    this.userService.reindexAll(null);
    Thread.sleep(300000);
  }

  // @Async
  public void reindexItems() throws Exception {
    this.itemService.reindexAll(null);
    Thread.sleep(28800000);
  }

  public void reindexItem(String id) throws Exception {
    this.itemService.reindex(id, null);
  }
}
