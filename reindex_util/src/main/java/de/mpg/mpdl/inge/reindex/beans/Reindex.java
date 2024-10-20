package de.mpg.mpdl.inge.reindex.beans;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Reindex {

  private static final Logger log = Logger.getLogger(Reindex.class.getName());

  @Autowired
  private Reindexing reIndexing;

  public boolean run(String what, String id, String[] idList) throws Exception {
    switch (what) {
      case "contexts_reindex":
        reIndexing.reindexContexts();
        break;
      case "items_reindex":
        reIndexing.reindexItems();
        break;
      case "item_list_reindex":
        if (idList != null && idList.length > 0) {
          for (String listId : idList) {
            if (listId != null && !listId.isEmpty()) {
              reIndexing.reindexItem(listId);
            } else {
              log.error("Invalid Id ind idList [" + listId + "]");
              return false;
            }
          }
        } else {
          log.error("Invalid idList");
          return false;
        }
      case "ous_reindex":
        reIndexing.reindexOus();
        break;
      case "single_reindex":
        if (id != null && !id.isEmpty()) {
          reIndexing.reindexItem(id);
        } else {
          log.error("Invalid Id!!! [" + id + "]");
          return false;
        }
        break;
      case "users_reindex":
        reIndexing.reindexUsers();
        break;
      default:
        log.error("You don't really know, what exactly you want to do!!! [" + what + "]");
        return false;
    }

    return true;
  }

}
