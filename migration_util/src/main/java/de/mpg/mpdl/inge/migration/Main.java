package de.mpg.mpdl.inge.migration;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import de.mpg.mpdl.inge.migration.beans.Migration;
import de.mpg.mpdl.inge.migration.config.MigrationConfiguration;

public class Main {

  static Logger log = Logger.getLogger(Main.class.getName());

  public static void main(String[] args) {

    try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(MigrationConfiguration.class)) {
      Migration bean = ctx.getBean(Migration.class);
      String furl = ctx.getEnvironment().getProperty("escidoc.url");
      // String what = System.getProperty("what");
      String what = null, id = null;

      System.out.println("NUMBER OF ARGS " + args.length);
      if (args.length < 1) {
        System.out.println("You need to specify, what you're going to migrate.");
        System.out.println("Valid args: ctxs, ous, users, logins, items");
        System.out.println("In order to reindex, append _reindex, e.g. items_reindex");
      }
      if (args.length == 1) {
        what = args[0];
      }
      if (args.length == 2) {
        what = args[0];
        id = args[1];
      }
      if (what != null && !what.isEmpty()) {
        log.info("... migrating from " + furl);
        try {
          bean.run(what, id);

        } catch (Exception e) {
          e.printStackTrace();
        }
      } else {
        System.out.println("Invalid attempt !!!");
      }

      // ((AnnotationConfigApplicationContext) (ctx)).close();

    }

  }

}
