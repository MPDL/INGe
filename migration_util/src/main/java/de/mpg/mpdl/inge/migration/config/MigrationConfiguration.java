package de.mpg.mpdl.inge.migration.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import de.mpg.mpdl.inge.db.spring.JPAConfiguration;
import de.mpg.mpdl.inge.service.spring.AppConfigPubmanLogic;

@Configuration
@ComponentScan("de.mpg.mpdl.inge.migration.beans")
@Import(value = {JPAConfiguration.class, AppConfigPubmanLogic.class})
@PropertySource(value = "file:migration.properties", ignoreResourceNotFound = false)
public class MigrationConfiguration {

}
