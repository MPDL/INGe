package de.mpg.mpdl.inge.service.listener;

import java.io.ByteArrayInputStream;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.service.util.OaiFileTools;

@Component
public class OaiPmhItemChangeListener {

  private Logger logger = LogManager.getLogger(OaiPmhItemChangeListener.class);

  @JmsListener(containerFactory = "topicContainerFactory", destination = "items-topic", selector = "method='release'")
  public void createOrUpdateOai(PubItemVO item) {
    try {
      String s = XmlTransformingService.transformToItem(item);
      OaiFileTools.createFile(new ByteArrayInputStream(s.getBytes()), item.getVersion().getObjectIdAndVersion() + ".xml");
    } catch (Exception e) {
      logger.error("Error while creating OAI-PMH file for " + item.getVersion().getObjectId(), e);

    }
  }

  @JmsListener(containerFactory = "topicContainerFactory", destination = "items-topic", selector = "method='delete' OR method='withdraw'")
  public void deleteOai(PubItemVO item) {
    try {
      OaiFileTools.deleteFile(item.getVersion().getObjectIdAndVersion() + ".xml");
    } catch (Exception e) {
      logger.error("Error while creating OAI-PMH file for " + item.getVersion().getObjectId(), e);

    }
  }

}
