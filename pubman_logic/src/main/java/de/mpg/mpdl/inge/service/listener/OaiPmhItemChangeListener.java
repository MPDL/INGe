package de.mpg.mpdl.inge.service.listener;

import java.io.ByteArrayInputStream;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.service.util.OaiFileTools;

@Component
public class OaiPmhItemChangeListener {


  @JmsListener(containerFactory = "topicContainerFactory", destination = "items-topic",
      selector = "method='release'")
  public void createOrUpdateOai(PubItemVO item) throws Exception {
    String s = XmlTransformingService.transformToItem(item);
    OaiFileTools.createFile(new ByteArrayInputStream(s.getBytes()), item.getVersion()
        .getObjectIdAndVersion() + ".xml");
  }

  @JmsListener(containerFactory = "topicContainerFactory", destination = "items-topic",
      selector = "method='delete' OR method='withdraw'")
  public void deleteOai(PubItemVO item) throws Exception {
    OaiFileTools.deleteFile(item.getVersion().getObjectIdAndVersion() + ".xml");
  }

}
