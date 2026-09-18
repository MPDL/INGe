package de.mpg.mpdl.inge.service.listener;

import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import de.mpg.mpdl.inge.es.dao.AttachmentDocument;
import de.mpg.mpdl.inge.es.dao.PubItemDaoEs;
import de.mpg.mpdl.inge.filestorage.FileStorageInterface;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;
import jakarta.jms.ObjectMessage;

@Component
public class FulltextIndexer {

  private static final Logger logger = LogManager.getLogger(FulltextIndexer.class);
  private final FulltextAttachmentDocumentExtractor fulltextAttachmentDocumentExtractor = new FulltextAttachmentDocumentExtractor();

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
      this.pubItemDao.deleteByQuery(q, 1000);

      if (null != item.getFiles()) {
        for (FileDbVO fileVO : item.getFiles()) {
          if (FileDbVO.Storage.INTERNAL_MANAGED.equals(fileVO.getStorage()) && FileDbVO.Visibility.PUBLIC.equals(fileVO.getVisibility())) {
            long start = System.currentTimeMillis();
            logger.info("Index fulltext for: " + item.getObjectIdAndVersion() + " - " + fileVO.getObjectId() + " - "
                + fileVO.getLocalFileIdentifier() + " - " + fileVO.getSize());
            try (InputStream inputStream = this.fsi.readFile(fileVO.getLocalFileIdentifier())) {
              AttachmentDocument attachmentDocument = this.fulltextAttachmentDocumentExtractor.extract(fileVO, inputStream);
              this.pubItemDao.createFulltext(item.getObjectIdAndVersion(), fileVO.getObjectId(), attachmentDocument);
            }
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
