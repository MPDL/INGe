package de.mpg.mpdl.inge.service.spring;

import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import de.mpg.mpdl.inge.dao.OrganizationDao;
import de.mpg.mpdl.inge.es.dao.impl.OrganizationDaoImpl;

@Configuration
@ComponentScan("de.mpg.mpdl.inge.service")
@EnableAspectJAutoProxy
public class AppConfigPubmanLogic {


}
