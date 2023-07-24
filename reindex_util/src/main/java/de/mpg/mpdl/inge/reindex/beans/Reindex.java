package de.mpg.mpdl.inge.reindex.beans;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Reindex {

  private static final Logger logger = Logger.getLogger(Reindex.class);

  @Autowired
  private Reindexing reIndexing;

  public boolean run(String what, String id) throws Exception {
    switch (what) {
      case "ctxs_reindex":
        reIndexing.reindexContexts();
        break;
      case "items_reindex":
        reIndexing.reindexItems();
        break;
      case "ous_reindex":
        reIndexing.reindexOus();
        break;
      case "single_reindex":
        if (id != null && !id.isEmpty()) {
          reIndexing.reindexItem(id);
        } else {
          logger.error("Invalid Id!!! [" + id + "]");
          return false;
        }
        break;
      case "users_reindex":
        reIndexing.reindexUsers();
        break;
      default:
        logger.error("You don't really know, what exactly you want to do!!! [" + what + "]");
        return false;
    }

    return true;
  }

}
