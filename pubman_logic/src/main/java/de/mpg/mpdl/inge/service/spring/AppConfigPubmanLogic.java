package de.mpg.mpdl.inge.service.spring;

import java.io.File;
import java.util.Arrays;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import de.mpg.mpdl.inge.db.spring.JPAConfiguration;
import de.mpg.mpdl.inge.es.spring.AppConfigIngeEsConnector;
import de.mpg.mpdl.inge.filestorage.spring.AppConfigFileStorage;
import de.mpg.mpdl.inge.inge_validation.spring.AppConfigIngeValidation;
import de.mpg.mpdl.inge.util.PropertyReader;

@Configuration
@ComponentScan("de.mpg.mpdl.inge.service")
@Import({AppConfigIngeEsConnector.class, JPAConfiguration.class, AppConfigFileStorage.class, AppConfigIngeValidation.class})
@EnableTransactionManagement
@EnableScheduling
@EnableJms
@PropertySource("classpath:pubman.properties")
public class AppConfigPubmanLogic {
  private static final Logger logger = LogManager.getLogger(AppConfigPubmanLogic.class);

  private static final String DEFAULT_BROKER_URL = "vm://localhost:0";

  private static BeanFactory PUBMAN_LOGIC_BEAN_FACTORY;//XmlBeanFactory(new ClassPathResource(("beanRefContext.xml")));

  @Bean
  public PasswordEncoder passwordEncoder() {
    logger.info("Initializing Spring Bean PasswordEncoder");
    return new BCryptPasswordEncoder();
  }

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }


  /**
   * Start and configure an ActiveMQ broker when Spring application is started
   * 
   * @return
   * @throws Exception
   */
  @Bean(initMethod = "start", destroyMethod = "stop")
  public BrokerService brokerService() throws Exception {
    BrokerService brokerService = new BrokerService();
    brokerService.setPersistent(true);
    String jbossHomeDir = System.getProperty(PropertyReader.JBOSS_HOME_DIR);
    if (jbossHomeDir != null) {
      brokerService.setDataDirectoryFile(new File(jbossHomeDir + "/standalone/data/activemq"));
    } else {
      brokerService.setDataDirectory(System.getProperty(PropertyReader.JAVA_IO_TMPDIR));
    }
    brokerService.setUseJmx(false);
    brokerService.addConnector(DEFAULT_BROKER_URL);
    brokerService.setBrokerName("localhost");
    brokerService.setUseShutdownHook(true);
    return brokerService;
  }

  @Bean
  public ConnectionFactory jmsConnectionFactory() {
    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
    connectionFactory.setBrokerURL(DEFAULT_BROKER_URL);
    connectionFactory.setUseAsyncSend(true);
    connectionFactory.setTrustedPackages(Arrays.asList("de.mpg.mpdl.inge.model", "java.util", "java.sql", "org.hibernate.collection"));
    return connectionFactory;
  }


  @Bean
  public DefaultJmsListenerContainerFactory topicContainerFactory(ConnectionFactory connectionFactory) {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setPubSubDomain(true);
    // factory.setDestinationResolver(new BeanFactoryDestinationResolver(springContextBeanFactory));
    factory.setConcurrency("1");
    return factory;
  }

  @Bean
  public JmsTemplate topicJmsTemplate(ConnectionFactory jmsConnectionFactory) throws JMSException {
    JmsTemplate jmsTemplate = new JmsTemplate(jmsConnectionFactory);
    jmsTemplate.setDefaultDestination(new ActiveMQTopic("items-topic"));
    jmsTemplate.setPubSubDomain(true);
    return jmsTemplate;
  }


  @Bean
  public DefaultJmsListenerContainerFactory queueContainerFactory(ConnectionFactory connectionFactory) {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setPubSubDomain(false);
    // factory.setDestinationResolver(new BeanFactoryDestinationResolver(springContextBeanFactory));
    factory.setConcurrency("3-10");
    return factory;
  }

  @Bean
  public JmsTemplate queueJmsTemplate(ConnectionFactory connectionFactory) throws JMSException {
    JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
    jmsTemplate.setDefaultDestination(new ActiveMQQueue("items-queue"));
    jmsTemplate.setPubSubDomain(false);
    return jmsTemplate;
  }



  public static BeanFactory getRootContextBeanFactory() {
    if (PUBMAN_LOGIC_BEAN_FACTORY == null) {
      PUBMAN_LOGIC_BEAN_FACTORY = new ClassPathXmlApplicationContext("beanRefContext.xml");
    }
    return PUBMAN_LOGIC_BEAN_FACTORY;
  }
}
