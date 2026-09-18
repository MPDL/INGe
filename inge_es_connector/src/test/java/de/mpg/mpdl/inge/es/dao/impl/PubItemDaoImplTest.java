package de.mpg.mpdl.inge.es.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import de.mpg.mpdl.inge.es.dao.AttachmentDocument;

public class PubItemDaoImplTest {

  @Test
  public void createFulltextDocumentShouldMatchExistingIndexedStructure() {
    PubItemDaoImpl dao = new PubItemDaoImpl();
    AttachmentDocument attachmentDocument = new AttachmentDocument();
    attachmentDocument.setContent("fulltext");
    attachmentDocument.setContentType("application/pdf");
    attachmentDocument.setContentLength(8L);
    attachmentDocument.setTitle("Example");

    ObjectNode document = dao.createFulltextDocument("item1", "file1", attachmentDocument);

    assertEquals("item1", document.path("fileData").path("itemId").asText());
    assertEquals("file1", document.path("fileData").path("fileId").asText());
    assertEquals("fulltext", document.path("fileData").path("attachment").path("content").asText());
    assertEquals("application/pdf", document.path("fileData").path("attachment").path("content_type").asText());
    assertEquals(8L, document.path("fileData").path("attachment").path("content_length").asLong());
    assertEquals("file", document.path("joinField").path("name").asText());
    assertEquals("item1", document.path("joinField").path("parent").asText());
    assertFalse(document.path("fileData").has("data"));
  }
}
