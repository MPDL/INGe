package de.mpg.mpdl.inge.migration.beans;

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
    ouService.reindexAll(null);
    Thread.sleep(300000);
  }

  public void reindexContexts() throws Exception {
    ctxService.reindexAll(null);
    Thread.sleep(300000);
  }

  public void reindexUsers() throws Exception {
    userService.reindexAll(null);
    Thread.sleep(300000);
  }

  public void reindexItems() throws Exception {
    itemService.reindexAll(null);
  }

}
