package de.mpg.mpdl.inge.es.spring;

import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import de.mpg.mpdl.inge.es.dao.OrganizationDaoEs;

@Configuration
@ComponentScan("de.mpg.mpdl.inge.es")
public class AppConfig {



  public static void main(String[] args) {
    ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
    OrganizationDaoEs<QueryBuilder> printer = context.getBean(OrganizationDaoEs.class);
  }
}
