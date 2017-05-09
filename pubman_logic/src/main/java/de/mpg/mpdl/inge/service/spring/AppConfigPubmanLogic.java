package de.mpg.mpdl.inge.service.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import de.mpg.mpdl.inge.db.spring_config.JPAConfiguration;
import de.mpg.mpdl.inge.es.spring.AppConfig;

@Configuration
@ComponentScan("de.mpg.mpdl.inge.service")
@Import({AppConfig.class, JPAConfiguration.class})
@EnableTransactionManagement
public class AppConfigPubmanLogic {
}
