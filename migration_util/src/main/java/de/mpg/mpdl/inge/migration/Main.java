package de.mpg.mpdl.inge.migration;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import de.mpg.mpdl.inge.migration.beans.Migration;
import de.mpg.mpdl.inge.migration.config.MigrationConfiguration;
import de.mpg.mpdl.inge.util.PropertyReader;

public class Main {

  static Logger log = Logger.getLogger(Main.class.getName());

  public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(MigrationConfiguration.class);
    Migration bean = ctx.getBean(Migration.class);
    String furl = ctx.getEnvironment().getProperty("escidoc.url");
    String what = System.getProperty("what");
    if (what != null && !what.isEmpty()) {
      bean.sayHello(what);
      System.out.println("from " + furl);
      log.info("... migrating from " + furl);
      try {
        bean.run(what);
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      System.out.println("You need to specify, what you're going to migrate.");
      System.out.println("-Dwhat=...");
    }

    ((AnnotationConfigApplicationContext) (ctx)).close();

  }

}
