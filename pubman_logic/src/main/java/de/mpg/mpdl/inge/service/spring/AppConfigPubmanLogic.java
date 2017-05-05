package de.mpg.mpdl.inge.service.spring;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import de.mpg.mpdl.inge.db.spring_config.JPAConfiguration;
import de.mpg.mpdl.inge.es.spring.AppConfig;
import de.mpg.mpdl.inge.util.PropertyReader;

@Configuration
@ComponentScan("de.mpg.mpdl.inge.service")
@Import({AppConfig.class, JPAConfiguration.class})
// @EnableAspectJAutoProxy
@EnableTransactionManagement
public class AppConfigPubmanLogic {



}
