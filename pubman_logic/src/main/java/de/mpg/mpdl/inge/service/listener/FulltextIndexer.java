package de.mpg.mpdl.inge.service.listener;

import java.io.ByteArrayOutputStream;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import de.mpg.mpdl.inge.es.dao.PubItemDaoEs;
import de.mpg.mpdl.inge.filestorage.FileStorageInterface;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO.Storage;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO.Visibility;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;
import jakarta.jms.ObjectMessage;

@Component
public class FulltextIndexer {

  private static final Logger logger = Logger.getLogger(FulltextIndexer.class);

  @Autowired
  PubItemDaoEs pubItemDao;

  @Autowired
  @Qualifier("fileSystemServiceBean")
  private FileStorageInterface fsi;


  @JmsListener(containerFactory = "queueContainerFactory", destination = "reindex-fulltext")
  public void receiveMessage(ObjectMessage msg) {
    try {
      ItemVersionVO item = (ItemVersionVO) msg.getObject();

      //Delete all fulltexts for this item
      Query q = Query.of(i -> i.term(t -> t.field(PubItemServiceDbImpl.INDEX_FULLTEXT_ITEM_ID).value(item.getObjectIdAndVersion())));
      pubItemDao.deleteByQuery(q, 1000);

      if (item.getFiles() != null) {
        for (FileDbVO fileVO : item.getFiles()) {
          if (Storage.INTERNAL_MANAGED.equals(fileVO.getStorage()) && Visibility.PUBLIC.equals(fileVO.getVisibility())) {
            long start = System.currentTimeMillis();
            logger.info("Index fulltext for: " + item.getObjectIdAndVersion() + " - " + fileVO.getObjectId() + " - "
                + fileVO.getLocalFileIdentifier() + " - " + fileVO.getSize());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            fsi.readFile(fileVO.getLocalFileIdentifier(), bos);
            bos.flush();
            bos.close();
            pubItemDao.createFulltext(item.getObjectIdAndVersion(), fileVO.getObjectId(), bos.toByteArray());
            long time = System.currentTimeMillis() - start;
            logger.info("Finished fulltext indexing for: " + item.getObjectIdAndVersion() + " - " + fileVO.getObjectId() + " - "
                + fileVO.getLocalFileIdentifier() + " - " + fileVO.getSize() + " - " + time + " ms");
          }
        }
      }
    } catch (Exception e) {
      logger.error("Error while indexing fulltext", e);
    }
  }
}
