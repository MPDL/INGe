package de.mpg.mpdl.inge.reindex;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import de.mpg.mpdl.inge.reindex.beans.Reindex;
import de.mpg.mpdl.inge.reindex.config.ReindexConfiguration;

public class Main {
  private static final Logger log = Logger.getLogger(Main.class.getName());

  public static void main(String[] args) {
    log.info("########### START Reindex ###########");
    boolean success = false;

    try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ReindexConfiguration.class)) {
      Reindex bean = ctx.getBean(Reindex.class);
      String what = null, id = null;
      String[] idList = null;

      if (args.length < 1) {
        log.warn("You need to specify, what you're going to reindex.");
        log.warn("Valid args: ctxs_reindex, items_reindex, ous_reindex, users_reindex, single_reindex item_xxxx");
      } else {
        if (args.length == 1) {
          what = args[0];
          log.info("What: " + what);
        }
        if (args.length == 2) {
          what = args[0];
          log.info("What: " + what);
          id = args[1];
          log.info("Id: " + id);
        }
        if (args.length > 2) {
          what = args[0];
          log.info("What: " + what);
          for (int i = 0; i < args.length - 1; i++) {
            idList[i] = args[i + 1];
          }
        }
        if (what != null && !what.isEmpty()) {
          try {
            success = bean.run(what, id, idList);
          } catch (Exception e) {
            log.error(e);
          }
        } else {
          log.error("Invalid attempt !!!");
        }
      }
    }

    if (success) {
      log.info("########### ENDE Reindex ###########");
    } else {
      log.error("########### ABORTING Reindex ###########");
    }
  }
}
