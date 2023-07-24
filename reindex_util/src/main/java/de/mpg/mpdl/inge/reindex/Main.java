package de.mpg.mpdl.inge.reindex;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import de.mpg.mpdl.inge.reindex.beans.Reindex;
import de.mpg.mpdl.inge.reindex.config.ReindexConfiguration;

public class Main {
  private static final Logger logger = Logger.getLogger(Main.class);

  public static void main(String[] args) {
    logger.info("########### START Reindex ###########");
    boolean success = false;

    try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ReindexConfiguration.class)) {
      Reindex bean = ctx.getBean(Reindex.class);
      String what = null, id = null;

      if (args.length < 1) {
        logger.warn("You need to specify, what you're going to reindex.");
        logger.warn("Valid args: ctxs_reindex, items_reindex, ous_reindex, users_reindex, single_reindex item_xxxx");
      } else {
        if (args.length == 1) {
          what = args[0];
          logger.info("What: " + what);
        }
        if (args.length == 2) {
          what = args[0];
          logger.info("What: " + what);
          id = args[1];
          logger.info("Id: " + id);
        }
        if (what != null && !what.isEmpty()) {
          try {
            success = bean.run(what, id);
          } catch (Exception e) {
            logger.error(e);
          }
        } else {
          logger.error("Invalid attempt !!!");
        }
      }
    }

    if (success) {
      logger.info("########### ENDE Reindex ###########");
    } else {
      logger.error("########### ABORTING Reindex ###########");
    }
  }
}
