package de.mpg.mpdl.inge.service.spring;

import de.mpg.mpdl.inge.dataacquisition.spring.AppConfigDataacquisition;
import de.mpg.mpdl.inge.db.spring.JPAConfiguration;
import de.mpg.mpdl.inge.es.spring.AppConfigIngeEsConnector;
import de.mpg.mpdl.inge.filestorage.spring.AppConfigFileStorage;
import de.mpg.mpdl.inge.inge_validation.spring.AppConfigIngeValidation;
import de.mpg.mpdl.inge.util.PropertyReader;
import jakarta.jms.JMSException;
import java.io.File;
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.apache.activemq.artemis.jms.client.ActiveMQTopic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan("de.mpg.mpdl.inge.service")
@Import({AppConfigIngeEsConnector.class, JPAConfiguration.class, AppConfigFileStorage.class, AppConfigIngeValidation.class,
    AppConfigDataacquisition.class, AsyncExecutorConfiguration.class})
@EnableAsync
@EnableJms
@EnableScheduling
@EnableTransactionManagement
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
  public EmbeddedActiveMQ brokerService() throws Exception {

    EmbeddedActiveMQ brokerService = new EmbeddedActiveMQ();

    org.apache.activemq.artemis.core.config.Configuration config = new ConfigurationImpl();
    config.setPersistenceEnabled(true);
    config.addAcceptorConfiguration("in-vm", DEFAULT_BROKER_URL);
    config.setJMXUseBrokerName(false);
    config.setName("pubman");
    config.setGracefulShutdownEnabled(true);
    config.setSecurityEnabled(false);

    String jbossHomeDir = System.getProperty(PropertyReader.JBOSS_HOME_DIR);
    if (null != jbossHomeDir) {
      config.setBrokerInstance(new File(jbossHomeDir + "/standalone/data/activemq"));
    } else {
      config.setBrokerInstance(new File(System.getProperty(PropertyReader.JAVA_IO_TMPDIR)));
    }

    brokerService.setConfiguration(config);

    return brokerService;
  }

  @Bean
  public jakarta.jms.ConnectionFactory jmsConnectionFactory() throws JMSException {

    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
    connectionFactory.setBrokerURL(DEFAULT_BROKER_URL);
    connectionFactory.setDeserializationWhiteList("de.mpg.mpdl.inge.model,java.util,java.sql,org.hibernate.collection,java.lang");

    return connectionFactory;
  }

  @Bean
  public DefaultJmsListenerContainerFactory topicContainerFactory(jakarta.jms.ConnectionFactory connectionFactory) {

    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setPubSubDomain(true);
    factory.setConcurrency("1");

    return factory;
  }

  @Bean
  public JmsTemplate topicJmsTemplate(jakarta.jms.ConnectionFactory jmsConnectionFactory) throws JMSException {

    JmsTemplate jmsTemplate = new JmsTemplate(jmsConnectionFactory);
    jmsTemplate.setDefaultDestination(new ActiveMQTopic("items-topic"));
    jmsTemplate.setPubSubDomain(true);

    return jmsTemplate;
  }

  @Bean
  public DefaultJmsListenerContainerFactory queueContainerFactory(jakarta.jms.ConnectionFactory connectionFactory) {

    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setPubSubDomain(false);
    factory.setConcurrency("3-10");

    return factory;
  }

  @Bean
  public JmsTemplate queueJmsTemplate(jakarta.jms.ConnectionFactory connectionFactory) throws JMSException {

    JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
    jmsTemplate.setDefaultDestination(new ActiveMQQueue("items-queue"));
    jmsTemplate.setPubSubDomain(false);

    return jmsTemplate;
  }

  public static BeanFactory getRootContextBeanFactory() {

    if (null == PUBMAN_LOGIC_BEAN_FACTORY) {
      PUBMAN_LOGIC_BEAN_FACTORY = new ClassPathXmlApplicationContext("beanRefContext.xml");
    }

    return PUBMAN_LOGIC_BEAN_FACTORY;
  }
}
