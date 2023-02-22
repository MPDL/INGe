package de.mpg.mpdl.inge.service.listener;

import java.io.ByteArrayInputStream;

import org.apache.log4j.Logger;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.util.EntityTransformer;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.service.util.OaiFileTools;

@Component
public class OaiPmhItemChangeListener {

  private static final Logger logger = Logger.getLogger(OaiPmhItemChangeListener.class);

  @JmsListener(containerFactory = "topicContainerFactory", destination = "items-topic", selector = "method='release'")
  public void createOrUpdateOai(ItemVersionVO item) {
    try {
      String s = XmlTransformingService.transformToItem(EntityTransformer.transformToOld(item));
      OaiFileTools.createFile(new ByteArrayInputStream(s.getBytes()), item.getObjectId() + ".xml");
    } catch (Exception e) {
      logger.error("Error while creating OAI-PMH file for " + item.getObjectId(), e);

    }
  }

  @JmsListener(containerFactory = "topicContainerFactory", destination = "items-topic", selector = "method='delete' OR method='withdraw'")
  public void deleteOai(ItemVersionVO item) {
    try {
      OaiFileTools.deleteFile(item.getObjectId() + ".xml");
    } catch (Exception e) {
      logger.error("Error while creating OAI-PMH file for " + item.getObjectId(), e);

    }
  }

}
