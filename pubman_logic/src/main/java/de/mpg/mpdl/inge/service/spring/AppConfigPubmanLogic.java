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

import de.mpg.mpdl.inge.es.spring.AppConfig;
import de.mpg.mpdl.inge.util.PropertyReader;

@Configuration
@ComponentScan("de.mpg.mpdl.inge.service")
@Import(AppConfig.class)
// @EnableAspectJAutoProxy
@EnableTransactionManagement
public class AppConfigPubmanLogic {



  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(restDataSource());
    em.setPackagesToScan(new String[] {"de.mpg.mpdl.inge.service"});

    JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    em.setJpaVendorAdapter(vendorAdapter);
    em.setJpaProperties(hibernateProperties());
    return em;
  }



  /*
   * @Bean public LocalSessionFactoryBean sessionFactory() { LocalSessionFactoryBean sessionFactory
   * = new LocalSessionFactoryBean(); sessionFactory.setDataSource(restDataSource());
   * sessionFactory.setPackagesToScan(new String[] {"de.mpg.mpdl.inge.model"});
   * sessionFactory.setHibernateProperties(hibernateProperties());
   * 
   * return sessionFactory; }
   */


  @Bean
  public DataSource restDataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(PropertyReader.getProperty("inge.database.driver.class"));
    dataSource.setUrl(PropertyReader.getProperty("inge.database.jdbc.url"));
    dataSource.setUsername(PropertyReader.getProperty("inge.database.user.name"));
    dataSource.setPassword(PropertyReader.getProperty("inge.database.user.password"));

    return dataSource;
  }



  @Bean
  public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(emf);

    return transactionManager;
  }


  /*
   * 
   * @Bean
   * 
   * @Autowired public HibernateTransactionManager transactionManager(SessionFactory sessionFactory)
   * {
   * 
   * HibernateTransactionManager txManager = new HibernateTransactionManager();
   * txManager.setSessionFactory(sessionFactory);
   * 
   * return txManager; }
   */

  /*
   * 
   * @Bean public PersistenceExceptionTranslationPostProcessor exceptionTranslation() { return new
   * PersistenceExceptionTranslationPostProcessor(); }
   */
  Properties hibernateProperties() {
    return new Properties() {
      {
        // setProperty("hibernate.hbm2ddl.auto", "create");
        setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL9Dialect");
        // Makes it slow if set tot true
        setProperty("show_sql", "false");
      }
    };
  }


}
