package de.mpg.mpdl.inge.service.listener;



import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class FulltextIndexer {

  private final static Logger logger = LogManager.getLogger(FulltextIndexer.class);

  @JmsListener(containerFactory = "topicContainerFactory", destination = "items-topic")
  public void receiveMessage(ObjectMessage msg) {
    try {
      logger.info("JMS message received: ");
      // PubItemVO item = (PubItemVO)msg.getObject();
      // logger.info(item.getVersion().getObjectId());
      logger.info(msg.getStringProperty("method"));
    } catch (JMSException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }



}
