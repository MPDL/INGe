package de.mpg.mpdl.inge.db.spring;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.SharedCacheMode;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.ehcache.core.util.ClassLoading;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import de.mpg.mpdl.inge.util.PropertyReader;

import java.net.URI;

@Configuration
@ComponentScan({"de.mpg.mpdl.inge.db.repository", "de.mpg.mpdl.inge.db.filestorage", "de.mpg.mpdl.inge.db.spring"})
@EnableJpaRepositories(basePackages = "de.mpg.mpdl.inge.db.repository", entityManagerFactoryRef = "entityManagerFactory",
    transactionManagerRef = "transactionManager")
@EnableTransactionManagement
public class JPAConfiguration {

  private static final Logger logger = Logger.getLogger(JPAConfiguration.class);

  @Bean
  @Primary
  public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws Exception {
    //Set default class loader for CacheManager to avoid problems with ear classloading.
    //Caching.setDefaultClassLoader(JPAConfiguration.class.getClassLoader());
    //Create a ehcache cache manager
    //defaultCacheManager();

    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(restDataSource());
    em.setPackagesToScan(new String[] {"de.mpg.mpdl.inge.model.db"});
    JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    em.setJpaVendorAdapter(vendorAdapter);
    // Hibernate Properties are under /src/main/resources
    // em.setJpaProperties(hibernateProperties());
    em.setSharedCacheMode(SharedCacheMode.ENABLE_SELECTIVE);
    return em;
  }


  @Bean
  @Primary
  public DataSource restDataSource() throws Exception {
    ComboPooledDataSource dataSource = new ComboPooledDataSource();

    dataSource.setDriverClass(PropertyReader.getProperty(PropertyReader.INGE_DATABASE_DRIVER_CLASS));


    dataSource.setJdbcUrl(PropertyReader.getProperty(PropertyReader.INGE_DATABASE_JDBC_URL));
    dataSource.setUser(PropertyReader.getProperty(PropertyReader.INGE_DATABASE_USER_NAME));
    dataSource.setPassword(PropertyReader.getProperty(PropertyReader.INGE_DATABASE_USER_PASSWORD));

    logger.info("Using database <" + PropertyReader.getProperty(PropertyReader.INGE_DATABASE_JDBC_URL) + ">");
    logger.info("Using database user <" + PropertyReader.getProperty(PropertyReader.INGE_DATABASE_USER_NAME) + ">");


    return dataSource;
  }

  @Bean
  @Primary
  public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(emf);
    transactionManager.setDefaultTimeout(30);

    return transactionManager;
  }


  @Bean
  public CacheManager defaultCacheManager() throws Exception {
    CachingProvider provider = Caching.getCachingProvider(EhcacheCachingProvider.class.getName());
    URI ehcacheConfigFileURI = JPAConfiguration.class.getClassLoader().getResource("ehcache.xml").toURI();
    logger.info("URI for ehcache:" + ehcacheConfigFileURI);
    CacheManager cacheManager = provider.getCacheManager(ehcacheConfigFileURI, ClassLoading.getDefaultClassLoader());

    return cacheManager;
  }



}
